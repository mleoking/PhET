// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'tpl!templates/net-charge.html'
        ], function ( _, netChargeTemplate ) {

    function NetChargeView( atom ) {

        // Parameter checking.
        if ( !atom ) {
            throw new Error( 'No atom supplied.' );
        }
        this.atom = atom;

        // Grab the div out of the DOM.
        this.$el = $( '#net-charge-container' );
        this.el = this.$el[0];

        // Set up to redraw when atom configuration changes.
        var self = this;
        this.atom.events.on( 'configurationChanged', function () {
            self.render();
        } );

        this.render();
    }

    NetChargeView.prototype.render = function () {

        var atomCharge = this.atom.getCharge();
        var chargeIndicator = this.atom.getCharge() > 0 ? "+" : "";
        var color;

        if ( atomCharge > 0 ){
            color = 'red';
        } else if( atomCharge < 0 ) {
            color = 'blue';
        } else {
            color = 'black';
        }

        var numberOfPairs = Math.min( this.atom.getNumProtons(), this.atom.getNumElectrons() );

        var template = netChargeTemplate({
            charge: chargeIndicator + atomCharge,
            pairs: numberOfPairs
        });

        this.$el.html( template );

        this.$el.find('.net-charge').css('color', color);
    };


    NetChargeView.prototype.renderChargePairingGraph = function(){

    };

    return NetChargeView;
} );
