// Copyright 2002-2012, University of Colorado
define( [
            'model/atom',
            'model/Bucket'
        ], function ( Atom, Bucket ) {

    function BuildAnAtomModel() {
        this.atom = new Atom();

        this.buckets = {
          protonBucket: new Bucket( 100, 100, 150, "Protons" ),
          neutronBucket: new Bucket( 300, 100, 150, "Neutrons" ),
          electronBucket: new Bucket( 600, 100, 150, "Electrons" )
        };
    }

    return BuildAnAtomModel;
} );
