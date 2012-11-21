define( [], function () {

    function Vector2D( _x, _y ) {
        var vector = {x:_x || 0, y:_y || 0};
        vector.plus = function ( a, b ) { return Vector2D( vector.x + a, vector.y + b ); };
        vector.times = function ( scale ) {return Vector2D( vector.x * scale, vector.y * scale );};
        vector.magnitude = function () {return Math.sqrt( vector.x * vector.x + vector.y * vector.y )};
        return vector;
    }

    return Vector2D;
} );

