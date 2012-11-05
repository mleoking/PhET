define( ["vector2d"], function ( Vector2D ) {
    return {create:function ( image, groundHeight ) {
        var skater = new createjs.Bitmap( image );
        skater.mass = 50;//kg
        //put registration point at bottom center of the skater
        skater.regX = image.width / 2;
        skater.regY = image.height;
        skater.x = 100;
        skater.y = 20;
        skater.velocity = new Vector2D( 0, 0 );
        var scaleFactor = 0.65;
        skater.scaleX = scaleFactor;
        skater.scaleY = scaleFactor;

        function pressHandler( e ) {
            skater.dragging = true;
            skater.attached = false;
            //Make dragging relative to touch point
            var relativePressPoint = null;
            e.onMouseMove = function ( event ) {
                var transformed = event.target.parent.globalToLocal( event.stageX, event.stageY );
                if ( relativePressPoint === null ) {
                    relativePressPoint = {x:e.target.x - transformed.x, y:e.target.y - transformed.y};
                }
                else {
                    e.target.x = transformed.x + relativePressPoint.x;

                    //don't let the skater go below ground
                    e.target.y = Math.min( transformed.y + relativePressPoint.y, 768 - groundHeight );
//                    console.log( e.target.y );
                }
                skater.dragging = true;
            };
            e.onMouseUp = function ( event ) {
                skater.dragging = false;
                skater.velocity = new Vector2D();
            };
        }

        skater.onPress = pressHandler;

        return skater;
    }};
} );