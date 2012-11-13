// Copyright 2002-2012, University of Colorado

/*
 * Renders text in the center of the canvas.
 */
function renderCenteredText( text, context ) {

    context.save();

    // text style and color
    context.font = '40pt Arial';
    context.fillStyle = 'red';
    context.strokeStyle = 'black';

    // center in the canvas
    context.textAlign = 'center';
    context.textBaseline = 'middle';
    var centerX = ( canvas.width / 2 ),
            centerY = ( canvas.height / 2 );

    // fill and stroke the text
    context.fillText( text, centerX, centerY );
    context.strokeText( text, centerX, centerY );

    context.restore();
}