require( [
             'websocket-refresh',
             'skater',
             'control-panel',
             'background',
             'spline',
             'physics'
         ], function ( WebsocketRefresh, Skater, ControlPanel, Background, Spline, Physics ) {

    WebsocketRefresh.listenForRefresh();

    //See sample at: http://stackoverflow.com/questions/3675519/raphaeljs-drag-and-drop
    var nowX;
    var nowY;
    var paper = Raphael( 0, 0, window.innerWidth, window.innerHeight );
//    paper.setViewBox( 0, 0, 2000, 2000, false );
    var c = paper.rect( 200, 200, 100, 100 ).attr( {
                                                       fill:"hsb(.8, 1, 1)",
                                                       stroke:"none",
                                                       cursor:"move"
                                                   } );
    var j = paper.rect( 0, 0, 100, 100 );
    // start, move, and up are the drag functions
    var start = function () {
        // storing original coordinates
        this.ox = this.attr( "x" );
        this.oy = this.attr( "y" );
        this.attr( {opacity:1} );
        if ( this.attr( "y" ) < 60 && this.attr( "x" ) < 60 ) {
            this.attr( {fill:"#000"} );
        }
    };
    var move = function ( dx, dy ) {
        // move will be called with dx and dy
        if ( this.attr( "y" ) > 60 || this.attr( "x" ) > 60 ) {
            this.attr( {x:this.ox + dx, y:this.oy + dy} );
        }
        else {
            nowX = Math.min( 60, this.ox + dx );
            nowY = Math.min( 60, this.oy + dy );
            nowX = Math.max( 0, nowX );
            nowY = Math.max( 0, nowY );
            this.attr( {x:nowX, y:nowY } );
            if ( this.attr( "fill" ) != "#000" ) {
                this.attr( {fill:"#000"} );
            }
        }
    };
    var up = function () {
        // restoring state
        this.attr( {opacity:.5} );
        if ( this.attr( "y" ) < 60 && this.attr( "x" ) < 60 ) {
            this.attr( {fill:"#AEAEAE"} );
        }
    };
    // rstart and rmove are the resize functions;
    c.drag( move, start, up );
} );