// Copyright 2002-2012, University of Colorado
define( [
            'model/atom',
            'model/Bucket',
            'model/particle'
        ], function ( Atom, Bucket, Particle ) {

    var NUCLEON_DIAMETER = 15; // In pixels.
    var NUM_PROTONS = 5;
    var PROTON_COLOR = "red";
    var NUM_NEUTRONS = 5;
    var NEUTRON_COLOR = "gray";

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
        var self = this;

        // Add the protons.
        _.times( NUM_PROTONS, function(){
            var proton = new Particle( self.buckets.protonBucket.x, self.buckets.protonBucket.y, PROTON_COLOR, NUCLEON_DIAMETER, "proton" );
            self.nucleons.push( proton );
            proton.events.on( 'userReleased', function () {
                self.atom.addParticle( proton );
            } );
        });

        // Add the neutron.
        _.times( NUM_NEUTRONS, function(){
            var neutron = new Particle( self.buckets.neutronBucket.x, self.buckets.protonBucket.y, NEUTRON_COLOR, NUCLEON_DIAMETER, "neutron" );
            self.nucleons.push( neutron );
            neutron.events.on( 'userReleased', function () {
                self.atom.addParticle( neutron );
            } );
        });
    }

    return BuildAnAtomModel;
} );
