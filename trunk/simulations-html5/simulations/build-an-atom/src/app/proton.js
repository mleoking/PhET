define([
  'nucleon'
], function( Nucleon ){

function Proton() {
  Nucleon.call( this, "gray" );
}

Proton.prototype = Nucleon.prototype;

return Proton;

});
