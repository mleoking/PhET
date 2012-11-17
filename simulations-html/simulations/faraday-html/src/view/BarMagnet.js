// Copyright 2002-2012, University of Colorado

define( [ 'image!../../data/faraday-html/barMagnet.png'], function ( barMagnetImage ) {

    // create a bitmap display object
    var barMagnet = createjs.BitMap( barMagnetImage );

    // move registration point to the center
    barMagnet.regX = 30; //TODO half the width
    barMagnet.regY = 30; //TODO half the height

    return barMagnet;
} );
