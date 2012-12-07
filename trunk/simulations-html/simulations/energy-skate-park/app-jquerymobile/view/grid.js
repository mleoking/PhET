define( ['easel', ], function ( createjs ) {

    //Copied from C:\workingcopy\phet\svn-1.7\trunk\simulations-html\simulations\faraday-html\src\common\Inheritance.js
    //TODO: update to point to common version when commonized.
    function inheritPrototype( subtype, supertype ) {
        var prototype = Object( supertype.prototype ); // create a clone of the supertype's prototype
        prototype.constructor = subtype; // account for losing the default constructor when prototype is overwritten
        subtype.prototype = prototype; // assign cloned prototype to subtype
    }

    function Grid( groundY ) {
        createjs.Container.call( this ); // constructor stealing, called second
        var shape = new createjs.Shape();
        shape.graphics.beginStroke( 'black' ).setStrokeStyle( 1 );
        var gridSpacing = 70;
        for ( var i = 0; i < 10; i++ ) {
            var y = groundY - gridSpacing * i;
            shape.graphics.moveTo( 0, y ).lineTo( 1024, y );
        }
        for ( i = 0; i < 15; i++ ) {
            var x = 0 + i * gridSpacing;
            shape.graphics.moveTo( x, 0 ).lineTo( x, groundY );
        }
        shape.graphics.endStroke();
        this.addChild( shape );

        //Cache the grid to get better performance, especially on iPad
        shape.cache( 0, 0, 1024, 768 );
    }

    inheritPrototype( Grid, createjs.Container );

    return Grid;
} );