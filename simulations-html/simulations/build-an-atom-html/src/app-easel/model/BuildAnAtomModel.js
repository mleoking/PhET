// Copyright 2002-2012, University of Colorado
define( [
            'model/atom',
            'model/Bucket'
        ], function ( Atom, Bucket ) {

    function BuildAnAtomModel() {
        this.atom = new Atom();
        this.protonBucket = new Bucket( 100, 100, 150, "Test" );
    }

    return BuildAnAtomModel;
} );
