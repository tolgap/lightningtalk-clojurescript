(ns lightningtalk.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :refer [put! <! chan]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

(defonce app-state (atom {:titles ["Hello world!"]}))

(defn change-title [data owner]
  (let [new-title (-> (om/get-node owner "changer")
                      .-value)]
    (when (not (empty? new-title))
      (om/transact! data :titles #(conj % new-title)))))
;; Same as:
;; (om/transact! data :titles (fn [xs] (conj xs new-title)))

(defn changer-component [data owner]
  (om/component
   (html [:div
          [:input {:type "text" :ref "changer" :placeholder "Add a new title"}]
          [:button {:onClick #(change-title data owner)} "Add"]])))

(defn title-component [title owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (html [:li.title
             [:span title]
             [:button {:onClick #(put! delete title)} "Delete"]]))))

(defn index-component [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:delete (chan)})
    om/IWillMount
    (will-mount [_]
      (let [delete (om/get-state owner :delete)]
        (go (loop []
          (let [title (<! delete)]
            (om/transact! data :titles
                          (fn [xs] (vec (remove #(= title %) xs))))
            (recur))))))
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (html [:div
             [:ul
              (om/build-all title-component (data :titles)
                            {:init-state {:delete delete}})]
             (om/build changer-component data)]))))

(om/root
 index-component
 app-state
 {:target (. js/document (getElementById "app"))})


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
