(function () {
    console.log( "started" );

    //Create the canvas and add to the document
    var canvas = document.createElement( "canvas" );
    var ctx = canvas.getContext( "2d" );
    document.body.appendChild( canvas );

    //Don't allow the page to scroll up on ipad
    document.ontouchmove = function ( e ) {e.preventDefault()};

    var drag = [];
    var hammer = new Hammer( canvas );
    hammer.ondragstart = function ( ev ) {};

    var skaterX = 0;
    var skaterY = 0;
    var skaterVelocityX = 0;
    var skaterVelocityY = 0;
    var skaterDragging = false;
    document.onmousemove = function ( e ) {

        //How to tell if mouse is over something?
        if ( navigator.browser !== 'iOS' ) {
            document.body.style.cursor = 'pointer';
        }
    };

    //Disable text selection, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
    canvas.onselectstart = function () { return false; }; // ie
    canvas.onmousedown = function () { return false; }; // mozilla

    hammer.ondrag = function ( ev ) {
        drag = [];
        var touches = ev.originalEvent.touches || [ev.originalEvent];
        for ( var t = 0; t < touches.length; t++ ) {
            if ( t == 0 ) {
                skaterX = ev.touches[t].x;
                skaterY = canvas.height - ev.touches[t].y;

                skaterVelocityX = 0.0;
                skaterVelocityY = 0.0;
                skaterDragging = true;
            }
        }
    };
    hammer.ondragend = function ( ev ) {};

    hammer.onswipe = function ( ev ) {};

    hammer.ontap = function ( ev ) {};
    hammer.ondoubletap = function ( ev ) {};
    hammer.onhold = function ( ev ) {};

    hammer.ontransformstart = function ( ev ) {};
    hammer.ontransform = function ( ev ) {};
    hammer.ontransformend = function ( ev ) {};

    hammer.onrelease = function ( ev ) { skaterDragging = false; };

    //Preload images
    var loadedImages = 0;
    var skaterImage = new Image();
    skaterImage.src = "resources/skater.png";
    skaterImage.onload = function () { loadedImages++; };

    //or another game loop here: http://www.playmycode.com/blog/2011/08/building-a-game-mainloop-in-javascript/
    //or here: http://jsfiddle.net/Y9uBv/5/
    var requestAnimationFrame =
            requestAnimationFrame ||
            webkitRequestAnimationFrame ||
            mozRequestAnimationFrame ||
            msRequestAnimationFrame ||
            oRequestAnimationFrame;

    var blockX = 100;
    var lastTime = new Date().getTime();
    var deltas = [];

    function updatePhysics() {
        //free fall

        var dt = 1.0 / 60.0 * 10;

        var skaterAccelerationX = 0;
        var skaterAccelerationY = skaterY <= 0 || skaterDragging ? 0.0 : -9.8;
        skaterVelocityX = skaterVelocityX + skaterAccelerationX * dt;
        skaterVelocityY = skaterVelocityY + skaterAccelerationY * dt;
        skaterX = skaterX + skaterVelocityX * dt + 0.5 * skaterAccelerationX * dt * dt;
        skaterY = skaterY + skaterVelocityY * dt + 0.5 * skaterAccelerationY * dt * dt;

        if ( skaterY < 0 ) {
            skaterY = 0;
            skaterVelocityY = 0.0;
        }
    }

    function renderGraphics() {
        //http://stackoverflow.com/questions/1664785/html5-canvas-resize-to-fit-window
        ctx.canvas.width = window.innerWidth;
        ctx.canvas.height = window.innerHeight;

        var desiredAspectRatio = 1024.0 / 768.0;
        var actualAspectRatio = ctx.canvas.width / ctx.canvas.height;

        var widthLimited = actualAspectRatio > desiredAspectRatio;
        var scale = widthLimited ? ctx.canvas.height / 768 : ctx.canvas.width / 1024;
        //        console.log( "scale = " + scale );

        //        ctx.fillStyle = 'blue';
        //        ctx.fillRect( 0, 0, window.innerWidth / 2, window.innerHeight / 2 );

        ctx.fillStyle = 'black';
        ctx.fillRect( blockX, 100, 20, 20 );

        ctx.save();
        ctx.translate( skaterX, ctx.canvas.height - skaterY );
        ctx.fillStyle = 'red';
        ctx.fillRect( -2, -2, 4, 4 );
        ctx.scale( scale, scale );
        ctx.drawImage( skaterImage, -skaterImage.width / 2, -skaterImage.height );
        ctx.restore();

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

        ctx.fillText( "FPS: " + FPS.toPrecision( 6 ), 100, 100 );

        lastTime = currentTime;

        ++blockX;
    }

    function loop() {
        if ( loadedImages == 0 ) {
            requestAnimationFrame( loop );
            return;
        }

        updatePhysics();
        renderGraphics();
        requestAnimationFrame( loop );
    }

    requestAnimationFrame( loop );
})();