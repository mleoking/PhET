define([
  'underscore'
], function( _ ){

  function Atom(){
    this.protons = 0;
    this.neutrons = 0;
    this.electrons = 0;
  }

  Atom.prototype.toJSON = function(){
    // hard coded properties for now
    return {
      symbol: "He",
      weight: 1,
      number: 1,
      charge: 0
    };
  };

  return Atom;

});
