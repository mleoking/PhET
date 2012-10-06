(function () {

    function createKnob( image, minX, y, maxX ) {
        var knob = new CAAT.Actor().setBackgroundImage( image, true ).setPosition( minX, y );
        knob.x = minX;
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
            if ( knobX > maxX ) {
                knobX = maxX;
            }
            if ( knobX < minX ) {
                knobX = minX;
            }
            knob.x = knobX;
        };

        //Returns a value between 0 and 1
        knob.getValue = function () {
            return (knob.x - minX) / (maxX - minX);
        };
        return knob;
    }

    function createButton( director, text, width, background ) {
        var actor = new CAAT.Actor().setSize( width, 40 );

        actor.paint = function ( director, time ) {

            var ctx = director.ctx;
            ctx.save();

            ctx.fillStyle = this.pointed ? '#f3f' : background;
            ctx.fillRect( 0, 0, this.width, this.height );

            ctx.strokeStyle = this.pointed ? 'gray' : 'black';
            ctx.strokeRect( 0, 0, this.width, this.height );

            ctx.font = '18px sans-serif';
            ctx.fillStyle = 'black';
            ctx.fillText( text, 3, 28 );

            ctx.restore();
        };

        return actor;
    }

    function interpolate( x0, y0, x1, y1, x ) {
        return y0 + (x - x0) * (y1 - y0) / (x1 - x0);
    }

    function getColor( distance ) {
        var source = {red:200, green:200, blue:255};
        var destination = {red:255, green:0, blue:0};
        var relative = {
            red:interpolate( 0, source.red, 1, destination.red, distance ),
            green:interpolate( 0, source.green, 1, destination.green, distance ),
            blue:interpolate( 0, source.blue, 1, destination.blue, distance )};

        var fillStyleString = 'rgb(' + Math.round( relative.red ) + ',' + Math.round( relative.green ) + ',' + Math.round( relative.blue ) + ')';
        return fillStyleString;
    }

    function getFractionTowardSaturation( absorbedCrystals ) {
        var distance = absorbedCrystals / 100;
        if ( distance > 1 ) {
            distance = 1;
        }
        return distance;
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

            var water = {red:200, green:200, blue:255};
            ctx.fillStyle = 'rgb(' + Math.round( water.red ) + ',' + Math.round( water.green ) + ',' + Math.round( water.blue ) + ')';

            if ( topFlowAmount > 0.1 && fluidHeight < beakerHeight ) {
                ctx.fillRect( beakerX + 7, 180, 50 * topFlowAmount, beakerHeight - fluidHeight + 20 );
            }

            ctx.restore();
        };

        var bottomFlowingWater = new CAAT.Actor().setSize( director.width, director.height );
        bottomFlowingWater.enableDrag();
        var bottomFlowAmount = 0.0;

        bottomFlowingWater.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.save();

            ctx.fillStyle = getColor( getFractionTowardSaturation( absorbedCrystals ) );

            if ( bottomFlowAmount > 0.1 && fluidHeight > 0 ) {
                ctx.fillRect( beakerMaxX + 60, beakerMaxY + 50, 50 * bottomFlowAmount, 800 );
            }
            ctx.restore();
        };

        var fluid = new CAAT.Actor().setSize( director.width, director.height );
        var fluidHeight = beakerHeight / 2;

        var absorbedCrystals = 0;
        fluid.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.save();

            ctx.fillStyle = getColor( getFractionTowardSaturation( absorbedCrystals ) );
            ctx.fillRect( beakerX, beakerMaxY - fluidHeight, beakerWidth, fluidHeight );

            ctx.restore();
        };

        var topFaucet = new CAAT.Actor().setBackgroundImage( director.getImage( 'faucet_front' ), true ).setPosition( 100, 50 );
        var topFaucetPipe = new CAAT.Actor().setBackgroundImage( director.getImage( 'faucet_pipe_long' ), true ).setPosition( -292, 82 );
        var bottomFaucet = new CAAT.Actor().setBackgroundImage( director.getImage( 'faucet_front' ), true ).setPosition( 752, 520 );
        var rootNode = new CAAT.ActorContainer().setSize( director.width, director.height );
        rootNode.setPosition( 0, 0 );

        var topKnob = createKnob( director.getImage( 'slider-knob' ), 90, 34, 177 );
        var bottomKnob = createKnob( director.getImage( 'slider-knob' ), 738, 498, 738 + (177 - 90) );

        var border = new CAAT.Actor().setSize( director.width, director.height );

        border.paint = function ( director, time ) {
            var ctx = director.ctx;
            ctx.save();

            ctx.strokeStyle = 'orange';
            ctx.strokeRect( 0, 0, 1024, 768 );

            ctx.restore();
        };

        function createTick( fraction, extentX ) {
            var tick = new CAAT.Actor().setSize( director.width, director.height );
            tick.paint = function ( director, time ) {
                var ctx = director.ctx;
                ctx.save();

                ctx.strokeStyle = 'black';
                ctx.beginPath();
                ctx.moveTo( beakerX, beakerMaxY - fraction * beakerHeight );
                ctx.lineTo( beakerX + extentX, beakerMaxY - fraction * beakerHeight );

                ctx.lineWidth = 4;
                ctx.stroke();

                ctx.restore();
            };
            return tick;
        }

        function createEvaporationControlPanel() {
            var background = new CAAT.ShapeActor().setSize( 400, 100 ).setShape( CAAT.ShapeActor.prototype.SHAPE_RECTANGLE ).setFillStyle( 'rgb(220,220,220)' );
            var sliderTrack = new CAAT.ShapeActor().setSize( 200, 4 ).setShape( CAAT.ShapeActor.prototype.SHAPE_RECTANGLE ).setFillStyle( 'rgb(255,255,255)' ).setStrokeStyle( 'rgb(0,0,0)' );
            var text = new CAAT.TextActor().setFont( "25px sans-serif" ).setText( "Evaporation" ).calcTextSize( director ).
                    setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap();

            var container = new CAAT.ActorContainer().setLocation( 175, 612 );
            container.setSize( director.width, director.height );
            container.addChild( background );
            container.addChild( text.setLocation( 2, 30 ) );
            var evaporationKnob = createKnob( director.getImage( 'slider-knob' ), 150, 25, 350 );
            container.addChild( sliderTrack.setPosition( 150 + 10, 25 + evaporationKnob.height / 2 ) );
            container.addChild( evaporationKnob );

            container.evaporationKnob = evaporationKnob;
            return container;
        }

        function createSoluteControlPanel() {
            var background = new CAAT.ShapeActor().setSize( 300, 120 ).setShape( CAAT.ShapeActor.prototype.SHAPE_RECTANGLE ).setFillStyle( 'rgb(220,220,220)' );
            var text = new CAAT.TextActor().setFont( "25px sans-serif" ).setText( "Solute:" ).calcTextSize( director ).
                    setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap();

            var container = new CAAT.ActorContainer().setLocation( 700, 20 );
            container.setSize( background.width, background.height );
            container.addChild( background );
            container.addChild( text.setLocation( 2, 4 + 8 ) );
            var offsetY = 20;
            container.addChild( new CAAT.TextActor().setFont( "20px sans-serif" ).setText( "Solid" ).calcTextSize( director ).setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap().setLocation( 60, 55 + offsetY ) );
            container.addChild( new CAAT.TextActor().setFont( "20px sans-serif" ).setText( "Solution" ).calcTextSize( director ).setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap().setLocation( 180, 55 + offsetY ) );
            var solidCircle = new CAAT.ShapeActor().setSize( 50, 50 ).setShape( CAAT.ShapeActor.prototype.SHAPE_CIRCLE ).setStrokeStyle( 'black' ).setFillStyle( 'black' ).setLocation( 5, 62 + 6 - 30 + 5 + offsetY );
            container.addChild( solidCircle );
            var fluidCircle = new CAAT.ShapeActor().setSize( 50, 50 ).setShape( CAAT.ShapeActor.prototype.SHAPE_CIRCLE ).setStrokeStyle( 'black' ).setFillStyle( 'white' ).setLocation( 5 + 180 - 60, 62 + 6 - 30 + 5 + offsetY );
            container.addChild( fluidCircle );

            var comboBox = new CAAT.ShapeActor().setSize( 190, 40 ).setShape( CAAT.ShapeActor.prototype.SHAPE_RECTANGLE ).setFillStyle( 'white' ).setStrokeStyle( 'black' ).setLocation( 100, 5 );
            container.addChild( comboBox );

            function createSquareAndTextNode( color, text ) {
                var square = new CAAT.ShapeActor().setSize( 30, 30 ).setShape( CAAT.ShapeActor.prototype.SHAPE_RECTANGLE ).setFillStyle( color );
                var entryText = new CAAT.TextActor().setFont( "24px sans-serif" ).setText( text ).calcTextSize( director ).setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap().setLocation( 40, 5 ).enableEvents( false );
                var container = new CAAT.ActorContainer().setSize( 400, 30 );
                square.enableEvents( false );
                entryText.enableEvents( false );
                container.addChild( square );
                container.addChild( entryText );
                container.enableEvents( false );
                return container;
            }

            var node = createSquareAndTextNode( 'red', "Drink Mix" );
            comboBox.addChild( node.setLocation( 5, 5 ) );

            var buttonBox = new CAAT.ShapeActor().setSize( 30, 30 ).setShape( CAAT.ShapeActor.prototype.SHAPE_RECTANGLE ).setFillStyle( 'rgb(220,220,220)' ).setLocation( comboBox.width - 30 - 5, 5 );
            comboBox.addChild( buttonBox );

            var triangle = new CAAT.Actor().setSize( 30, 30 ).setLocation( 10, 10 );
            triangle.paint = function ( director, time ) {
                var ctx = director.ctx;
                ctx.save();
                ctx.strokeStyle = 'gray';
                ctx.beginPath();
                ctx.moveTo( 0, 0 );
                ctx.lineTo( 10, 0 );
                ctx.lineTo( 5, 10 );
                ctx.closePath();

                ctx.lineWidth = 6;
                ctx.lineJoin = 'square';
                ctx.lineCap = 'square';
                ctx.stroke();

                ctx.restore();
            };

            buttonBox.addChild( triangle );

            solidCircle.mouseClick = function ( e ) {
                solidCircle.setFillStyle( 'black' );
                fluidCircle.setFillStyle( 'white' );
            };
            fluidCircle.mouseClick = function ( e ) {
                solidCircle.setFillStyle( 'white' );
                fluidCircle.setFillStyle( 'black' );
            };

            buttonBox.enableEvents( false );
            var popup = new CAAT.ShapeActor().setSize( 350, 260 ).setShape( CAAT.ShapeActor.prototype.SHAPE_RECTANGLE ).setFillStyle( 'white' ).setStrokeStyle( 'black' );
            popup.addChild( createSquareAndTextNode( 'red', 'Drink Mix' ).setLocation( 2, 0 * 32 + 2 ) );
            popup.addChild( createSquareAndTextNode( 'red', 'Cobalt (II) nitrate' ).setLocation( 2, 1 * 32 + 2 ) );
            popup.addChild( createSquareAndTextNode( 'pink', 'Cobalt chloride' ).setLocation( 2, 2 * 32 + 2 ) );
            popup.addChild( createSquareAndTextNode( 'orange', 'Potassium dichromate' ).setLocation( 2, 3 * 32 + 2 ) );
            popup.addChild( createSquareAndTextNode( 'yellow', 'Potassium chromate' ).setLocation( 2, 4 * 32 + 2 ) );
            popup.addChild( createSquareAndTextNode( 'green', 'Nickel (II) chloride' ).setLocation( 2, 5 * 32 + 2 ) );
            popup.addChild( createSquareAndTextNode( 'blue', 'Copper sulfate' ).setLocation( 2, 6 * 32 + 2 ) );
            popup.addChild( createSquareAndTextNode( 'purple', 'Potassium permanganate' ).setLocation( 2, 7 * 32 + 2 ) );
            popup.setLocation( 640, 66 );
            var inTree = false;
            comboBox.mouseClick = function ( e ) {
                if ( !inTree ) {
                    rootNode.addChild( popup );
                    inTree = true;
                }
                else {
                    rootNode.removeChild( popup );
                    inTree = false;
                }
            };
            return container;
        }

        var evaporationControlPanel = createEvaporationControlPanel();
        var soluteControlPanel = createSoluteControlPanel();

        rootNode.addChild( border );
        rootNode.addChild( fluid );

        rootNode.addChild( bottomFlowingWater );
        rootNode.addChild( topFlowingWater );

        rootNode.addChild( createTick( 0.1, 30 ) );
        rootNode.addChild( createTick( 0.2, 30 ) );
        rootNode.addChild( createTick( 0.3, 30 ) );
        rootNode.addChild( createTick( 0.4, 30 ) );
        rootNode.addChild( createTick( 0.5, 60 ) );//TODO: show label
        rootNode.addChild( createTick( 0.6, 30 ) );
        rootNode.addChild( createTick( 0.7, 30 ) );
        rootNode.addChild( createTick( 0.8, 30 ) );
        rootNode.addChild( createTick( 0.9, 30 ) );
        rootNode.addChild( createTick( 1.0, 60 ) );//TODO: show label

        rootNode.addChild( beaker );

        rootNode.addChild( topFaucetPipe );
        rootNode.addChild( topFaucet );
        rootNode.addChild( shaker );
        rootNode.addChild( topKnob );

        rootNode.addChild( bottomFaucet );
        rootNode.addChild( bottomKnob );


        var resetAllButton = createButton( director, "Reset All", 80, 'orange' );
        resetAllButton.setPosition( 1024 - 80 - 40 - 100, 768 - 40 );
        resetAllButton.mouseClick = function ( e ) {
            document.location.reload( true );
        };
        rootNode.addChild( resetAllButton );
        rootNode.addChild( evaporationControlPanel );
        rootNode.addChild( soluteControlPanel );

        var oneLiterLabel = new CAAT.TextActor().setFont( "25px sans-serif" ).setText( "1 L" ).calcTextSize( director ).
                setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap().setPosition( 224, 186 );
        rootNode.addChild( oneLiterLabel );

        var oneHalfLiterLabel = new CAAT.TextActor().setFont( "25px sans-serif" ).setText( "1/2 L" ).calcTextSize( director ).
                setTextFillStyle( 'black' ).setLineWidth( 2 ).cacheAsBitmap().setPosition( 224, 186 + 200 );
        rootNode.addChild( oneHalfLiterLabel );

        var removeSoluteButton = createButton( director, "Remove Solute", 130, 'rgb(240,240,240)' ).setLocation( 589, 641 );
        removeSoluteButton.mouseClick = function ( e ) {
            absorbedCrystals = 0;
        };
        rootNode.addChild( removeSoluteButton );
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
//                               console.log( bottomKnob.x + ", " + bottomKnob.y );
//                               console.log( bottomFlowingWater.x + ", " + bottomFlowingWater.y );
//                               console.log( evaporationControlPanel.x + ", " + evaporationControlPanel.y );
//                               console.log( oneLiterLabel.x + ", " + oneLiterLabel.y );
//                               console.log( removeSoluteButton.x + ", " + removeSoluteButton.y );

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

                               if ( topKnob.getValue() > 0 ) {
                                   //add fluid
                                   fluidHeight = fluidHeight + topKnob.getValue() * 4;
                                   if ( fluidHeight > beakerHeight ) {
                                       fluidHeight = beakerHeight;
                                   }
                               }
                               topFlowAmount = topKnob.getValue();

                               if ( bottomKnob.getValue() > 0 ) {
                                   //remove fluid
                                   fluidHeight = fluidHeight - bottomKnob.getValue() * 4;
                                   if ( fluidHeight < 0 ) {
                                       fluidHeight = 0;
                                   }
                               }
                               bottomFlowAmount = bottomKnob.getValue();

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

                               var evaporationValue = evaporationControlPanel.evaporationKnob.getValue();
                               if ( evaporationValue > 0 ) {
                                   //remove fluid
                                   fluidHeight = fluidHeight - evaporationControlPanel.evaporationKnob.getValue() * 4;
                                   if ( fluidHeight < 0 ) {
                                       fluidHeight = 0;
                                   }
                               }
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