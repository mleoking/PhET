//Code for handling the "this is a prototype" dialog for the Dec 2012 newsletter.

(function () {
    $( document ).ready( function () {
//        $( '<div/>', {class: "prototype-overlay"} ).appendTo( 'body' );
        $( '.prototype-dialog-ok-button' ).click( function () {
            $( '.prototype-overlay' ).hide();
        } );
    } );
})();