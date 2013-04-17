define([],function(){


  //-----------------------------------------------------------------------------
  // Nucleus label class.  This is the label that is placed near the nucleus and
  // shows what kind of element has been created and whether it is stable.
  //-----------------------------------------------------------------------------

  function NucleusLabel( initialLocation, nucleons ) {
      this.location = initialLocation;
      this.text = "";
      this.nucleonsInNucleus = nucleons;
  }

  NucleusLabel.prototype.draw = function ( context ) {
      var protonCount = 0;
      var neutronCount = 0;
      for ( var i = 0; i < this.nucleonsInNucleus.length; i++ ) {
          if ( this.nucleonsInNucleus[i].color == "red" ) {
              protonCount++;
          }
          else if ( this.nucleonsInNucleus[i].color == "gray" ) {
              neutronCount++;
          }
      }
      this.updateText( protonCount, neutronCount );
      context.fillStyle = '#000';
      context.font = '28px sans-serif';
      context.textBaseline = 'top';
      context.textAlign = 'left';
      context.fillText( this.text, this.location.x, this.location.y );
  };

  NucleusLabel.prototype.updateText = function ( protonCount, neutronCount ) {
      switch( protonCount ) {
          case 0:
              this.text = "";
              break;

          case 1:
              this.text = "Hydrogen";
              if ( neutronCount == 0 || neutronCount == 1 ) {
                  this.text += " (Stable)";
              }
              else {
                  this.text += " (Unstable)";
              }
              break;

          case 2:
              this.text = "Helium";
              if ( neutronCount == 1 || neutronCount == 2 ) {
                  this.text += " (Stable)";
              }
              else {
                  this.text += " (Unstable)";
              }
              break;

          case 3:
              this.text = "Lithium";
              if ( neutronCount == 3 || neutronCount == 4 ) {
                  this.text += " (Stable)";
              }
              else {
                  this.text += " (Unstable)";
              }
              break;

          case 4:
              this.text = "Beryllium";
              if ( neutronCount == 5 ) {
                  this.text += " (Stable)";
              }
              else {
                  this.text += " (Unstable)";
              }
              break;

          case 5:
              this.text = "Boron";
              if ( neutronCount == 5 || neutronCount == 6 ) {
                  this.text += " (Stable)";
              }
              else {
                  this.text += " (Unstable)";
              }
              break;

          case 6:
              this.text = "Carbon";
              if ( neutronCount == 6 || neutronCount == 7 ) {
                  this.text += " (Stable)";
              }
              else {
                  this.text += " (Unstable)";
              }
              break;

          case 7:
              this.text = "Nitrogen";
              if ( neutronCount == 7 || neutronCount == 8 ) {
                  this.text += " (Stable)";
              }
              else {
                  this.text += " (Unstable)";
              }
              break;

          case 8:
              this.text = "Oxygen";
              if ( neutronCount == 8 || neutronCount == 9 || neutronCount == 10 ) {
                  this.text += " (Stable)";
              }
              else {
                  this.text += " (Unstable)";
              }
              break;

          case 9:
              this.text = "Fluorine";
              if ( neutronCount == 10 ) {
                  this.text += " (Stable)";
              }
              else {
                  this.text += " (Unstable)";
              }
              break;

          case 10:
              this.text = "Neon";
              if ( neutronCount == 10 || neutronCount == 11 || neutronCount == 12 ) {
                  this.text += " (Stable)";
              }
              else {
                  this.text += " (Unstable)";
              }
              break;

          default:
              this.text = "Phetium - " + (protonCount + neutronCount);
              break;
      }
  };

  NucleusLabel.prototype.setLocationComponents = function ( x, y ) {
      this.location.x = x;
      this.location.y = y;
  };

  NucleusLabel.prototype.setLocation = function ( location ) {
      this.setLocationComponents( location.x, location.y );
  };


  return NucleusLabel;

});
