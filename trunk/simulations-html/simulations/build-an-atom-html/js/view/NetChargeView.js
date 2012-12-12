// Copyright 2002-2012, University of Colorado
define([
  'underscore',
  'tpl!templates/net-charge.html'
], function( _, netChargeTemplate ){

  function NetChargeView( atom ){
    this.atom = atom;

    if( !this.atom ){
      throw new Error('No atom supplied.');
    }

    this.$el = $('#net-charge-container');
    this.el = this.$el[0];

    var self = this;
    this.atom.events.on('configurationChanged', function(){
        console.log( "self.atom.getCharge()" + self.atom.getCharge() );
      self.render();
    });

    this.render();
  }

  NetChargeView.prototype.render = function(){

    var template = netChargeTemplate({
      charge: this.atom.getCharge()
    });

    this.$el.html( template );
  };

  return NetChargeView;
});
