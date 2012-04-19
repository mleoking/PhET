package edu.colorado.phet.collisionlab.control {
import edu.colorado.phet.collisionlab.CollisionLab;
import edu.colorado.phet.collisionlab.constants.CLConstants;
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.Util;
import edu.colorado.phet.collisionlab.view.BallImage;
import edu.colorado.phet.collisionlab.view.MainView;
import edu.colorado.phet.flashcommon.SimStrings;
import edu.colorado.phet.flashcommon.TextFieldUtils;

import fl.controls.*;
import fl.events.*;

import flash.display.*;
import flash.events.*;
import flash.text.*;
import flash.filters.*;

/**
 * View of table of numbers for displaying and setting initial conditions
 * one of two data tables is displayed: full data table or partial data table
 * each has header row + row for each ball
 * partial data table has column for ball number, col for mass, and col for mass slider
 */
public class DataTable extends Sprite {
    private var colWidth: int;			//width of column in pix
    private const ballColWidth: int = 60;
    private static const rowHeight: int = 27;			//height of row in pix

    private var myModel: Model;
    private var myMainView: MainView;
    private var nbrBalls: int;
    private var canvas: Sprite;			//canvas holds several rowCanvases, full data table
    private var invisibleBorder: Sprite;	//draggable border
    private var rowCanvas_arr: Array;	//array of Sprites, each holds one row of textFields
    private var rowWidth: int;			//width of row in pix, used to set borderwidth

    private var text_arr: Array;			//row of textFields, one for each of 9 columns, text must be internationalized
    private var addBallButton_sp: Sprite;		//addBallButton sprite
    private var removeBallButton_sp: Sprite;		//removeBallButton sprite
    private var moreDataButton_sp: Sprite;	//moreOrLessDataButton Sprite
    private var lessDataButton_sp: Sprite;	//moreOrLessDataButton Sprite
    private var massSlider_arr: Array;	//array of mass sliders
    private var tFormat: TextFormat;
    private var manualUpdating: Boolean;	//true if user is typing into textField, needed to prevent input-model-output loop
    private var sliderUpdating: Boolean; //true if use is using mass slider

    private var addBallButton: NiceButton;		//button to add ball, originally on Control Panel
    private var removeBallButton: NiceButton; 	//button to remove ball, originally on Control Panel
    private var moreDataButton: NiceButton;//button to toggle full or partial data display
    private var lessDataButton: NiceButton;//button to toggle full or partial data display

    private var showingMore: Boolean = false; // whether we are showing the "more data" instead of sliders

    private const ballColumnNbr: int = 0;
    private const massColumnNbr: int = 1;
//    private const xColumnNbr: int = 2;
    private const vxColumnNbr: int = 2;
    private const pxColumnNbr: int = 3;
//    private const initialPositionColumnNbr: int = 5;
    private const initialVelocityColumnNbr: int = 4;

    private const separatorPaddingSize: int = 20;

    private const highlightColor: int = 0xffff33;
    private const unhighlightColor: int = 0xffffff;

    public function DataTable( myModel: Model, myMainView: MainView ) {
        this.myModel = myModel;
        colWidth = 100;
        myModel.registerView( this );
        this.myMainView = myMainView;
        nbrBalls = this.myModel.nbrBalls;
        rowCanvas_arr = new Array( maxRows ); //header row + row for each ball
        text_arr = new Array( maxRows );	//rows
        massSlider_arr = new Array( maxRows );
        tFormat = new TextFormat();
        tFormat.font = "Arial";
        tFormat.size = 13; // SIZE was 14
        tFormat.align = TextFormatAlign.CENTER;

        //create textfields for full data table and mass sliders for partial data table
        for ( var row: int = 0; row < maxRows; row++ ) {  //header row + row for each ball
            rowCanvas_arr[row] = new Sprite();
            text_arr[row] = new Array( nbrColumns );
            if ( row > headerRowNbr ) {
                var ballNum: int = ballNbr( row );
                massSlider_arr[ballNum] = new Slider();
                massSlider_arr[ballNum].name = ballNum; //label slider with ball number: 0, 1, ..
                setupSlider( this.massSlider_arr[ballNum] );
            }
            for ( var col: int = 0; col < nbrColumns; col++ ) {
                text_arr[row][col] = new TextField();
                text_arr[row][col].defaultTextFormat = tFormat;
                text_arr[row][col].name = row;  //label textfield with ball number
            }
        }
        manualUpdating = false;
        initialize(); //initialize full data table
        displayPartialDataTable( true );
    }

    //create full data table
    private function initialize(): void {
        canvas = new Sprite;
        invisibleBorder = new Sprite();

        //following are symbols in Flash Library
        addBallButton_sp = new DataTableButtonBody();
        removeBallButton_sp = new DataTableButtonBody();
        moreDataButton_sp = new DataTableButtonBody();
        lessDataButton_sp = new DataTableButtonBody();
        addBallButton = new NiceButton( addBallButton_sp, 100, addBall );
        removeBallButton = new NiceButton( removeBallButton_sp, 100, removeBall );
        moreDataButton = new NiceButton( moreDataButton_sp, 100, toggleDataButton );
        lessDataButton = new NiceButton( lessDataButton_sp, 100, toggleDataButton );
        initializeStrings();
        //don't put buttons on canvas, since want buttons stationary when canvas resizes
        if ( myMainView.module.allowAddRemoveBalls() ) {
            addChild( addBallButton_sp );
            addChild( removeBallButton_sp );
        }
        addChild( moreDataButton_sp );
        addChild( lessDataButton_sp );
        lessDataButton_sp.visible = false;
        addChild( canvas );
        canvas.addChild( invisibleBorder );
        myMainView.addChild( this );

        //layout textFields in full data table
        for ( var row: int = 0; row < maxRows; row++ ) {
            canvas.addChild( rowCanvas_arr[row] );

            var ballBackground: Sprite = new Sprite();
            if ( row >= headerOffset ) {
                var ballNum: int = ballNbr( row );
                rowCanvas_arr[row].addChild( massSlider_arr[ballNum] );
                massSlider_arr[ballNum].x = 2.2 * colWidth;
                massSlider_arr[ballNum].y = 0.2 * rowHeight;
                massSlider_arr[ballNum].visible = false;

                ballBackground.x = 0;
                ballBackground.y = 0;
                ballBackground.graphics.beginFill( BallImage.ballColor_arr[ballNum] );
                ballBackground.graphics.lineStyle( 0, 0x000000 );
                ballBackground.graphics.drawCircle( (ballColWidth - 5) / 2, (rowHeight - 5) / 2, 10 );
                ballBackground.graphics.endFill();
                rowCanvas_arr[row].addChild( ballBackground );
            }

            for ( var col: int = 0; col < nbrColumns; col++ ) {
                if ( row >= headerOffset && col == 0 ) {
                    // outline all ball numbers
                    var glow: GlowFilter = new GlowFilter( 0x000000, 1.0, 2.0, 2.0, 10 );
                    glow.quality = BitmapFilterQuality.MEDIUM;
                    text_arr[row][col].textColor = 0xFFFFFF;
                    text_arr[row][col].filters = [glow];
                }
                rowCanvas_arr[row].addChild( text_arr[row][col] );
                rowCanvas_arr[row].y = row * rowHeight - 5;
                if ( row == 0 ) {
                    rowCanvas_arr[row].y = 5;
                }

                // decrease width (and thus position) for the ball column
                text_arr[row][col].x = (col == 0) ? 0 : (col * colWidth - (colWidth - ballColWidth)) + (col > pxColumnNbr ? separatorPaddingSize : 0);
                text_arr[row][col].y = 0;
                text_arr[row][col].text = "row" + row;
                text_arr[row][col].width = (col == 0) ? (ballColWidth - 5) : (colWidth - 5);
                text_arr[row][col].height = rowHeight - 5;
                text_arr[row][col].border = false;
                //Not user-settable: ballnbr, mass, px, py
                if ( row <= unitsRowNbr || col == 0 || col == pxColumnNbr ) {
                    text_arr[row][col].type = TextFieldType.DYNAMIC;
                    text_arr[row][col].selectable = false;
                }
                else {
                    if ( col > 0 && col < 3 ) {
                        dressInputTextField( row, col );
                        text_arr[row][col].restrict = "0-9.";
                    }
                    else {
                        if ( col == vxColumnNbr ) {
                            dressInputTextField( row, col );
                            text_arr[row][col].restrict = "0-9.\\-";  //velocities can be negative
                        }
                        else {
//                            trace( "ERROR in DataTable.initialize. Invalid value of col" );
                        }
                    }
                }
            }
        }
        drawBorder( nbrBalls );  //nbr of rows
        //manually set rowWidth for 1st call to positionButtons()
        rowWidth = 4.5 * colWidth;
        positionButtons();
        makeHeaderRow();
        //setNbrDisplayedRows();  //not necessary, called in update()
        createTextChangeListeners();
        Util.makePanelDraggableWithBorder( this, invisibleBorder );
        update();
    }

    public function reset(): void {
        checkBallNbrLimits();
        if ( showingMore ) {
            // unfortunately best way of resetting more/less view
            toggleDataButton();
        }
    }

    //following function to be altered during internationalization
    public function initializeStrings(): void {
        addBallButton.setLabel( SimStrings.get( "DataTable.addBall", "Add Ball" ) );
        removeBallButton.setLabel( SimStrings.get( "DataTable.removeBall", "Remove Ball" ) );
        moreDataButton.setLabel( SimStrings.get( "DataTable.moreData", "More Data" ) );
        lessDataButton.setLabel( SimStrings.get( "DataTable.lessData", "Less Data" ) );
    }

    public function get nbrColumns(): int {
        return 5;
    }

    /**
     * Maximum number of rows (if all of the balls were included)
     */
    private function get maxRows(): int {
        return CLConstants.MAX_BALLS + headerOffset; //header row + row for each ball
    }

    public function dressInputTextField( i: int, j: int ): void {
        text_arr[i][j].type = TextFieldType.INPUT;
        text_arr[i][j].border = true;
        text_arr[i][j].background = true;
        text_arr[i][j].backgroundColor = 0xffffff;
    }

    private function drawBorder( nbrBalls: int ): void {
        var nbrRows: int = nbrBalls + headerOffset;  //one header row + 1 row per ball
        var g: Graphics = canvas.graphics;
        var bWidth: Number = 5;   //borderWidth
        var del: Number = bWidth / 2;

        // how much we need to chop off the top in the Intro tab when we are not showing more. top row shouldn't be visible
        var offTopOffset: Number = 0;

        g.clear();
        g.lineStyle( bWidth, 0x2222ff );
        g.beginFill( 0xffff99 );
        var areaHeight: Number = nbrRows * rowHeight + 2 * del - offTopOffset;
        g.drawRect( -del, -del + offTopOffset, rowWidth + 2 * del + (showingMore ? separatorPaddingSize : 0), areaHeight );
        g.endFill();

        var gI: Graphics = invisibleBorder.graphics;
        gI.clear();
        gI.lineStyle( bWidth, 0x000000, 0 );
        gI.drawRect( -del, -del + offTopOffset, rowWidth + 2 * del + (showingMore ? separatorPaddingSize : 0), nbrRows * rowHeight + 2 * del - offTopOffset );

        if( showingMore ) {
            var separatorX: Number = (pxColumnNbr + 1) * colWidth - (colWidth - ballColWidth) + separatorPaddingSize / 2;
            g.lineStyle( bWidth, 0x2222ff );
            g.moveTo( separatorX, -del + offTopOffset );
            g.lineTo( separatorX, -del + offTopOffset + areaHeight );
        }
    }

//    public function get vxColumnNbr(): int {
//        return 3;
//    }
//
//    public function get pxColumnNbr(): int {
//        return 4;
//    }

    /**
     * Row number of the main header row
     */
    public function get headerRowNbr(): int {
        return 0;
    }

    public function get unitsRowNbr(): int {
        return 1;
    }

    /**
     * How many rows are added as "headers" at the top
     */
    public function get headerOffset(): int {
        return headerRowNbr + 2; // labels and units
    }

    //header row is
    //ball	mass	x	y	vx	vy	px	py,   no radius for now
    private function makeHeaderRow(): void {
        // add a column above the main header row
        text_arr[0][ballColumnNbr].text = SimStrings.get( "DataTable.ball", "Ball" );
        text_arr[0][massColumnNbr].text = SimStrings.get( "DataTable.mass", "Mass" );
//        text_arr[0][xColumnNbr].text = SimStrings.get( "DataTable.position", "Position" );
        text_arr[0][vxColumnNbr].text = SimStrings.get( "DataTable.velocity", "Velocity" );
        text_arr[0][pxColumnNbr].text = SimStrings.get( "DataTable.momentum", "Momentum" );
//        text_arr[0][initialPositionColumnNbr].text = SimStrings.get( "DataTable.initialVelocity", "Initial Position" );
        text_arr[0][initialVelocityColumnNbr].text = SimStrings.get( "DataTable.initialMomentum", "Initial Velocity" );

        text_arr[1][ballColumnNbr].text = "";
        text_arr[1][massColumnNbr].text = SimStrings.get( "DataTable.units.kilograms", "kg" );
//        text_arr[1][xColumnNbr].text = SimStrings.get( "DataTable.units.meters", "m" );
        text_arr[1][vxColumnNbr].text = SimStrings.get( "DataTable.units.metersPerSecond", "m/s" );
        text_arr[1][pxColumnNbr].text = SimStrings.get( "DataTable.units.kilogramMetersPerSecond", "kg m/s" );
//        text_arr[1][initialPositionColumnNbr].text = SimStrings.get( "DataTable.units.meters", "m" );
        text_arr[1][initialVelocityColumnNbr].text = SimStrings.get( "DataTable.units.metersPerSecond", "m/s" );

        tFormat.bold = true;
        for ( var row: int = 0; row < maxRows; row++ ) {
            if ( row >= headerOffset ) {text_arr[row][0].text = row - headerOffset + 1;}
            for ( var col: int = 0; col < nbrColumns; col++ ) {
                if ( row == 0 || col == 0 ) {
                    text_arr[row][col].setTextFormat( tFormat );
                }
            }
        }

        TextFieldUtils.resizeText( text_arr[headerRowNbr][ballColumnNbr], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[headerRowNbr][massColumnNbr], TextFieldAutoSize.CENTER );
//        TextFieldUtils.resizeText( text_arr[headerRowNbr][xColumnNbr], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[headerRowNbr][vxColumnNbr], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[headerRowNbr][pxColumnNbr], TextFieldAutoSize.CENTER );
//        TextFieldUtils.resizeText( text_arr[headerRowNbr][initialPositionColumnNbr], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[headerRowNbr][initialVelocityColumnNbr], TextFieldAutoSize.CENTER );
//        TextFieldUtils.resizeText( text_arr[0][xColumnNbr], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[0][vxColumnNbr], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[0][pxColumnNbr], TextFieldAutoSize.CENTER );
//        TextFieldUtils.resizeText( text_arr[0][initialPositionColumnNbr], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[0][initialVelocityColumnNbr], TextFieldAutoSize.CENTER );
    }


    public function positionButtons(): void {
        moreDataButton_sp.x += 200;
        moreDataButton_sp.y = invisibleBorder.height + 15;

        // less button is where the more button is (they just toggle visibility)
        lessDataButton_sp.x = moreDataButton_sp.x;
        lessDataButton_sp.y = moreDataButton_sp.y;
    }


    public function setupSlider( mSlider: Slider ): void {
        mSlider.minimum = 0.1;
        mSlider.maximum = 3.0;
        mSlider.snapInterval = 0.1;
        mSlider.value = 1;
        mSlider.width = 2.8 * colWidth;
        mSlider.liveDragging = true;
        mSlider.addEventListener( Event.CHANGE, massSliderListener );
    }

    public function displayPartialDataTable( showSliders: Boolean ): void {
        showingMore = !showSliders;
        canvas.x = 100;
        if ( showSliders ) {
            rowWidth = 5.5 * colWidth;
//            canvas.x = -rowWidth / 2;
        }
        else {
            rowWidth = nbrColumns * colWidth - (colWidth - ballColWidth); // compensate for the shorter ball column
//            canvas.x = -rowWidth / 2;
        }
        drawBorder( nbrBalls );
        //hide all but 1st two columns for partial
        for ( var row: int = 0; row < maxRows; row++ ) {
            if ( row > headerRowNbr ) {
                massSlider_arr[ballNbr( row )].visible = showSliders;
            }
            for ( var col: int = 2; col < nbrColumns; col++ ) {
                text_arr[row][col].visible = !showSliders;
            }
        }
        update(); // since we change the display format for the mass, we need to update
    }

    public function setNbrDisplayedRows(): void {
        nbrBalls = myModel.nbrBalls;
        drawBorder( nbrBalls );
        for ( var i: int = 0; i < maxRows; i++ ) {
            rowCanvas_arr[i].visible = i < nbrBalls + headerOffset;
        }
    }

    public function createTextChangeListeners(): void {
        for ( var row: int = 1; row < maxRows; row++ ) {
            for ( var col: int = 1; col < nbrColumns; col++ ) {
                if ( col == massColumnNbr ) {
                    text_arr[row][col].addEventListener( Event.CHANGE, changeMassListener );
                }
//                if ( col == xColumnNbr ) {
//                    text_arr[row][col].addEventListener( Event.CHANGE, changeXListener );
//                }
                if ( col == vxColumnNbr ) {
                    text_arr[row][col].addEventListener( Event.CHANGE, changeVXListener );
                }
            }
        }
    }

    public function setPositionHighlight( ballIndex: int, highlighted: Boolean ): void {
//        text_arr[rowOfBall( ballIndex )][xColumnNbr].backgroundColor = highlighted ? highlightColor : unhighlightColor;
    }

    public function setVelocityHighlight( ballIndex: int, highlighted: Boolean ): void {
        text_arr[rowOfBall( ballIndex )][vxColumnNbr].backgroundColor = highlighted ? highlightColor : unhighlightColor;
    }

    private function changeMassListener( evt: Event ): void {
        manualUpdating = true;
        var mass: Number = Number( evt.target.text );
        var row: Number = Number( evt.target.name );  //first ball is ball 1, is Model.ball_arr[0]
        var ballNum: int = ballNbr( row );
        myModel.setMass( ballNum, mass );
        massSlider_arr[ballNum].value = mass;
        myMainView.myTableView.ballImage_arr[ballNum].drawLayer1();  //redraw ballImage for new diameter
        myMainView.myTableView.ballImage_arr[ballNum].drawLayer1a(); //redraw ballImage for new diameter
        myMainView.myTableView.ballImage_arr[ballNum].drawLayer4();  //redraw ballImage for new diameter
        manualUpdating = false;
    }

    private function changeXListener( evt: Event ): void {
        manualUpdating = true;
        var xPos: Number = evtTextToNumber( evt );
        var row: Number = Number( evt.target.name );  //first ball is ball 1, is Model.ball_arr[0]
        myModel.setX( ballNbr( row ), xPos );
        manualUpdating = false;
    }

    private function changeYListener( evt: Event ): void {
        manualUpdating = true;
        var yPos: Number = evtTextToNumber( evt );
        var row: Number = Number( evt.target.name );  //first ball is ball 1, is Model.ball_arr[0]
        myModel.setY( ballNbr( row ), yPos );
        manualUpdating = false;
    }

    private function changeVXListener( evt: Event ): void {
        manualUpdating = true;
        //var xVel = Number(evt.target.text);
        var xVel: Number = evtTextToNumber( evt );
        var row: Number = Number( evt.target.name );  //first ball is ball 1, is Model.ball_arr[0]
        myModel.setVX( ballNbr( row ), xVel );
        manualUpdating = false;
    }

    private function changeVYListener( evt: Event ): void {
        manualUpdating = true;
        //var yVel = Number(evt.target.text);
        var yVel: Number = evtTextToNumber( evt );
        var row: int = Number( evt.target.name );  //first ball is ball 1, is Model.ball_arr[0]
        myModel.setVY( ballNbr( row ), yVel );
        manualUpdating = false;
    }


    private function evtTextToNumber( evt: Event ): Number {
        var inputText: String = evt.target.text;
        var outputNumber: Number;
        if ( inputText == "." ) {
            evt.target.text = "0.";
            evt.target.setSelection( 2, 2 ); //sets cursor at end of line
            outputNumber = 0;
        }
        else {
            if ( inputText == "-" ) {
                outputNumber = 0;
            }
            else {
                if ( inputText == "-." ) {
                    evt.target.text = "-0.";
                    evt.target.setSelection( 3, 3 ); //sets cursor at end of line
                    outputNumber = 0;
                }
                else {
                    if ( isNaN( Number( inputText ) ) ) {
                        evt.target.text = "0";
                        outputNumber = 0;
                    }
                    else {
                        outputNumber = Number( inputText );
                    }
                }
            }
        }
        return outputNumber;
    }

    private function massSliderListener( evt: SliderEvent ): void {
        sliderUpdating = true;
        var ballNbr: int = Number( evt.target.name );
        var mass = Number( evt.target.value );
        myModel.setMass( ballNbr, mass );
        myMainView.myTableView.ballImage_arr[ballNbr].drawLayer1();  //redraw ballImage for new diameter
        myMainView.myTableView.ballImage_arr[ballNbr].drawLayer1a(); //redraw ballImage for new diameter
        myMainView.myTableView.ballImage_arr[ballNbr].drawLayer4();  //redraw ballImage for new diameter
        sliderUpdating = false;
    }

    private function resetMassSliders(): void {  //called when reset button on Control panel
        for ( var i: int = 0; i < CLConstants.MAX_BALLS; i++ ) { // TODO: not adding +1 to MAX_BALLS. Is this an error?
            massSlider_arr[i].value = myModel.ball_arr[i].getMass();
            myMainView.myTableView.ballImage_arr[i].drawLayer1();  //redraw ballImage for new diameter
            myMainView.myTableView.ballImage_arr[i].drawLayer1a(); //redraw ballImage for new diameter
            myMainView.myTableView.ballImage_arr[i].drawLayer4();  //redraw ballImage for new diameter
        }
    }

    private function toggleDataButton(): void {
        if ( moreDataButton_sp.visible ) {
            moreDataButton_sp.visible = false;
            lessDataButton_sp.visible = true;
            displayPartialDataTable( false );
        }
        else {
            moreDataButton_sp.visible = true;
            lessDataButton_sp.visible = false;
            displayPartialDataTable( true );
        }
    }

    private function addBall(): void {
        myModel.addBall();
        checkBallNbrLimits();
    }

    private function removeBall(): void {
        myModel.removeBall();
        checkBallNbrLimits();
    }

    //if nbrBalls = max or min allowed, adjust display appropriately
    public function checkBallNbrLimits(): void {
        if ( nbrBalls == 1 ) {
            removeBallButton_sp.visible = false;
            myMainView.myTableView.CM.visible = false;
        }
        else {
            removeBallButton_sp.visible = true;
            if ( myMainView.controlPanel.showCMOn ) {
                myMainView.myTableView.CM.visible = true;
            }
        }
        addBallButton_sp.visible = nbrBalls != CLConstants.MAX_BALLS;
    }

    private function get generalPrecision(): int {
        return 2;
    }

    private function get massPrecision(): int {
        return showingMore ? generalPrecision : 1;
    }

    public function update(): void {
        setNbrDisplayedRows();
        var row: int;
        var col: int;
        var mass: Number;
        var yVel: Number;
        if ( !manualUpdating ) {   //do not update if user is manually filling textFields
            for ( row = headerOffset; row < maxRows; row++ ) {  //skip header row
                var ballNum: int = ballNbr( row );
                for ( col = 0; col < nbrColumns; col++ ) {
                    if ( col == massColumnNbr ) { // mass in kg
                        mass = myModel.ball_arr[ballNum].getMass();
                        text_arr[row][col].text = round( mass, 1 ); //round(mass, 1);
                    }
//                    if ( col == xColumnNbr ) { //x position in m
//                        var xPos: Number = myModel.ball_arr[ballNum].position.getX();
//                        text_arr[row][col].text = round( xPos, generalPrecision ); //round(xPos, nbrPlaces);
//                    }
                    if ( col == vxColumnNbr ) { // v_x in m/s
                        var xVel: Number = myModel.ball_arr[ballNum].velocity.getX();
                        text_arr[row][col].text = round( xVel, 1 );
//                        text_arr[row][col].text = xVel.toFixed( generalPrecision ); //round(xVel, nbrPlaces);
                    }
//                    if ( col == initialPositionColumnNbr ) {
//                        text_arr[row][col].text = round( myModel.ball_arr[ballNum].initialPosition.getX(), generalPrecision );
//                    }
                    if ( col == initialVelocityColumnNbr ) {
                        text_arr[row][col].text = round( myModel.ball_arr[ballNum].initialVelocity.getX(), 1 );
                    }
                }
            }
        }

        if ( sliderUpdating ) {
            // WARNING: here, "row" is actually the ball number
            for ( row = 0; row < CLConstants.MAX_BALLS; row++ ) {
                mass = myModel.ball_arr[row].getMass();
                text_arr[rowOfBall( row )][massColumnNbr].text = round( mass, massPrecision );
            }
        }

        if ( myModel.resetting ) {
            resetMassSliders();
        }

        //update Momenta fields regardless of whether user is manually updating other fields
        for ( row = headerOffset; row < maxRows; row++ ) {  //skip header row
            mass = myModel.ball_arr[ballNbr( row )].getMass();
            xVel = myModel.ball_arr[ballNbr( row )].velocity.getX();
            yVel = myModel.ball_arr[ballNbr( row )].velocity.getY();

            var xMom: Number = mass * xVel;
            text_arr[row][pxColumnNbr].text = xMom.toFixed( generalPrecision );
        }
    }

    private function ballNbr( row: int ) {
        return row - headerOffset;
    }

    private function rowOfBall( ballNum: int ) {
        return ballNum + headerOffset;
    }

    //round decimal number to n places; made obsolete by toFixed() Number function
    private function round( input: Number, nPlaces: int ): String {
        var result: Number;
        var factor: Number = Math.pow( 10, nPlaces );
        result = Math.round( factor * input ) / factor;
        return result.toFixed( nPlaces );
    }

}
}