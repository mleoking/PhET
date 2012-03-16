// Copyright 2002-2011, University of Colorado

// Defines a function that creates a draggable circle on a CAKE canvas.
window.onload = function() {
    var CAKECanvas = new Canvas( document.body, 600, 400,
                                 {
                                     fill: 'rgb(255, 255, 153)'
                                 }
    );

    // Root of our scene graph.
    var scene = new CanvasNode( {x: 0, y: 0} )

    var circle = new Circle( 40,
                             {
                                 id: 'myCircle',
                                 x: CAKECanvas.width / 3,
                                 y: CAKECanvas.height / 2,
                                 fill: 'red',
                                 stroke: 'black',
                                 strokeWidth: 1
                             }
    );

    var text = new TextNode( "help", {
        text: 'TextNode!',
        cx : 100,
        cy : 100,
        height : 40
    } );

    circle.addEventListener( 'dragstart', function( event ) {
        console.log( event.type + " caught!" );
        text.text = event.type;
        circle.fill = "cyan";
    }, true );
    circle.addEventListener( 'drag', function( event ) {
        console.log( event.type + " caught!" );
        text.text = event.type;
        circle.fill = "magenta";
    }, true );
    circle.addEventListener( 'touchstart', function( event ) {
        console.log( event.type + " caught!" );
        text.text = event.type;
        circle.fill = "white";
    }, true );
    circle.addEventListener( 'touchmove', function( event ) {
        console.log( event.type + " caught!" );
        text.text = event.type;
        circle.fill = "green";
    }, true );
    circle.addEventListener( 'touchend', function( event ) {
        console.log( event.type + " caught!" );
        text.text = event.type;
        circle.fill = "black";
    }, true );

    circle.makeDraggable();

    scene.append( circle );
    scene.append( text );
    CAKECanvas.append( scene );

    // Temp - Handler to log touch start events.
//    document.addEventListener( 'touchstart', function( event ) {
//        console.log( event.type + " caught!" );
//        text.text = event.type;
//        circle.fill = "orange";
//    }, true );


    // Disable elastic scrolling.
    document.addEventListener(
            'touchmove',
            function( e ) {
                e.preventDefault();
            },
            false
    );
};
