(ns app.interface.core
  (:require ["react-dom/client" :refer [createRoot]]
            [app.counter :as counter]
            [app.wizard :as wizard]
            [goog.dom :as gdom]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as log]))

(defn- main
  "Main view for the application."
  []
  (let [!wizard (rf/subscribe [:wizard])
        !counter (rf/subscribe [:counter])]
    [:div.container
     [:h1 "Welcome"]
     [:p "My first page!"]
     [:div.mt-2
      [:button.btn.btn-primary {:on-click #(rf/dispatch [:counter/decrement])}
       "-"]
      [:span.d-inline-block.ms-2.me-2.text-center.fw-bold.fs-2.align-middle {:style {:width "15rem"}}
       (counter/display @!counter)]
      [:button.btn.btn-primary {:on-click #(rf/dispatch [:counter/increment])}
       "+"]]
     [:div.mt-5
      [:button.btn.btn-outline-primary {:on-click #(rf/dispatch [:wizard/get])}
       "Query Wizard from Backend"]
      (when @!wizard [:p.display-1.pt-3 @!wizard])]]))


(rf/reg-fx
 :log/error
 (fn [message]
   (log/error message)))

;; -- Entry Point -------------------------------------------------------------

(defonce root (createRoot (gdom/getElement "app")))

(defn render
  []
  (.render root (r/as-element [main])))

(defn- ^:dev/after-load re-render
  "The `:dev/after-load` metadata causes this function to be called after
  shadow-cljs hot-reloads code. This function is called implicitly by its
  annotation."
  []
  (rf/clear-subscription-cache!)
  (render))

(defn start! []
  (rf/dispatch-sync [:counter/get])
  (render))