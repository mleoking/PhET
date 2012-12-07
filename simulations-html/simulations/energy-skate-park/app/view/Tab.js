define( ['model/EnergySkateParkModel', 'underscore', 'view/EnergySkateParkCanvas', 'model/physics', 'model/Property'], function ( EnergySkateParkModel, _, EnergySkateParkCanvas, Physics, Property ) {
    function Tab( $tab, Easel, Strings, analytics ) {
        var self = this;
        self.dt = new Property( 0.02 );
        var $canvas = $tab.find( 'canvas' );

        var model = new EnergySkateParkModel();
        var groundHeight = 116;
        var groundY = 768 - groundHeight;

        var energySkateParkCanvas = new EnergySkateParkCanvas( $canvas, Strings, analytics, model, groundHeight, groundY );

        var paused = false;

        Easel.Ticker.addListener( function () {
            if ( !paused ) {
                var subdivisions = 1;
                for ( var i = 0; i < subdivisions; i++ ) {
                    Physics.updatePhysics( model.skater, groundHeight, energySkateParkCanvas.root.splineLayer, self.dt.get() / subdivisions );
                }

//                updateFrameRate();
                energySkateParkCanvas.root.tick();
            }
            energySkateParkCanvas.render();
        } );

        //Wire up the buttons
        $tab.find( '.barGraphButton' ).click( function () {model.barChartVisible.toggle();} );
        $tab.find( '.pieChartButton' ).click( function () {model.pieChartVisible.toggle();} );
        $tab.find( '.gridButton' ).click( function () {model.gridVisible.toggle();} );
        $tab.find( '.speedometerButton' ).click( function () {model.speedometerVisible.toggle();} );
    }

    Tab.prototype.$ = function ( selector ) {
    };

    Tab.prototype.render = function () {
    };

    Tab.prototype.getValue = function () {
    };

    return Tab;
} );
