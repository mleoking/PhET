define( ['model/EnergySkateParkModel', 'underscore', 'view/EnergySkateParkCanvas', 'model/Physics', 'model/Property'], function ( EnergySkateParkModel, _, EnergySkateParkCanvas, Physics, Property ) {
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

        //Wire up the buttons
        //Use fastButton to make sure they are highlighted and dispatched immediately (otherwise it takes a long time on ipad)
        function connectBoolean( $component, property ) {
            new MBP.fastButton( $component[0], function ( e ) {property.toggle();} );
            property.addObserver( function ( selected ) {
                if ( selected && !$component.hasClass( "js-active-button" ) ||
                     !selected && $component.hasClass( "js-active-button" ) ) {
                    $component.toggleClass( "js-active-button" );
                }
            } );
        }

        connectBoolean( $tab.find( '.barGraphButton' ), model.barChartVisible );
        connectBoolean( $tab.find( '.pieChartButton' ), model.pieChartVisible );
        connectBoolean( $tab.find( '.gridButton' ), model.gridVisible );
        connectBoolean( $tab.find( '.speedometerButton' ), model.speedometerVisible );

        new MBP.fastButton( $tab.find( '.play-pause-button' )[0], function ( e ) {
            model.playing.toggle();
            $tab.find( '.play-pause-button' ).html( !model.playing.get() ? "&#9654;" : "&#10074;&#10074;" );
        } );

        new MBP.fastButton( $tab.find( '.reset-all-button' )[0], function ( e ) {model.resetAll();} );

        new MBP.fastButton( $tab.find( '.slow-motion-button' )[0], model.slowMotion.setTrueBound() );
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
