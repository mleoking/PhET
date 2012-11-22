// Copyright 2002-2012, University of Colorado
define( [
            'model/atom',
            'model/Bucket'
        ], function ( Atom, Bucket ) {

    function BuildAnAtomModel() {
        this.atom = new Atom();

        this.buckets = {
          protonBucket: new Bucket( 105, 600, 150, "Protons" ),
          neutronBucket: new Bucket( 305, 600, 150, "Neutrons" ),
          electronBucket: new Bucket( 505, 600, 150, "Electrons" )
        };
    }

    return BuildAnAtomModel;
} );
