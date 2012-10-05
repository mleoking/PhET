(function () {

    function createButton( director ) {
        var actor = new CAAT.Actor().
                setSize( 80, 40 );

        actor.paint = function ( director, time ) {

            var ctx = director.ctx;
            ctx.save();

            ctx.fillStyle = this.pointed ? '#f3f' : 'orange';
            ctx.fillRect( 0, 0, this.width, this.height );

            ctx.strokeStyle = this.pointed ? 'gray' : 'black';
            ctx.strokeRect( 0, 0, this.width, this.height );

            ctx.restore();

            ctx.font = '18px sans-serif';
            ctx.fillStyle = 'black';
            ctx.fillText( 'Reset All', 3, 28 );
        };

        return actor;
    }

    function interpolate( x0, y0, x1, y1, x ) {
        return y0 + (x - x0) * (y1 - y0) / (x1 - x0);
    }

    /**
     * This function will be called to let you define new scenes that will be
     * shown after the splash screen.
     * @param director
     */
    function createScenes( director ) {
        var scene = director.createScene();

        //Set background to white
        scene.fillStyle = 'rgb(255,255,255)';

        var shaker = new CAAT.Actor().
                setBackgroundImage( director.getImage( 'shaker' ), true ).setRotation( -Math.PI / 4 );
        shaker.enableDrag();
        shaker.x = 389;
        shaker.lastY = shaker.y;

        var beaker = new CAAT.Actor().setSize( director.width, director.height );

        var lipWidth = 40;
        var beakerX = 150;
        var beakerY = 200;
        var beakerHeight = 400;
        var beakerWidth = 600;
        var beakerMaxX = beakerX + beakerWidth;
        var beakerMaxY = beakerY + beakerHeight;

        beaker.paint = function ( director, time ) {

            var ctx = director.ctx;
            ctx.save();

            ctx.strokeStyle = 'black';
            ctx.beginPath();
            ctx.moveTo( beakerX - lipWidth, beakerY - lipWidth );
            ctx.lineTo( beakerX, beakerY );
            ctx.lineTo( beakerX, beakerMaxY );
            ctx.lineTo( beakerX + beakerWidth, beakerMaxY );
            ctx.lineTo( beakerX + beakerWidth, beakerMaxY );
            ctx.lineTo( beakerX + beakerWidth, beakerY );
            ctx.lineTo( beakerX + beakerWidth + lipWidth, beakerY - lipWidth );

            ctx.lineWidth = 6;
            ctx.lineJoin = 'round';
            ctx.lineCap = 'round';
            ctx.stroke();

            ctx.restore();
        };

        var topFlowingWater = new CAAT.Actor().setSize( director.width, director.height );
        var topFlowAmount = 0.0;

        topFlowingWater.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.save();

            var distance = absorbedCrystals / 100;
            if ( distance > 1 ) {
                distance = 1;
            }
            var water = {red:200, green:200, blue:255};
            ctx.fillStyle = 'rgb(' + Math.round( water.red ) + ',' + Math.round( water.green ) + ',' + Math.round( water.blue ) + ')';

            if ( topFlowAmount > 0.1 ) {
                ctx.fillRect( beakerX + 7, 180, 50 * topFlowAmount, beakerHeight - fluidHeight + 50 );
            }

            ctx.restore();
        };

        var fluid = new CAAT.Actor().setSize( director.width, director.height );
        var fluidHeight = beakerHeight / 2;

        var absorbedCrystals = 0;
        fluid.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.save();

            var distance = absorbedCrystals / 100;
            if ( distance > 1 ) {
                distance = 1;
            }

            var source = {red:200, green:200, blue:255};
            var destination = {red:255, green:0, blue:0};
            var relative = {
                red:interpolate( 0, source.red, 1, destination.red, distance ),
                green:interpolate( 0, source.green, 1, destination.green, distance ),
                blue:interpolate( 0, source.blue, 1, destination.blue, distance )};

            var fillStyleString = 'rgb(' + Math.round( relative.red ) + ',' + Math.round( relative.green ) + ',' + Math.round( relative.blue ) + ')';
            ctx.fillStyle = fillStyleString;
            ctx.fillRect( beakerX, beakerMaxY - fluidHeight, beakerWidth, fluidHeight );

            ctx.restore();
        };

        var topFaucet = new CAAT.Actor().setBackgroundImage( director.getImage( 'faucet_front' ), true ).setPosition( 100, 50 );
        var topFaucetPipe = new CAAT.Actor().setBackgroundImage( director.getImage( 'faucet_pipe_long' ), true ).setPosition( -292, 82 );
        var bottomFaucet = new CAAT.Actor().setBackgroundImage( director.getImage( 'faucet_front' ), true ).setPosition( 752, 520 );
        var rootNode = new CAAT.ActorContainer().setSize( director.width, director.height );
        rootNode.setPosition( 0, 0 );
//        var slider = new CAAT.ActorContainer().setSize( director.width, director.height );

        var KNOB_MIN_X = 90;
        var knob = new CAAT.Actor().setBackgroundImage( director.getImage( 'slider-knob' ), true ).setPosition( KNOB_MIN_X, 34 );
        knob.x = KNOB_MIN_X;
        knob.enableDrag();
        knob.mouseDrag = function ( mouseEvent ) {

            var pt = knob.modelToView( new CAAT.Point( mouseEvent.x, mouseEvent.y ) );
            knob.parent.viewToModel( pt );

            if ( knob.__d_ax === -1 || this.__d_ay === -1 ) {
                knob.__d_ax = pt.x;
                knob.__d_ay = +1;
                knob.__relativeTouchPoint = knob.x - pt.x;
            }

            var knobX = pt.x + knob.__relativeTouchPoint;
            if ( knobX > 177 ) {
                knobX = 177;
            }
            if ( knobX < KNOB_MIN_X ) {
                knobX = KNOB_MIN_X;
            }
            knob.x = knobX;
        };

        var border = new CAAT.Actor().setSize( director.width, director.height );

        border.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.save();

            ctx.strokeStyle = 'orange';
            ctx.strokeRect( 0, 0, 1024, 768 );

            ctx.restore();
        };

        rootNode.addChild( border );
        rootNode.addChild( fluid );
        rootNode.addChild( beaker );
        rootNode.addChild( topFlowingWater );
        rootNode.addChild( topFaucetPipe );
        rootNode.addChild( topFaucet );
        rootNode.addChild( shaker );
        rootNode.addChild( knob );
        rootNode.addChild( bottomFaucet );

        var resetAllButton = createButton( director );
        resetAllButton.setPosition( 1024 - 80 - 40 - 100, 768 - 40 );
        resetAllButton.mouseClick = function ( e ) {
            document.location.reload( true );
        };
        rootNode.addChild( resetAllButton );
        scene.addChild( rootNode );

        //This resize strategy is buggy on ipad if you change orientation more than once
        //{function(director{CAAT.Director}, width{integer}, height{integer})}
        var onResizeCallback = function ( director, width, height ) {
            //Scale up or down to fit the screen
            var designWidth = 1024;
            var designHeight = 768;
            var windowWidth = director.width;
            var windowHeight = director.height;
            var sx = windowWidth / designWidth;
            var sy = windowHeight / designHeight;
            var min = Math.min( sx, sy );
            rootNode.setScaleAnchored( min, min, 0, 0 );
        };

        //http://stackoverflow.com/questions/7814984/detect-ios5-within-mobile-safari-javascript-preferred
        // this helps detect minor versions such as 5_0_1
        //TODO: would be nice to get resizing working properly on ios
        if ( navigator.userAgent.match( /OS 5(_\d)+ like Mac OS X/i ) || navigator.userAgent.match( /OS 6(_\d)+ like Mac OS X/i ) ) {
//            document.write( "You have iOS 5 or 6!" );
        }
        else {
            director.enableResizeEvents( CAAT.Director.prototype.RESIZE_BOTH, onResizeCallback );
        }

        //causes buggy behavior on ipad in safari
        onResizeCallback( director, director.width, director.height );

        //TODO: Center content in the window
//        rootNode.setPosition( director.width / 2 - designWidth / 2, director.height / 2 - designHeight / 2 );

        var crystals = [];

        // Array Remove - By John Resig (MIT Licensed)
        //http://stackoverflow.com/questions/500606/javascript-array-delete-elements
        Array.prototype.remove = function ( from, to ) {
            var rest = this.slice( (to || from) + 1 || this.length );
            this.length = from < 0 ? this.length + from : from;
            return this.push.apply( this, rest );
        };

        scene.createTimer( 0, 30,
                           function ( scene_time, timer_time, timertask_instance ) {   // timeout
                               timertask_instance.reset( scene_time );

//                               console.log( topFaucetPipe.x + ", " + topFaucetPipe.y );
//                               console.log( knob.x + ", " + knob.y );

                               if ( shaker.y != shaker.lastY ) {
                                   var w = 20;
                                   var x = shaker.x + 20 + Math.random() * 50;
                                   var y = shaker.y + 140 + Math.random() * 20;
                                   if ( x > beakerX && x < beakerMaxX ) {
                                       var crystal = new CAAT.Actor().
                                               setBounds( x, y, w, w ).
                                               setFillStyle( 'rgb(' + 255 + ',' + 0 + ',' + 0 + ')' );
                                       crystal.velocity = 0;
                                       rootNode.addChild( crystal );
                                       crystals.push( crystal );
                                   }
                               }

                               var sliderAmount = (knob.x - 90) / (177.0 - 90);
                               if ( sliderAmount > 0 ) {
                                   //add fluid
                                   fluidHeight = fluidHeight + 1 * sliderAmount * 4;
                                   if ( fluidHeight > beakerHeight ) {
                                       fluidHeight = beakerHeight;
                                   }
                               }
                               topFlowAmount = sliderAmount;

                               for ( var index = 0; index < crystals.length; index++ ) {
                                   crystals[index].velocity = crystals[index].velocity + 1;
                                   crystals[index].setPosition( crystals[index].x, crystals[index].y + crystals[index].velocity );
                               }

                               for ( var i = 0; i < crystals.length; i++ ) {
                                   var c = crystals[i];
                                   if ( c.y + c.height > beakerMaxY - fluidHeight ) {
                                       crystals.splice( i, 1 );
                                       rootNode.removeChild( c );
                                       i--;
                                       absorbedCrystals++;
                                   }
                               }
                               shaker.lastY = shaker.y;
                           },
                           function ( scene_time, timer_time, timertask_instance ) {   // tick
                           },
                           function ( scene_time, timer_time, timertask_instance ) {   // cancel
                           } )
    }

    /**
     * Startup it all up when the document is ready.
     * Change for your favorite frameworks initialization code.
     */
    window.addEventListener(
            'load',
            function () {
                CAAT.modules.initialization.init(
                        /* canvas will be 800x600 pixels */
                        window.innerWidth, window.innerHeight,

                        /* and will be added to the end of document. set an id of a canvas or div element */
                        undefined,

                        /*
                         load these images and set them up for non splash scenes.
                         image elements must be of the form:
                         {id:'<unique string id>',    url:'<url to image>'}

                         No images can be set too.
                         */
                        [
                            {id:'shaker', url:'resources/shaker.png'},
                            {id:'faucet_front', url:'resources/faucet_front.png'},
                            {id:'faucet_pipe_long', url:'resources/faucet_pipe_long.png'},
                            {id:'slider-knob', url:'resources/slider-knob.png'}
                        ],

                        /*
                         onEndSplash callback function.
                         Create your scenes on this method.
                         */
                        createScenes

                );
            },
            false );
})();