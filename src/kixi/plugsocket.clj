(ns kixi.plugsocket
  (:require [clojure.java.io :as io])
  (:import [java.io OutputStream FileInputStream FileOutputStream BufferedInputStream File ByteArrayInputStream]
           org.apache.poi.xslf.usermodel.XMLSlideShow
           org.apache.poi.sl.usermodel.PictureData$PictureType
           org.apache.commons.io.IOUtils
           javax.imageio.ImageIO
           java.awt.Dimension
           java.awt.Rectangle
           java.net.URL))

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
    (cond
      (every? boolean [x y width height])
      (.setAnchor box (Rectangle. x y width height))
      (every? boolean [x y height])
      (.setAnchor box (Rectangle. x y 500 height))
      (every? boolean [x y width])
      (.setAnchor box (Rectangle. x y width 50))
      (every? boolean [x y])
      (.setAnchor box (Rectangle. x y 500 50)))
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
    (.setAnchor out (Rectangle. x y width height))))

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

(comment

  ;; create-slide usage
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
       :font-size 50.0}]])

  (create-slide (vector (first slides)) (XMLSlideShow.))

  )

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
