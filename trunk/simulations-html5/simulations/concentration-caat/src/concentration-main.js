(function () {

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
        shaker.lastY = shaker.y;

        var beaker = new CAAT.Actor().setSize( director.width, director.height );

        var lipWidth = 40;
        var beakerX = 150;
        var beakerY = 200;
        var beakerHeight = 400;
        var beakerWidth = 600;
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
        scene.addChild( beaker );
        scene.addChild( shaker );

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

                               if ( shaker.y != shaker.lastY ) {
                                   var w = 20;
                                   var crystal = new CAAT.Actor().
                                           setBounds( shaker.x + 20 + Math.random() * 50, shaker.y + 140 + Math.random() * 20, w, w ).
                                           setFillStyle( 'rgb(' + 255 + ',' + 0 + ',' + 0 + ')' );
                                   crystal.velocity = 0;
                                   scene.addChild( crystal );
                                   crystals.push( crystal );
                               }

                               for ( var index = 0; index < crystals.length; index++ ) {
                                   crystals[index].velocity = crystals[index].velocity + 1;
                                   crystals[index].setPosition( crystals[index].x, crystals[index].y + crystals[index].velocity );
                               }

                               for ( var i = 0; i < crystals.length; i++ ) {
                                   var c = crystals[i];
                                   if ( c.y + c.height > beakerMaxY ) {
                                       crystals.splice( i, 1 );
                                       scene.removeChild( c );
                                       i--;
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
                            {id:'shaker', url:'resources/shaker.png'}
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