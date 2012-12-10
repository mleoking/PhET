window.addEventListener( "load", function onLoad() {
    QUnit.config.autostart = false;
    // Without this setTimeout, the specs don't always get execute in webKit browsers

    setTimeout( function () {
        //load tests using require
        require( ['../tests/PropertyTest'], function ( one ) {
            //now trigger them.
            QUnit.start();
        } );

    }, 10 );
    window.removeEventListener( "load", onLoad, true );
}, true );