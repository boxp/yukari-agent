(ns yukari-agent.system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [yukari-agent.infra.datasource.os :refer [os-datasource]]
            [yukari-agent.infra.datasource.slack-rtm :refer [slack-rtm-datasource]]
            [yukari-agent.infra.datasource.ai-talk :refer [ai-talk-datasource]]
            [yukari-agent.infra.repository.speak :refer [speak-repository]]
            [yukari-agent.infra.repository.mention :refer [mention-repository]]
            [yukari-agent.domain.usecase.speak :refer [speak-usecase]]
            [yukari-agent.domain.usecase.mention :refer [mention-usecase]]
            [yukari-agent.app.slackbot.yukari-agent :refer [yukari-agent]])
  (:gen-class))

(defn yukari-agent-system
  [{:keys [ai-talk-api-key
           slack-token] :as conf}]
  (component/system-map
    :ai-talk-datasource (ai-talk-datasource ai-talk-api-key)
    :slack-rtm-datasource (slack-rtm-datasource slack-token)
    :os-datasource (os-datasource)
    :speak-repository (component/using (speak-repository)
                                       [:os-datasource :ai-talk-datasource])
    :mention-repository (component/using (mention-repository)
                                         [:slack-rtm-datasource])
    :speak-usecase (component/using (speak-usecase)
                                    [:speak-repository])
    :mention-usecase (component/using (mention-usecase)
                                      [:mention-repository])
    :yukari-agent (component/using (yukari-agent)
                                   [:speak-usecase :mention-usecase])))

(defn load-config []
  {:ai-talk-api-key (env :yukari-agent-ai-talk-api-key)
   :slack-token (env :yukari-agent-slack-token)})

(defn -main []
  (component/start
    (yukari-agent-system (load-config))))
