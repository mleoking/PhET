// set up some global namespaces
light = {};
light.EPSILON = 0.0001;
light.assert = function( bool, mess ) {
    if ( !bool ) {
        throw new Error( mess );
    }
};
light.assertNormalized = function( v, mess ) {
    light.assert( Math.abs( v.magnitude() - 1 ) < light.EPSILON, mess + " not normalized" );
};