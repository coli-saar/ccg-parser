
grammar Category;


@header {
    package de.saar.coli.ccgparser.categoryparser;
}


category:
    ATOMIC |
    functional_category;

subcategory:
    ATOMIC |
    OPBR functional_category CLBR;


functional_category:
    subcategory FORWARD subcategory |
    subcategory BACKWARD subcategory;


ATOMIC: ([a-zA-Z.,]|'['|']')+;
OPBR: '(';
CLBR: ')';
FORWARD: '/';
BACKWARD: '\\';


