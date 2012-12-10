define( [
            'underscore',
            'easel',
            'common/ModelViewTransform2D',
            'common/Utils',
            'model/Atom',
            'view/ParticleView',
            'view/AtomView',
            'view/BucketHole',
            'view/BucketFront',
            'view/ElectronShellView'
        ], function ( _, Easel, ModelViewTransform2D, Utils, Atom, ParticleView, AtomView, BucketHole, BucketFront, ElectronShellView ) {

    function BuildAnAtomStage( canvas, model ) {

        var self = this;
        var $window = $( window );

        // Create the stage.
        this.stage = new Easel.Stage( canvas );
        var stageWidth = canvas.width;
        var stageHeight = canvas.height;

        // Set up the model-view transform. The center of the nucleus is at the
        // the point (0, 0) in model space, so this is set up to make that
        // point centered in the x direction and somewhat above center in the
        // y direction.
        var mvt = new ModelViewTransform2D( 1, { x:stageWidth / 2, y:stageHeight * 0.4 } );

        // Create a root node for the scene graph.
        var root = this.root = new Easel.Container();
        this.stage.addChild( root );

        // Create and add the place where the nucleus will be constructed.
        var atomView = new AtomView( model.atom, mvt );
        root.addChild( atomView );

        // Add the electron shell.
        var electronShell = new ElectronShellView( model.atom, Atom.INNER_ELECTRON_SHELL_RADIUS, Atom.OUTER_ELECTRON_SHELL_RADIUS, mvt );
        root.addChild( electronShell );

        // Create and add the bucket holes where the idle particles will be kept.
        _.each( model.buckets, function ( bucketModel, bucketName ) {
            var bucketHole = self[ bucketName + 'Hole' ] = new BucketHole( bucketModel, mvt );
            root.addChild( bucketHole );
        } );

        // Create and add the particles, and put them on their own layer.
        this.particleLayer = new Easel.Container();
        root.addChild( this.particleLayer );
        _.each( model.nucleons, function ( particleModel ) {
            var particleView = new ParticleView( particleModel, mvt );
            self.particleLayer.addChild( particleView );
        } );
        _.each( model.electrons, function ( particleModel ) {
            var particleView = new ParticleView( particleModel, mvt );
            self.particleLayer.addChild( particleView );
        } );

        // Create and add the bucket fronts.
        _.each( model.buckets, function ( bucketModel, bucketName ) {
            var bucketFront = new BucketFront( bucketModel, mvt );
            root.addChild( bucketFront );
        } );

        // Hook up a listener to re-layer particles in the nucleus when needed.
        model.atom.events.on( 'configurationChanged', function () {
            self.particleLayer.sortChildren( function ( pv1, pv2 ) {
                return( Utils.distanceBetweenPoints( 0, 0, pv2.particle.x, pv2.particle.y ) - Utils.distanceBetweenPoints( 0, 0, pv1.particle.x, pv1.particle.y ));
            } );
        } );

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
