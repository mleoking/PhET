// Copyright 2002-2012, University of Colorado
define( [
            'easel'
        ], function ( Easel ) {

    function showPointer( mouseEvent ) { document.body.style.cursor = "pointer"; }
    function showDefault( mouseEvent ) { document.body.style.cursor = "default"; }

    function pressHandler( e ) {
        //Make dragging relative to touch point
        var relativePressPoint = null;
        e.onMouseMove = function ( event ) {
            var transformed = event.target.parent.globalToLocal( event.stageX, event.stageY );
            if ( relativePressPoint === null ) {
                relativePressPoint = {x:e.target.x - transformed.x, y:e.target.y - transformed.y};
            }
            else {
                e.target.x = transformed.x + relativePressPoint.x;
                e.target.y = transformed.y + relativePressPoint.y;
            }
        };
    }

    function createParticleView( particle ) {
        var particleView = new Easel.Shape();
        particleView.graphics.beginFill( "red" ).drawCircle( particle.xPos, particle.yPos, particle.radius ).endFill();
        particleView.onMouseOver = showPointer;
        particleView.onMouseOut = showDefault;
        particleView.onPress = pressHandler;
        return particleView;
    }



    var ParticleView = {};
    ParticleView.createParticleView = createParticleView;
    return ParticleView;

} );
