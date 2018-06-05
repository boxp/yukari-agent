(ns yukari-agent.domain.entity.speak
  (:require [clojure.spec.alpha :as s]))

(s/def :speak/text string?)
(s/def :speak/cast #{:yukari :sasara})
(s/def ::speak (s/keys :req-un [:speak/text :speak/cast]))
