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
    var paper = new ScaleRaphael( "container", 1024, 768 ),
            r = paper.circle( 100, 100, 50 ).attr( {fill:"hsb(0, 1, 1)", stroke:"none", opacity:1} ),
            g = paper.circle( 210, 100, 50 ).attr( {fill:"hsb(.3, 1, 1)", stroke:"none", opacity:1} ),
            b = paper.circle( 320, 100, 50 ).attr( {fill:"hsb(.6, 1, 1)", stroke:"none", opacity:1} ),
            p = paper.circle( 430, 100, 50 ).attr( {fill:"hsb(.8, 1, 1)", stroke:"none", opacity:1} );
    var start = function () {
                this.ox = this.attr( "cx" );
                this.oy = this.attr( "cy" );
                this.animate( {r:70, opacity:1}, 500, ">" );
            },
            move = function ( dx, dy ) {
                this.attr( {cx:this.ox + dx, cy:this.oy + dy} );
            },
            up = function () {
                this.animate( {r:50, opacity:1}, 500, ">" );
            };
    paper.set( r, g, b, p ).drag( move, start, up );


    function resizePaper() {
        paper.changeSize( window.innerWidth, window.innerHeight, true, false );
    }

    resizePaper();
    $( window ).resize( resizePaper );
} );