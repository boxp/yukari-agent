(ns yukari-agent.domain.entity.mention
  (:require [clojure.spec.alpha :as s]))

(s/def :field/title string?)
(s/def :field/value string?)
(s/def :field/short? boolean?)
(s/def ::field (s/keys :req-un [:field/title :field/value :field/short?]))

(s/def :attachment/text string?)
(s/def :attachment/color string?)
(s/def :attachment/fields (s/coll-of ::field))
(s/def ::attachment (s/keys :req-un [:attachment/text
                                     :attachment/color
                                     :attachment/fields]))

(s/def :mention/type #{:mention :post})
(s/def :mention/for-me? boolean?)
(s/def :mention/from-me? #{:post :mention})
(s/def :mention/channel-id string?)
(s/def :mention/text string?)
(s/def :mention/timestamp string?)
(s/def :mention/attachments (s/coll-of ::attachment))
(s/def :mention/username string?)
(s/def ::mention (s/keys :req-un [:mention/type
                                  :mention/for-me?
                                  :mention/from-me?
                                  :mention/channel-id
                                  :mention/text
                                  :mention/timestamp
                                  :mention/attachments
                                  :mention/username]))

(s/def :mention-error/type #{:error})
(s/def :mention-error/reason string?)
(s/def ::mention-error (s/keys :req-un [:mention-error/type
                                        :mention-error/reason]))
