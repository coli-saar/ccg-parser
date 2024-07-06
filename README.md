# A* CCG parser

This is a reimplementation of the A* CCG parser by [Lewis et al. (2014)](https://aclanthology.org/D14-1107/).

The parser reads a file with supertag scores as input (see e.g. [supertags.json](https://github.com/coli-saar/ccg-parser/blob/main/supertags.json) for an example). It outputs two files: "parses.txt" contains CCG derivation trees, and "statistics.tsv" contains runtime statistics for the sentences that were parsed.

The parser currently uses only application and harmonic compositions of degree one. Adding crossed compositions increased the parsing time at no real improvement in parsing coverage. It also uses the type-changing rules specified e.g. in [unary-rules.txt](https://github.com/coli-saar/ccg-parser/blob/main/unary_rules.txt).

You can generate supertag scores using the companion project [ccg-supertagger](https://github.com/coli-saar/ccg-supertagger).

## Running the parser

You can call the parser as follows:

```
java -cp <ccg-parser.jar> de.saar.coli.ccgparser.App <supertags.json> [<output-directory>]
```

Replace `<ccg-parser.jar>` with the Jar file that you downloaded, and replace `<supertags.json>` with the file that contains the supertag scores.

You can optionally pass an argument `<output-directory>` to specify a directory in which the parser will save images of the derivation trees for the different sentences. The filenames of the image files correspond to the rows in `statistics.tsv`. Note that if a sentence could not be parsed, there will be no image file for it.