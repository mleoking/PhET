define( [
            'underscore',
            'easel',
            'model/particle2',
            'view/particle-view',
            'view/atom-view',
            'view/bucket-hole',
            'view/bucket-front',
            'view/symbol-view',
            'view/mass-number-view',
            'model/atom',
            'view/electron-shell-view'
        ], function ( _, Easel, Particle2, ParticleView, AtomView, BucketHole, BucketFront, SymbolView, MassNumberView, Atom, ElectronShellView ) {

    function BuildAnAtomStage( canvas, model ) {

        var $window = $( window );
        var self = this;

        // Create the stage.
        this.stage = new Easel.Stage( canvas );
        var stageWidth = canvas.width;
        var stageHeight = canvas.height;

        // Create a root node for the scene graph.
        var root = new Easel.Container();
        this.stage.addChild( root );

        // Create and add the place where the nucleus will be constructed.
        var atomView = new AtomView();
        atomView.x = stageWidth / 2;
        atomView.y = stageHeight * 0.4;
        root.addChild( atomView );

        // Add the electron shell.
        var electronShell = new ElectronShellView();
        electronShell.x = stageWidth / 2;
        electronShell.y = stageHeight * 0.4;
        root.addChild( electronShell );

        // Create and add the bucket holes where the idle particles will be kept.

        _.each(model.buckets, function(bucketModel, bucketName){

          var bucketHole = self[ bucketName +'Hole' ] = new BucketHole( bucketModel );
          root.addChild( bucketHole );

          var protonBucketFront = new BucketFront( bucketModel );
          root.addChild( protonBucketFront );

        });



//        var bucketWidth = 150;
//        var bucketHoleY = stageHeight * 0.75;
//        var neutronBucketHole = new BucketHole( stageWidth / 2, bucketHoleY, bucketWidth );
//        root.addChild( neutronBucketHole );
//        var protonBucketHole = new BucketHole( ( stageWidth - bucketWidth ) / 4, bucketHoleY, bucketWidth );
//        root.addChild( protonBucketHole );
//        var electronBucketHole = new BucketHole( stageWidth - (( stageWidth - bucketWidth ) / 4), bucketHoleY, bucketWidth );
//        root.addChild( electronBucketHole );

        // Create and add the particles.
//        root.addChild( ParticleView.createParticleView( new Particle2( protonBucketHole.x + 30, protonBucketHole.y, "red", 15, "proton" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( protonBucketHole.x + 60, protonBucketHole.y, "red", 15, "proton" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( protonBucketHole.x + 90, protonBucketHole.y, "red", 15, "proton" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( protonBucketHole.x + 120, protonBucketHole.y, "red", 15, "proton" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( neutronBucketHole.x + 30, neutronBucketHole.y, "gray", 15, "neutron" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( neutronBucketHole.x + 60, neutronBucketHole.y, "gray", 15, "neutron" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( neutronBucketHole.x + 90, neutronBucketHole.y, "gray", 15, "neutron" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( neutronBucketHole.x + 120, neutronBucketHole.y, "gray", 15, "neutron" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( neutronBucketHole.x + 120, neutronBucketHole.y, "gray", 15, "neutron" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( electronBucketHole.x + 30, electronBucketHole.y, "blue", 8, "electron" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( electronBucketHole.x + 60, electronBucketHole.y, "blue", 8, "electron" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( electronBucketHole.x + 90, electronBucketHole.y, "blue", 8, "electron" ) ) );
//        root.addChild( ParticleView.createParticleView( new Particle2( electronBucketHole.x + 120, electronBucketHole.y, "blue", 8, "electron" ) ) );
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
