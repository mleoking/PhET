define( [
            'underscore',
            'easel',
            'common/ModelViewTransform2D',
            'view/ParticleView',
            'view/AtomView',
            'view/BucketHole',
            'view/BucketFront',
            'view/SymbolView',
            'view/MassNumberView',
            'view/ElectronShellView'
        ], function ( _, Easel, ModelViewTransform2D, ParticleView, AtomView, BucketHole, BucketFront, SymbolView, MassNumberView, ElectronShellView ) {

    function BuildAnAtomStage( canvas, model ) {

        var $window = $( window );
        var self = this;

        // Create the stage.
        this.stage = new Easel.Stage( canvas );
        var stageWidth = canvas.width;
        var stageHeight = canvas.height;

        // Set up the model-view transform. The center of the nucleus is at the
        // the point (0, 0) in model space, so this is set up to make that
        // point centered in the x direction and somewhat above center in the
        // y direction.
        var mvt = new ModelViewTransform2D( 1, { x: stageWidth / 2, y: stageHeight * 0.4 } );

        // Create a root node for the scene graph.
        var root = new Easel.Container();
        this.stage.addChild( root );

        // Create and add the place where the nucleus will be constructed.
        var atomView = new AtomView( model.atom, mvt );
        root.addChild( atomView );

        // Add the electron shell.
        var electronShell = new ElectronShellView( model.atom, mvt );
        root.addChild( electronShell );

        // Create and add the bucket holes where the idle particles will be kept.
        _.each( model.buckets, function ( bucketModel, bucketName ) {

            var bucketHole = self[ bucketName + 'Hole' ] = new BucketHole( bucketModel, mvt );
            root.addChild( bucketHole );

            var protonBucketFront = new BucketFront( bucketModel, mvt );
            root.addChild( protonBucketFront );

        } );

        // Create and add the particles.
        _.each( model.particles, function ( particle ) {
            // TODO: Fix ParticleView to have a constructor instead.
            root.addChild( ParticleView.createParticleView( particle ));
        } );
//
//        // Add the bucket fronts.

//        root.addChild( new BucketFront( neutronBucketHole.centerX, neutronBucketHole.centerY, bucketWidth, "Neutrons" ) );
//        root.addChild( new BucketFront( electronBucketHole.centerX, electronBucketHole.centerY, bucketWidth, "Electrons" ) );

        // Initial stage update.  TODO: Is this needed?
        this.stage.update();

        //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
        canvas.onselectstart = function () {
            return false;
        }; // IE
        canvas.onmousedown = function () {
            return false;
        }; // Mozilla

        // Set the frame rate.
        Easel.Ticker.setFPS( 60 );

        // Enable and configure touch and mouse events.
        Easel.Touch.enable( this.stage, false, false );
        this.stage.enableMouseOver( 10 );
        this.stage.mouseMoveOutside = true;

        Easel.Ticker.addListener( this.stage, true );

        //resize the canvas when the window is resized.
        var buildAnAtomCanvas = $( canvas );
        var self = this;
        $window.on( 'resize', function () {
            var winW = $window.width();
            var winH = $window.height();

            var scale = Math.min( winW / 614, winH / 768 );

            var canvasW = scale * 614;
            var canvasH = scale * 768;

            var container = buildAnAtomCanvas.parent();

            // set the canvas size
            buildAnAtomCanvas.attr( {
                                        width:~~canvasW,
                                        height:~~canvasH
                                    } );

            // center the canvas in its parent container
            buildAnAtomCanvas.css( {
                                       marginLeft:(container.width() - ~~canvasW) / 2
                                   } );

            root.scaleX = root.scaleY = scale;
            self.stage.update();
        } );

        $window.resize();
    }

    return BuildAnAtomStage;

} );
