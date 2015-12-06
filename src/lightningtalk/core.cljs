(ns lightningtalk.core
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

(defonce app-state (atom {:title "Hello world!"}))

(defn index-component [data owner]
  (om/component
   (html [:div
          (data :title)])))

(om/root
 index-component
 app-state
 {:target (. js/document (getElementById "app"))})


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
