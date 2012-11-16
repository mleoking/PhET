// Copyright 2002-2012, University of Colorado
require( [
             'underscore',
             'easel',
             'model/particle2',
             'view/particle-view',
             'view/atom-view',
             'view/bucket-view',
             'tpl!templates/periodic-table.html'
         ], function ( _, Easel, Particle2, ParticleView, AtomView, BucketView, periodicTable ) {

    var $window = $(window);

    // Create the canvas where atoms will be constructed.
    var atomConstructionCanvas = $( '#atom-construction-canvas' );
    var atomStage = new Easel.Stage( atomConstructionCanvas[0] );

    // Create a root node for the scene graph.
    var root = new Easel.Container();
    atomStage.addChild( root );

    // Create and add the place where the nucleus will be constructed.
    var atomView = new AtomView();

    atomView.x = 307;
    atomView.y = 384;
    root.addChild( atomView );

    // Create and add the buckets.
    var protonBucket = new BucketView( 125, 500 );
    root.addChild( protonBucket );
    var neutronBucket = new BucketView( 300, 500 );
    root.addChild( neutronBucket );
    var electronBucket = new BucketView( 475, 500 );
    root.addChild( electronBucket );

    // Create and add the particles.
    root.addChild( ParticleView.createParticleView( new Particle2( protonBucket.x + 30, protonBucket.y, "red", 15, "proton" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( protonBucket.x + 60, protonBucket.y, "red", 15, "proton" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( protonBucket.x + 90, protonBucket.y, "red", 15, "proton" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( protonBucket.x + 120, protonBucket.y, "red", 15, "proton" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucket.x + 30, neutronBucket.y, "gray", 15, "neutron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucket.x + 60, neutronBucket.y, "gray", 15, "neutron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucket.x + 90, neutronBucket.y, "gray", 15, "neutron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucket.x + 120, neutronBucket.y, "gray", 15, "neutron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucket.x + 120, neutronBucket.y, "gray", 15, "neutron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( electronBucket.x + 30, electronBucket.y, "blue", 8, "electron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( electronBucket.x + 60, electronBucket.y, "blue", 8, "electron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( electronBucket.x + 90, electronBucket.y, "blue", 8, "electron" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( electronBucket.x + 120, electronBucket.y, "blue", 8, "electron" ) ) );

    atomStage.update();

    //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
    atomConstructionCanvas[0].onselectstart = function () {
        return false;
    }; // IE
    atomConstructionCanvas[0].onmousedown = function () {
        return false;
    }; // Mozilla

    // Set the frame rate.
    Easel.Ticker.setFPS( 60 );

    // Enable and configure touch and mouse events.
    Easel.Touch.enable( atomStage, false, false );
    atomStage.enableMouseOver( 10 );
    atomStage.mouseMoveOutside = true;

    Easel.Ticker.addListener( atomStage, true );

    //resize the canvas when the window is resized
    //Copied from energy skate park easel prototype
    $window.on('resize', function () {
        var winW = $window.width();
        var winH = $window.height();

        var scale = Math.min( winW / 614, winH / 768 );

        var canvasW = scale * 614;
        var canvasH = scale * 768;

        var container = atomConstructionCanvas.parent();

        // set the canvas size
        atomConstructionCanvas.attr({
          width: ~~canvasW,
          height: ~~canvasH
        });

        // center the canvas in its parent container
        atomConstructionCanvas.css({
          marginLeft: (container.width() - ~~canvasW) / 2
        });

        root.scaleX = root.scaleY = scale;
        atomStage.update();
    });

    $window.resize();

    $( document ).ready( function (){
       $('#periodic-table' ).html( periodicTable() );
    });
} );
