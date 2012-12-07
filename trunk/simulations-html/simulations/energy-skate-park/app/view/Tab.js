define( ['model/Skater', 'underscore', 'view/EnergySkateParkCanvas', 'model/physics', 'Property'], function ( Skater, _, EnergySkateParkCanvas, Physics, Property ) {
    function Tab( $tab, Easel, Strings, analytics ) {
        var self = this;
        self.dt = new Property( 0.02 );
        var $canvas = $tab.find( 'canvas' );

        var skaterModel = new Skater();
        var groundHeight = 116;
        var groundY = 768 - groundHeight;

        var energySkateParkCanvas = new EnergySkateParkCanvas( $canvas, Strings, analytics, skaterModel, groundHeight, groundY );

        var paused = false;

        Easel.Ticker.addListener( function () {
            if ( !paused ) {
                var subdivisions = 1;
                for ( var i = 0; i < subdivisions; i++ ) {
                    Physics.updatePhysics( skaterModel, groundHeight, energySkateParkCanvas.root.splineLayer, self.dt.get() / subdivisions );
                }

//                updateFrameRate();
                energySkateParkCanvas.root.tick();
            }
            energySkateParkCanvas.render();
        } );
    }

    Tab.prototype.$ = function ( selector ) {
    };

    Tab.prototype.render = function () {
    };

    Tab.prototype.getValue = function () {
    };

    return Tab;
} );
