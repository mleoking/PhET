define([
  'underscore',
  'common/AtomIdentifier',
  'tpl!templates/detailed-element-symbol.html'
], function( _, AtomIdentifier, symbolTemplate ){

  function SymbolView( atom ){
    this.atom = atom;

    if( !this.atom ){
      throw new Error('I need an atom!');
    }

    var self = this;

    this.atom.events.on('configurationChanged', function(){
      self.render();
    });

    this.$el = $('#detailed-element-symbol');
    this.el = this.$el[0];

    this.render();
  }

  SymbolView.prototype.render = function(){

    // TODO: present atom data
    var atomData = {
      symbol: AtomIdentifier.getSymbol( this.atom.getNumProtons() ),
      weight: this.atom.getWeight(),
      number: this.atom.getNumProtons(),
      charge: this.atom.getCharge()
    };


    var template = symbolTemplate( atomData );

    this.$el.html( template );
  };

  return SymbolView;

});
