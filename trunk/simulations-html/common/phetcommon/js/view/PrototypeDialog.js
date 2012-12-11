//Code for handling the "this is a prototype" dialog for the Dec 2012 newsletter.
define( ['tpl!../../../phetcommon/html/prototype-dialog.html'], function ( template ) {
    return {init: function ( simName, url ) {
        $( template( {simName: simName, url: url} ) ).appendTo( $( "body" ) );
        $( '.prototype-dialog-ok-button' ).click( function () {
            $( '.prototype-overlay' ).hide();
        } );
    }};
} );