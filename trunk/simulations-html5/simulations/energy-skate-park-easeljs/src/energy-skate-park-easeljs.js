$( function () {

    //Listen for file changes for auto-refresh
    function listenForRefresh() {
        if ( "WebSocket" in window ) {
            // Let us open a web socket
            var ws = new WebSocket( "ws://localhost:8887/echo" );
            ws.onmessage = function ( event ) { document.location.reload( true ); };
            ws.onclose = function () { };
            console.log( "opened websocket" );
        }
        else {
            // The browser doesn't support WebSocket
            //            alert( "WebSocket NOT supported by your Browser!" );
            console.log( "WebSocket NOT supported by your Browser!" );
        }
    }

    function run( images ) {

        listenForRefresh();
        // Widgets

        var Slider = function ( opts ) {
            this.initialize( opts );
        };
        Slider.prototype = $.extend( new createjs.Container(), {
            initialize:function ( opts ) {
                opts = opts || {};
                this.min = opts.min;
                this.max = opts.max;
                this.size = opts.size;
                this.thumbSize = this.size.y / 2;
                this.trackWidth = this.size.x - this.size.y;
                this.onChange = opts.onChange || function ( value ) {};
                this.x = opts.pos.x;
                this.y = opts.pos.y;

                this.track = new createjs.Shape();
                this.track.graphics
                        .setStrokeStyle( this.size.y / 4, 1 /*round*/ )
                        .beginStroke( createjs.Graphics.getHSL( 192, 10, 80 ) )
                        .moveTo( this.thumbSize, 0 )
                        .lineTo( this.thumbSize + this.trackWidth, 0 );
                this.addChild( this.track );

                this.thumb = new createjs.Shape();
                this.thumb.graphics
                        .setStrokeStyle( this.size.y / 12 )
                        .beginStroke( createjs.Graphics.getHSL( 18, 50, 30 ) )
                        .beginFill( createjs.Graphics.getHSL( 18, 30, 90 ) )
                        .drawCircle( 0, 0, this.thumbSize );
                this.addChild( this.thumb );

                this.thumb.onPress = this.drag.bind( this );
                this.thumb.onMouseDrag = this.drag.bind( this );

                if ( opts.value ) {
                    this.setValue( opts.value )
                }
            },

            getValue:function () {
                return this._value
            },

            setValue:function ( value ) {
                if ( this.max && !(value && value < this.max) )  // This phrasing deals with NaN
                {
                    value = this.max;
                }
                if ( this.min && !(value && value > this.min) )  // max === null / undefined / NaN defaults to min
                {
                    value = this.min;
                }
                this._value = value;

                this.thumb.x = this.thumbSize + this.trackWidth * (value - this.min) / (this.max - this.min);

                this.onChange( value )
            },

            drag:function ( event ) {
                event.onMouseMove = this.drag.bind( this );
                var dragAtX = this.globalToLocal( event.stageX, event.stageY ).x;
                if ( event.type === 'onPress' ) {
                    this.dragOffset = this.thumb.x - this.thumbSize - dragAtX;
                }
                else {
                    this.setValue( (dragAtX + this.dragOffset) / this.trackWidth * (this.max - this.min) + this.min )
                }
            }
        } );

        var group = new createjs.Container();
        var fpsText = new createjs.Text( '-- fps', '24px "Lucida Grande",Tahoma', createjs.Graphics.getRGB( 153, 153, 230 ) );
        fpsText.x = 4;
        fpsText.y = 280;
        group.addChild( fpsText );
        var skater = new createjs.Bitmap( images[0] );

        function pressHandler( e ) {
            e.onMouseMove = function ( event ) {
                var transformed = event.target.parent.globalToLocal( event.stageX, event.stageY );
                e.target.x = transformed.x;
                e.target.y = transformed.y;
            }
        }

        skater.onPress = pressHandler;

        var groundGraphics = new createjs.Graphics();
        groundGraphics.setStrokeStyle( 1 );
//        groundGraphics.beginStroke( createjs.Graphics.getRGB( 0, 0, 0 ) );
//        groundGraphics.beginStroke( "#64aa64" );
        groundGraphics.beginFill( "#64aa64" );
        var groundHeight = 200;
        groundGraphics.rect( 0, 768 - groundHeight, 1024, groundHeight );
        var ground = new createjs.Shape( groundGraphics );

        var background = new createjs.Container();
        background.addChild( ground );

        group.addChild( background );
        group.addChild( skater );

        //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
        var canvas = document.getElementById( "c" );
        canvas.onselectstart = function () { return false; }; // ie
        canvas.onmousedown = function () { return false; }; // mozilla

        stage = new createjs.Stage( canvas );
        stage.addChild( group );
        stage.update();

        // Physics

        Vector = {
            add:function ( v, w ) {
                return { x:v.x + w.x,
                    y:v.y + w.y }
            },
            sub:function ( v, w ) {
                return { x:v.x - w.x,
                    y:v.y - w.y }
            },
            mult:function ( v, s ) {
                return { x:v.x * s,
                    y:v.y * s }
            },
            dot:function ( v, w ) {
                return v.x * w.x + v.y * w.y
            },
            length:function ( v ) {
                return Math.sqrt( Vector.dot( v, v ) )
            },
            normalize:function ( v ) {
                var length = Vector.length( v );
                return { x:v.x / length,
                    y:v.y / length }
            }
        };

        // UI

        var frameCount = 0,
                lastFrameRateUpdate = null;

        function displayFrameRate() {
            frameCount++;
            if ( frameCount > 30 ) {
                var now = new Date().getTime();

                var rate = frameCount * 1000 / (now - lastFrameRateUpdate);
                fpsText.text = rate.toFixed( 1 ) + " fps";

                frameCount = 0;
                lastFrameRateUpdate = now
            }
        }


        // Event handling

        var onResize = function ( event ) {
            var winW = $( window ).width(),
                    winH = $( window ).height(),
                    scale = Math.min( winW / 1024, winH / 768 ),
                    canvasW = scale * 1024,
                    canvasH = scale * 768;
            $( '#c' ).attr( 'width', canvasW );
            $( '#c' ).attr( 'height', canvasH );
            $( '#c' ).offset( {left:(winW - canvasW) / 2, top:(winH - canvasH) / 2} );
            group.scaleX = group.scaleY = scale;
            stage.update();
        };
        $( window ).resize( onResize );
        onResize(); // initial position

        updateBallCountFromInput = function () {
            val = parseInt( $( '#ballCount' ).val() );
            if ( val >= 0 && val <= 500 && val != balls.length ) {
                setBallCount( val )
            }
        };
        $( '#ballCount' ).change( updateBallCountFromInput );
        $( '#ballCount' ).keyup( updateBallCountFromInput );

        stage.update();
        createjs.Ticker.setFPS( 60 );
        createjs.Ticker.addListener( displayFrameRate );
        createjs.Ticker.addListener( stage );

        //Enable touch and prevent default
        createjs.Touch.enable( stage, false, false );
    }

    //http://www.javascriptkit.com/javatutors/preloadimagesplus.shtml
    function preloadImages( a ) {
        var newImages = [], loadedImages = 0;
        var postAction = function () {};
        var arr = (typeof a != "object") ? [a] : a;

        function imageLoadPost() {
            loadedImages++;
            if ( loadedImages == arr.length ) {
                postAction( newImages ); //call postAction and pass in newImages array as parameter
            }
        }

        for ( var i = 0; i < arr.length; i++ ) {
            newImages[i] = new Image();
            newImages[i].src = arr[i];
            newImages[i].onload = function () {imageLoadPost()};
            newImages[i].onerror = function () {imageLoadPost()};
        }
        return { //return blank object with done() method
            done:function ( f ) {
                postAction = f || postAction; //remember user defined callback functions to be called when images load
            }
        }
    }

    //TODO: Switch to http://www.createjs.com/#!/PreloadJS
    // Only executed our code once the DOM is ready.
    window.onload = function () {
        preloadImages( ["resources/skater.png", "resources/house.png", "resources/mountains.png"] ).done( run )
    }
} );
