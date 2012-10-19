define( ["concentration"], function () {

    function vbox( distanceBetweenItems, items ) {

        var maxItemWidth = 0;
        var maxItemHeight = 0;
        var sumHeight = 0;
        for ( var z = 0; z < items.length; z++ ) {
            if ( z != 0 ) {
                sumHeight = sumHeight + distanceBetweenItems;
            }
            maxItemWidth = Math.max( maxItemWidth, items[z].width );
            maxItemHeight = Math.max( maxItemHeight, items[z].height );
            sumHeight = sumHeight + items[z].height;
        }

        var container = new CAAT.ActorContainer().setSize( maxItemWidth, sumHeight );

        var y = 0;
        for ( var k = 0; k < items.length; k++ ) {
            items[k].setLocation( maxItemWidth / 2 - items[k].width / 2, y );
            container.addChild( items[k] );
            y += items[k].height + distanceBetweenItems;
        }

        return container;
    }

    function createSegmentedButton( buttonContents, model ) {

        var maxItemWidth = 0;
        var maxItemHeight = 0;
        for ( var z = 0; z < buttonContents.length; z++ ) {
            maxItemWidth = Math.max( maxItemWidth, buttonContents[z].width );
            maxItemHeight = Math.max( maxItemHeight, buttonContents[z].height );
            buttonContents[z].enableEvents( false );
        }

        var leftRightInset = 10;
        var distanceBetweenButtons = 20;
        var width = leftRightInset + maxItemWidth * buttonContents.length + distanceBetweenButtons * (buttonContents.length - 1) + leftRightInset;

        var container = new CAAT.ActorContainer().setSize( width, 65 );

        var background = new CAAT.Actor().setSize( container.width, container.height ).enableEvents( false );
        background.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.strokeStyle = 'black';
            ctx.lineWidth = 1;
            ctx.fillStyle = model.solidSelected ? 'rgb(200,200,200)' : 'blue';
            roundRectRight( ctx, 0, 0, container.width, container.height, 20, true, true );
            ctx.fillStyle = model.solidSelected ? 'blue' : 'rgb(200,200,200)';
            roundRectLeft( ctx, 0, 0, container.width, container.height, 20, true, true );
            ctx.beginPath();
            ctx.moveTo( container.width / 2, 0 );
            ctx.lineTo( container.width / 2, container.height );
            ctx.closePath();
            ctx.stroke();
        };
        container.addChild( background );

        var x = leftRightInset;
        for ( var k = 0; k < buttonContents.length; k++ ) {
            x += maxItemWidth / 2;
            buttonContents[k].setLocation( x - buttonContents[k].width / 2, container.height / 2 - buttonContents[k].height / 2 );
            container.addChild( buttonContents[k] );
            x += distanceBetweenButtons;
            x += maxItemWidth / 2;
        }

        return container;
    }

    function roundRectLeft( ctx, x, y, width, height, radius, fill, stroke ) {
        if ( typeof stroke == "undefined" ) {
            stroke = true;
        }
        if ( typeof radius === "undefined" ) {
            radius = 5;
        }
        ctx.beginPath();
        ctx.moveTo( x + radius, y );
        ctx.lineTo( x + width / 2, y );
        ctx.lineTo( x + width / 2, y + height );
        ctx.lineTo( x + radius, y + height );
        ctx.quadraticCurveTo( x, y + height, x, y + height - radius );
        ctx.lineTo( x, y + radius );
        ctx.quadraticCurveTo( x, y, x + radius, y );
        ctx.closePath();
        if ( stroke ) {
            ctx.stroke();
        }
        if ( fill ) {
            ctx.fill();
        }
    }

    function roundRectRight( ctx, x, y, width, height, radius, fill, stroke ) {
        if ( typeof stroke == "undefined" ) {
            stroke = true;
        }
        if ( typeof radius === "undefined" ) {
            radius = 5;
        }
        ctx.beginPath();
        ctx.moveTo( x + width / 2, y );
        ctx.lineTo( x + width - radius, y );
        ctx.quadraticCurveTo( x + width, y, x + width, y + radius );
        ctx.lineTo( x + width, y + height - radius );
        ctx.quadraticCurveTo( x + width, y + height, x + width - radius, y + height );
        ctx.lineTo( x + width / 2, y + height );
        ctx.lineTo( x + width / 2, y );
        ctx.closePath();
        if ( stroke ) {
            ctx.stroke();
        }
        if ( fill ) {
            ctx.fill();
        }
    }

    /**
     * http://stackoverflow.com/questions/1255512/how-to-draw-a-rounded-rectangle-on-html-canvas
     * Draws a rounded rectangle using the current state of the canvas.
     * If you omit the last three params, it will draw a rectangle
     * outline with a 5 pixel border radius
     * @param {CanvasRenderingContext2D} ctx
     * @param {Number} x The top left x coordinate
     * @param {Number} y The top left y coordinate
     * @param {Number} width The width of the rectangle
     * @param {Number} height The height of the rectangle
     * @param {Number} radius The corner radius. Defaults to 5;
     * @param {Boolean} fill Whether to fill the rectangle. Defaults to false.
     * @param {Boolean} stroke Whether to stroke the rectangle. Defaults to true.
     */
    function roundRect( ctx, x, y, width, height, radius, fill, stroke ) {
        if ( typeof stroke == "undefined" ) {
            stroke = true;
        }
        if ( typeof radius === "undefined" ) {
            radius = 5;
        }
        ctx.beginPath();
        ctx.moveTo( x + radius, y );
        ctx.lineTo( x + width - radius, y );
        ctx.quadraticCurveTo( x + width, y, x + width, y + radius );
        ctx.lineTo( x + width, y + height - radius );
        ctx.quadraticCurveTo( x + width, y + height, x + width - radius, y + height );
        ctx.lineTo( x + radius, y + height );
        ctx.quadraticCurveTo( x, y + height, x, y + height - radius );
        ctx.lineTo( x, y + radius );
        ctx.quadraticCurveTo( x, y, x + radius, y );
        ctx.closePath();
        if ( stroke ) {
            ctx.stroke();
        }
        if ( fill ) {
            ctx.fill();
        }
    }
} );