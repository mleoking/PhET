// Copyright 2002-2012, University of Colorado
define( [
            'easel'
        ], function ( Easel ) {

    function showPointer( mouseEvent ) { document.body.style.cursor = "pointer"; }
    function showDefault( mouseEvent ) { document.body.style.cursor = "default"; }

    function createParticleView( particle ) {
        var particleView = new Easel.Shape();
        particleView.graphics.beginFill( "red" ).drawCircle( particle.xPos, particle.yPos, particle.radius ).endFill();
        particleView.onMouseOver = showPointer;
        particleView.onMouseOut = showDefault;
        return particleView;
    }

    var ParticleView = {};
    ParticleView.createParticleView = createParticleView;
    return ParticleView;

} );
