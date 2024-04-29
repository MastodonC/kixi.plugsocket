# kixi.plugsocket
## Convert Clerk notebooks to PPTX slideshows

--- _alpha release_ ---

Plugsocket was created with the idea of being able to create a static version of a [Clerk](https://github.com/nextjournal/clerk) notebook that can be used and edited by non-programmers.

Later versions are planned that will aim to preserve positioning from a Clerk notebook and enable more automated conversion.

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

Once a sequence of objects and slides has been built this can be passed to `create-powerpoint` which will make a PowerPoint presentation, that can then be further edited or saved to pptx using `save-powerpoint!`.

## Usage
