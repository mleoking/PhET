define( ['../../.'], function () {
    Object.__proto__.extend = function ( className, subclassMethods ) {
        var subclass = function () {
            this.initialize.apply( this, arguments );
        };

        var p = subclass.prototype = new this();
        p.className = className; // experimental debugging aid; may not actually be useful -PPC
        var superInit = p.initialize || function () {};

        // Add subclass methods
        _.extend( p, subclassMethods );

        // Pass superInit as first arg to initialize()
        var init = subclassMethods.initialize;
        if ( init ) {
            p.initialize = function () {
                init.apply( this, [superInit.bind( this )].concat( Array.prototype.slice.call( arguments ) ) );
            };
        }

        return subclass;
    }
} )