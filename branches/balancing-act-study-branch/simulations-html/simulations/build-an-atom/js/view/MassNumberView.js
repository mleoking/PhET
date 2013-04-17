define([
  'underscore',
  'tpl!templates/mass-number.html'
], function( _, massNumberTemplate ){

  function MassNumberView( atom ){
    this.atom = atom;

    if( !this.atom ){
      throw new Error('No atom supplied.');
    }

    this.$el = $('#mass-number-container');
    this.el = this.$el[0];

    var self = this;
    this.atom.events.on('configurationChanged', function(){
      self.render();
    });

    this.render();
  }

  MassNumberView.prototype.render = function(){

    var template = massNumberTemplate({
      mass: this.atom.getWeight()
    });

    this.$el.html( template );
  };

  return MassNumberView;

});
