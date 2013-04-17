light.Ray2 = function( pos, dir ) {
    this.pos = pos;
    this.dir = dir.normalized();
};

light.Ray2.prototype.withDistance = function( distance ) {
    return this.pos.add( this.dir.mul( distance ) );
};

light.Ray2.prototype.toString = function() {
    return this.pos + " => " + this.dir;
};
