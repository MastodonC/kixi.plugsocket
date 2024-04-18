(ns kixi.plugsocket
  (:import [java.io OutputStream FileOutputStream]
           org.apache.poi.xslf.usermodel.XMLSlideShow
           java.awt.Dimension
           java.awt.Rectangle))

(defn text-box [{:keys [slide text
                        x y
                        bold? italic?
                        font-size
                        width height]
                 :or {x false y false
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

                          :or {width 1920
                               height 1080}}]
  (let [powerpoint (XMLSlideShow.)]
    (.setPageSize powerpoint (Dimension. width height))
    powerpoint))

(defmacro assert-type [value expected-type]
  `(when-not (isa? (class ~value) ~expected-type)
     (throw (IllegalArgumentException.
             (format "%s is invalid. Expected %s. Actual type %s, value: %s"
                     (str '~value) ~expected-type (class ~value) ~value)))))

(defn save-powerpoint-into-stream!
  "Save the workbook into a stream.
  The caller is required to close the stream after saving is completed."
  [^OutputStream stream ^XMLSlideShow powerpoint]
  (assert-type powerpoint XMLSlideShow)
  (.write powerpoint stream))

(defn save-powerpoint-into-file!
  "Save the workbook into a file."
  [^String filename ^XMLSlideShow powerpoint]
  (assert-type powerpoint XMLSlideShow)
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
