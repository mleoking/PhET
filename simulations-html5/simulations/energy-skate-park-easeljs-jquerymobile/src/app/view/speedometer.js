define( [], function () {
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

        function pressHandler( e ) {
            //Make dragging relative to touch point
            var relativePressPoint = null;
            e.onMouseMove = function ( event ) {
                var transformed = event.target.parent.globalToLocal( event.stageX, event.stageY );
                if ( relativePressPoint === null ) {
                    relativePressPoint = {x: e.target.x - transformed.x, y: e.target.y - transformed.y};
                }
                else {
                    e.target.x = transformed.x + relativePressPoint.x;
                    e.target.y = transformed.y + relativePressPoint.y;
//                    console.log( e.target.x + ', ' + e.target.y );
                }
            };
            e.onMouseUp = function ( event ) { };
        }

        that.onMouseOver = function () { document.body.style.cursor = "pointer"; };
        that.onMouseOut = function () { document.body.style.cursor = "default"; };

        that.onPress = pressHandler;

        that.x = 512;
        that.y = 160;

        return that;
    }};
} );