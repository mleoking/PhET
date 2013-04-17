light.Circle = function( center, radius ) {
    this.center = center;
    this.radius = radius;
};

light.CircleHit = function( distance, hitPoint, normal, fromOutside ) {
    this.distance = distance;
    this.hitPoint = hitPoint;
    this.normal = normal;
    this.fromOutside = fromOutside;
};

light.Circle.prototype.toString = function() {
    return "circle(center:" + this.center + ",radius:" + this.radius + ")";
};

light.Circle.prototype.isPointInside = function( point ) {
    return point.sub( this.center ).magnitude() < this.radius;
};

/**
 * Returns a CircleHit, or null
 * @param ray Ray2 of the current light ray
 */
light.Circle.prototype.intersect = function( ray ) {
    var EPSILON = light.EPSILON;
    var centerToRay = ray.pos.sub( this.center );
    var tmp = ray.dir.dot( centerToRay );
    var centerToRayDistSq = centerToRay.magnitudeSq();
    var det = 4 * tmp * tmp - 4 * (centerToRayDistSq - this.radius * this.radius);
    if ( det < EPSILON ) {
        // ray misses circle entirely
        return null;
    }
    var base = ray.dir.dot( this.center ) - ray.dir.dot( ray.pos );
    var sqt = Math.sqrt( det ) / 2;
    var ta = base - sqt;
    var tb = base + sqt;

    if ( tb < EPSILON ) {
        // circle is behind ray
        return null;
    }

    var pb = ray.withDistance( tb );
    var nb = pb.sub( this.center ).normalized();

    if ( ta < EPSILON ) {
        // we are inside the circle
        // in => out
        return new light.CircleHit( tb, pb, nb.negated(), false );
    }
    else {
        // two possible hits (outside circle)
        var pa = ray.withDistance( ta );
        var na = pa.sub( this.center ).normalized();

        // close hit, we have out => in
        return new light.CircleHit( ta, pa, na, true );
    }
};

light.CircleHit.prototype.toString = function() {
    return "Hit(dist:" + this.distance + ",hit:" + this.hitPoint + ",norm:" + this.normal + ",outside:" + this.
            fromOutside + ")";
};
