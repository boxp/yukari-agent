(ns yukari-agent.infra.datasource.slack-rtm
  (:require [slack-rtm.core :as rtm]
            [clojure.core.async :refer [chan put! close!]]
            [com.stuartsierra.component :as component]))

(defn wrap-for-me?
  [rtm-connection m]
  (-> m
      (assoc :for-me?
             (some-> m
                     :text
                     (.contains (str "<@" (-> rtm-connection :start :self :id) ">"))))
      (update :text
              #(clojure.string/replace %
                                       (-> (str "\\<\\@"
                                                (-> rtm-connection :start :self :id)
                                                "\\>")
                                           re-pattern)
                                       ""))))

(defn wrap-from-me?
  [rtm-connection m]
  (-> m
      (assoc :from-me?
             (some->> m
                      :user
                      (= (-> rtm-connection :start :self :id))))))

(defn- subscribe-message
  [rtm-connection]
  (let [c (chan)
        f #(some->> %
                    (wrap-for-me? rtm-connection)
                    (wrap-from-me? rtm-connection)
                    (put! c))]
    (rtm/sub-to-event (:events-publication rtm-connection) :message f)
    c))

(defrecord SlackRtmDatasource [rtm-connection message-channel token]
  component/Lifecycle
  (start [this]
    (let [rtm-connection (rtm/connect token)]
      (-> this
          (assoc :rtm-connection rtm-connection)
          (assoc :message-channel (subscribe-message rtm-connection)))))
  (stop [{:keys [rtm-connection message-channel] :as this}]
    (close! message-channel)
    (when-not (nil? rtm-connection)
      (rtm/send-event (:dispatcher rtm-connection) :close))
    (-> this
        (dissoc :rtm-connection))))

(defn slack-rtm-datasource
  [token]
  (map->SlackRtmDatasource {:token token}))
