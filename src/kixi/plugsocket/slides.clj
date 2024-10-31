(ns kixi.plugsocket.slides)

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
