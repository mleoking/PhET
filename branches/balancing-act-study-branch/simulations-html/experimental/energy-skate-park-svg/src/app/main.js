require( [
             'websocket-refresh',
             'skater',
             'control-panel',
             'background',
             'spline',
             'physics'
         ], function ( WebsocketRefresh, Skater, ControlPanel, Background, Spline, Physics ) {

    WebsocketRefresh.listenForRefresh();

    //See sample code here: http://raphaeljs.com/touches.html
    var paper = new ScaleRaphael( "container", 1024, 768 );
    paper.overallScaleFactor = 1.0;
    var groundHeight = 116;
    var sky = paper.rect( 0, 0, 1024, 768 - groundHeight ).attr( {fill:"90-#ffffff:5-#7cc7fe:95", stroke:null} );
    var skater = paper.image( "resources/skater.png", 10, 10, 150, 200 );
    skater.attr( {x:100, y:100} );

    var skaterStart = function () {
                this.ox = this.attr( "x" );
                this.oy = this.attr( "y" );
                skater.dragging = true;
            },
            skaterMove = function ( dx, dy ) {
                this.attr( {x:this.ox + dx / paper.overallScaleFactor, y:this.oy + dy / paper.overallScaleFactor} );
            },
            skaterUp = function () {
                skaterY = skater.attr( "y" );
                skater.dragging = false;
            };
    skater.drag( skaterMove, skaterStart, skaterUp );

    var ground = paper.rect( 0, 768 - groundHeight, 1024, 768 ).attr( {fill:"#64aa64"} );

    //or another game loop here: http://www.playmycode.com/blog/2011/08/building-a-game-mainloop-in-javascript/
    //or here: http://jsfiddle.net/Y9uBv/5/
    //See http://stackoverflow.com/questions/5605588/how-to-use-requestanimationframe
    var requestAnimationFrame = function () {
        return (
                window.requestAnimationFrame ||
                window.webkitRequestAnimationFrame ||
                window.mozRequestAnimationFrame ||
                window.oRequestAnimationFrame ||
                window.msRequestAnimationFrame ||
                function ( /* function */ callback ) {
                    window.setTimeout( callback, 1000 / 60 );
                }
                );
    }();

    var lastTime = Date.now();

    var skaterY = 100;
    var skaterHeight = skater.attr( "height" );

    function updatePhysics() {
        var newTime = Date.now();
        var dt = newTime - lastTime;
        if ( dt > 0 && !skater.dragging ) {

            skaterY = Math.min( 768 - groundHeight - skaterHeight, skaterY + 0.1 * dt );
            skater.attr( {y:skaterY} );
            lastTime = newTime;
        }
    }

    function loop() {
        updatePhysics();
        requestAnimationFrame( loop );
    }

    requestAnimationFrame( loop );

    function resizePaper() {
        paper.changeSize( window.innerWidth, window.innerHeight, true, false );
    }

    resizePaper();
    $( window ).resize( resizePaper );
} );