function vector2d( _x, _y ) {
    var vector = {x:_x, y:_y};
    vector.plus = function ( a, b ) { return vector2d( vector.x + a, vector.y + b ); };
    vector.times = function ( scale ) {return vector2d( vector.x * scale, vector.y * scale );};
    return vector;
}

function zero() {return vector2d( 0, 0 );}

function getSign( value ) {
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
}