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

(defn agenda-slide [{:keys [agenda]
                     :or   {agenda ["Agenda point 1"
                                    "Agenda point 2"
                                    "Agenda point 3"]}}]
  [{:slide-fn :text-box
    :text "Agenda"
    :width (- 1920 100)
    :x 50 :y 200
    :bold? true
    :font-size 90.0}
   {:slide-fn :text-box
    :text (bulleted-list agenda)
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

(defn title-stacked-charts-table-slide [{:keys [title
                                                chart-1
                                                chart-2
                                                chart-3
                                                ds]
                                         :or   {title "Title"
                                                chart-1 {:mark "bar"}
                                                chart-2 {:mark "bar"}
                                                chart-3 {:mark "bar"}
                                                ds (tc/dataset)}}]
  [{:slide-fn :text-box
    :text title
    :width (- 1920 100)
    :x 50 :y 100
    :bold? true
    :font-size 50.0}
   {:slide-fn :chart-box
    :vega-lite-chart-map chart-1
    :y 300}
   {:slide-fn :chart-box
    :vega-lite-chart-map chart-2
    :y 500}
   {:slide-fn :chart-box
    :vega-lite-chart-map chart-3
    :y 700}
   {:slide-fn :text-box
    :text (tc/dataset-name ds)
    :x 1270
    :y 300}
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
