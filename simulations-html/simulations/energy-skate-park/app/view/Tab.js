define( ['model/EnergySkateParkModel', 'underscore', 'view/EnergySkateParkCanvas', 'model/physics', 'model/Property'], function ( EnergySkateParkModel, _, EnergySkateParkCanvas, Physics, Property ) {
    function Tab( $tab, Easel, Strings, analytics, tabID, activeTab ) {
        var self = this;
        var $canvas = $tab.find( 'canvas' );

        var model = new EnergySkateParkModel();
        var energySkateParkCanvas = new EnergySkateParkCanvas( $canvas, Strings, analytics, model );

        Easel.Ticker.addListener( function () {
            if ( model.playing.get() && activeTab.get() == tabID ) {
                var subdivisions = 1;
                for ( var i = 0; i < subdivisions; i++ ) {
                    Physics.updatePhysics( model.skater, model.groundHeight, energySkateParkCanvas.root.splineLayer, model.slowMotion.get() ? 0.01 : 0.02 / subdivisions );
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
        new MBP.fastButton( $tab.find( '.play-pause-button' )[0], function ( e ) {
            model.playing.toggle();
            $tab.find( '.play-pause-button' ).html( !model.playing.get() ? "&#9654;" : "&#10074;&#10074;" );
        } );

        //TODO: I'd like to rewrite this using model.slowMotion.setTrue but can't get the "this" assigned properly.
        new MBP.fastButton( $tab.find( '.slow-motion-button' )[0], function ( e ) {model.slowMotion.set( true );} );
        new MBP.fastButton( $tab.find( '.normal-button' )[0], function ( e ) {model.slowMotion.set( false );} );

        model.slowMotion.addObserver( function ( slowMotion ) {
            $tab.find( '.slow-motion-button' ).removeClass( "js-active-button" );
            $tab.find( '.normal-button' ).removeClass( "js-active-button" );
            if ( slowMotion ) {
                $tab.find( '.slow-motion-button' ).addClass( "js-active-button" );
            }
            else {
                $tab.find( '.normal-button' ).addClass( "js-active-button" );
            }
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
