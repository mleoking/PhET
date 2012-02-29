// Copyright 2002-2011, University of Colorado

/**
 * Main JavaScript file for the HTML5 prototype of the Build an Atom
 * simulation.
 */

// Constants.
var numProtons = 10;
var numNeutrons = 10;
var nucleonRadius = 20;
var maxNucleonsInBucket = 10;

// Global variables.
var canvas;
var touchInProgress = false;
var context;
var nucleonsInNucleus = new Array();
var neutronBucket;
var protonBucket;
var nucleonBeingDragged = null;
var resetButton;
var electronShell;
var nucleusLabel;

// Hook up the initialization function.
$( document ).ready( function() {
    init();
} );

// Hook up event handler for window resize.
$( window ).resize( resizer );

// Handler for window resize events.
function resizer() {
    console.log( "resize received" );
    canvas.width = $( window ).width();
    canvas.height = $( window ).height();
    draw();
}

// Initialize the canvas, context,
function init() {

    // Initialize references to the HTML5 canvas and its context.
    canvas = $( '#canvas' )[0];
    if ( canvas.getContext ) {
        context = canvas.getContext( '2d' );
    }

    // Set up event handlers.
    // TODO: Work with JO to "jquery-ize".
    document.onmousedown = onDocumentMouseDown;
    document.onmouseup = onDocumentMouseUp;
    document.onmousemove = onDocumentMouseMove;

    document.addEventListener( 'touchstart', onDocumentTouchStart, false );
    document.addEventListener( 'touchmove', onDocumentTouchMove, false );
    document.addEventListener( 'touchend', onDocumentTouchEnd, false );

    // Add the electron shell.
    electronShell = new ElectronShell( new Point2D( 325, 150 ) );

    // Add the buckets where nucleons are created and returned.
    neutronBucket = new Bucket( new Point2D( 100, 300 ), "gray", "Neutrons" );
    protonBucket = new Bucket( new Point2D( 400, 300 ), "red", "Protons" );

    // Add the protons and neutrons.  They are initially in the buckets.
    for ( i = 0; i < numProtons; i++ ) {
        protonBucket.addNucleonToBucket( new Proton() );
    }
    for ( i = 0; i < numNeutrons; i++ ) {
        neutronBucket.addNucleonToBucket( new Neutron() );
    }

    // Add the reset button.
    resetButton = new ResetButton( new Point2D( 600, 325 ), "orange" );

    // Add the nucleus label.  This gets updated as the nucleus configuration
    // changes.
    nucleusLabel = new NucleusLabel( new Point2D( 450, 80 ) );

    // Commenting out, since iPad seems to send these continuously.
//	window.addEventListener( 'deviceorientation', onWindowDeviceOrientation, false );

    // Disable elastic scrolling.  This is specific to iOS.
    document.addEventListener(
            'touchmove',
            function( e ) {
                e.preventDefault();
            },
            false
    );

    // Do the initial drawing, events will cause subsequent updates.
    resizer();
}

function clearBackground() {
    context.save();
    context.globalCompositeOperation = "source-over";
    context.fillStyle = "rgb(255, 255, 153)";
    context.fillRect( 0, 0, canvas.width, canvas.height );
    context.restore();
}

function drawTitle() {
    context.fillStyle = '#00f';
    context.font = '30px sans-serif';
    context.textBaseline = 'top';
    context.textAlign = 'left';
    context.fillText( 'Build an Atom', 10, 10 );
}

function drawPhetLogo() {
    context.fillStyle = '#f80';
    context.font = 'italic 20px sans-serif';
    context.textBaseline = 'top';
    context.fillText( 'PhET', canvas.width - 70, canvas.height / 2 );
}

//-----------------------------------------------------------------------------
// Point2D class.
//-----------------------------------------------------------------------------

function Point2D( x, y ) {
    // Instance Fields or Data Members
    this.x = x;
    this.y = y;
}

Point2D.prototype.toString = function() {
    return this.x + ", " + this.y;
}

Point2D.prototype.setComponents = function( x, y ) {
    this.x = x;
    this.y = y;
}

Point2D.prototype.set = function( point2D ) {
    this.setComponents( point2D.x, point2D.y );
}

Point2D.prototype.equals = function ( point2D ) {
    return (point2D.x == this.x) && (point2D.y == this.y);
}

//-----------------------------------------------------------------------------
// Nucleon class.
//-----------------------------------------------------------------------------

function Nucleon( color ) {
    this.location = new Point2D( 0, 0 );
    this.radius = nucleonRadius;
    this.color = color;
}

Nucleon.prototype.draw = function( context ) {
    var xPos = this.location.x;
    var yPos = this.location.y;
    var gradient = context.createRadialGradient( xPos - this.radius / 3, yPos - this.radius / 3, 0, xPos, yPos, this.radius );
    gradient.addColorStop( 0, "white" );
    gradient.addColorStop( 1, this.color );
    context.fillStyle = gradient;
    context.beginPath();
    context.arc( xPos, yPos, this.radius, 0, Math.PI * 2, true );
    context.closePath();
    context.fill();
}

Nucleon.prototype.setLocation = function( location ) {
    this.setLocationComponents( location.x, location.y );
}

Nucleon.prototype.setLocationComponents = function( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

Nucleon.prototype.containsPoint = function( point ) {
    return Math.sqrt( Math.pow( point.x - this.location.x, 2 ) + Math.pow( point.y - this.location.y, 2 ) ) < this.radius;
}

//-----------------------------------------------------------------------------
// Proton and Neutron classes.
//-----------------------------------------------------------------------------

Proton.prototype = new Nucleon();

function Proton() {
    Nucleon.call( this, "red" );
}

Neutron.prototype = new Nucleon();

function Neutron() {
    Nucleon.call( this, "gray" );
}

//-----------------------------------------------------------------------------
// Electron shell class.
//-----------------------------------------------------------------------------

function ElectronShell( initialLocation ) {
    this.location = initialLocation;
    this.radius = 120;
}

ElectronShell.prototype.draw = function( context ) {
    var xPos = this.location.x;
    var yPos = this.location.y;
    var gradient = context.createRadialGradient( xPos, yPos, 0, xPos, yPos, this.radius );
    gradient.addColorStop( 0, "rgba( 0, 0, 200, 0.2)" );
    gradient.addColorStop( 1, "rgba( 0, 0, 200, 0.05)" );
    context.fillStyle = gradient;
    context.beginPath();
    context.arc( xPos, yPos, this.radius, 0, Math.PI * 2, true );
    context.closePath();
    context.fill();
}

ElectronShell.prototype.setLocationComponents = function( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

ElectronShell.prototype.setLocation = function( location ) {
    this.setLocationComponents( location.x, location.y );
}

ElectronShell.prototype.containsPoint = function( point ) {
    return Math.sqrt( Math.pow( point.x - this.location.x, 2 ) + Math.pow( point.y - this.location.y, 2 ) ) < this.radius;
}

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

    this.nucleonsInBucket = new Array();
}

Bucket.prototype.drawFront = function( context ) {
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
}

Bucket.prototype.drawInterior = function( context ) {
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
}

Bucket.prototype.addNucleonToBucket = function ( nucleon ) {
    nucleon.setLocation( this.getNextOpenNucleonLocation() );
    this.nucleonsInBucket.push( nucleon );
}

Bucket.prototype.removeNucleonFromBucket = function ( nucleon ) {
    var index = this.nucleonsInBucket.indexOf( nucleon );
    if ( index != -1 ) {
        this.nucleonsInBucket.splice( index, 1 );
    }
}

// Algorithm that maps an index to a location in the bucket.  This is limited
// to a certain number of nucleons, so be careful if reusing.
Bucket.prototype.getNucleonLocationByIndex = function ( index ) {
    var location;
    var nucleonRadius = new Nucleon( "black" ).radius;
    // Assumes 1.5r margin on both sides of the bucket.
    var numInCenterRow = Math.round( ( this.width - 2 * nucleonRadius ) / ( nucleonRadius * 2 ) );
    if ( index < numInCenterRow - 1 ) {
        // In back row, which is populated first.
        console.log( "back row " );
        location = new Point2D( this.location.x + nucleonRadius * 2.5 + index * nucleonRadius * 2, this.location.y - nucleonRadius * 0.5 );
    }
    else if ( index < 2 * numInCenterRow - 1 ) {
        // In center row.
        console.log( "center row " );
        location = new Point2D( this.location.x + nucleonRadius * 1.5 + (index - numInCenterRow + 1) * nucleonRadius * 2, this.location.y );
    }
    else if ( index < 3 * numInCenterRow - 2 ) {
        // In back row.
        console.log( "front row " );
        location = new Point2D( this.location.x + nucleonRadius * 2.5 + (index - numInCenterRow * 2 + 1) * nucleonRadius * 2, this.location.y + nucleonRadius * 0.5 );
    }
    else {
        console.log( "bucket capacity exceeded, using center" );
        location = new Point2D( this.location.x + this.width / 2, this.location.y );
    }
    return location;
}

Bucket.prototype.getNextOpenNucleonLocation = function () {
    for ( var i = 0; i < maxNucleonsInBucket; i++ ) {
        var openLocation = this.getNucleonLocationByIndex( i );
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
}


Bucket.prototype.setLocationComponents = function( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

Bucket.prototype.setLocation = function( location ) {
    this.setLocationComponents( location.x, location.y );
}

Bucket.prototype.containsPoint = function( point ) {
    // Treat the shape as rectangle, even though it may not exactly be one.
    return point.x > this.location.x && point.x < this.location.x + this.width &&
           point.y > this.location.y && point.y < this.location.y + this.height;
}

//-----------------------------------------------------------------------------
// Nucleus label class.  This is the label that is placed near the nucleus and
// shows what kind of element has been created and whether it is stable.
//-----------------------------------------------------------------------------

function NucleusLabel( initialLocation ) {
    this.location = initialLocation;
    this.text = "";
}

NucleusLabel.prototype.draw = function( context ) {
    var protonCount = 0;
    var neutronCount = 0;
    for ( i = 0; i < nucleonsInNucleus.length; i++ ) {
        if ( nucleonsInNucleus[i].color == "red" ) {
            protonCount++;
        }
        else if ( nucleonsInNucleus[i].color == "gray" ) {
            neutronCount++;
        }
    }
    this.updateText( protonCount, neutronCount );
    context.fillStyle = '#000';
    context.font = '28px sans-serif';
    context.textBaseline = 'top';
    context.textAlign = 'left';
    context.fillText( this.text, this.location.x, this.location.y );
}

NucleusLabel.prototype.updateText = function( protonCount, neutronCount ) {
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

        default:
            this.text = "Phetium - " + (protonCount + neutronCount);
            break;
    }
}

NucleusLabel.prototype.setLocationComponents = function( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

NucleusLabel.prototype.setLocation = function( location ) {
    this.setLocationComponents( location.x, location.y );
}

//-----------------------------------------------------------------------------
// Reset button class
//-----------------------------------------------------------------------------

function ResetButton( initialLocation, color ) {
    this.location = initialLocation;
    this.width = 90;
    this.height = 40;
    this.color = color;
}

ResetButton.prototype.draw = function( context ) {
    var xPos = this.location.x;
    var yPos = this.location.y;
    var gradient = context.createLinearGradient( xPos, yPos, xPos, yPos + this.height );
    gradient.addColorStop( 0, "white" );
    gradient.addColorStop( 1, this.color );
    // Draw box that defines button outline.
    context.fillStyle = gradient;
    context.fillRect( xPos, yPos, this.width, this.height );
    // Put text on the box.
    context.fillStyle = '#000';
    context.font = '28px sans-serif';
    context.textBaseline = 'top';
    context.textAlign = 'left';
    context.fillText( 'Reset', xPos + 5, yPos + 5 );
}

ResetButton.prototype.setLocationComponents = function( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

ResetButton.prototype.setLocation = function( location ) {
    this.setLocationComponents( location.x, location.y );
}

ResetButton.prototype.containsPoint = function( point ) {
    return point.x > this.location.x && point.x < this.location.x + this.width &&
           point.y > this.location.y && point.y < this.location.y + this.height;
}

//-----------------------------------------------------------------------------


// Main drawing function.  This is where the z-ordering, i.e. the layering
// effect, is created.
function draw() {

    clearBackground();

    // Draw the text.
    drawTitle();
    drawPhetLogo();

    // Draw the electron shell.
    electronShell.draw( context );

    // Draw the reset button.
    resetButton.draw( context );

    // Draw the bucket interiors.  These need to be behind the particles in
    // the z-order.
    neutronBucket.drawInterior( context );
    protonBucket.drawInterior( context );

    // Draw the nucleons.  Some may be in the nucleus, some in buckets.
    for ( var i = 0; i < nucleonsInNucleus.length; i++ ) {
        nucleonsInNucleus[i].draw( context );
    }
    var copyOfNucleons = protonBucket.nucleonsInBucket.slice();
    // Reverse array so that layering on canvas makes first particles in bucket be in front.
    copyOfNucleons.reverse();
    for ( var i = 0; i < copyOfNucleons.length; i++ ) {
        copyOfNucleons[i].draw( context );
    }
    copyOfNucleons = neutronBucket.nucleonsInBucket.slice();
    // Reverse array so that layering on canvas makes first particles in bucket be in front.
    copyOfNucleons.reverse();
    for ( var i = 0; i < copyOfNucleons.length; i++ ) {
        copyOfNucleons[i].draw( context );
    }

    // Draw particle that is being dragged if there is one.
    if ( nucleonBeingDragged != null ) {
        nucleonBeingDragged.draw( context );
    }

    // Draw the fronts of the buckets.
    neutronBucket.drawFront( context );
    protonBucket.drawFront( context );

    // Draw the nucleus label.
    nucleusLabel.draw( context );
}

//-----------------------------------------------------------------------------
// Utility functions
//-----------------------------------------------------------------------------

function removeParticleFromNucleus( particle ) {
    for ( i = 0; i < nucleonsInNucleus.length; i++ ) {
        if ( nucleonsInNucleus[i] == particle ) {
            nucleonsInNucleus.splice( i, 1 );
            break;
        }
    }
    adjustNucleusConfiguration();
}

function removeAllParticlesFromNucleus() {
    nucleonsInNucleus.length = 0;
}

// Adjust the positions of the nucleons in the nucleus to look good.
function adjustNucleusConfiguration() {
    var particleRadius = new Nucleon( "black" ).radius;
    if ( nucleonsInNucleus.length == 0 ) {
        return;
    }
    else if ( nucleonsInNucleus.length == 1 ) {
        nucleonsInNucleus[0].setLocation( electronShell.location );
    }
    else if ( nucleonsInNucleus.length == 2 ) {
        nucleonsInNucleus[0].setLocationComponents( electronShell.location.x - particleRadius, electronShell.location.y );
        nucleonsInNucleus[1].setLocationComponents( electronShell.location.x + particleRadius, electronShell.location.y );
    }
    else if ( nucleonsInNucleus.length == 3 ) {
        nucleonsInNucleus[0].setLocationComponents( electronShell.location.x, electronShell.location.y - particleRadius * 1.1 );
        nucleonsInNucleus[1].setLocationComponents( electronShell.location.x + particleRadius * 0.77, electronShell.location.y + particleRadius * 0.77 );
        nucleonsInNucleus[2].setLocationComponents( electronShell.location.x - particleRadius * 0.77, electronShell.location.y + particleRadius * 0.77 );
    }
    else if ( nucleonsInNucleus.length == 4 ) {
        nucleonsInNucleus[0].setLocationComponents( electronShell.location.x, electronShell.location.y - particleRadius * 1.5 );
        nucleonsInNucleus[1].setLocationComponents( electronShell.location.x + particleRadius, electronShell.location.y );
        nucleonsInNucleus[2].setLocationComponents( electronShell.location.x - particleRadius, electronShell.location.y );
        nucleonsInNucleus[3].setLocationComponents( electronShell.location.x, electronShell.location.y + particleRadius * 1.5 );
    }
    else if ( nucleonsInNucleus.length >= 5 ) {
        // Place the last five as a diamond with one in center.
        nucleonsInNucleus[nucleonsInNucleus.length - 1].setLocationComponents( electronShell.location.x, electronShell.location.y );
        nucleonsInNucleus[nucleonsInNucleus.length - 2].setLocationComponents( electronShell.location.x, electronShell.location.y - particleRadius * 1.5 );
        nucleonsInNucleus[nucleonsInNucleus.length - 3].setLocationComponents( electronShell.location.x + particleRadius, electronShell.location.y );
        nucleonsInNucleus[nucleonsInNucleus.length - 4].setLocationComponents( electronShell.location.x - particleRadius, electronShell.location.y );
        nucleonsInNucleus[nucleonsInNucleus.length - 5].setLocationComponents( electronShell.location.x, electronShell.location.y + particleRadius * 1.5 );
        // Place remaining particles around the edges of this configuration.
        var placementRadius = particleRadius * 2;
        for ( i = nucleonsInNucleus.length - 6; i >= 0; i-- ) {
            var angle = Math.random() * Math.PI * 2;
            nucleonsInNucleus[i].setLocationComponents( electronShell.location.x + placementRadius * Math.cos( angle ),
                                                        electronShell.location.y + placementRadius * Math.sin( angle ) );
        }
    }
}

//-----------------------------------------------------------------------------
// Event handlers.
//-----------------------------------------------------------------------------

function onDocumentMouseDown( event ) {
    onTouchStart( new Point2D( event.clientX, event.clientY ) );
}

function onDocumentMouseUp( event ) {
    onTouchEnd();
}

function onDocumentMouseMove( event ) {
    onDrag( new Point2D( event.clientX, event.clientY ) );
}

function onDocumentTouchStart( event ) {
    if ( event.touches.length == 1 ) {
        event.preventDefault();
        onTouchStart( new Point2D( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY ) );
    }
}

function onDocumentTouchMove( event ) {
    if ( event.touches.length == 1 ) {
        event.preventDefault();
        onDrag( new Point2D( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY ) );
    }
}

function onDocumentTouchEnd( event ) {
    onTouchEnd();
}

function onWindowDeviceOrientation( event ) {
    console.log( "onWindowDeviceOrientation" );
}

function onTouchStart( location ) {
    touchInProgress = true;
    nucleonBeingDragged = null;

    // See if this event occurred over any of the nucleons in the nucleus.
    for ( var i = 0; i < nucleonsInNucleus.length; i++ ) {
        if ( nucleonsInNucleus[i].containsPoint( location ) ) {
            nucleonBeingDragged = nucleonsInNucleus[i];
            removeParticleFromNucleus( nucleonBeingDragged );
            break;
        }
    }
    if ( nucleonBeingDragged == null ) {
        // See if touch occurred over a nucleon in the proton bucket.
        for ( var i = 0; i < protonBucket.nucleonsInBucket.length; i++ ) {
            if ( protonBucket.nucleonsInBucket[i].containsPoint( location ) ) {
                nucleonBeingDragged = protonBucket.nucleonsInBucket[i];
                protonBucket.removeNucleonFromBucket( nucleonBeingDragged );
                break;
            }
        }
    }
    if ( nucleonBeingDragged == null ) {
        // See if touch occurred over a nucleon in the neutron bucket.
        for ( var i = 0; i < neutronBucket.nucleonsInBucket.length; i++ ) {
            if ( neutronBucket.nucleonsInBucket[i].containsPoint( location ) ) {
                nucleonBeingDragged = neutronBucket.nucleonsInBucket[i];
                neutronBucket.removeNucleonFromBucket( nucleonBeingDragged );
                break;
            }
        }
    }

    // Position the nucleon (if there is one) at the location of this event.
    if ( nucleonBeingDragged != null ) {
        nucleonBeingDragged.setLocation( location );
    }
    else {
        // Check if the reset button was pressed.
        if ( resetButton.containsPoint( location ) ) {
            // Perform a reset by moving any particles that are in the nucleus
            // into their bucket.
            for ( var i = 0; i < nucleonsInNucleus.length; i++ ) {
                if ( nucleonsInNucleus[i] instanceof Proton ) {
                    protonBucket.addNucleonToBucket( nucleonsInNucleus[i] );
                }
                else {
                    neutronBucket.addNucleonToBucket( nucleonsInNucleus[i] );
                }
            }
            removeAllParticlesFromNucleus();
        }
    }

    draw();
}

function onDrag( location ) {
    if ( touchInProgress && nucleonBeingDragged != null ) {
        nucleonBeingDragged.setLocation( location );
        draw();
    }
}

function onTouchEnd() {
    touchInProgress = false;
    if ( nucleonBeingDragged != null ) {
        // If the nucleon has been dropped within the electron shell, add it
        // to the nucleus.
        if ( electronShell.containsPoint( nucleonBeingDragged.location ) ) {
            nucleonsInNucleus.push( nucleonBeingDragged );
            adjustNucleusConfiguration();
        }
        else {
            // Return the particle to the appropriate bucket.
            if ( nucleonBeingDragged instanceof Proton ) {
                protonBucket.addNucleonToBucket( nucleonBeingDragged );
            }
            else {
                neutronBucket.addNucleonToBucket( nucleonBeingDragged );
            }
        }
        // Always set to null to indicate that no nucleon is being dragged.
        nucleonBeingDragged = null;
    }
    draw();
}