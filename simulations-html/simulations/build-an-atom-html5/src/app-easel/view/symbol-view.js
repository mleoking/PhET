define([
  'underscore',
  'tpl!templates/detailed-element-symbol.html'
], function( _, symbolTemplate ){

  function SymbolView( atom ){
    this.atom = atom;

    if( !this.atom ){
      throw new Error('I need an atom!');
    }

    this.$el = $('#detailed-element-symbol');
    this.el = this.$el[0];

    this.render();
  }

  SymbolView.prototype.render = function(){

    // TODO: present atom data
    var atomData = this.atom.toJSON();
    var template = symbolTemplate( atomData );

    this.$el.html( template );
  };

  return SymbolView;

});
