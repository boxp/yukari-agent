(ns yukari-agent.infra.datasource.sasara
  (:require [com.stuartsierra.component :as component]
            [clj-http.client :as client]))

(defn post-speak
  [{:keys [endpoint]} text]
  (some-> (client/get (str endpoint "/speak/" text)
                       {:as :json})
          :body
          :link
          (client/get {:as :byte-array
                       :accept "audio/L16"})
          :body))

(defrecord SasaraDatasource [endpoint]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))
