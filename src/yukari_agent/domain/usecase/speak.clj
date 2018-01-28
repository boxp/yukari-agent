(ns yukari-agent.domain.usecase.speak
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async :as async]
            [com.stuartsierra.component :as component]
            [yukari-agent.infra.repository.speak :as speak-repository]))

(defn speak
  [{:keys [speak-repository]} username text]
  (speak-repository/speak speak-repository
                          {:text
                           (str
                             (or username "名無し")
                             "さんからのメンションです。"
                             text)}))

(defrecord SpeakUsecase [speak-repository]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn speak-usecase []
  (map->SpeakUsecase {}))
