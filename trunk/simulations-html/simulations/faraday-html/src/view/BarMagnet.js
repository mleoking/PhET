// Copyright 2002-2012, University of Colorado

define( [ 'easel', 'image!resources/images/barMagnet.png', 'common/easel-util' ], function ( Easel, barMagnetImage, EaselUtil ) {

    function createBarMagnet() {
        // create a bitmap display object
        var barMagnet = new Easel.Bitmap( barMagnetImage );

        // move registration point to the center
        barMagnet.regX = barMagnet.image.width / 2;
        barMagnet.regY = barMagnet.image.height / 2;

        EaselUtil.makeDraggable( barMagnet );

        return barMagnet;
    }

    return createBarMagnet;
} );
