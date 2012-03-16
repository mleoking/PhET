/**
 * Construct a half-plane
 * @param normal Normal pointing outside of the half-plane
 * @param distance Distance to the edge (line) from the origin
 */
light.Halfplane = function( normal, distance ) {
    this.normal = normal;
    this.distance = distance;
};

light.HalfplaneFromNormalPoint = function( normal, point ) {
    throw new Error( "not implemented" );
};

light.HalfplaneFromTwoPoints = function( a, b ) {
    throw new Error( "not implemented" );
};

light.HalfplaneHit = function( distance, hitPoint, normal, fromOutside ) {
    this.distance = distance;
    this.hitPoint = hitPoint;
    this.normal = normal;
    this.fromOutside = fromOutside;
};

light.Halfplane.prototype.toString = function() {
    return "halfplane(normal:" + this.normal + ",distance:" + this.distance + ")";
};

light.Halfplane.prototype.isPointInside = function( point ) {
    return point.sub( this.normal.mul( this.distance ) ).dot( this.normal ) < 0;
};

light.Halfplane.prototype.intersect = function( ray ) {
    var vx = ray.dir.dot( normal );
    if ( Math.abs( vx ) < light.EPSILON * 2 ) {
        // too close to parallel with the plane. consider it not a hit
        return null;
    }
    var t = (this.distance - ray.pos.dot( this.normal ) ) / vx;
    if ( t < light.EPSILON * 2 ) {
        // halfplane behind ray
        return null;
    }
    if ( vx > 0 ) {
        // hit from inside
        return new light.HalfplaneHit( t, ray.withDistance( t ), this.normal.negated(), false );
    }
    else {
        // hit from outside
        return new light.HalfplaneHit( t, ray.withDistance( t ), this.normal, true );
    }
};

light.HalfplaneHit.prototype.toString = function() {
    return "Hit(dist:" + this.distance + ",hit:" + this.hitPoint + ",norm:" + this.normal + ",outside:" + this.
            fromOutside + ")";
};