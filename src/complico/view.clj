(ns complico.view
  (:require [selmer.parser :as selmer]
            [hiccup.core :as hiccup]))

(def home-page 
  (selmer/render-file "templates/index.html" {}))

(defn create-base-html [url]
  (hiccup/html
    [:head [:base {:href url}]]))

(defn create-cljs-html [original-host complico-host]
  (hiccup/html
    [:script 
      {:id "complico_host_vars"
       :original_host_name original-host
       :complico_host_name complico-host
       :src (str complico-host "/js/complico-debug.js")}]))
