define([
  'nucleon'
], function( Nucleon ){

  function Proton() {
    Nucleon.call( this, "red" );
  }

  Proton.prototype = Nucleon.prototype;

  return Proton;

});
