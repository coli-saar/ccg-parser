# A* CCG parser

This is a reimplementation of the A* CCG parser by [Lewis et al. (2014)](https://aclanthology.org/D14-1107/).

The parser reads a file with supertag scores as input (see e.g. [supertags.json](https://github.com/coli-saar/ccg-parser/blob/main/supertags.json) for an example). It outputs two files: "parses.txt" contains CCG derivation trees, and "statistics.tsv" contains runtime statistics for the sentences that were parsed.

The parser currently uses only application and harmonic compositions of degree one. Adding crossed compositions increased the parsing time at no real improvement in parsing coverage. It also uses the type-changing rules specified e.g. in [unary-rules.txt](https://github.com/coli-saar/ccg-parser/blob/main/unary_rules.txt).

You can generate supertag scores using the companion project [ccg-supertagger](https://github.com/coli-saar/ccg-supertagger).

