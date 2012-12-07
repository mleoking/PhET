define( ['easel', "view/easel-util", "model/vector2d", 'i18n!../../../nls/energy-skate-park-strings'], function ( createjs, EaselUtil, Vector2D, Strings ) {
    return {createSpeedometer: function ( skater ) {
        var that = new createjs.Container();
        var shape = new createjs.Shape();
        var radius = 100;
        shape.graphics.beginFill( "white" ).beginStroke( "black" ).setStrokeStyle( 2 ).drawCircle( 0, 0, radius ).endStroke().endFill();
        that.shape = shape;
        that.addChild( shape );

        var energyLabel = new createjs.Text( Strings["properties.speed"], '24px "Arial",Tahoma', 'blue' );
        energyLabel.x = -energyLabel.getMeasuredWidth() / 2;
        energyLabel.y = -energyLabel.getMeasuredLineHeight() / 2 - 40;
        energyLabel.shadow = new createjs.Shadow( 'black', 1, 1, 2 );
        that.addChild( energyLabel );

        var ticks = new createjs.Shape();
        ticks.graphics.beginStroke( 'black' ).setStrokeStyle( 2 );
        //8 ticks makes 90 degrees
        var degreesPerTick = 90.0 / 8.0;

        function toDegrees( radians ) { return radians * 180 / Math.PI; }

        function toRadians( degrees ) { return degrees * Math.PI / 180;}

        for ( var i = 0; i <= 20; i++ ) {
            var angle = 0 + degreesPerTick * i;

            var tickLength = (i % 2 == 0) ? 16 : 8;
            var innerVector = Vector2D.fromAngle( toRadians( angle + 180 - degreesPerTick * 2 ) ).times( radius - tickLength );
            var vector = Vector2D.fromAngle( toRadians( angle + 180 - degreesPerTick * 2 ) ).times( radius );
            ticks.graphics.moveTo( innerVector.x, innerVector.y ).lineTo( vector.x, vector.y );
        }
        ticks.graphics.endStroke();

        var needle = new createjs.Shape();
        that.addChild( needle );
        that.addChild( ticks );

        var pin = new createjs.Shape();
        pin.graphics.beginFill( 'blue' ).drawCircle( 0, 0, 3 ).endFill();

        that.addChild( pin );

        EaselUtil.makeDraggable( that );

        that.x = 512;
        that.y = 160;

        that.tick = function () {
            var angle = skater.model.velocity.magnitude() * 25;

            var innerVector = Vector2D.fromAngle( toRadians( angle + 180 - degreesPerTick * 2 ) ).times( -20 );
            var vector = Vector2D.fromAngle( toRadians( angle + 180 - degreesPerTick * 2 ) ).times( radius );
            needle.graphics.clear().beginStroke( "red" ).setStrokeStyle( 4 ).moveTo( innerVector.x, innerVector.y ).lineTo( vector.x, vector.y );
        };

        return that;
    }};
} );