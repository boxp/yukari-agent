(ns yukari-agent.domain.usecase.mention
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async :as async]
            [com.stuartsierra.component :as component]
            [yukari-agent.infra.repository.mention :as mention-repository]))

(defn subscribe
  [{:keys [mention-repository] :as c}]
  (mention-repository/subscribe-message mention-repository))

(defrecord MentionUsecase [mention-repository]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn mention-usecase []
  (map->MentionUsecase {}))
