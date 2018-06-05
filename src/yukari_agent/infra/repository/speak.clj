(ns yukari-agent.infra.repository.speak
  (:require [com.stuartsierra.component :as component]
            [clojure.spec.alpha :as s]
            [yukari-agent.domain.entity.speak :as speak]
            [yukari-agent.infra.datasource.ai-talk :as ai-talk]
            [yukari-agent.infra.datasource.sasara :as sasara]
            [yukari-agent.infra.datasource.os :as os]))

(def tmp-file-path "/tmp/yukarisan.raw")

(defmulti speak
  #(:cast %2))

(defmethod speak :yukari
  [{:keys [os-datasource ai-talk-datasource]} speak]
  (some->> (ai-talk/post-speak ai-talk-datasource (:text speak))
           (os/play-content os-datasource tmp-file-path)))

(defmethod speak :sasara
  [{:keys [os-datasource sasara-datasource]} speak]
  (try
    (as-> (sasara/post-speak sasara-datasource (:text speak)) $
          (os/play-content os-datasource
                           tmp-file-path
                           $
                           :sample-rate "48000"
                           :format "S16_LE"))
    (catch Exception e
      (println "Warning: メッセージが不正でした " (:text speak)))))

(defmethod speak :default [_ _])

(defrecord SpeakRepository [os-datasource ai-talk-datasource sasara-datasource]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn speak-repository []
  (map->SpeakRepository {}))
