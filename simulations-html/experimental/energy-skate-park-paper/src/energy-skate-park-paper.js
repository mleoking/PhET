(function () {

    //http://www.javascriptkit.com/javatutors/preloadimagesplus.shtml
    function preloadimages( a ) {
        var newimages = [], loadedimages = 0;
        var postaction = function () {};
        var arr = (typeof a != "object") ? [a] : a;

        function imageloadpost() {
            loadedimages++;
            if ( loadedimages == arr.length ) {
                postaction( newimages ); //call postaction and pass in newimages array as parameter
            }
        }

        for ( var i = 0; i < arr.length; i++ ) {
            newimages[i] = new Image();
            newimages[i].src = arr[i];
            newimages[i].onload = function () {
                imageloadpost()
            };
            newimages[i].onerror = function () {
                imageloadpost()
            };
        }
        return { //return blank object with done() method
            done:function ( f ) {
                postaction = f || postaction; //remember user defined callback functions to be called when images load
            }
        }
    }

    function run( images ) {
        console.log( "hello" );

        var canvas = document.createElement( "canvas" );

        // Create an empty project and a view for the canvas:
        paper.setup( canvas );

        var raster = new paper.Raster( images[0] );
        raster.scale( 0.5 );

        var rootNode = new paper.Group();
        rootNode.addChild( raster );
        var scaleFactor = 0.8;
        rootNode.selectedScaling = scaleFactor;
        rootNode.scale( scaleFactor );

        console.log( "started" );

        //Create the canvas and add to the document
        var ctx = canvas.getContext( "2d" );
        document.body.appendChild( canvas );

        //Don't allow the page to scroll up on ipad
        document.ontouchmove = function ( e ) {e.preventDefault()};

        var drag = [];

        function createCircle( x, y ) {
            var circle = new paper.Path.Circle( new paper.Point( x, y ), 10 );
            circle.fillColor = 'blue';
            rootNode.addChild( circle );
            return circle;
        }

        var controlCircles = [
            createCircle( 100, 100 ),
            createCircle( 200, 200 ),
            createCircle( 300, 100 )
        ];

        var hammer = new Hammer( canvas );
        hammer.ondragstart = function ( ev ) {};

        var skaterX = 0;
        var skaterY = 0;
        var skaterVelocityX = 0;
        var skaterVelocityY = 0;
        var skaterDragging = false;
        var draggingControlPoint = null;//null means not any

        document.onmousemove = function ( e ) {

            //How to tell if mouse is over something?
            if ( navigator.browser !== 'iOS' ) {
                document.body.style.cursor = 'pointer';
            }
        };

        //Disable text selection, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
        canvas.onselectstart = function () { return false; }; // ie
        canvas.onmousedown = function () { return false; }; // mozilla

        function distance( x1, y1, x2, y2 ) {
            var dx = x2 - x1;
            var dy = y2 - y1;
            return Math.sqrt( dx * dx + dy * dy );
        }

        var draggingItem = null;

        hammer.ondrag = function ( ev ) {
            drag = [];
            var touches = ev.originalEvent.touches || [ev.originalEvent];
            for ( var t = 0; t < touches.length; t++ ) {
                if ( t == 0 ) {

                    var touchX = ev.touches[t].x;
                    var touchY = ev.touches[t].y;

                    if ( draggingItem == null ) {
                        var result = rootNode.hitTest( new paper.Point( touchX, touchY )
//                                ,
//                                                       {fill:true, stroke:false, tolerance:1000}
                        );
                        console.log( result );
                        if ( result && result.item ) {
                            draggingItem = result.item;
                        }
                    }

                    if ( draggingItem != null ) {
                        draggingItem.setPosition( touchX / rootNode.selectedScaling, touchY / rootNode.selectedScaling );
                    }

//                    if ( !skaterDragging && draggingControlPoint == null ) {
//                        var distanceToSkater = distance( touchX, canvas.height - touchY, skaterX, skaterY );
//                        if ( distanceToSkater < 200 ) {
//                            skaterDragging = true;
//                        }
//                        else {
//                            for ( var i = 0; i < controlCircles.length; i++ ) {
//                                var distanceToControlPoint = distance( controlCircles[i].getPosition().x, controlCircles[i].getPosition().y, touchX, touchY );
//                                if ( distanceToControlPoint < 200 ) {
//                                    draggingControlPoint = i;
//                                }
//                            }
//                        }
//                    }

//                    if ( drag ) {
//                        if ( skaterDragging ) {
//                            skaterX = touchX;
//                            skaterY = canvas.height - touchY;
//
//                            skaterVelocityX = 0.0;
//                            skaterVelocityY = 0.0;
//                        }
//                        if ( draggingControlPoint >= 0 ) {
//                            controlCircles[draggingControlPoint].setPosition( touchX, touchY );
//                        }
//                    }
                }
            }
        };
        hammer.ondragend = function ( ev ) {draggingItem = null;};

        hammer.onswipe = function ( ev ) {};

        hammer.ontap = function ( ev ) {};
        hammer.ondoubletap = function ( ev ) {};
        hammer.onhold = function ( ev ) {};

        hammer.ontransformstart = function ( ev ) {};
        hammer.ontransform = function ( ev ) {};
        hammer.ontransformend = function ( ev ) {};

        hammer.onrelease = function ( ev ) {
            skaterDragging = false;
            draggingControlPoint = -1;
        };

        //or another game loop here: http://www.playmycode.com/blog/2011/08/building-a-game-mainloop-in-javascript/
        //or here: http://jsfiddle.net/Y9uBv/5/
        var requestAnimationFrame =
                requestAnimationFrame ||
                webkitRequestAnimationFrame ||
                mozRequestAnimationFrame ||
                msRequestAnimationFrame ||
                oRequestAnimationFrame;

        var blockX = 100;
        var lastTime = new Date().getTime();
        var deltas = [];

        function updatePhysics() {
            //free fall

            var dt = 1.0 / 60.0 * 10;

            var skaterAccelerationX = 0;
            var skaterAccelerationY = skaterY <= 0 || skaterDragging ? 0.0 : -9.8;
            skaterVelocityX = skaterVelocityX + skaterAccelerationX * dt;
            skaterVelocityY = skaterVelocityY + skaterAccelerationY * dt;
            skaterX = skaterX + skaterVelocityX * dt + 0.5 * skaterAccelerationX * dt * dt;
            skaterY = skaterY + skaterVelocityY * dt + 0.5 * skaterAccelerationY * dt * dt;

            if ( skaterY < 0 ) {
                skaterY = 0;
                skaterVelocityY = 0.0;
            }
        }

        function renderGraphics() {
            //http://stackoverflow.com/questions/1664785/html5-canvas-resize-to-fit-window
            ctx.canvas.width = window.innerWidth;
            ctx.canvas.height = window.innerHeight;

            var desiredAspectRatio = 1024.0 / 768.0;
            var actualAspectRatio = ctx.canvas.width / ctx.canvas.height;

            var widthLimited = actualAspectRatio > desiredAspectRatio;
            var scale = widthLimited ? ctx.canvas.height / 768 : ctx.canvas.width / 1024;

            ctx.fillStyle = 'black';
            ctx.fillRect( blockX, 100, 20, 20 );

            //Draw skater
            ctx.save();
            ctx.translate( skaterX, ctx.canvas.height - skaterY );
            ctx.fillStyle = 'red';
            ctx.fillRect( -2, -2, 4, 4 );
            ctx.scale( scale, scale );
//            ctx.drawImage( images[0], -images[0].width / 2, -images[0].height );
            ctx.restore();

            //Draw track
            ctx.save();
            ctx.strokeStyle = 'black';
            ctx.lineWidth = 2;

            function getX( point ) {return point.getPosition().x;}

            function getY( point ) {return point.getPosition().y;}

            var x = controlCircles.map( getX );
            var y = controlCircles.map( getY );
            var s = numeric.spline( x, y );

            //http://stackoverflow.com/questions/1669190/javascript-min-max-array-values
            var x0 = numeric.linspace( Math.min.apply( null, x ), Math.max.apply( null, x ), 1000 );
            ctx.beginPath();
            for ( var i = 0; i < x0.length; i++ ) {
                var a = x0[i];
                var b = s.at( x0[i] );
                if ( i == 0 ) {
                    ctx.moveTo( a, b );
                }
                else {
                    ctx.lineTo( a, b );
                }
            }
            ctx.stroke();
            ctx.restore();

            var currentTime = new Date().getTime();
            deltas.push( currentTime - lastTime );
            if ( deltas.length > 60 ) {
                deltas.splice( 0, 1 );
            }
            var sum = 0.0;
            for ( var i = 0; i < deltas.length; i++ ) {
                sum += deltas[i];
            }
            var delta = sum / deltas.length;
            var deltaSeconds = delta / 1000;
            var FPS = 1.0 / deltaSeconds;
            ctx.fillStyle = 'black';

            ctx.fillText( "FPS: " + FPS.toPrecision( 6 ), 100, 100 );

            lastTime = currentTime;

            ++blockX;

            raster.setPosition( {x:skaterX, y:ctx.canvas.height - skaterY - raster.getHeight() / 2} );

            paper.view.draw();
        }

        function loop() {
            updatePhysics();
            renderGraphics();
            requestAnimationFrame( loop );
        }

        requestAnimationFrame( loop );
    }

    // Only executed our code once the DOM is ready.
    window.onload = function () {
        preloadimages( "resources/skater.png" ).done( run )
    }
})();