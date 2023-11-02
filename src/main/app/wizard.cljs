(ns app.wizard
  (:require [ajax.core :as ajax]
            [app.config :as config]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [taoensso.timbre :as log]))


;; -----------------------------------------------------------------------------
;; Events and Subscriptions to query the backend and store the result in the
;; app-state.

(rf/reg-event-fx
 :wizard/get
 (fn [_ _]
   {:fx [[:http-xhrio {:method :get
                       :uri (str config/api-location "/wizard")
                       :format (ajax/transit-request-format)
                       :response-format (ajax/transit-response-format)
                       :on-success [:wizard.get/success]
                       :on-failure [:wizard.get/error]}]]}))

(rf/reg-event-db
 :wizard.get/success
 (fn [db [_ response]]
   (assoc db :wizard (:wizard response))))

(rf/reg-event-fx
 :wizard.get/error
 (fn [_ [_ error]]
   {:fx [[:log/error (str "Could not query the wizard. Did you forget to start the api? " error)]]}))

(rf/reg-sub
 :wizard
 (fn [db _]
   (:wizard db)))
