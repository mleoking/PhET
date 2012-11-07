define( [
            'image!resources/house.png',
            'image!resources/mountains.png'], function ( h, m ) {
    var houseImage = h;
    var mountainImage = m;
    var result = {createBackground:function ( groundHeight ) {
        var groundGraphics = new createjs.Graphics();
        groundGraphics.beginFill( "#64aa64" );

        groundGraphics.rect( 0, 768 - groundHeight, 1024, groundHeight );
        var ground = new createjs.Shape( groundGraphics );

        var skyGraphics = new createjs.Graphics();
        skyGraphics.beginLinearGradientFill( ["#7cc7fe", "#ffffff"], [0, 1], 0, 0, 0, 768 - groundHeight );
        skyGraphics.rect( 0, 0, 1024, 768 - groundHeight );
        var sky = new createjs.Shape( skyGraphics );


        var background = new createjs.Container();
        background.addChild( sky );
        background.addChild( ground );
        var house = new createjs.Bitmap( houseImage );
        house.y = 768 - groundHeight - houseImage.height;
        house.x = 800;
        var mountain = new createjs.Bitmap( mountainImage );
        var mountainScale = 0.43;
        mountain.x = -50;
        mountain.y = 768 - groundHeight - mountainImage.height * mountainScale;
        mountain.scaleX = mountainScale;
        mountain.scaleY = mountainScale;
        background.addChild( mountain );
        background.addChild( house );
        return background;
    }};
    return result;
} );