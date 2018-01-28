(ns yukari-agent.infra.repository.speak
  (:require [com.stuartsierra.component :as component]
            [clojure.spec.alpha :as s]
            [yukari-agent.domain.entity.speak :as speak]
            [yukari-agent.infra.datasource.ai-talk :as ai-talk]
            [yukari-agent.infra.datasource.os :as os]))

(def tmp-file-path "/tmp/yukarisan.raw")

(defn speak
  [{:keys [os-datasource ai-talk-datasource] :as c} speak]
  (some->> (ai-talk/post-speak ai-talk-datasource (:text speak))
           (os/play-content os-datasource tmp-file-path)))

(defrecord SpeakRepository [os-datasource ai-talk-datasource]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn speak-repository []
  (map->SpeakRepository {}))
