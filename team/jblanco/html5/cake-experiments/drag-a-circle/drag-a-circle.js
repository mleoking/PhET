// Copyright 2002-2011, University of Colorado

// Defines a function that creates a draggable circle on a CAKE canvas.
window.onload = function() {
    var CAKECanvas = new Canvas( document.body, 600, 400,
                                 {
                                     fill: 'rgb(255, 255, 153)'
                                 }
    );

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

    circle.makeDraggable();

    circle.addFrameListener(
            function( t, dt ) {
                // Should set canvas position.
            }
    );

    CAKECanvas.append( circle );
};
