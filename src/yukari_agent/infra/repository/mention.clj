(ns yukari-agent.infra.repository.mention
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async :as async]
            [com.stuartsierra.component :as component]
            [yukari-agent.domain.entity.mention :as mention]
            [yukari-agent.infra.datasource.slack-rtm :as slack-rtm]))

(defn- Post->Mention
  [{:keys [type channel user text ts for-me? from-me? username optionals] :as post}]
  (if (= type :on-close)
    {:type :error
     :reason (:reason post)}
    {:type (cond for-me? :mention
                 :default :post)
     :channel-id channel
     :user-id user
     :text text
     :for-me? for-me?
     :from-me? from-me?
     :timestamp ts
     :username username
     :attachments (:attachments optionals)}))

(defn subscribe-message
  [{:keys [slack-rtm-datasource]}]
  (->> [(:message-channel slack-rtm-datasource)]
       (async/map Post->Mention)))

(defrecord MentionRepository [slack-rtm-datasource]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn mention-repository []
  (map->MentionRepository {}))
