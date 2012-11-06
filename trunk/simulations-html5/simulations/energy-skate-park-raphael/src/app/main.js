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
            },
            skaterMove = function ( dx, dy ) {
                this.attr( {x:this.ox + dx / paper.overallScaleFactor, y:this.oy + dy / paper.overallScaleFactor} );
            },
            skaterUp = function () {};
    skater.drag( skaterMove, skaterStart, skaterUp );

    var ground = paper.rect( 0, 768 - groundHeight, 1024, 768 ).attr( {fill:"#64aa64"} );

    function resizePaper() {
        paper.changeSize( window.innerWidth, window.innerHeight, true, false );
    }

    resizePaper();
    $( window ).resize( resizePaper );
} );