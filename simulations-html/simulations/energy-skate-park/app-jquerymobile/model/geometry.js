define( [], function () {
    return {
        //http://wiki.processing.org/w/Find_which_side_of_a_line_a_point_is_on
        linePointPosition2DVector: function ( p1, p2, p3 ) {

            var diff = this.sub( p2, p1 );
            var perp = {x: -diff.y, y: diff.x};
            var pp = this.sub( p3, p1 );
            return this.dot( pp, perp );
        },
        getSign: function getSign( value ) {
            if ( value == 0 ) {
                return 0;
            }
            else if ( value > 0 ) {
                return +1;
            }
            else if ( value < 0 ) {
                return -1;
            }
            return "wrong value";
        },
        dot: function ( a, b ) { return a.x * b.x + a.y * b.y; },
        sub: function ( a, b ) { return {x: (a.x - b.x), y: (a.y - b.y)}; }
    };
} );