// canvas and context
var canvas = document.getElementById( 'canvas' ),
        context = canvas.getContext( '2d' );

// text to render
var myString = "Hello World";

// text style and color
context.font = '40pt Arial';
context.fillStyle = 'red';
context.strokeStyle = 'black';

// center in the canvas
context.textAlign = 'center';
context.textBaseline = 'middle';
var x = ( canvas.width / 2 ),
        y = ( canvas.height / 2 );

// fill and stroke the text
context.fillText( myString, x, y );
context.strokeText( myString, x, y );