(ns yukari-agent.app.slackbot.yukari-agent
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async :as async]
            [com.stuartsierra.component :as component]
            [yukari-agent.domain.usecase.speak :as speak-usecase]
            [yukari-agent.domain.usecase.mention :as mention-usecase]))

(defmulti reaction
  (fn [_ m] (:type m)))

(defmethod reaction :mention
  [{:keys [speak-usecase] :as c}
   {:keys [username text] :as m}]
  (println m)
  (speak-usecase/speak speak-usecase username text))

(defmethod reaction :default
  [_ _])

(defn- agent-loop
  [{:keys [mention-usecase] :as c}]
  (let [mention-chan (mention-usecase/subscribe mention-usecase)]
    (async/go-loop [m {}]
      (when m
        (reaction c m)
        (recur (async/<! mention-chan))))))

(defrecord YukariAgent [speak-usecase mention-usecase]
  component/Lifecycle
  (start [this]
    (agent-loop this)
    this)
  (stop [this] this))

(defn yukari-agent []
  (map->YukariAgent {}))
