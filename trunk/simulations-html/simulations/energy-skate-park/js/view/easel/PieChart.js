define( ['easel' ], function ( createjs ) {
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
        this.shape = shape;
        this.addChild( shape );
    };

    p.tick = function () {
        this.x = this.skater.x;

        var delta = 250;
        this.y = this.skater.y - delta;

        var kineticEnergy = this.skater.getKineticEnergy();
        var potentialEnergy = this.skater.getPotentialEnergy();
        var thermalEnergy = this.skater.getThermalEnergy();
        var totalEnergy = this.skater.getTotalEnergy();
        var pieRadius = totalEnergy / 100 * 1.5;

        var keAngle = kineticEnergy / totalEnergy * 2 * Math.PI;
        var peAngle = potentialEnergy / totalEnergy * 2 * Math.PI;
        var thermalAngle = thermalEnergy / totalEnergy * 2 * Math.PI;
        var keStartAngle = 0;
        var peStartAngle = keStartAngle + keAngle;
        var thermalStartAngle = peStartAngle + peAngle;
        var antiClockwise = false;

        this.shape.graphics.clear().
                beginFill( 'green' ).moveTo( 0, 0 ).lineTo( pieRadius * Math.cos( keStartAngle ), pieRadius * Math.sin( keStartAngle ) ).arc( 0, 0, pieRadius, keStartAngle, peStartAngle, antiClockwise ).lineTo( 0, 0 ).endFill().
                beginFill( 'blue' ).moveTo( 0, 0 ).lineTo( pieRadius * Math.cos( peStartAngle ), pieRadius * Math.sin( peStartAngle ) ).arc( 0, 0, pieRadius, peStartAngle, thermalStartAngle, antiClockwise ).lineTo( 0, 0 ).endFill().
                beginFill( 'red' ).moveTo( 0, 0 ).lineTo( pieRadius * Math.cos( thermalStartAngle ), pieRadius * Math.sin( thermalStartAngle ) ).arc( 0, 0, pieRadius, thermalStartAngle, 2 * Math.PI, antiClockwise ).lineTo( 0, 0 ).endFill().
                beginStroke( 'black' ).drawCircle( 0, 0, pieRadius ).endStroke();
    };

    return PieChart;
} );