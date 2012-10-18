define([
  'nucleon'
], function( Nucleon ){

  function Neutron() {
    Nucleon.call( this, "gray" );
  }

  Neutron.prototype = Nucleon.prototype;

  return Neutron;

});
