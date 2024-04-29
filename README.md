# kixi.plugsocket
## Convert Clerk notebooks to PPTX slideshows

--- _alpha release_ ---

Plugsocket was created due to the need to create a static version of a [Clerk](https://github.com/nextjournal/clerk) notebook that can be used and edited by non-programmers.

## Concept

A "presentation" is a sequence of vectors each representing a slide.

Each slide is then a vector made up of zero or more maps, each map representing an object on the slide.

Currently there are four types of objects that can be placed on a slide:
* Text box
* Vega-Lite chart
* Image
* Table

### Example presentation with two slides

```
(def presentation
 ["a presentation"
    ["slide 1"
        {"object 1"
         :slide-fn :text-box
         :text "foo bar"
         :x 50 :y 10
         :width (- 1920 100)
         :bold? true
         :font-size 120.0}
        {"object 2"
         :slide-fn :picture-box
         :image "https://www.mastodonc.com/wp-content/themes/MastodonC-2018/dist/images/logo_mastodonc.png"
         :height (partial * 4)}]
    ["slide 2"
        "an empty slide"]
])
```

## Usage

Firstly a sequence of objects and slides should be built (see example above).

All slide functions accept a map and require, as a minimum, what will be displayed on the slide.
* For a text box a string
* For a Vega-Lite chart a standard vega view description map (see [example](./vega-lite-example.edn))
* For an image a filepath or a URL
* For a table a [tablecloth](https://github.com/scicloj/tablecloth) dataset

All slide functions can also take `:x` and `:y` parameters for placement of associated box on a slide and most also take `:height` and `:width`. All other parameters have defaults but can be adjusted with additional keys (see [individual "box" functions for options](./src/kixi/plugsocket.clj)).

Your slides can be then passed to `create-powerpoint` which will make a PowerPoint presentation, that can then be further edited or saved to pptx using `save-powerpoint!`.
