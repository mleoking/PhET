(function () {
    console.log( "started" );
    var canvas = document.createElement( "canvas" );
    var ctx = canvas.getContext( "2d" );
    document.body.appendChild( canvas );

    //or another game loop here: http://www.playmycode.com/blog/2011/08/building-a-game-mainloop-in-javascript/
    //or here: http://jsfiddle.net/Y9uBv/5/
    var requestAnimationFrame =
            requestAnimationFrame ||
            webkitRequestAnimationFrame ||
            mozRequestAnimationFrame ||
            msRequestAnimationFrame ||
            oRequestAnimationFrame;

    var x = 100;

    var lastTime = new Date().getTime();

    var deltas = [];

    function loop() {

        //http://stackoverflow.com/questions/1664785/html5-canvas-resize-to-fit-window
        ctx.canvas.width = window.innerWidth;
        ctx.canvas.height = window.innerHeight;

        ctx.fillStyle = 'blue';
        ctx.fillRect( 0, 0, window.innerWidth / 2, window.innerHeight / 2 );

        ctx.fillStyle = 'black';
        ctx.fillRect( x, 100, 20, 20 );

        var currentTime = new Date().getTime();
        deltas.push( currentTime - lastTime );
        if ( deltas.length > 60 ) {
            deltas.splice( 0, 1 );
        }
        var sum = 0.0;
        for ( var i = 0; i < deltas.length; i++ ) {
            sum += deltas[i];
        }
        var delta = sum / deltas.length;
        var deltaSeconds = delta / 1000;
        var FPS = 1.0 / deltaSeconds;
        ctx.fillStyle = 'black';

        ctx.fillText( "FPS: " + FPS.toPrecision( 2 ), 100, 100 );

        lastTime = currentTime;

        ++x;
        requestAnimationFrame( loop );
    }

    requestAnimationFrame( loop );
})();