// Copyright 2002-2011, University of Colorado

var canvas;
var touchInProgress = false;
var context;
var particlesInNucleus = new Array();
var neutronBucket;
var protonBucket;
var particleBeingDragged = null;
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

    // Add the reset button.
    resetButton = new ResetButton( new Point2D( 600, 325 ), "orange" );

    // Add the nucleus label.
    nucleusLabel = new NucleusLabel( new Point2D( 500, 100 ) );

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

//-----------------------------------------------------------------------------
// Particle class.
//-----------------------------------------------------------------------------

function Nucleon( color ) {
    this.location = new Point2D( 0, 0 );
    this.radius = 20;
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
    this.width = 150;
    this.height = this.width * 0.5;
}

Bucket.prototype.draw = function( context ) {
    var xPos = this.location.x;
    var yPos = this.location.y;

    // Create the gradient used to fill the bucket.
    var gradient = context.createLinearGradient( xPos, yPos, xPos + this.width, yPos );
    gradient.addColorStop( 0, "white" );
    gradient.addColorStop( 1, this.color );

    // Draw the bucket.
    context.beginPath();
    context.moveTo( xPos, yPos );
    context.lineTo( xPos + this.width * 0.15, yPos + this.height );
    context.lineTo( xPos + this.width * 0.85, yPos + this.height );
    context.lineTo( xPos + this.width, yPos );
    context.lineTo( xPos, yPos );
    context.closePath();
    context.fillStyle = gradient;
    context.fill();

    // Add the label.
    context.fillStyle = '#000';
    context.font = '28px sans-serif';
    context.textBaseline = 'top';
    context.fillText( this.labelText, this.location.x, this.location.y );
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
// Nucleus label class.
//-----------------------------------------------------------------------------

function NucleusLabel( initialLocation ) {
    this.location = initialLocation;
    this.text = "";
}

NucleusLabel.prototype.draw = function( context ) {
    var protonCount = 0;
    var neutronCount = 0;
    for ( i = 0; i < particlesInNucleus.length; i++ ) {
        if ( particlesInNucleus[i].color == "red" ) {
            protonCount++;
        }
        else if ( particlesInNucleus[i].color == "gray" ) {
            neutronCount++;
        }
    }
    this.updateText( protonCount, neutronCount );
    context.fillStyle = '#000';
    context.font = '28px sans-serif';
    context.textBaseline = 'top';
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


// Main drawing function.
function draw() {

    clearBackground();

    // Draw the text.
    drawTitle();
    drawPhetLogo();

    // Draw the electron shell.
    electronShell.draw( context );

    // Draw the reset button.
    resetButton.draw( context );

    // Draw the buckets.
    neutronBucket.draw( context );
    protonBucket.draw( context );

    // Draw the particles that are in the nucleus.
    for ( var i = 0; i < particlesInNucleus.length; i++ ) {
        particlesInNucleus[i].draw( context );
    }

    // Draw particle that is being dragged if there is one.
    if ( particleBeingDragged != null ) {
        particleBeingDragged.draw( context );
    }

    // Draw the nucleus label.
    nucleusLabel.draw( context );
}

//-----------------------------------------------------------------------------
// Utility functions
//-----------------------------------------------------------------------------

function removeAllParticles() {
    particlesInNucleus.length = 0;
}

function removeParticleFromNucleus( particle ) {
    for ( i = 0; i < particlesInNucleus.length; i++ ) {
        if ( particlesInNucleus[i] == particle ) {
            particlesInNucleus.splice( i, 1 );
            break;
        }
    }
    adjustNucleonPositions();
}

// Adjust the positions of the nucleons to look good.
function adjustNucleonPositions() {
    var particleRadius = new Nucleon( "black" ).radius;
    if ( particlesInNucleus.length == 0 ) {
        return;
    }
    else if ( particlesInNucleus.length == 1 ) {
        particlesInNucleus[0].setLocation( electronShell.location );
    }
    else if ( particlesInNucleus.length == 2 ) {
        particlesInNucleus[0].setLocationComponents( electronShell.location.x - particleRadius, electronShell.location.y );
        particlesInNucleus[1].setLocationComponents( electronShell.location.x + particleRadius, electronShell.location.y );
    }
    else if ( particlesInNucleus.length == 3 ) {
        particlesInNucleus[0].setLocationComponents( electronShell.location.x, electronShell.location.y - particleRadius * 1.1 );
        particlesInNucleus[1].setLocationComponents( electronShell.location.x + particleRadius * 0.77, electronShell.location.y + particleRadius * 0.77 );
        particlesInNucleus[2].setLocationComponents( electronShell.location.x - particleRadius * 0.77, electronShell.location.y + particleRadius * 0.77 );
    }
    else if ( particlesInNucleus.length == 4 ) {
        particlesInNucleus[0].setLocationComponents( electronShell.location.x, electronShell.location.y - particleRadius * 1.5 );
        particlesInNucleus[1].setLocationComponents( electronShell.location.x + particleRadius, electronShell.location.y );
        particlesInNucleus[2].setLocationComponents( electronShell.location.x - particleRadius, electronShell.location.y );
        particlesInNucleus[3].setLocationComponents( electronShell.location.x, electronShell.location.y + particleRadius * 1.5 );
    }
    else if ( particlesInNucleus.length >= 5 ) {
        // Place the last five as a diamond with one in center.
        particlesInNucleus[particlesInNucleus.length - 1].setLocationComponents( electronShell.location.x, electronShell.location.y );
        particlesInNucleus[particlesInNucleus.length - 2].setLocationComponents( electronShell.location.x, electronShell.location.y - particleRadius * 1.5 );
        particlesInNucleus[particlesInNucleus.length - 3].setLocationComponents( electronShell.location.x + particleRadius, electronShell.location.y );
        particlesInNucleus[particlesInNucleus.length - 4].setLocationComponents( electronShell.location.x - particleRadius, electronShell.location.y );
        particlesInNucleus[particlesInNucleus.length - 5].setLocationComponents( electronShell.location.x, electronShell.location.y + particleRadius * 1.5 );
        // Place remaining particles around the edges of this configuration.
        var placementRadius = particleRadius * 2;
        for ( i = particlesInNucleus.length - 6; i >= 0; i-- ) {
            var angle = Math.random() * Math.PI * 2;
            particlesInNucleus[i].setLocationComponents( electronShell.location.x + placementRadius * Math.cos( angle ),
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
    onTouchEnd( new Point2D( event.clientX, event.clientY ) );
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
    particleBeingDragged = null;
    // See if this touch start is on any of the existing particles.
    for ( i = 0; i < particlesInNucleus.length; i++ ) {
        if ( particlesInNucleus[i].containsPoint( location ) ) {
            particleBeingDragged = particlesInNucleus[i];
            removeParticleFromNucleus( particleBeingDragged );
            break;
        }
    }
    if ( particleBeingDragged == null ) {
        // See if the touch was on any of the buckets and, if so, create the
        // corresponding particle.
        if ( neutronBucket.containsPoint( location ) ) {
            particleBeingDragged = new Nucleon( "gray" );
        }
        else if ( protonBucket.containsPoint( location ) ) {
            particleBeingDragged = new Nucleon( "red" );
        }
        // Else see if the button is being touched.
        else if ( resetButton.containsPoint( location ) ) {
            removeAllParticles();
        }
    }

    // Position the particle (if there is one) at the location of this event.
    if ( particleBeingDragged != null ) {
        particleBeingDragged.setLocation( location );
    }

    draw();
}

function onDrag( location ) {
    if ( touchInProgress && particleBeingDragged != null ) {
        particleBeingDragged.setLocation( location );
        draw();
    }
}

function onTouchEnd() {
    touchInProgress = false;
    if ( particleBeingDragged != null ) {
        // If the particle has been dropped within the electron shell, add it
        // to the nucleus.
        if ( electronShell.containsPoint( particleBeingDragged.location ) ) {
            particlesInNucleus.push( particleBeingDragged );
            adjustNucleonPositions();
        }
        // Always set to null to indicate that no particle is being dragged.
        particleBeingDragged = null;
    }
    draw();
}