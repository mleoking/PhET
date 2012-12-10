define( [], function () {

    function Vector2D( x, y ) {
        var vector = {x: x || 0, y: y || 0};
        vector.plus = function ( a ) { return Vector2D( vector.x + a.x, vector.y + a.y ); };
        vector.minus = function ( a ) { return Vector2D( vector.x - a.x, vector.y - a.y ); };
        vector.times = function ( scale ) {return Vector2D( vector.x * scale, vector.y * scale );};
        vector.magnitude = function () {return Math.sqrt( vector.magnitudeSquared() )};
        vector.dot = function ( a ) {return a.x * vector.x + a.y * vector.y};
        vector.magnitudeSquared = function () {return vector.x * vector.x + vector.y * vector.y };
        vector.unit = function () {
            var m = vector.magnitude();
            return new Vector2D( x / m, y / m );
        };
        return vector;
    }

    Vector2D.fromAngle = function ( angle ) { return new Vector2D( Math.cos( angle ), Math.sin( angle ) ); };

    return Vector2D;
} );