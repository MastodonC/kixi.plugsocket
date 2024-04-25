(ns kixi.plugsocket
  (:require [clojure.java.io :as io]
            [kixi.transcode :as trans]
            [tablecloth.api :as tc])
  (:import [java.io OutputStream FileInputStream FileOutputStream BufferedInputStream File ByteArrayInputStream]
           org.apache.poi.xslf.usermodel.XMLSlideShow
           org.apache.poi.xslf.usermodel.XSLFPictureShape
           org.apache.poi.xslf.usermodel.XSLFPictureData
           org.apache.poi.sl.usermodel.PictureData$PictureType
           org.apache.poi.sl.usermodel.TableCell$BorderEdge
           org.apache.poi.sl.usermodel.TextParagraph$TextAlign
           org.apache.commons.io.IOUtils
           javax.imageio.ImageIO
           java.awt.Dimension
           java.awt.Rectangle
           java.net.URL))

(defn box-placement [box x y width height]
  (cond
    (every? boolean [x y width height])
    (.setAnchor box (Rectangle. x y width height))
    (every? boolean [x y height])
    (.setAnchor box (Rectangle. x y 500 height))
    (every? boolean [x y width])
    (.setAnchor box (Rectangle. x y width 50))
    (every? boolean [x y])
    (.setAnchor box (Rectangle. x y 500 50))))

(defn text-box [{:keys [slide text
                        x y
                        bold? italic?
                        font-size
                        width height]
                 :or {x 50 y 50
                      width false
                      height false
                      bold? false
                      italic? false
                      font-size 30.0}}]
  (let [box (.createTextBox slide)
        paragraph (.addNewTextRun (.addNewTextParagraph box))]
    (box-placement box x y width height)
    (when
        bold?
      (.setBold paragraph true))
    (when
        italic?
      (.setItalic paragraph true))
    (when
        (double? font-size)
      (.setFontSize paragraph font-size))
    (.setText paragraph text)))

(defn image-params [bytearray]
  (with-open [stream (ByteArrayInputStream. bytearray)]
    (bean (ImageIO/read stream))))

(defn picture-box [{:keys [slide powerpoint
                           image height
                           width x y]
                    :or {height false
                         width false
                         x 50
                         y 50}}]
  (let [bytearray (with-open [stream (io/input-stream image)]
                    (IOUtils/toByteArray stream))
        in (.addPicture powerpoint bytearray PictureData$PictureType/PNG)
        params (image-params bytearray)
        out (.createPicture slide in)
        height (cond
                 (number? height)
                 height
                 (fn? height)
                 (height (:height params))
                 :else
                 (:height params))
        width (cond
                (number? width)
                width
                (fn? width)
                (width (:width params))
                :else
                (:width params))]
    (box-placement out x y width height)))

(defn chart-box [{:keys [vega-lite-chart-map slide
                         powerpoint height
                         width x y]
                  :or {height false
                       width false
                       x 50
                       y 50}}]
  (let [png-byte-array (trans/vl-map->bytearray vega-lite-chart-map)
        png (trans/svg-document->png png-byte-array)
        format-chart (.addPicture powerpoint png PictureData$PictureType/PNG)
        chart (.createPicture slide format-chart)
        params (-> chart
                   .getPictureData
                   .getImageDimensionInPixels
                   bean)
        height (cond
                 (number? height)
                 height
                 (fn? height)
                 (height (:height params))
                 :else
                 (:height params))
        width (cond
                (number? width)
                width
                (fn? width)
                (width (:width params))
                :else
                (:width params))]
    (box-placement chart x y width height)))

(defn table-border [{:keys [cell colour color border-size]
                     :or {colour java.awt.Color/BLACK
                          color java.awt.Color/BLACK
                          border-size 2.0}}]
  (.setBorderWidth cell TableCell$BorderEdge/bottom border-size)
  (.setBorderColor cell TableCell$BorderEdge/bottom colour)
  (.setBorderWidth cell TableCell$BorderEdge/top border-size)
  (.setBorderColor cell TableCell$BorderEdge/top colour)
  (.setBorderWidth cell TableCell$BorderEdge/left border-size)
  (.setBorderColor cell TableCell$BorderEdge/left colour)
  (.setBorderWidth cell TableCell$BorderEdge/right border-size)
  (.setBorderColor cell TableCell$BorderEdge/right colour))

(defn table-box [{:keys [ds slide x y colWidth
                         border-colour
                         border-color
                         border-size
                         font-size]
                  :or {x false
                       y false
                       colWidth 200.0
                       border-colour java.awt.Color/BLACK
                       border-color java.awt.Color/BLACK
                       border-size 2.0
                       font-size 20.0}}]
  (let [table (.createTable slide)
        numColumns (tc/column-count ds)
        numRows (tc/row-count ds)
        headerRow (.addRow table)
        ds-header (-> ds tc/column-names vec)]
    (box-placement table x y false false)
    (run! (fn [h]
            (let [th (.addCell headerRow)
                  p (.addNewTextParagraph th)
                  r (.addNewTextRun p)]
              (.setBold r true)
              (.setTextAlign p TextParagraph$TextAlign/CENTER)
              (.setText r (name (nth ds-header h)))
              (.setFontSize r (+ font-size 10.0))
              (.setColumnWidth table h (double colWidth))
              (table-border {:cell th :colour border-color
                             :border-size border-size})))
          (range 0 numColumns))
    (run! (fn [rowNum]
            (let [tr (.addRow table)]
              (run! (fn [h]
                      (let [cell (.addCell tr)
                            p (.addNewTextParagraph cell)
                            r (.addNewTextRun p)
                            col-name (nth ds-header h)]
                        (.setText r (str (nth (ds col-name) rowNum)))
                        (.setFontSize r font-size)
                        (table-border {:cell cell :colour border-color
                                       :border-size border-size})))
                    (range 0 numColumns))))
          (range 0 numRows))))

(defn create-slide
  ;; takes a sequence of maps corresponding to a number of objects
  ;; (text boxes, tables, images) to display on a slide
  ([powerpoint]
   (.createSlide powerpoint))
  ([seq-of-maps powerpoint]
   (let [slide (.createSlide powerpoint)]
     (run!
      #((:slide-fn %) (assoc %
                             :slide slide
                             :powerpoint powerpoint))
      seq-of-maps))))

(defn create-powerpoint [{:keys [width height
                                 slides]
                          :or {width 1920
                               height 1080}}]
  (let [powerpoint (XMLSlideShow.)]
    (.setPageSize powerpoint (Dimension. width height))
    (run!
     #(create-slide % powerpoint)
     slides)
    powerpoint))

(defn save-powerpoint-into-stream!
  "Save the workbook into a stream.
  The caller is required to close the stream after saving is completed."
  [^OutputStream stream ^XMLSlideShow powerpoint]
  (.write powerpoint stream))

(defn save-powerpoint-into-file!
  "Save the workbook into a file."
  [^String filename ^XMLSlideShow powerpoint]
  (with-open [file-out (FileOutputStream. filename)]
    (.write powerpoint file-out)))

(defmulti save-powerpoint!
  "Save the workbook into a stream or a file.
          In the case of saving into a stream, the caller is required
          to close the stream after saving is completed."
  (fn [x _] (class x)))

(defmethod save-powerpoint! OutputStream
  [stream powerpoint]
  (save-powerpoint-into-stream! stream powerpoint))

(defmethod save-powerpoint! String
  [filename powerpoint]
  (save-powerpoint-into-file! filename powerpoint))

(comment

  ;; Usage guidance
  ;; a "slide" is a sequence of maps making up a number of objects
  ;; (text boxes, tables, images) to display on a slide

  (def slides
    [[{:slide-fn text-box
       :text "foo bar"
       :x 50 :y 10
       :width (- 1920 100)
       :bold? true
       :font-size 120.0}
      {:slide-fn picture-box
       :image "https://www.mastodonc.com/wp-content/themes/MastodonC-2018/dist/images/logo_mastodonc.png"
       :height (partial * 4)}]
     []
     [{:slide-fn text-box
       :text "Hello World!"
       :x 50 :y 10
       :width (- 1920 100)
       :bold? true
       :font-size 120.0}
      {:slide-fn picture-box
       :image "./designation-decision-tree.png"
       :x 500
       :y 500}]
     [{:slide-fn text-box
       :text "First page"
       :x 50 :y 330
       :bold? true
       :font-size 50.0}]
     [{:slide-fn text-box
       :text "last page"
       :width 1920
       :bold? true
       :font-size 50.0}]
     [{:slide-fn chart-box
       :vega-lite-chart-map {:data {:values [{:a "A" :b 28}
                                             {:a "B" :b 55}
                                             {:a "C" :b 43}
                                             {:a "D" :b 91}
                                             {:a "E" :b 81}
                                             {:a "F" :b 53}
                                             {:a "G" :b 19}
                                             {:a "H" :b 87}
                                             {:a "I" :b 52}]}
                             :encoding {:x {:axis {:labelAngle 0}
                                            :field "a" :type "nominal"}
                                        :y {:field "b" :type "quantitative"}}
                             :mark "bar"}}
      {:slide-fn chart-box
       :vega-lite-chart-map {:data {:values [{:a "A" :b 28}
                                             {:a "B" :b 55}
                                             {:a "C" :b 43}
                                             {:a "D" :b 91}
                                             {:a "E" :b 81}
                                             {:a "F" :b 53}
                                             {:a "G" :b 19}
                                             {:a "H" :b 87}
                                             {:a "I" :b 52}]}
                             :encoding {:x {:axis {:labelAngle 0}
                                            :field "a" :type "nominal"}
                                        :y {:field "b" :type "quantitative"}}
                             :mark "bar"}
       :width (partial * 4)
       :height (partial * 4)
       :x 300}]
     [{:slide-fn table-box
       :ds (tc/dataset [[:A [1 2 3]] [:B ["A" "B" "C"]]])}]
     [{:slide-fn table-box
       :ds (tc/dataset [[:A [1 2 3]] [:B ["A" "B" "C"]]])
       :x 200
       :y 200}]])

  (create-slide (first slides) (XMLSlideShow.))

  (save-powerpoint! "./test.pptx" (create-powerpoint {:slides slides}))

  )
