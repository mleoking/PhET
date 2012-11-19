define( [
            'image!resources/house.png',
            'image!resources/mountains.png'], function ( houseImage, mountainImage ) {
//    var houseImage = skaterImage;
//    var mountainImage = skaterImage;
    var result = {createBackground: function ( groundHeight ) {
        var groundGraphics = new createjs.Graphics();
        groundGraphics.beginFill( "#64aa64" );

        var extentOutsideOfNominalBounds = 1000;
        groundGraphics.rect( -extentOutsideOfNominalBounds, 768 - groundHeight, 1024 + 2 * extentOutsideOfNominalBounds, groundHeight + 500 );
        var ground = new createjs.Shape( groundGraphics );

        var skyGraphics = new createjs.Graphics();
        skyGraphics.beginLinearGradientFill( ["#7cc7fe", "#ffffff"], [0, 1], 0, -500, 0, 768 - groundHeight );
        skyGraphics.rect( -extentOutsideOfNominalBounds, -500, 1024 + 2 * extentOutsideOfNominalBounds, 768 - groundHeight + 500 );
        var sky = new createjs.Shape( skyGraphics );

        var background = new createjs.Container();
        background.addChild( sky );
        background.addChild( ground );

        background.addChild( createjs.Bitmap.create( {image: houseImage,
                                                         x: 800,
                                                         y: 768 - groundHeight - houseImage.height
                                                     } )
        );
        var mountainScale = 0.43;
        background.addChild( createjs.Bitmap.create( {image: mountainImage,
                                                         x: -50,
                                                         y: 768 - groundHeight - mountainImage.height * mountainScale,
                                                         scaleX: mountainScale,
                                                         scaleY: mountainScale
                                                     } )
        );

        //Cache as an image
//        background.cache( 0, 0, 1024, 768 );
        return background;
    }};
    return result;
} );