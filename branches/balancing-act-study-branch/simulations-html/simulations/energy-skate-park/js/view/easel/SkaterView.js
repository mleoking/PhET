define( ['easel', "model/vector2d", 'view/easel/SkaterImageBase64' ], function ( createjs, Vector2D, skaterImage ) {
    return {createSkater: function ( skaterModel, groundHeight, groundY, analytics ) {

        var metersPerPixel = 8.0 / 768.0;

        function showPointer( mouseEvent ) { document.body.style.cursor = "pointer"; }

        function showDefault( mouseEvent ) { document.body.style.cursor = "default"; }

        function setCursorHand( displayObject ) {
            displayObject.onMouseOver = showPointer;
            displayObject.onMouseOut = showDefault;
        }

        var skater = new createjs.Bitmap( skaterImage );
        skater.model = skaterModel;
        skaterModel.attachmentVelocity = 0.0;
        setCursorHand( skater );
        //put registration point at bottom center of the skater
        skater.regX = skaterImage.width / 2;
        skater.regY = skaterImage.height;
        skater.velocity = new Vector2D( 0, 0 );
        var scaleFactor = 0.65;
        skater.scaleX = scaleFactor;
        skater.scaleY = scaleFactor;

        function pressHandler( e ) {
            e.nativeEvent.preventDefault();
            skaterModel.dragging = true;
            skaterModel.attached = false;
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

                    //don't let the skater go below ground

                    //Convert from view to model coordinates
                    skaterModel.position.y = -(e.target.y - groundY) * metersPerPixel;
                    skaterModel.position.x = (e.target.x) * metersPerPixel;

                    if ( skaterModel.position.y < 0 ) {
                        skaterModel.position.y = 0;
                    }
                }
                skaterModel.dragging = true;
                skaterModel.velocity = new Vector2D();
            };
            e.onMouseUp = function ( event ) {
                skaterModel.dragging = false;
                skaterModel.velocity = new Vector2D();
                analytics.log( "skater", "sprite", "released", [
                    {y: skaterModel.position.y.toFixed( 2 )}
                ] );
            };
        }

        skater.onPress = pressHandler;

        skater.getKineticEnergy = skaterModel.getKineticEnergy;
        skater.getPotentialEnergy = skaterModel.getPotentialEnergy;
        skater.getThermalEnergy = skaterModel.getThermalEnergy;
        skater.getTotalEnergy = skaterModel.getTotalEnergy;

        skater.updateFromModel = function () {
            this.x = skaterModel.position.x / metersPerPixel;
            this.y = -skaterModel.position.y / metersPerPixel + groundY;
            this.rotation = skaterModel.angle * 360 / 2 / Math.PI;
        };

        skater.updateFromModel();

        return skater;
    }};
} );