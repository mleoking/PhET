// Copyright 2002-2012, University of Colorado
require( [
             'underscore',
             'easel',
             'model/particle2',
             'view/particle-view',
             'view/atom-view',
             'view/bucket-hole',
             'view/bucket-front',
             'view/symbol-view',
             'view/mass-number-view',
             'view/BuildAnAtomStage',
             'model/atom',
             'view/electron-shell-view',
             'tpl!templates/periodic-table.html'
         ], function ( _, Easel, Particle2, ParticleView, AtomView, BucketHole, BucketFront, SymbolView, MassNumberView, BuildAnAtomStage, Atom, ElectronShellView, periodicTable ) {

    var buildAnAtomStage = new BuildAnAtomStage( document.getElementById( 'atom-construction-canvas' ) );

//    var $window = $( window );
//
//    // Create the canvas where atoms will be constructed.
//    var atomConstructionStage = $( '#atom-construction-canvas' );
//    var atomStage = new Easel.Stage( atomConstructionStage[0] );
//    var stageWidth = atomConstructionStage[0].width;
//    var stageHeight = atomConstructionStage[0].height;
//
//    // Create a root node for the scene graph.
//    var root = new Easel.Container();
//    atomStage.addChild( root );
//
//    // Create and add the place where the nucleus will be constructed.
//    var atomView = new AtomView();
//    atomView.x = stageWidth / 2;
//    atomView.y = stageHeight * 0.4;
//    root.addChild( atomView );
//
//    // Add the electron shell.
//    atomStage.addChild( new ElectronShellView( stageWidth / 2, stageHeight * 0.4 ) );
//
//    // Add the location where the atom will be constructed.
//
//    // Create and add the bucket holes where the idle particles will be kept.
//    var bucketWidth = 150;
//    var bucketHoleY = stageHeight * 0.75;
//    var neutronBucketHole = new BucketHole( stageWidth / 2, bucketHoleY, bucketWidth );
//    root.addChild( neutronBucketHole );
//    var protonBucketHole = new BucketHole( ( stageWidth - bucketWidth ) / 4, bucketHoleY, bucketWidth );
//    root.addChild( protonBucketHole );
//    var electronBucketHole = new BucketHole( stageWidth - (( stageWidth - bucketWidth ) / 4), bucketHoleY, bucketWidth );
//    root.addChild( electronBucketHole );
//
//    // Create and add the particles.
//    root.addChild( ParticleView.createParticleView( new Particle2( protonBucketHole.x + 30, protonBucketHole.y, "red", 15, "proton" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( protonBucketHole.x + 60, protonBucketHole.y, "red", 15, "proton" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( protonBucketHole.x + 90, protonBucketHole.y, "red", 15, "proton" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( protonBucketHole.x + 120, protonBucketHole.y, "red", 15, "proton" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucketHole.x + 30, neutronBucketHole.y, "gray", 15, "neutron" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucketHole.x + 60, neutronBucketHole.y, "gray", 15, "neutron" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucketHole.x + 90, neutronBucketHole.y, "gray", 15, "neutron" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucketHole.x + 120, neutronBucketHole.y, "gray", 15, "neutron" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( neutronBucketHole.x + 120, neutronBucketHole.y, "gray", 15, "neutron" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( electronBucketHole.x + 30, electronBucketHole.y, "blue", 8, "electron" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( electronBucketHole.x + 60, electronBucketHole.y, "blue", 8, "electron" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( electronBucketHole.x + 90, electronBucketHole.y, "blue", 8, "electron" ) ) );
//    root.addChild( ParticleView.createParticleView( new Particle2( electronBucketHole.x + 120, electronBucketHole.y, "blue", 8, "electron" ) ) );
//
//    // Add the bucket fronts.
//    root.addChild( new BucketFront( protonBucketHole.centerX, protonBucketHole.centerY, bucketWidth, "Protons" ) );
//    root.addChild( new BucketFront( neutronBucketHole.centerX, neutronBucketHole.centerY, bucketWidth, "Neutrons" ) );
//    root.addChild( new BucketFront( electronBucketHole.centerX, electronBucketHole.centerY, bucketWidth, "Electrons" ) );
//
//    atomStage.update();
//
//    //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
//    atomConstructionStage[0].onselectstart = function () {
//        return false;
//    }; // IE
//    atomConstructionStage[0].onmousedown = function () {
//        return false;
//    }; // Mozilla
//
//    // Set the frame rate.
//    Easel.Ticker.setFPS( 60 );
//
//    // Enable and configure touch and mouse events.
//    Easel.Touch.enable( atomStage, false, false );
//    atomStage.enableMouseOver( 10 );
//    atomStage.mouseMoveOutside = true;
//
//    Easel.Ticker.addListener( atomStage, true );
//
//    //resize the canvas when the window is resized
//    //Copied from energy skate park easel prototype
//    $window.on( 'resize', function () {
//        var winW = $window.width();
//        var winH = $window.height();
//
//        var scale = Math.min( winW / 614, winH / 768 );
//
//        var canvasW = scale * 614;
//        var canvasH = scale * 768;
//
//        var container = atomConstructionStage.parent();
//
//        // set the canvas size
//        atomConstructionStage.attr( {
//                                         width:~~canvasW,
//                                         height:~~canvasH
//                                     } );
//
//        // center the canvas in its parent container
//        atomConstructionStage.css( {
//                                        marginLeft:(container.width() - ~~canvasW) / 2
//                                    } );
//
//        root.scaleX = root.scaleY = scale;
//        atomStage.update();
//    } );
//
//    $window.resize();
//
    $( document ).ready( function () {

        var atom = new Atom();

        atom.protons = 1;
        atom.neutrons = 1;

        var symbolWidget = new SymbolView( atom );
        var massNumberWidget = new MassNumberView( atom );

        $( '#periodic-table' ).html( periodicTable() );
    } );
} );
