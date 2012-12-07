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
        new MBP.fastButton( $tab.find( '.barGraphButton' )[0], function ( e ) {model.barChartVisible.toggle();} );
        new MBP.fastButton( $tab.find( '.pieChartButton' )[0], function ( e ) {model.pieChartVisible.toggle();} );
        new MBP.fastButton( $tab.find( '.gridButton' )[0], function ( e ) {model.gridVisible.toggle();} );
        new MBP.fastButton( $tab.find( '.speedometerButton' )[0], function ( e ) {model.speedometerVisible.toggle();} );
    }

    Tab.prototype.$ = function ( selector ) {
    };

    Tab.prototype.render = function () {
    };

    Tab.prototype.getValue = function () {
    };

    return Tab;
} );
