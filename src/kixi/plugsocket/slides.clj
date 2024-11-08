(ns kixi.plugsocket.slides
  (:require [tablecloth.api :as tc]))

(def mc-logo-url "https://www.mastodonc.com/wp-content/themes/MastodonC-2018/dist/images/logo_mastodonc.png")

(defn bulleted-list [seq-of-text]
  (->> seq-of-text
       (map #(str "- " %))
       (clojure.string/join "\n\n")))

(defn title-slide [{:keys [presentation-title
                           work-package
                           presentation-date
                           client-name]
                    :or   {presentation-title ""
                           work-package ""
                           presentation-date ""
                           client-name ""}}]
  [{:slide-fn :text-box
    :text presentation-title
    :x 50 :y 10
    :width (- 1920 100)
    :bold? true
    :font-size 120.0}
   {:slide-fn :text-box
    :text work-package
    :x 50 :y 330
    :bold? true
    :font-size 50.0}
   {:slide-fn :text-box
    :text presentation-date
    :italic? true
    :x 50 :y 440
    :font-size 50.0}
   {:slide-fn :text-box
    :text (format "For %s" client-name)
    :width (- 1920 100)
    :x 50 :y 530
    :bold? true
    :font-size 80.0}
   {:slide-fn :text-box
    :text "Presented by Mastodon C"
    :width (- 1920 100)
    :x 50 :y 650
    :bold? true
    :italic? true
    :font-size 50.0}
   {:slide-fn :image-box
    :image mc-logo-url
    :x (- 1920 350)
    :y 900
    :height (partial * 1.5)
    :width (partial * 1.5)}])

(defn list-slide [{:keys [title
                          text]
                   :or   {title "Title"
                          text ["Point 1"
                                "Point 2"
                                "Point 3"]}}]
  [{:slide-fn :text-box
    :text title
    :width (- 1920 100)
    :x 50 :y 200
    :bold? true
    :font-size 90.0}
   {:slide-fn :text-box
    :text (bulleted-list text)
    :width (- 1920 100)
    :x 50 :y 400
    :font-size 50.0}
   {:slide-fn :image-box
    :image mc-logo-url
    :x (- 1920 350)
    :y 900
    :height (partial * 1.5)
    :width (partial * 1.5)}])

(defn section-header-slide [{:keys [section-title]
                             :or   {section-title "Section Title"}}]
  [{:slide-fn :text-box
    :text section-title
    :bold? true
    :font-size 80.0
    :x 50 :y 400
    :width (- 1920 100)}
   {:slide-fn :image-box
    :image mc-logo-url
    :x (- 1920 350)
    :y 900
    :height (partial * 1.5)
    :width (partial * 1.5)}])

(defn title-chart-slide [{:keys [title
                                 chart]
                          :or   {title "Title"
                                 chart {:mark "bar"}}}]
  [{:slide-fn :text-box
    :text title
    :width (- 1920 100)
    :x 50 :y 100
    :bold? true
    :font-size 80.0}
   {:slide-fn :chart-box
    :vega-lite-chart-map chart
    :y 400}
   {:slide-fn :image-box
    :image mc-logo-url
    :x (- 1920 350)
    :y 900
    :height (partial * 1.5)
    :width (partial * 1.5)}])

(defn title-chart-text-slide [{:keys [title
                                      chart
                                      text]
                               :or   {title "Title"
                                      chart {:mark "bar"}
                                      text ["Point 1"
                                            "Point 2"
                                            "Point 3"]}}]
  [{:slide-fn :text-box
    :text title
    :width (- 1920 100)
    :x 50 :y 100
    :bold? true
    :font-size 80.0}
   {:slide-fn :chart-box
    :vega-lite-chart-map chart
    :y 400}
   {:slide-fn :text-box
    :text (bulleted-list text)
    :width 700
    :x 1200 :y 400
    :font-size 30.0}
   {:slide-fn :image-box
    :image mc-logo-url
    :x (- 1920 350)
    :y 900
    :height (partial * 1.5)
    :width (partial * 1.5)}])

(defn title-chart-table-slide [{:keys [title
                                       chart
                                       ds]
                                :or   {title "Title"
                                       chart {:mark "bar"}
                                       ds (tc/dataset)}}]
  [{:slide-fn :text-box
    :text title
    :width (- 1920 100)
    :x 50 :y 100
    :bold? true
    :font-size 70.0}
   {:slide-fn :chart-box
    :vega-lite-chart-map chart
    :y 400}
   {:slide-fn :table-box
    :ds ds
    :x 1300
    :y 370}
   {:slide-fn :image-box
    :image mc-logo-url
    :x (- 1920 350)
    :y 900
    :height (partial * 1.5)
    :width (partial * 1.5)}])
