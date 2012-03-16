light.Vec2 = function( x, y ) {
    this.x = x;
    this.y = y;
};

light.Vec2.prototype.add = function( other ) {
    return new light.Vec2( this.x + other.x, this.y + other.y );
};

light.Vec2.prototype.sub = function( other ) {
    return new light.Vec2( this.x - other.x, this.y - other.y );
};

light.Vec2.prototype.mul = function( scalar ) {
    return new light.Vec2( this.x * scalar, this.y * scalar );
};

light.Vec2.prototype.toString = function() {
    return '[' + this.x + ',' + this.y + ']';
};

light.Vec2.prototype.magnitude = function() {
    return Math.sqrt( this.dot( this ) );
};

light.Vec2.prototype.magnitudeSq = function() {
    return this.dot( this );
};

light.Vec2.prototype.normalized = function() {
    var mag = this.magnitude();
    if ( mag == 0 ) {
        return this;
    }
    else {
        return new light.Vec2( this.x / mag, this.y / mag );
    }
};

light.Vec2.prototype.negated = function() {
    return new light.Vec2( -this.x, -this.y );
};

light.Vec2.prototype.dot = function( other ) {
    return this.x * other.x + this.y * other.y;
};

light.Vec2.prototype.angle = function() {
    return Math.atan2( this.y, this.x );
};

light.Vec2.prototype.perpendicular = function() {
    return new light.Vec2( this.y, -this.x );
};

light.Vec2.prototype.crossProductScalar = function( other ) {
    // unchecked? easier way?
    return this.magnitude() * other.magnitude() * Math.sin( this.angle() - other.angle() );
};