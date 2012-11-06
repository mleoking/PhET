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
    var R = Raphael( 0, 0, "100%", "100%" ),
            r = R.circle( 100, 100, 50 ).attr( {fill:"hsb(0, 1, 1)", stroke:"none", opacity:1} ),
            g = R.circle( 210, 100, 50 ).attr( {fill:"hsb(.3, 1, 1)", stroke:"none", opacity:1} ),
            b = R.circle( 320, 100, 50 ).attr( {fill:"hsb(.6, 1, 1)", stroke:"none", opacity:1} ),
            p = R.circle( 430, 100, 50 ).attr( {fill:"hsb(.8, 1, 1)", stroke:"none", opacity:1} );
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
    R.set( r, g, b, p ).drag( move, start, up );
} );