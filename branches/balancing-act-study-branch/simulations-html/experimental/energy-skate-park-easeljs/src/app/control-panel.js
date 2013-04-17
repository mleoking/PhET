define( ['image!resources/barChartIcon.png',
            'image!resources/gridIcon.png',
            'image!resources/pieChartIcon.png',
            'image!resources/speedIcon.png'], function ( barChartIconImage, gridIconImage, pieChartIconImage, speedIconImage ) {

//    var barChartIconImage=skaterImage, gridIconImage=skaterImage, pieChartIconImage=skaterImage, speedIconImage = skaterImage;

    var result = {};

    function showPointer( mouseEvent ) { document.body.style.cursor = "pointer"; }

    function showDefault( mouseEvent ) { document.body.style.cursor = "default"; }

    //Use the allTexts to measure row width for layout
    function checkBoxRow( allTexts, index, image ) {
        var row = new createjs.Container();

        //Larger area behind for hit detection
        var backgroundShape = new createjs.Shape( new createjs.Graphics().beginFill( "#c8f0c8" ).drawRoundRect( 0, 0, 180, 40, 5 ).endStroke() );
        row.addChild( backgroundShape );

        var checkBox = new createjs.Shape( new createjs.Graphics().beginFill( "#cccccc" ).drawRoundRect( 5, 9, 20, 20, 5 ).endStroke() );
        checkBox.selected = false;

        var widths = [];
        for ( var i = 0; i < allTexts.length; i++ ) {
            var width = new createjs.Text( allTexts[i], '20px "Arial",Tahoma' ).getMeasuredWidth();
            widths.push( width );
        }
        var maxTextWidth = Math.max.apply( null, widths );

        var text2 = new createjs.Text( allTexts[index], '20px "Arial",Tahoma' );

        //Caching the text saves a few percent in profiling but makes it less crisp.
        //TODO: maybe the cache scale should match the globalScale to make it super crisp
        //            text2.cache( 0, 0, 100, 100, 1.1 );

        text2.y = 7;
        text2.x = 35;
        row.height = 40;//make all rows same height

        row.width = text2.getMeasuredWidth();
        row.addChild( checkBox );
        row.addChild( text2 );
        var bitmap = new createjs.Bitmap( image );
        bitmap.y = (row.height - bitmap.image.height) / 2;
        bitmap.x = text2.x + maxTextWidth + 15;
        row.addChild( bitmap );
        row.onMouseOver = function ( event ) {
            showPointer( event );
            backgroundShape.graphics.clear().beginFill( "#6dff7e" ).drawRoundRect( 0, 0, 180, 40, 5 ).endStroke();
//            controlPanel.updateCache();
        };
        row.onMouseOut = function ( event ) {
            showDefault( event );
            backgroundShape.graphics.clear().beginFill( "#c8f0c8" ).drawRoundRect( 0, 0, 180, 40, 5 ).endStroke();
//            controlPanel.updateCache();
        };
        row.onPress = function ( mouseEvent ) {
            console.log( "pressed" );
            checkBox.selected = !checkBox.selected;


            if ( checkBox.selected ) {
                var offsetY = 1;
                checkBox.graphics.clear().beginFill( "#3e84b5" ).drawRoundRect( 5, 9, 20, 20, 5 ).endStroke().
                        beginStroke( 'black' ).setStrokeStyle( 5 ).moveTo( 8, 20 - offsetY ).lineTo( 14, 30 - offsetY - 2 ).lineTo( 28, 10 - offsetY ).endStroke().
                        beginStroke( 'white' ).setStrokeStyle( 4 ).moveTo( 8, 20 - offsetY ).lineTo( 14, 30 - offsetY - 2 ).lineTo( 28, 10 - offsetY ).endStroke();
            }
            else {
                checkBox.graphics.clear().beginFill( "#cccccc" ).drawRoundRect( 5, 9, 20, 20, 5 ).endStroke();
            }
//            controlPanel.updateCache();
        };
        //Update once
        //            row.onPress( "hi" );


        return row;
    }

    function verticalLayoutPanel() {
        var container = new createjs.Container();
        var offsetY = 8;
        var insetX = 8;
        var width = 200;
        container.addChild( new createjs.Shape( new createjs.Graphics().beginFill( "#c8f0c8" ).drawRoundRect( 0, 0, width, 195, 10 ).endFill().beginStroke( "black" ).drawRoundRect( 0, 0, width, 195, 10 ).endStroke() ) );
        container.addLayoutItem = function ( child ) {
            child.x = insetX;
            child.y = offsetY;
            container.addChild( child );
            offsetY += child.height;
        };
        container.y = 8;
        container.x = 1024 - 200 - 8;
        return container;
    }


    function createControlPanel() {
        var texts = ["Bar Graph", "Pie Chart", "Grid", "Speed"];
        var checkBoxRows = [
            checkBoxRow( texts, 0, barChartIconImage ),
            checkBoxRow( texts, 1, pieChartIconImage ),
            checkBoxRow( texts, 2, gridIconImage ),
            checkBoxRow( texts, 3, speedIconImage )
        ];

        var controlPanel = verticalLayoutPanel();
        for ( var i = 0; i < checkBoxRows.length; i++ ) {
            controlPanel.addLayoutItem( checkBoxRows[i] );
        }

        var container = new createjs.Container();
        var text = new createjs.Text( "Skater Mass", '24px "Arial",Tahoma' );
        text.x = 25;
        container.addChild( text );
        controlPanel.addLayoutItem( container );

        //Cache the control panel.  This has two effects:
        //1. Renders it as an image, this improves performance but can cause it to be fuzzy or pixellated (on win7/chrome but not ios/safari).  Maybe we could tune the scale to fix that problem.
        //2. Only repaint it when there is an actual change, such as mouse press or mouseover.  This also improves performance.
        //TODO: Make sure the cache fits snugly
//        controlPanel.cache( 0, 0, 200, 400, 1 );

        return controlPanel;
    }

    result.createControlPanel = createControlPanel;
    return result;

} );