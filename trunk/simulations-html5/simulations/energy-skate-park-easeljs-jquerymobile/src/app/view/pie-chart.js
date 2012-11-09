define( ["skater"], function ( Skater ) {
    var PieChart = function ( skater ) {
        this.initialize( skater );
    };
    var p = PieChart.prototype = new createjs.Container(); // inherit from Container

    p.Container_initialize = p.initialize;
    p.initialize = function ( skater ) {
        this.Container_initialize();
        var shape = new createjs.Shape();
        shape.graphics.beginStroke( "orange" ).setStrokeStyle( 5 ).moveTo( -10, -10 ).lineTo( 10, 10 ).moveTo( -10, 10 ).lineTo( 10, -10 ).endStroke();
        this.addChild( shape );

        var container = this;

        //uses object.watch polyfill
        var update = function ( id, oldValue, newValue ) {
//            console.log( "value changed: id  = " + id + ", old = " + oldValue + ", new = " + newValue );
            container.x = skater.x;
            container.y = skater.y;
            return newValue;
        };
        skater.watch( "x", update );
        skater.watch( "y", update );
    };

    return PieChart;
} );