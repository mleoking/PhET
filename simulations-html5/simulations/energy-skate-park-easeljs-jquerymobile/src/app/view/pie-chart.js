define( ["skater"], function ( Skater ) {
    var PieChart = function ( skater ) {
        this.initialize( skater );
    };
    var p = PieChart.prototype = new createjs.Container(); // inherit from Container

    p.Container_initialize = p.initialize;

    p.initialize = function ( skater ) {
        this.Container_initialize();
        this.skater = skater;
        var shape = new createjs.Shape();
        shape.graphics.beginStroke( "orange" ).setStrokeStyle( 5 ).moveTo( -10, -10 ).lineTo( 10, 10 ).moveTo( -10, 10 ).lineTo( 10, -10 ).endStroke();
        this.addChild( shape );
//        this.tick();
    };

    p.tick = function () {
        this.x = this.skater.x;
        this.y = this.skater.y;
    };

    return PieChart;
} );