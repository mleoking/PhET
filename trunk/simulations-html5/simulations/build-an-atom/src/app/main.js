require( [
             'electron_shell',
             'bucket',
             'point2d',
             'reset_button',
             'nucleus_label',
             'nucleon',
             'proton',
             'neutron',
             'caat-init'
         ], function ( ElectronShell, Bucket, Point2D, ResetButton, NucleusLabel, Nucleon, Proton, Neutron, CaatInit ) {

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

    function listenForRefresh() {
        if ( "WebSocket" in window ) {
            // Let us open a web socket
            var ws = new WebSocket( "ws://localhost:8887/echo" );
            ws.onmessage = function ( evt ) { document.location.reload( true ); };
            ws.onclose = function () { };
            console.log( "opened websocket" );
        }
        else {
            // The browser doesn't support WebSocket
            alert( "WebSocket NOT supported by your Browser!" );
        }
    }

    var updateViewport = function () {
        if ( window.innerWidth !== viewportWidth || window.innerHeight !== viewportHeight ) {
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
    $( document ).ready( function () {
        init();
    } );

    // Hook up event handler for window resize.
    $( window ).resize( resizer );

    // Handler for window resize events.
    function resizer() {
        updateViewport();
    }

    // Initialize the canvas and context.
    function init() {

        // Listen for changes to source files and reload when changes occur.
        listenForRefresh();

        // Initialize references to the HTML5 canvas and its context.
        canvas = $( '#canvas' )[0];
        if ( canvas.getContext ) {
            context = canvas.getContext( '2d' );
        }

        // Initialize the scene graph.
        console.log("About to init CAAT");
        CaatInit();

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
        resetButton = new ResetButton( new Point2D( 600, 325 ), "orange", reset );

        // Add the nucleus label.  This gets updated as the nucleus configuration
        // changes.
        nucleusLabel = new NucleusLabel( new Point2D( 450, 80 ), nucleonsInNucleus );

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
        for ( i = 0; i < copyOfNucleons.length; i++ ) {
            copyOfNucleons[i].draw( context );
        }
        copyOfNucleons = neutronBucket.nucleonsInBucket.slice();

        // Reverse array so that layering on canvas makes first particles in bucket be in front.
        copyOfNucleons.reverse();
        for ( i = 0; i < copyOfNucleons.length; i++ ) {
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
        //    console.log( "onWindowDeviceOrientation, alpha = " + event.alpha + ", beta = " + event.beta + ", gamma = " + event.gamma );
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
                resetButton.press();
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

        // If reset button was pressed, then release it.
        if ( resetButton.pressed ) {
            resetButton.unPress();
        }

        draw();
    }


} );
