//Code for handling the "this is a prototype" dialog for the Dec 2012 newsletter.
//NOTE: path phetcommon_html must be defined by the client.
define( ['tpl!phetcommon_html/prototype-dialog.html'], function ( template ) {
    return {init: function ( simName ) {
        $( template( {simName: simName} ) ).appendTo( $( "body" ) );
        $( '.dialog-overlay' ).click( function () {
            $(this).hide();
        } );
    }};
} );