// Copyright 2002-2012, University of Colorado

define( [ 'easel', 'image!resources/images/barMagnet.png', 'common/easel-util' ], function ( Easel, barMagnetImage, EaselUtil ) {

    function createBarMagnet() {
        // create a bitmap display object
        var barMagnet = new Easel.Bitmap( barMagnetImage );

        // move registration point to the center
        barMagnet.regX = barMagnet.image.width / 2;
        barMagnet.regY = barMagnet.image.height / 2;

//        EaselUtil.makeDraggable( barMagnet );

        barMagnet.onMouseOver = function () { document.body.style.cursor = "pointer"; };
        barMagnet.onMouseOut = function () { document.body.style.cursor = "default"; };

        return barMagnet;
    }

    return createBarMagnet;
} );
