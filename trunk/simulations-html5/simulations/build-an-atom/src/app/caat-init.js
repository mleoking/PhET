define( [ ], function () {

    function CaatInit() {

        console.log( "Hello from CAAT woman." );
        CAAT.modules.initialization = CAAT.modules.initialization || {};
        CAAT.modules.initialization.init = function ( width, height, runHere, imagesURL, onEndLoading ) {

            var canvascontainer = document.getElementById( runHere );
            var director;

            if ( CAAT.__CSS__ ) {   // css renderer
                if ( canvascontainer ) {
                    if ( false === canvascontainer instanceof HTMLDivElement ) {
                        canvascontainer = null;
                    }
                }

                if ( canvascontainer === null ) {
                    canvascontainer = document.createElement( 'div' ); // create a new DIV
                    document.body.appendChild( canvascontainer );
                }

                director = new CAAT.Director().
                        initialize(
                        width || 800,
                        height || 600,
                        canvascontainer );

            }
            else {

                if ( canvascontainer ) {
                    if ( canvascontainer instanceof HTMLDivElement ) {
                        var ncanvascontainer = document.createElement( "canvas" );
                        canvascontainer.appendChild( ncanvascontainer );
                        canvascontainer = ncanvascontainer;
                    }
                    else if ( false == canvascontainer instanceof HTMLCanvasElement ) {
                        var ncanvascontainer = document.createElement( "canvas" );
                        document.body.appendChild( ncanvascontainer );
                        canvascontainer = ncanvascontainer;
                    }
                }
                else {
                    canvascontainer = document.createElement( 'canvas' );
                    document.body.appendChild( canvascontainer );
                }

                //Show the CAAT Debug panel at the bottom of the screen
                CAAT.DEBUG = 1;

                director = new CAAT.Director().
                        initialize(
                        width || 800,
                        height || 600,
                        canvascontainer );
//            director.setClear( CAAT.Director.CLEAR_DIRTY_RECTS );
            }

            /**
             * Load splash images. It is supossed the splash has some images.
             */
            new CAAT.ImagePreloader().loadImages(
                    imagesURL,
                    function on_load( counter, images ) {

                        if ( counter == images.length ) {

                            director.emptyScenes();
                            director.setImagesCache( images );

                            onEndLoading( director );

                            /**
                             * Change this sentence's parameters to play with different entering-scene
                             * curtains.
                             * just perform a director.setScene(0) to play first director's scene.
                             */
//                    director.easeIn(
//                            0,
//                            CAAT.Scene.prototype.EASE_ROTATION,
//                            1000,
//                            false,
//                            CAAT.Actor.prototype.ANCHOR_CENTER,
//                            new CAAT.Interpolator().createElasticOutInterpolator( 2.5, .4 ) );
                            director.setScene( 0 );

                            CAAT.loop( 60 );
                        }
                    }

            );
        };
    }
    return CaatInit;
} );