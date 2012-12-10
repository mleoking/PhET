define( ['easel', 'view/easel-create',
            'image!images/house.png',
            'image!images/mountains.png'],
        function ( createjs, EaselExtensions, houseImage, mountainImage ) {
//    var houseImage = skaterImage;
//    var mountainImage = skaterImage;
            var result = {createBackground: function ( groundHeight ) {
                var background = new createjs.Container();
                var extentOutsideOfNominalBounds = 1000;

                background.addChild( createjs.Shape.create( {graphics: function ( g ) {
                    g.beginLinearGradientFill( ["#7cc7fe", "#ffffff"], [0, 1], 0, -500, 0, 768 - groundHeight )
                            .rect( -extentOutsideOfNominalBounds, -500, 1024 + 2 * extentOutsideOfNominalBounds, 768 - groundHeight + 500 );
                }} ) );

                background.addChild( createjs.Shape.create( {graphics: function ( g ) {
                    g.beginFill( "#64aa64" ).rect( -extentOutsideOfNominalBounds, 768 - groundHeight, 1024 + 2 * extentOutsideOfNominalBounds, groundHeight + 500 );
                }} ) );

                background.addChild( createjs.Bitmap.create( {image: houseImage, x: 800, y: 768 - groundHeight - houseImage.height} ) );

                var mountainScale = 0.43;
                background.addChild( createjs.Bitmap.create( {
                                                                 image: mountainImage,
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