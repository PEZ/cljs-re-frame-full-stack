(ns app.counter
  (:require [ajax.core :as ajax]
            [app.config :as config]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [taoensso.timbre :as log]))

;; -----------------------------------------------------------------------------
;; Events and Subscriptions to query the backend and store the result in the
;; app-state.

(rf/reg-event-fx
 :counter/get
 (fn [_ _]
   {:fx [[:http-xhrio {:method :get
                       :uri (str config/api-location "/counter")
                       :format (ajax/transit-request-format)
                       :response-format (ajax/transit-response-format)
                       :on-success [:counter.get/success]
                       :on-failure [:counter.get/error]}]]}))

(rf/reg-event-fx
 :counter/post
 (fn [_ [_ counter]]
   (def counter counter)
   {:fx [[:http-xhrio {:method :post
                       :uri (str config/api-location "/counter")
                       :params {:counter counter}
                       :format (ajax/transit-request-format)
                       :response-format (ajax/transit-response-format)
                       :on-success [:counter.post/success]
                       :on-failure [:counter.post/error]}]]}))

(rf/reg-event-db
 :counter.get/success
 (fn [db [_ response]]
   (assoc db :counter (:counter response))))

(rf/reg-event-fx
 :counter.get/error
 (fn [_ [_ error]]
   {:fx [[:log/error (str "Could not query the counter. Did you forget to start the api? " error)]]}))

(rf/reg-event-db
 :counter.post/success
 (fn [db [_ response]]
   (assoc db :counter-saved-as (:counter response))))

(rf/reg-event-fx
 :counter.post/error
 (fn [_ [_ error]]
   {:fx [[:log/error (str "Could not post the counter. Did you forget to start the api? " error)]]}))

(defn update-counter [db f]
  (let [counter (f (:counter db))]
    {:db (assoc db :counter counter)
     :dispatch [:counter/post counter]}))

(rf/reg-event-fx
 :counter/increment
 (fn [{db :db}]
   (update-counter db inc)))

(rf/reg-event-fx
 :counter/decrement
 (fn [{db :db}]
   (update-counter db dec)))

(rf/reg-sub
 :counter
 (fn [db _]
   (:counter db)))

(defn display [n]
  (str n))
