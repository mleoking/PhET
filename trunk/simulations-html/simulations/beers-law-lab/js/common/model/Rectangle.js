// Copyright 2002-2013, University of Colorado

define( [], function () {

    function Rectangle( x, y, width, height ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    Rectangle.prototype.getMaxX = function() {
        return this.x + this.width;
    }

    Rectangle.prototype.getCenterX = function () {
        return this.x + this.width / 2;
    }

    Rectangle.prototype.getMaxY = function () {
        return this.y + this.height;
    }

    Rectangle.prototype.getCenterY = function () {
        return this.y + this.height / 2;
    }

    Rectangle.prototype.contains = function( x, y ) {
        return ( x >= this.x && x <= this.getMaxX() && y >= this.y && y <= this.getMaxY() );
    }

    return Rectangle;
} );
