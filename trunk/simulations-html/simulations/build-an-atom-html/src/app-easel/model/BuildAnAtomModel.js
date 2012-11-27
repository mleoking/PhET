// Copyright 2002-2012, University of Colorado
define( [
            'model/atom',
            'model/Bucket',
            'model/particle'
        ], function ( Atom, Bucket, Particle ) {

    /**
     * Constructor for main model object.
     *
     * @constructor
     */
    function BuildAnAtomModel() {

        this.atom = new Atom( 0, 0 );

        this.buckets = {
            protonBucket:new Bucket( -200, 300, 150, "Protons" ),
            neutronBucket:new Bucket( 0, 300, 150, "Neutrons" ),
            electronBucket:new Bucket( 200, 300, 150, "Electrons" )
        };

        this.nucleons = [];
        var NUCLEON_DIAMETER = 15; // In pixels.
        var proton = new Particle( this.buckets.protonBucket.x, this.buckets.protonBucket.y, "red", NUCLEON_DIAMETER, "proton" );
        this.nucleons.push( proton );
        var self = this;
        proton.events.on( 'userReleased', function () {
            self.atom.addParticle( proton );
        } );
    }

    return BuildAnAtomModel;
} );
