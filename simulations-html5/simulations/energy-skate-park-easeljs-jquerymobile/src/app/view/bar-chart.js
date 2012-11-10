define( [], function () {
    return {createBarChart: function ( skater ) {
        var that = new createjs.Container();
        var shape = new createjs.Shape();
        shape.graphics.beginFill( "white" ).beginStroke( "black" ).setStrokeStyle( 2 ).drawRoundRect( 0, 0, 200, 600, 10 ).endStroke();
        that.shape = shape;
        that.addChild( shape );

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