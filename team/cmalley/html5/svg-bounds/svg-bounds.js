// Copyright 2002-2012, University of Colorado

/**
 * Demonstrates using Canvas Image to load and render SVG.
 * This allows us to get the bounds of the SVG object.
 * This might be an approach for getting bounds for shapes and text.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
var canvas = document.getElementById( "canvas" );
var context = canvas.getContext( '2d' );
var img = new Image();
img.src = "rectangle.svg";
console.log( "width=" + img.width + " height=" + img.height );
var x = 50;
var y = 50;
context.drawImage( img, x, y );
context.strokeStyle = 'red';
context.rect( x, y, img.width, img.height );
context.stroke();


