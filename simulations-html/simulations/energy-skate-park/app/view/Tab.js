define( ['model/EnergySkateParkModel', 'underscore', 'view/EnergySkateParkCanvas', 'model/physics', 'model/Property'], function ( EnergySkateParkModel, _, EnergySkateParkCanvas, Physics, Property ) {
    function Tab( $tab, Easel, Strings, analytics, tabID, activeTab ) {
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
        $tab.find( '.' + tabID + "Button" ).toggleClass( "active" );

        $tab.find( '.introductionTabButton' ).click( function () {activeTab.set( "introductionTab" );} );
        $tab.find( '.frictionTabButton' ).click( function () {activeTab.set( "frictionTab" );} );
        $tab.find( '.trackPlaygroundTabButton' ).click( function () {activeTab.set( "trackPlaygroundTab" );} );

//        new MBP.fastButton( $tab.find( '.introductionTabButton' )[0], function ( e ) {tabChangeListener( "tab1" );} );
//        new MBP.fastButton( $tab.find( '.frictionTabButton' )[0], function ( e ) {tabChangeListener( "tab2" );} );
//        new MBP.fastButton( $tab.find( '.trackPlaygroundTabButton' )[0], function ( e ) {tabChangeListener( "tab3" );} );

        //Wire up the buttons
        //Use fastButton to make sure they are highlighted and dispatched immediately (otherwise it takes a long time on ipad)
        new MBP.fastButton( $tab.find( '.barGraphButton' )[0], function ( e ) {
            model.barChartVisible.toggle();
            $tab.find( '.barGraphButton' ).toggleClass( "js-active-button" );
        } );
        new MBP.fastButton( $tab.find( '.pieChartButton' )[0], function ( e ) {
            model.pieChartVisible.toggle();
            $tab.find( '.pieChartButton' ).toggleClass( "js-active-button" );
        } );
        new MBP.fastButton( $tab.find( '.gridButton' )[0], function ( e ) {
            model.gridVisible.toggle();
            $tab.find( '.gridButton' ).toggleClass( "js-active-button" );
        } );
        new MBP.fastButton( $tab.find( '.speedometerButton' )[0], function ( e ) {
            model.speedometerVisible.toggle();
            $tab.find( '.speedometerButton' ).toggleClass( "js-active-button" );
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
