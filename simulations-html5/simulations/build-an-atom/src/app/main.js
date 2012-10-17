require([

], function(){

  // Constants.
  var numProtons = 10;
  var numNeutrons = 10;
  var nucleonRadius = 20;
  var maxNucleonsInBucket = 10;

  // Global variables.
  var canvas;
  var touchInProgress = false;
  var context;
  var nucleonsInNucleus = [];
  var neutronBucket;
  var protonBucket;
  var nucleonBeingDragged = null;
  var resetButton;
  var electronShell;
  var nucleusLabel;
  var viewportWidth = 0;
  var viewportHeight = 0;
  var maxDetectedViewportWidth = 0.0;
  var maxDetectedViewportHeight = 0.0;

  var updateViewport = function () {
    if ( window.innerWidth != viewportWidth || window.innerHeight != viewportHeight ) {
        viewportWidth = window.innerWidth;
        canvas.width = viewportWidth;
        viewportHeight = window.innerHeight ? window.innerHeight : $( window ).height();
        canvas.height = viewportHeight;
        window.scrollTo( 0, 0 );
        if ( viewportWidth > maxDetectedViewportWidth ) {
            maxDetectedViewportWidth = viewportWidth;
        }
        if ( viewportHeight > maxDetectedViewportHeight ) {
            maxDetectedViewportHeight = viewportHeight;
        }
        draw();
    }
  };


  // Hook up the initialization function.
  $(document).ready(function () {
    init();
  });

  // Hook up event handler for window resize.
  $( window ).resize( resizer );

  // Handler for window resize events.
  function resizer() {
      updateViewport();
  }

  // Initialize the canvas and context.
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

      // Add the nucleus label.  This gets updated as the nucleus configuration
      // changes.
      nucleusLabel = new NucleusLabel( new Point2D( 450, 80 ) );

      // Add initial particles to buckets by initiating a reset.
      reset();

      // Commenting out, since iPad seems to send these continuously.
      window.addEventListener( 'deviceorientation', onWindowDeviceOrientation, false );

      // Disable elastic scrolling.  This is specific to iOS.
      document.addEventListener(
              'touchmove',
              function ( e ) {
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
      // Draw the background to be big enough in pretty much every case.
      context.fillRect( 0, 0, Math.max( 2000, maxDetectedViewportWidth ), Math.max( 2000, maxDetectedViewportHeight ) );
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
      context.fillText( 'PhET', viewportWidth - 70, viewportHeight - 30 );
  }

  function reset() {
      // Remove all existing particles.
      removeAllParticlesFromNucleus();
      protonBucket.removeAllParticles();
      neutronBucket.removeAllParticles();

      // Add the protons and neutrons.  They are initially in the buckets.
      for ( var i = 0; i < numProtons; i++ ) {
          protonBucket.addNucleonToBucket( new Proton() );
      }
      for ( var i = 0; i < numNeutrons; i++ ) {
          neutronBucket.addNucleonToBucket( new Neutron() );
      }
  }


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
    if ( nucleonBeingDragged !== null ) {
        nucleonBeingDragged.draw( context );
    }

    // Draw the fronts of the buckets.
    neutronBucket.drawFront( context );
    protonBucket.drawFront( context );

    // Draw the nucleus label.
    nucleusLabel.draw( context );
  }


});
