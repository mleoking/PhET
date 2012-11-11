define( ["view/easel-util"], function ( EaselUtil ) {
    return {createSpeedometer: function ( skater ) {
        var that = new createjs.Container();
        var shape = new createjs.Shape();
        shape.graphics.beginFill( "white" ).beginStroke( "black" ).setStrokeStyle( 2 ).drawCircle( 0, 0, 100 ).endStroke().endFill();
        that.shape = shape;
        that.addChild( shape );

        var energyLabel = new createjs.Text( 'Speed (m/s)', '24px "Arial",Tahoma', 'blue' );
        energyLabel.x = -energyLabel.getMeasuredWidth() / 2;
        energyLabel.y = -energyLabel.getMeasuredLineHeight() / 2;
        energyLabel.shadow = new createjs.Shadow( 'black', 1, 1, 2 );
        that.addChild( energyLabel );

        EaselUtil.makeDraggable( that );

        that.x = 512;
        that.y = 160;

        return that;
    }};
} );