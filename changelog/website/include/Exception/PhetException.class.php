<?php

//
// ** THIS FEATURE IS EXPRIMENTAL **
//
// Exceptions are not used much in the PhET code, but they are
// useful from time to time.  The general behavior of the PhET
// code (and PHP's general philosophy geneally supports this
// practice) is to be as permissive as possible.  Exceptions
// if untrapped stop rendering a page, and then visitors get
// to see broken pages.
//
// When used properly exceptions point out that things are
// operating out of spec and need to be fixed.  Unfortunately
// one needs to test by rendering web pages to make sure they
// are working properly.
//
// Enter unit tests.  Lightly used and experimental at this point,
// they provide a way for testing much of the functionality,
// ensure things are working properly, reduce bugs, etc.
//
// The idea is that the only time exceptions are used in the
// PhET code is when they either don't change the existing
// behavior (such as a die statement), or they have been
// thoroughly unit tested.
//
// Current method is to sandwich names between Phet and Exception,
// so a database exception is PhetDBException().  Kinda clunky, I'm
// being safe, prehaps overly so, and not polluting the global
// namespace with possible double exceptions.  Sure is ugly, maybe
// I'll change it?

class PhetException extends Exception {}

?>