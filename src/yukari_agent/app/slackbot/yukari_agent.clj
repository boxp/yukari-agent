(ns yukari-agent.app.slackbot.yukari-agent
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async :as async]
            [com.stuartsierra.component :as component]
            [yukari-agent.domain.usecase.speak :as speak-usecase]
            [yukari-agent.domain.usecase.mention :as mention-usecase]))

(defmulti reaction
  (fn [_ m] (:type m)))

(defmethod reaction :error
  [{:keys [speak-usecase] :as c}
   {:keys [reason] :as m}]
  (throw
    (ex-info "Slack Rtm Disconnected."
           {:reason reason})))

(defmethod reaction :mention
  [{:keys [speak-usecase] :as c}
   {:keys [username text] :as m}]
  (speak-usecase/speak speak-usecase username text))

(defmethod reaction :default
  [{:keys [speak-usecase] :as c}
   {:keys [username text] :as m}]
  (speak-usecase/speak speak-usecase username text))

(defn- agent-loop
  [c]
  (loop [m {}]
    (when m
      (reaction c m)
      (recur (async/<!! (mention-usecase/subscribe (:mention-usecase c)))))))

(defrecord YukariAgent [speak-usecase mention-usecase]
  component/Lifecycle
  (start [this]
    (agent-loop this)
    this)
  (stop [this] this))

(defn yukari-agent []
  (map->YukariAgent {}))
