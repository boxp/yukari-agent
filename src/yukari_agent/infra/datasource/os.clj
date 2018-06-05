(ns yukari-agent.infra.datasource.os
  (:require [com.stuartsierra.component :as component]
            [clojure.java.shell :as shell]
            [clojure.java.io :as io]))

(defn- write-file
  [file-path content]
  (some-> content
          (io/copy (io/file file-path))))

(defn- play
  [file-path {:keys [type sample-rate channels format] :as opts}]
  (let [res (shell/sh "aplay" "-t" (or type "raw")
                      "-r" (or sample-rate "16000")
                      "-c" (or channels "1")
                      "-f" (or format "S16_BE") file-path)]
    (when (not= 0 (:exit res))
      (throw
        (ex-info "Command execution failed"
          {:file-path file-path
           :error-message (:error res)})))))

(defn play-content
  [c file-path content & opts]
  (write-file file-path content)
  (play file-path opts))

(defrecord OsDatasource []
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn os-datasource []
  (map->OsDatasource {}))
