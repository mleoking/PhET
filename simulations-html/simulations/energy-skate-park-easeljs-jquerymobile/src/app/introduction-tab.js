define( [
            'model/skater-model',
            'skater',
            'view/control-panel',
            'view/background',
            'spline',
            'model/physics',
            'view/easel-create',
            'view/easel-util',
            'view/pie-chart',
            'view/grid',
            'view/bar-chart',
            'view/speedometer',
            'util/version',
            'i18n!nls/energy-skate-park-strings',
            'i18n!../../../../common/common-html/src/app/nls/phetcommon-strings'
        ], function ( SkaterModel, Skater, ControlPanel, Background, Spline, Physics, EaselCreate, EaselUtil, PieChart, Grid, BarChart, Speedometer, Version, Strings, CommonStrings ) {
    var IntroductionTab = function () {
        var root = new createjs.Container();

        var groundHeight = 116;
        var groundY = 768 - groundHeight;

        var skaterModel = new SkaterModel();
        var skater = Skater.createSkater( skaterModel, groundHeight, groundY );

        //Cache the background into a single image
        //        background.cache( 0, 0, 1024, 768, 1 );

        var splineLayer = Spline.createSplineLayer( groundHeight );

        root.addChild( Background.createBackground( groundHeight ) );
        var grid = new Grid( groundY );
        grid.visible = false;
        root.addChild( grid );
        root.addChild( splineLayer );
        var barChart = BarChart.createBarChart( skater );
        barChart.x = 50;
        barChart.y = 50;
        barChart.visible = false;
        root.addChild( barChart );

        root.addChild( skater );

        var fpsText = new createjs.Text( '-- fps', '24px "Lucida Grande",Tahoma', createjs.Graphics.getRGB( 153, 153, 230 ) );
        fpsText.x = 4;
        fpsText.y = 280;
        root.addChild( fpsText );
        var pieChart = new PieChart( skater );
        pieChart.visible = false;
        root.addChild( pieChart );

        var speedometer = Speedometer.createSpeedometer( skater );
        speedometer.visible = false;
        root.addChild( speedometer );

        //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
        var canvas = document.getElementById( "c" );
        canvas.onselectstart = function () { return false; }; // IE
        canvas.onmousedown = function () { return false; }; // Mozilla

        var stage = new createjs.Stage( canvas );
        stage.mouseMoveOutside = true;
        stage.addChild( root );

        var frameCount = 0;

        var filterStrength = 20;
        var frameTime = 0, lastLoop = new Date, thisLoop;
        var paused = false;

        //Wire up the pie chart check box button to the visibility of the pie chart
        $( "#checkbox1" ).click( function () { barChart.visible = $( "#checkbox1" ).is( ":checked" ); } );
        $( "#checkbox2" ).click( function () { pieChart.visible = $( "#checkbox2" ).is( ":checked" ); } );
        $( "#checkbox3" ).click( function () { grid.visible = $( "#checkbox3" ).is( ":checked" ); } );
        $( "#checkbox4" ).click( function () { speedometer.visible = $( "#checkbox4" ).is( ":checked" ); } );

        function updateFrameRate() {
            frameCount++;

            //Get frame rate but filter transients: http://stackoverflow.com/questions/4787431/check-fps-in-js
            var thisFrameTime = (thisLoop = new Date) - lastLoop;
            frameTime += (thisFrameTime - frameTime) / filterStrength;
            lastLoop = thisLoop;
            if ( frameCount > 30 ) {
//                fpsText.text = (1000 / frameTime).toFixed( 1 ) + " fps";// @"+location.href;
                fpsText.text = (1000 / frameTime).toFixed( 1 ) + " fps @" + Version.version;// @"+location.href;
            }
        }

        //http://stackoverflow.com/questions/4009524/change-button-text-jquery-mobile
        (function ( $ ) {
            /*
             * Changes the displayed text for a jquery mobile button.
             * Encapsulates the idiosyncracies of how jquery re-arranges the DOM
             * to display a button for either an <a> link or <input type="button">
             */
            $.fn.changeButtonText = function ( newText ) {
                return this.each( function () {
                    $this = $( this );
                    if ( $this.is( 'a' ) ) {
                        $( 'span.ui-btn-text', $this ).text( newText );
                        return;
                    }
                    if ( $this.is( 'input' ) ) {
                        $this.val( newText );
                        // go up the tree
                        var ctx = $this.closest( '.ui-btn' );
                        $( 'span.ui-btn-text', ctx ).text( newText );
                    }
                } );
            };
        })( jQuery );

        var pauseString = CommonStrings["Common.ClockControlPanel.Pause"];
        var playString = CommonStrings["Common.ClockControlPanel.Play"];
        console.log( "pauseString = " + pauseString + ", playString = " + playString );

        //TODO: use requirejs templating for this.
        $( "#tab1" ).append( $( '<select name="flip-min" id="flip-min" data-role="slider">' +
                                '<option value="off">' + pauseString + '</option>' +
                                '<option value="on">' + playString + '</option></select>' ) ).trigger( "create" );

        var slowMotionString = Strings["slow.motion"];
        var normalString = Strings.normal;
        $( "#tab1" ).append( $( '<div id="speedControl"><fieldset data-role="controlgroup" data-type="horizontal">' +
                                '<input type="radio" name="radio-choice-2" id="radio-choice-21" value="choice-1" checked="checked"/><label for="radio-choice-21">' + slowMotionString + '</label>' +
                                '<input type="radio" name="radio-choice-2" id="radio-choice-22" value="choice-2"/>' +
                                '<label for="radio-choice-22">' + normalString + '</label></fieldset></div>' ) ).trigger( "create" );
        $( "#tab1" ).append( $( '<div data-role="control-panel" id="controlPanel">' +
                                '<fieldset data-role="controlgroup" data-type="vertical" id="innereelement2">' +
                                '<legend></legend>' +
                                '<input id="checkbox1" name="" type="checkbox"/>' +
                                '<label for="checkbox1" id="barGraphLabel">Bar Graph</label>' +
                                '<input id="checkbox2" name="" type="checkbox"/><label for="checkbox2" id="pieChartLabel">Pie Chart</label>' +
                                '<input id="checkbox3" name="" type="checkbox"/><label for="checkbox3" id="gridLabel">Grid</label>' +
                                '<input id="checkbox4" name="" type="checkbox"/><label for="checkbox4" id="speedLabel">Speed</label>' +
                                '</fieldset><input type="range" name="slider-fill" id="slider-fill" value="60" min="0" max="100" data-highlight="true"/></div>' ) ).trigger( "create" );

        $( '#barGraphLabel' ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="barChartIconImage" src="resources/barChartIcon.png" />' );
        $( '#pieChartLabel' ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/pieChartIcon.png" />' );
        $( '#gridLabel' ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/gridIcon.png" />' );
        $( '#speedLabel' ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/speedIcon.png" />' );

        $( '#flip-min' ).val( 'on' ).slider( "refresh" );
        $( "#flip-min" ).bind( "change", function ( event, ui ) { paused = !paused; } );

        var onResize = function () {
            var winW = $( window ).width(),
                    winH = $( window ).height(),
                    scale = Math.min( winW / 1024, winH / 768 ),
                    canvasW = scale * 1024,
                    canvasH = scale * 768;

            //Allow the canvas to fill the screen, but still center the content within the window.
            var canvas = $( '#c' );
            canvas.attr( 'width', window.innerWidth );
            canvas.attr( 'height', window.innerHeight );
            var left = (winW - canvasW) / 2;
            var top = (winH - canvasH) / 2;
            canvas.offset( {left: 0, top: 0} );
            root.scaleX = root.scaleY = scale;
            root.x = left;
            root.y = top;
            stage.update();

            $( "#navBar" ).css( 'top', top + 'px' ).css( 'left', (left + 50) + 'px' ).css( 'width', (canvasW - 100) + 'px' );

            //Scale the control panel up and down using css 2d transform
            //        $( "#controlPanel" ).css( "-webkit-transform", "scale(" + scale + "," + scale + ")" );

            var controlPanel = $( '#controlPanel' );
            controlPanel.css( 'width', '200px' );
            controlPanel.css( 'top', (top + 30) + 'px' );
            controlPanel.css( 'right', left + 'px' );

            //Apply css overrides last (i.e. after other css takes effect.
            //There must be a better way to do this, hopefully this can be improved easily.
            $( "#barGraphLabel .ui-btn-inner .ui-btn-text" ).text( "Bar Chart" );
            $( ".ui-shadow-inset" ).remove();
            var slider = $( ".ui-slider" );
            slider.css( "width", "100%" );
            slider.css( "marginTop", "0px" );
            slider.css( "marginLeft", "0px" );
            slider.css( "marginBottom", "0px" );
            slider.css( "marginRight", "0px" );

            //TODO: this vertical alignment is a hack that won't work for different settings
            $( '#barGraphLabel' ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
            $( '#pieChartLabel' ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
            $( '#gridLabel' ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
            $( '#speedLabel' ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );

            //TODO: This will need to be made more specific since it will cause problems if it applies to all slider switches
            $( 'div.ui-slider-switch' ).css( 'position', 'absolute' ).css( 'width', '200px' );
            var leftSideOfPlayPauseButton = (left + canvasW / 2 - $( 'div.ui-slider-switch' ).width() / 2);
            $( 'div.ui-slider-switch' ).css( 'top', canvasH + top - 40 + 'px' ).css( 'left', leftSideOfPlayPauseButton + 'px' );
            $( '#speedControl' ).css( 'position', 'absolute' ).css( 'width', '400px' ).css( 'top', canvasH + top - 55 + 'px' ).css( 'left', (leftSideOfPlayPauseButton - 350) + 'px' );
        };
        $( window ).resize( onResize );
        onResize(); // initial position

        createjs.Ticker.setFPS( 60 );
        createjs.Ticker.addListener( function () {
            if ( !paused ) {
                var dt = 0.02;
                var subdivisions = 1;
                for ( var i = 0; i < subdivisions; i++ ) {
                    Physics.updatePhysics( skaterModel, groundHeight, splineLayer, dt / subdivisions );
                }
                skater.updateFromModel();
                updateFrameRate();
                barChart.tick();
                speedometer.tick();
                pieChart.tick();
            }
            stage.tick();
        } );

        //Enable touch and prevent default
        createjs.Touch.enable( stage, false, false );

        //Necessary to enable MouseOver events
        stage.enableMouseOver();

        //Paint once after initialization
        stage.update();

        //Hide everything with a cover until the sim is all layed out.  http://stackoverflow.com/questions/9550760/hide-page-until-everything-is-loaded-advanced
        //Hide/Remove don't work everywhere, but the combination seems to work everywhere.
        $( "#cover" ).hide().remove();
    };

    return IntroductionTab;
} );