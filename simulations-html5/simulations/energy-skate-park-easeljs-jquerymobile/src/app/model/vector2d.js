define( [], function () {

    function Vector2D( x, y ) {
        var vector = {x: x || 0, y: y || 0};
        vector.plus = function ( a, b ) { return Vector2D( vector.x + a, vector.y + b ); };
        vector.times = function ( scale ) {return Vector2D( vector.x * scale, vector.y * scale );};
        vector.magnitude = function () {return Math.sqrt( vector.magnitudeSquared() )};
        vector.magnitudeSquared = function () {return vector.x * vector.x + vector.y * vector.y };
        return vector;
    }

    Vector2D.fromAngle = function ( angle ) { return new Vector2D( Math.cos( angle ), Math.sin( angle ) ); }

    return Vector2D;
} );