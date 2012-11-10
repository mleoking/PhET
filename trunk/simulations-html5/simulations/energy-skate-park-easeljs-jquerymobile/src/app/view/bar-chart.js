define( ['image!resources/close-button.png'], function ( closeButtonImage ) {
    return {createBarChart: function ( skater ) {
        var that = new createjs.Container();
        var shape = new createjs.Shape();
        shape.graphics.beginFill( "white" ).beginStroke( "black" ).setStrokeStyle( 2 ).drawRoundRect( 0, 0, 200, 600, 10 ).endStroke();
        that.shape = shape;
        that.addChild( shape );

        var energyLabel = new createjs.Text( 'Energy (J)', '24px "Arial",Tahoma', 'blue' );
        energyLabel.x = 10;
        energyLabel.y = 10;
        energyLabel.shadow = new createjs.Shadow( 'black', 1, 1, 2 );
        that.addChild( energyLabel );

        var yAxisArrow = new createjs.Shape();
        var arrowX = 34;
        yAxisArrow.graphics.beginStroke( 'black' ).setStrokeStyle( 4 ).moveTo( arrowX, 500 ).lineTo( arrowX, 40 ).endStroke();
        var dx = arrowX;
        var dy = 40 - 3;
        yAxisArrow.graphics.beginFill( 'black' ).moveTo( 0 + dx, 0 + dy ).lineTo( 7 + dx, 10 + dy ).lineTo( -7 + dx, 10 + dy ).lineTo( 0 + dx, 0 + dy ).endFill();
        that.addChild( yAxisArrow );

        var xAxis = new createjs.Shape();
        xAxis.graphics.beginStroke( 'red' ).setStrokeStyle( 4 ).moveTo( arrowX, 500 ).lineTo( arrowX + 150, 500 ).endStroke();
        that.addChild( xAxis );

        var closeButton = new createjs.Bitmap( closeButtonImage );
        closeButton.x = 200 - closeButtonImage.width - 10;
        closeButton.y = 10;
        that.addChild( closeButton );

        that.onMouseOver = function () { document.body.style.cursor = "pointer"; };
        that.onMouseOut = function () { document.body.style.cursor = "default"; };

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
                }
            };
            e.onMouseUp = function ( event ) {
            };
        }

        that.onPress = pressHandler;

        return that;
    }};
} );