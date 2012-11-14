define([
  'point2d',
  'nucleon'
],function( Point2D, Nucleon ){

  // TODO: put this in a global config object that's passed to the
  // constr
  var nucleonRadius = 20;
  var maxNucleonsInBucket = 10;

  //-----------------------------------------------------------------------------
  // Bucket class.
  //-----------------------------------------------------------------------------

  function Bucket( initialLocation, color, labelText ) {
      this.location = initialLocation;
      this.color = color;
      this.labelText = labelText;

      // Size is fixed, at least for now.
      this.width = nucleonRadius * 9; // 4 nucleons and 1.5 radii at edge of bucket = (r * 2 * 3) + (r * 1.5 * 2).
      this.height = this.width * 0.35;

      this.nucleonsInBucket = [];
  }

  Bucket.prototype.drawFront = function ( context ) {
      var xPos = this.location.x;
      var yPos = this.location.y;

      // Create the gradient used to fill the bucket.
      var gradient = context.createLinearGradient( xPos, yPos, xPos + this.width, yPos );
      gradient.addColorStop( 0, "white" );
      gradient.addColorStop( 1, this.color );

      // Draw the bucket.
      context.beginPath();
      context.moveTo( xPos, yPos );
      context.lineTo( xPos + this.width * 0.15, yPos + this.height ); // Left edge.
      context.bezierCurveTo( xPos + this.width * 0.4, yPos + this.height * 1.1, xPos + this.width * 0.6, yPos + this.height * 1.1, xPos + this.width * 0.85, yPos + this.height );
      context.lineTo( xPos + this.width, yPos ); // Right edge.
      context.bezierCurveTo( xPos + this.width * 0.9, yPos + this.height * 0.2, xPos + this.width * 0.1, yPos + this.height * 0.2, xPos, yPos ); // Top.
      context.closePath();
      context.fillStyle = gradient;
      context.fill();

      // Add the label.
      context.fillStyle = '#000';
      context.font = '22px sans-serif';
      context.textAlign = 'center';
      context.textBaseline = 'middle';
      context.fillText( this.labelText, this.location.x + this.width / 2, this.location.y + this.height / 2 );
  };

  Bucket.prototype.drawInterior = function ( context ) {
      var xPos = this.location.x;
      var yPos = this.location.y;

      // Create the gradient used to portray the interior of the bucket.
      var gradient = context.createLinearGradient( xPos, yPos, xPos + this.width, yPos );
      gradient.addColorStop( 0, this.color );
      gradient.addColorStop( 1, "gray" );

      // Draw the interior of the bucket.
      context.beginPath();
      context.moveTo( xPos, yPos );
      context.bezierCurveTo( xPos + this.width * 0.1, yPos - this.height * 0.2, xPos + this.width * 0.9, yPos - this.height * 0.2, xPos + this.width, yPos );
      context.bezierCurveTo( xPos + this.width * 0.9, yPos + this.height * 0.2, xPos + this.width * 0.1, yPos + this.height * 0.2, xPos, yPos );
      context.fillStyle = gradient;
      context.fill();
  };

  Bucket.prototype.addNucleonToBucket = function ( nucleon ) {
      nucleon.setLocation( this.getNextOpenNucleonLocation() );
      this.nucleonsInBucket.push( nucleon );
  };

  Bucket.prototype.removeNucleonFromBucket = function ( nucleon ) {
      var index = this.nucleonsInBucket.indexOf( nucleon );
      if ( index != -1 ) {
          this.nucleonsInBucket.splice( index, 1 );
      }
  };

  Bucket.prototype.removeAllParticles = function () {
      this.nucleonsInBucket.length = 0;
  };

  // Algorithm that maps an index to a location in the bucket.  This is limited
  // to a certain number of nucleons, so be careful if reusing.
  Bucket.prototype.getNucleonLocationByIndex = function ( index ) {
      var location;
      var nucleonRadius = new Nucleon( "black" ).radius;
      // Assumes 1.5r margin on both sides of the bucket.
      var numInCenterRow = Math.round( ( this.width - 2 * nucleonRadius ) / ( nucleonRadius * 2 ) );
      if ( index < numInCenterRow - 1 ) {
          // In front row, which is populated first.
          location = new Point2D( this.location.x + nucleonRadius * 2.5 + index * nucleonRadius * 2, this.location.y + nucleonRadius * 0.5 );
      }
      else if ( index < 2 * numInCenterRow - 1 ) {
          // In center row.
          location = new Point2D( this.location.x + nucleonRadius * 1.5 + (index - numInCenterRow + 1) * nucleonRadius * 2, this.location.y );
      }
      else if ( index < 3 * numInCenterRow - 2 ) {
          // In back row.
          location = new Point2D( this.location.x + nucleonRadius * 2.5 + (index - numInCenterRow * 2 + 1) * nucleonRadius * 2, this.location.y - nucleonRadius * 0.5 );
      }
      else {
          console.log( "bucket capacity exceeded, using center" );
          location = new Point2D( this.location.x + this.width / 2, this.location.y );
      }
      return location;
  };

  Bucket.prototype.getNextOpenNucleonLocation = function () {
    var openLocation;
    for( var i = 0; i < maxNucleonsInBucket; i++ ) {
      openLocation = this.getNucleonLocationByIndex( i );
      var locationTaken = false;
      for ( var j = 0; j < this.nucleonsInBucket.length; j++ ) {
          if ( this.nucleonsInBucket[j].location.equals( openLocation ) ) {
              locationTaken = true;
              break;
          }
      }
      if ( !locationTaken ) {
          // This location is open, so we're done.
          break;
      }
    }

    return openLocation;
  };


  Bucket.prototype.setLocationComponents = function ( x, y ) {
      this.location.x = x;
      this.location.y = y;
  };

  Bucket.prototype.setLocation = function ( location ) {
      this.setLocationComponents( location.x, location.y );
  };

  Bucket.prototype.containsPoint = function ( point ) {
    // Treat the shape as rectangle, even though it may not exactly be one.
    return point.x > this.location.x && point.x < this.location.x + this.width &&
           point.y > this.location.y && point.y < this.location.y + this.height;
  };

  return Bucket;
});
