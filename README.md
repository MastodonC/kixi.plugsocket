# kixi.plugsocket
## Convert Clerk notebooks to PPTX slideshows

---_alpha release_ ---

Plugsocket was created with the idea of being able to create a static version of a Clerk notebook that can be used and edited by non-programmers.

Later versions are planned that will aim to preserve positioning from a Clerk notebook and enable more automated conversion.

## Usage

A "presentation" is a sequence of vectors each representing a slide.

Each slide is a vector made up of zero or more maps, each map representing an object on the slide.

Currently there are four types of objects that can be placed on a slide.

```[ "a presentation"
    ]
