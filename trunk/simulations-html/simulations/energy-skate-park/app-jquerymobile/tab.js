//One of the three tabs in Energy Skate Park
define( [
            'easel',
            'model/skater-model',
            'model/physics',
            'view/speedometer',
            'i18n!../nls/energy-skate-park-strings',
            'i18n!../../../common/phet/nls/phetcommon-strings',
            'tpl!view/control-panel.html',
            'tpl!view/play-pause-flip-switch.html',
            'view/speedControlView',
            'tpl!view/navbar.html',
            'view/easel-root',
            'Property'
        ], function ( Easel, SkaterModel, Physics, Speedometer, Strings, CommonStrings, controlPanelTemplate, playPauseFlipSwitch, SpeedControlView, navBar, createEaselRoot, Property ) {

    //id is the string that identifies the tab for this module, used for creating unique ids.
    function Tab( id, running, sliderControls, analytics ) {

        var self = this;
        this.events = $( {} );

        this.dt = new Property( 0.02 );


        // TODO: remove these helpers

        //Rename element id so they will be unique across tabs
        //Unique ID for the elements
        function getID( s ) { return id + s; }

        function getHashID( s ) {return "#" + getID( s );}

        //Get the jquery element with the name id+""+s
        function tab$( s ) {return $( getHashID( s ) );}


        this.$el = $( '#' + id );


        var canvasElement = $( '<canvas></canvas>' ).attr( "id", getID( "c" ) ).css( "position", "absolute" );
        var tab = $( "#" + id );
        //Put canvas first as the background
        tab.prepend( canvasElement );

        var skaterModel = new SkaterModel();
        var groundHeight = 116;
        var groundY = 768 - groundHeight;

        var root = createEaselRoot( skaterModel, groundHeight, groundY, analytics );

        //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
        var canvas = document.getElementById( getID( "c" ) );
        canvas.onselectstart = function () { return false; }; // IE
        canvas.onmousedown = function () { return false; }; // Mozilla

        var stage = new Easel.Stage( canvas );
        stage.mouseMoveOutside = true;
        stage.addChild( root );

        var frameCount = 0;

        var filterStrength = 20;
        var frameTime = 0, lastLoop = new Date, thisLoop;
        var paused = false;

        function updateFrameRate() {
            frameCount++;

            //Get frame rate but filter transients: http://stackoverflow.com/questions/4787431/check-fps-in-js
            var thisFrameTime = (thisLoop = new Date) - lastLoop;
            frameTime += (thisFrameTime - frameTime) / filterStrength;
            lastLoop = thisLoop;
            if ( frameCount > 30 ) {
                root.fpsText.text = (1000 / frameTime).toFixed( 1 ) + " fps";// @"+location.href;
            }
        }

        var pauseString = CommonStrings["Common.ClockControlPanel.Pause"];
        var playString = CommonStrings["Common.ClockControlPanel.Play"];

        var templateText = playPauseFlipSwitch( {tab: id, pauseString: pauseString, playString: playString} );
        console.log( templateText );
        tab.append( $( templateText ) ).trigger( "create" );

        var slowMotionString = Strings["slow.motion"];
        var normalString = Strings.normal;


        var speedControl = new SpeedControlView( Strings, this.dt );
        tab.append( speedControl.render() ).trigger( "create" );


        var text = controlPanelTemplate( {
                                             barGraph: Strings["plots.bar-graph"],
                                             pieChart: Strings["pieChart"],
                                             grid: Strings["controls.show-grid"],
                                             speedometer: Strings["properties.speed"],
                                             id: id,
                                             sliderControls: sliderControls} );
        tab.append( $( text ) ).trigger( "create" );

        var elements = [
            {dom: "checkbox1", component: "barGraphCheckBox", node: root.barChart},
            {dom: "checkbox2", component: "pieChartCheckBox", node: root.pieChart},
            {dom: "checkbox3", component: "gridCheckBox", node: root.grid},
            {dom: "checkbox4", component: "speedCheckBox", node: root.speedometer}
        ];

        //Wire up the pie chart check box button to the visibility of the pie chart
        _.each( elements, function ( element ) {
            tab$( element.dom ).click( function () {
                var newValue = tab$( element.dom ).is( ":checked" );
                analytics.log( element.component, "checkBox", "pressed", [
                    {value: newValue}
                ] );
                element.node.visible = newValue;
            } );
        } );

        tab$( "returnSkaterButton" ).bind( "click", function () {
            skaterModel.returnSkater();
        } );


        tab$( "resetAllButton" ).bind( "click", function () {
            analytics.log( "resetAllButton", "button", "pressed" );

            self.dt.set( 0.02 );

            //This line of code took a long to find.  You cannot simply attr("checked","").
            _.each( elements, function ( element ) {
                tab$( element.dom ).removeAttr( "checked" ).checkboxradio( "refresh" )
            } );

            root.resetAll();
            skaterModel.returnSkater();
        } );

        tab$( "barGraphLabel" ).find( '.ui-btn-inner' ).append( '<img class="alignRightPlease" id="barChartIconImage" src="resources/images/barChartIcon.png" />' );
        tab$( "pieChartLabel" ).find( '.ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/images/pieChartIcon.png" />' );
        tab$( "gridLabel" ).find( '.ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/images/gridIcon.png" />' );
        tab$( "speedLabel" ).find( '.ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/images/speedIcon.png" />' );

        $( '#flip-min' ).val( 'on' ).slider( "refresh" );
        $( "#flip-min" ).bind( "change", function ( event, ui ) { paused = !paused; } );

        var onResize = function () {
            var width = tab.width();
            var height = tab.height();
            var scale = Math.min( width / 1024, height / 768 );
            var canvasW = scale * 1024;
            var headerBarHeight = tab$( "headerbar" ).height();
            var canvasH = scale * 768 - headerBarHeight - 100;

            //Allow the canvas to fill the screen, but still center the content within the window.
            var canvas = $( "#" + getID( "c" ) );
            canvas.attr( 'width', width );
            canvas.attr( 'height', height );
            var left = (width - canvasW) / 2;
            var top = (height - canvasH) / 2;

            canvas.offset( {left: 0, top: 0} );
            root.scaleX = root.scaleY = scale;
            root.x = left;
            root.y = top;

            var controlPanel = $( '#' + id + " > .controlPanel" );
            controlPanel.css( 'width', '270px' );
            controlPanel.css( 'top', 50 + headerBarHeight + 'px' );
            controlPanel.css( 'right', 0 + 'px' );

            //Apply css overrides last (i.e. after other css takes effect.
            //There must be a better way to do this, hopefully this can be improved easily.
//            tab$( "slider-fill" ).remove();
            tab$( "frictionSlider" ).remove();

            //TODO: This code actually hits all of the sliders in every tab.  This should be fixed.
            var slider = $( ".ui-slider" );
            slider.css( "width", "100%" );
            slider.css( "marginTop", "0px" );
            slider.css( "marginLeft", "0px" );
            slider.css( "marginBottom", "0px" );
            slider.css( "marginRight", "0px" );

            //TODO: this vertical alignment is a hack that won't work for different settings
            tab$( "barGraphLabel" ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
            tab$( "pieChartLabel" ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
            tab$( "gridLabel" ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
            tab$( "speedLabel" ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );

            //TODO: This will need to be made more specific since it will cause problems if it applies to all slider switches
            var $2 = $( '#' + id + 'containerForPlayPauseFlipSwitch ' );
            $2.css( 'position', 'absolute' ).css( 'width', '200px' );
            var leftSideOfPlayPauseButton = (left + canvasW / 2 - $( 'div.ui-slider-switch' ).width() / 2);
            $2.css( 'left', leftSideOfPlayPauseButton + 'px' ).css( 'top', canvasH + top + headerBarHeight + 'px' );
            $( "#" + id + " > .speedControl" )
                    .css( 'position', 'absolute' )
                    .css( 'width', '200px' )
                    .css( 'top', canvasH + top + headerBarHeight - 60 + 'px' )
                    .css( 'left', (leftSideOfPlayPauseButton - 350) + 'px' );

            stage.update();
        };

        //Uses jquery resize plugin "jquery.ba-resize": http://benalman.com/projects/jquery-resize-plugin/
        //TODO: This line is too expensive on ipad, dropping the frame rate by 15FPS
//        $( "#" + id ).resize( onResize );

        function moduleActive() {return $.mobile.activePage[0] == tab[0];}

        Easel.Ticker.setFPS( 60 );
        Easel.Ticker.addListener( function () {
            if ( moduleActive() ) {
                if ( !paused ) {
                    var subdivisions = 1;
                    for ( var i = 0; i < subdivisions; i++ ) {
                        Physics.updatePhysics( skaterModel, groundHeight, root.splineLayer, self.dt.get() / subdivisions );
                    }

                    updateFrameRate();
                    root.tick();
                }
                stage.tick();
            }
        } );

        //Enable touch and prevent default
        Easel.Touch.enable( stage, false, false );

        //Necessary to enable MouseOver events
        stage.enableMouseOver();

        //Paint once after initialization
        stage.update();

        //Hide everything with a cover until the sim is all layed out.  http://stackoverflow.com/questions/9550760/hide-page-until-everything-is-loaded-advanced
        //Hide/Remove don't work everywhere, but the combination seems to work everywhere.
        $( "#cover" ).hide().remove();

        //Create the navbar
        var persist = "ui-btn-active ui-state-persist";
        var class1 = id == "tab" ? persist : "";
        var class2 = id == "tab2" ? persist : "";
        var class3 = id == "tab3" ? persist : "";
        tab.append( $( navBar( {class1: class1, class2: class2, class3: class3} ) ) ).trigger( "create" );

        tab$( 'controlgroup' ).css( 'margin-bottom', '0px' );
        tab$( 'controlgroup' ).css( 'margin-top', '0px' );
        tab$( 'headerbar' ).css( 'height', '50px' );

        $( window ).resize( onResize );
        onResize(); // initial position
//        onResize(); // initial position
//        onResize(); // initial position

        $( '#tab1popupMenu' ).on( {popupafterclose: function () {setTimeout( function () { $( '#popupDialog' ).popup( 'open' ) }, 100 );}} );
    }

    return Tab;
} );
