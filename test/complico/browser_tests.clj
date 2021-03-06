(ns complico.browser-tests 
  (:use [clj-webdriver.taxi]
        [clojure.test])
  (:require [complico.core :as core]
            [ring.adapter.jetty :as jetty]))

(defonce server (jetty/run-jetty #'core/app {:port 3000 :join? false}))

(defn browser-up 
  []
  (set-driver! {:browser :phantomjs}))

(defn browser-down 
  [] 
  (quit))

(defn setup-teardown-fixtures 
  [f]
  (.start server)
  (browser-up)
  (f)
  (browser-down)
  (.stop server))

(use-fixtures :once setup-teardown-fixtures)

(def home-page "http://localhost:3000/")
(def expected-link "http://localhost:3000/convert?url=http%3A%2F%2Flocalhost%3A3000%2Ftest_page_with_links.html")
(def expected-price "\u00A3XXX")
(def link-to-page-with-prices "a#test_page_with_prices")
(def link-to-page-with-links "a#test_page_with_links")
(def ribbon-link "a#complico-ribbon-link")

(defn extract-price-from 
  [element-name]
  (text (element (str element-name "#price"))))

(defn extract-link-from-page 
  []
  (attribute link-to-page-with-links :href))

(defn perform-search 
  []
  (-> "input#search_form_input_homepage"
    (input-text "search-term-goes-here")
    submit))

(defn existing-form-hidden? 
  [] 
  (not (displayed? "form")))

(deftest user-journey-does-it-basically-work
  (to home-page)
  (add-cookie {:name "test" :value ""})
  (perform-search)
  
  (is (= "Complico" (title)))

  ; need this check because we are hosting page on same server!
  (is (= expected-link (extract-link-from-page)))
  (is (existing-form-hidden?))
  (click link-to-page-with-links)
  (click link-to-page-with-prices)
  (is (= expected-price (extract-price-from "div"))))

(deftest clicking-on-ribbon-goes-back-to-homepage
  (to home-page)
  (add-cookie {:name "test" :value ""})
  (perform-search)
  (click ribbon-link)
  (is (= home-page (current-url))))
