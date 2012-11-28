define([
  'underscore',
  'tpl!templates/mass-number.html'
], function( _, massNumberTemplate ){

  function MassNumberlView( atom ){
    this.atom = atom;

    if( !this.atom ){
      throw new Error('I need an atom!');
    }

    this.$el = $('#mass-number-container');
    this.el = this.$el[0];

    var self = this;
    this.atom.events.on('configurationChanged', function(){
      self.render();
    });

    this.render();
  }

  MassNumberlView.prototype.render = function(){

    var template = massNumberTemplate({
      mass: this.atom.getWeight()
    });

    this.$el.html( template );
  };

  return MassNumberlView;

});
