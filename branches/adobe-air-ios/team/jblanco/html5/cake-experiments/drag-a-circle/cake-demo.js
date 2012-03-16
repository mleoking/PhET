window.onload = function() {
    var CAKECanvas = new Canvas( document.body, 600, 400 );

    var circle1 = new Circle( 100,
                              {
                                  id: 'myCircle1',
                                  x: CAKECanvas.width / 3,
                                  y: CAKECanvas.height / 2,
                                  stroke: 'cyan',
                                  strokeWidth: 20,
                                  endAngle: Math.PI * 2
                              }
    );

    circle1.addFrameListener(
            function( t, dt ) {
                this.scale = Math.sin( t / 1000 );
            }
    );

    CAKECanvas.append( circle1 );

    var circle2 = new Circle( 100,
                              {
                                  id: 'myCircle2',
                                  x: CAKECanvas.width / 3 * 2,
                                  y: CAKECanvas.height / 2,
                                  stroke: 'red',
                                  strokeWidth: 20,
                                  endAngle: Math.PI * 2
                              }
    );

    circle2.addFrameListener(
            function( t, dt ) {
                this.scale = Math.cos( t / 1000 );
            }
    );

    CAKECanvas.append( circle2 );

    var hello = new ElementNode( E( 'h2', 'Hello, world!' ),
                                 {
                                     fontFamily: 'Arial, Sans-serif',
                                     noScaling: true,
                                     color: 'black',
                                     x: CAKECanvas.width / 2,
                                     y: CAKECanvas.height / 2,
                                     align: 'center',
                                     valign: 'center'
                                 }
    );

    hello.every( 1000,
                 function() {
                     this.color = 'magenta';
                     this.after( 200,
                                 function() {
                                     this.color = 'blue';
                                 }
                     );
                 },
                 true
    );

    CAKECanvas.append( hello );
};
