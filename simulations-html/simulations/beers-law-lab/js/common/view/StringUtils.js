// Copyright 2002-2012, University of Colorado

define( [], function() {

    function StringUtils() {
    }

    /*
     * Similar to Java's MessageFormat.
     * Eg, messageFormat( "{0} + {1}", [2,3] ) -> "2 + 3"
     */
    StringUtils.messageFormat = function ( pattern, args ) {
        return pattern.replace( /\{(\d+)\}/g, function () {
            return args[arguments[1]];
        } );
    }

    return StringUtils;
} );
