(ns yukari-agent.infra.datasource.ai-talk
  (:require [com.stuartsierra.component :as component]
            [clojure.spec.alpha :as s]
            [clj-http.client :as client]
            [clojure.data.xml :as xml]))

(def endpoint "https://api.apigw.smt.docomo.ne.jp/aiTalk/v1/textToSpeech")

(s/def :voice/name
  #{"sumire" "maki" "reina"})
(s/def :voice/attribute
  (s/keys :req-un [:voice/name]))
(s/def :voice/value string?)
(s/def ::voice
  (s/tuple #{:voice} :voice/attribute :voice/value))

(s/def :speak/version string?)
(s/def :speak/attribute
  (s/keys :req-un [:speak/version]))
(s/def ::speak
  (s/cat :key #{:speak}
         :attribute :speak/attribute
         :voices (s/+ ::voice)))

(defn post-speak
  [{:keys [ai-talk-api-key] :as c} text]
  (some->
    (client/post (str endpoint "?APIKEY=" ai-talk-api-key)
                 {:as :byte-array
                  :content-type "application/ssml+xml"
                  :accept "audio/L16"
                  :body (-> (xml/sexp-as-element
                              [:speak {:version "1.1"}
                               [:voice {:name "sumire"} text]])
                            xml/emit-str)})
    :body))

(defrecord AiTalkDatasource [ai-talk-api-key]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn ai-talk-datasource
  [ai-talk-api-key]
  (map->AiTalkDatasource {:ai-talk-api-key ai-talk-api-key}))
