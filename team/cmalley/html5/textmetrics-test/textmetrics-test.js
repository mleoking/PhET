// Copyright 2002-2012, University of Colorado

/**
 * Rendering test to see how closely we can determine the bounds of Canvas text.
 *
 * Conclusions: This approximation is probably adequate for PhET's needs, but:
 *
 * - the results are better with some fonts (eg, Arial) than others (eg Comic Sans)
 * - results vary by browser (hugely with some fonts, eg Comic Sans
 * - textBaseline property works differently in different browsers, especially when using "hanging" or "ideographic"
 * - textBaseline is supported by IE9, Firefox, Opera, Chrome, and Safari
 *
 * @author Chris Malley (PixelZoom, Inc)
 */
var renderText = function ( string, font, context, x, y ) {

    context.save();

    // text style and color
    context.font = font;
    context.fillStyle = "black";

    // fill and stroke the text
    context.textAlign = "left";
    context.textBaseline = "top";
    context.fillText( string, x, y );
    context.strokeText( string, x, y );

    // draw a rectangle around the approximate bounds
    var textWidth = context.measureText( string ).width;
    var textHeight = 1.2 * context.measureText( "M" ).width
    console.log( "textWidth=" + textWidth + " textHeight=" + textHeight );

    context.strokeStyle = "red";
    context.rect( x, y, textWidth, textHeight );
    context.stroke();

    context.restore();
}
// text to render

var context = document.getElementById( 'canvas' ).getContext( '2d' );
renderText( "The quick brown fox jumps over the lazy dog", '28pt Comic Sans MS', context, 10, 10 );
renderText( "AgYpMa", '32pt Arial', context, 10, 60 );
renderText( "軌道遊樂場", '32pt Arial', context, 10, 120 ); // traditional Chinese (zh_TW)
renderText( "الطاقة ضد الزمن", '32pt Arial', context, 10, 180 ); // Arabic (ar)


