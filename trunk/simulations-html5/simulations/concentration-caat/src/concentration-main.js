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
        scene.addChild( shaker );

        var crystals = [];

        // Array Remove - By John Resig (MIT Licensed)
        //http://stackoverflow.com/questions/500606/javascript-array-delete-elements
        Array.prototype.remove = function ( from, to ) {
            var rest = this.slice( (to || from) + 1 || this.length );
            this.length = from < 0 ? this.length + from : from;
            return this.push.apply( this, rest );
        };

        scene.createTimer( 0, 30, function ( scene_time, timer_time, timertask_instance ) {   // timeout
                               timertask_instance.reset( scene_time );

                               var w = 20;
                               var crystal = new CAAT.Actor().
                                       setBounds(
                                       shaker.x + 50,
                                       shaker.y + 150,
                                       w,
                                       w ).
                                       setFillStyle( 'rgb(' + 255 + ',' + 0 + ',' + 0 + ')' );
                               scene.addChild( crystal );
                               crystals.push( crystal );

                               for ( var i = 0; i < crystals.length; i++ ) {
                                   var c = crystals[i];
                                   c.setPosition( c.x, c.y + 10 );
                               }

                               for ( var i = 0; i < crystals.length; i++ ) {
                                   var c = crystals[i];
                                   if ( c.y > window.innerHeight - 100 ) {
                                       crystals.splice( i, 1 );
                                       scene.removeChild( c );
                                       i--;
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