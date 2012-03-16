light.reflect = function( incident, normal ) {
    light.assertNormalized( incident, "incident" );
    light.assertNormalized( normal, "normal" );

    var dot = incident.dot( normal );
    light.assert( dot < -light.EPSILON, "dot > -EPSILON" );
    var ret = normal.mul( 2 * dot ).sub( incident ).negated();
    light.assertNormalized( ret, "ret" );
    return ret;
};

light.transmit = function( incident, normal, na, nb ) {
    light.assertNormalized( incident, "incident" );
    light.assertNormalized( normal, "normal" );

    // check for TIR
    light.assert( !light.isTotalInternalReflection( incident, normal, na, nb ), "TIR for transmit!" );

    light.assert( na >= 1, "invalid na" );
    light.assert( nb >= 1, "invalid nb" );

    var q = na / nb;
    var dot = incident.dot( normal );
    light.assert( dot < -light.EPSILON, "dot > -EPSILON" );
    var t = normal.mul( q * dot + ( Math.sqrt( 1 - q * q * ( 1 - dot * dot ) ) ) );
    var ret = incident.mul( q ).sub( t );

    light.assertNormalized( ret, "ret" );
    return ret;
};

light.isTotalInternalReflection = function( incident, normal, na, nb ) {
    if ( na <= nb ) {
        return false;
    }
    var dot = -normal.dot( incident );
    light.assert( dot > light.EPSILON, "normal pointing wrong way" );

    var cosineTIRAngle = Math.sqrt( 1 - (nb / na) * (nb / na) );

    return dot < cosineTIRAngle + light.EPSILON;
};

/**
 * Reflectance (and thus transmission) for dielectric surfaces (IE glass)
 *
 * @param incident    Incident unit vector
 * @param normal      Normal unit vector
 * @param transmitted Transmitted unit vector
 * @param na          IOR from
 * @param nb          IOR to
 * @return An object with S and P reflectances, formatted as {s:..., p:...}
 */
light.fresnelDielectric = function( incident, normal, transmitted, na, nb ) {
    var ret = {};
    var doti = Math.abs( incident.dot( normal ) );
    var dott = Math.abs( transmitted.dot( normal ) );
    ret.s = ( na * doti - nb * dott ) / ( na * doti + nb * dott );
    ret.s *= ret.s;
    ret.p = ( na * dott - nb * doti ) / ( na * dott + nb * doti );
    ret.p *= ret.p;
    return ret;
};

light.findClosestHit = function( surfaces, ray ) {
    var closestHit = null;
    for ( var ob in surfaces ) {
        var surface = surfaces[ob];
        var hit = surface.intersect( ray );
        if ( hit != null && (closestHit == null || hit.distance < closestHit.distance) ) {
            closestHit = hit;
            closestHit.surface = surface; // record which surface for later use
        }
    }
    return closestHit;
};