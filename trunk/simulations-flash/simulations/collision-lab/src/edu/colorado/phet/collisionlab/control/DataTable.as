package edu.colorado.phet.collisionlab.control {
import edu.colorado.phet.collisionlab.constants.CLConstants;
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.Util;
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
    public static const colWidth: int = 60;			//width of column in pix
    public static const rowHeight: int = 27;			//height of row in pix
    public static const MAX_ROWS = CLConstants.MAX_BALLS + 1; //header row + row for each ball

    public var myModel: Model;
    public var myMainView: MainView;
    public var nbrBalls: int;
    public var canvas: Sprite;			//canvas holds several rowCanvases, full data table
    public var invisibleBorder: Sprite;	//draggable border
    public var rowCanvas_arr: Array;	//array of Sprites, each holds one row of textFields
    public var rowWidth: int;			//width of row in pix, used to set borderwidth
    public var text_arr: Array;			//row of textFields, one for each of 9 columns, text must be internationalized
    public var addBallButton: NiceButton;		//button to add ball, originally on Control Panel
    public var removeBallButton: NiceButton; 	//button to remove ball, originally on Control Panel
    public var moreDataButton: NiceButton;//button to toggle full or partial data display
    public var lessDataButton: NiceButton;//button to toggle full or partial data display
    public var addBallButton_sp: Sprite;		//addBallButton sprite
    public var removeBallButton_sp: Sprite;		//removeBallButton sprite
    public var moreDataButton_sp: Sprite;	//moreOrLessDataButton Sprite
    public var lessDataButton_sp: Sprite;	//moreOrLessDataButton Sprite
    public var massSlider_arr: Array;	//array of mass sliders
    public var tFormat: TextFormat;
    public var manualUpdating: Boolean;	//true if user is typing into textField, needed to prevent input-model-output loop
    public var sliderUpdating: Boolean; //true if use is using mass slider

    private const ballColumnNbr: int = 0;
    private const massColumnNbr: int = 1;
    private const xColumnNbr: int = 2;
    private const yColumnNbr: int = 3; // only in intro
    private const vyColumnNbr: int = 5; // only in intro
    private const pyColumnNbr: int = 7; // only in intro

    private const headerRowNbr: int = 0;

    public function DataTable( myModel: Model, myMainView: MainView ) {
        this.myModel = myModel;
        myModel.registerView( this );
        this.myMainView = myMainView;
        nbrBalls = this.myModel.nbrBalls;
        rowCanvas_arr = new Array( MAX_ROWS ); //header row + row for each ball
        text_arr = new Array( MAX_ROWS );	//rows
        massSlider_arr = new Array( MAX_ROWS );
        tFormat = new TextFormat();
        tFormat.font = "Arial";
        tFormat.size = 14;
        tFormat.align = TextFormatAlign.CENTER;

        //create textfields for full data table and mass sliders for partial data table
        for ( var row: int = 0; row < MAX_ROWS; row++ ) {  //header row + row for each ball
            rowCanvas_arr[row] = new Sprite();
            text_arr[row] = new Array( nbrColumns );
            if ( row != headerRowNbr ) {
                var ballNum: int = row - 1;
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
        addBallButton = new NiceButton( addBallButton_sp, 90, addBall );
        removeBallButton = new NiceButton( removeBallButton_sp, 90, removeBall );
        moreDataButton = new NiceButton( moreDataButton_sp, 90, toggleDataButton );
        lessDataButton = new NiceButton( lessDataButton_sp, 90, toggleDataButton );
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
        for ( var row: int = 0; row < MAX_ROWS; row++ ) {
            canvas.addChild( rowCanvas_arr[row] );

            var ballBackground: Sprite = new Sprite();
            if ( row > 0 ) {
                var k: int = row - 1;
                rowCanvas_arr[row].addChild( massSlider_arr[k] );
                massSlider_arr[k].x = 2.2 * colWidth;
                massSlider_arr[k].y = 0.2 * rowHeight;
                massSlider_arr[k].visible = false;

                ballBackground.x = 0;
                ballBackground.y = 0;
                ballBackground.graphics.beginFill( myMainView.myTableView.ballColor_arr[row - 1] );
                ballBackground.graphics.lineStyle( 0, 0x000000 );
                ballBackground.graphics.drawCircle( (colWidth - 5) / 2, (rowHeight - 5) / 2, 10 );
                ballBackground.graphics.endFill();
                rowCanvas_arr[row].addChild( ballBackground );
            }

            for ( var col: int = 0; col < nbrColumns; col++ ) {
                if ( row > 0 && col == 0 ) {
                    var glow: GlowFilter = new GlowFilter( 0x000000, 1.0, 2.0, 2.0, 10 );
                    glow.quality = BitmapFilterQuality.MEDIUM;
                    text_arr[row][col].textColor = 0xFFFFFF;
                    text_arr[row][col].filters = [glow];
                }
                rowCanvas_arr[row].addChild( text_arr[row][col] );
                rowCanvas_arr[row].y = row * rowHeight;
                text_arr[row][col].x = col * colWidth;
                text_arr[row][col].y = 0;
                text_arr[row][col].text = "row" + row;
                text_arr[row][col].width = colWidth - 5;
                text_arr[row][col].height = rowHeight - 5;
                text_arr[row][col].border = false;
                //Not user-settable: ballnbr, mass, px, py
                //0)ball  1)mass  2)x  3)y  4)vx  5)vy  6)px  7)py
                if ( row == 0 || col == 0 || col == 6 || col == 7 ) {
                    text_arr[row][col].type = TextFieldType.DYNAMIC;
                    text_arr[row][col].selectable = false;
                }
                else {
                    if ( col > 0 && col < 4 ) {
                        dressInputTextField( row, col );
                        text_arr[row][col].restrict = "0-9.";
                    }
                    else {
                        if ( col == 4 || col == 5 ) {
                            dressInputTextField( row, col );
                            text_arr[row][col].restrict = "0-9.\\-";  //velocities can be negative
                        }
                        else {
                            trace( "ERROR in DataTable.initialize. Invalid value of j" );
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

    //following function to be altered during internationalization
    public function initializeStrings(): void {
        addBallButton.setLabel( SimStrings.get( "DataTable.addBall", "Add Ball" ) );
        removeBallButton.setLabel( SimStrings.get( "DataTable.removeBall", "Remove Ball" ) );
        moreDataButton.setLabel( SimStrings.get( "DataTable.moreData", "More Data" ) );
        lessDataButton.setLabel( SimStrings.get( "DataTable.lessData", "Less Data" ) );
    }

    public function get nbrColumns(): int {
        return 8; // TODO: replace with 5 in Intro tab
    }

    public function dressInputTextField( i: int, j: int ): void {
        text_arr[i][j].type = TextFieldType.INPUT;
        text_arr[i][j].border = true;
        text_arr[i][j].background = true;
        text_arr[i][j].backgroundColor = 0xffffff;
    }

    private function drawBorder( nbrBalls: int ): void {
        var nbrRows: int = nbrBalls + 1;  //one header row + 1 row per ball
        var g: Graphics = canvas.graphics;
        var bWidth: Number = 5;   //borderWidth
        var del: Number = bWidth / 2;
        g.clear();
        g.lineStyle( bWidth, 0x2222ff );
        g.beginFill( 0xffff99 );
        g.drawRect( -del, -del, rowWidth + 2 * del, nbrRows * rowHeight + 2 * del );
        g.endFill();

        var gI: Graphics = invisibleBorder.graphics;
        gI.clear();
        gI.lineStyle( bWidth, 0x000000, 0 );
        gI.drawRect( -del, -del, rowWidth + 2 * del, nbrRows * rowHeight + 2 * del );
    }

    public function get vxColumnNbr(): int {
        return 4; // TODO: use below version
//        return myModel.isIntro ? 3 : 4;
    }

    public function get pxColumnNbr(): int {
        return 6; // TODO: use below version
//        return myModel.isIntro ? 4:6;
    }

    //header row is
    //ball	mass	x	y	vx	vy	px	py,   no radius for now
    private function makeHeaderRow(): void {
        text_arr[0][ballColumnNbr].text = SimStrings.get( "DataTable.ball", "ball" );
        text_arr[0][massColumnNbr].text = SimStrings.get( "DataTable.mass", "mass" );
        text_arr[0][xColumnNbr].text = SimStrings.get( "DataTable.x", "x" );
        text_arr[0][vxColumnNbr].text = SimStrings.get( "DataTable.vx", "Vx" );
        text_arr[0][pxColumnNbr].text = SimStrings.get( "DataTable.vx", "Px" );
        if ( !myModel.isIntro ) {
            text_arr[0][yColumnNbr].text = SimStrings.get( "DataTable.y", "y" );
            text_arr[0][vyColumnNbr].text = SimStrings.get( "DataTable.vx", "Vy" );
            text_arr[0][pyColumnNbr].text = SimStrings.get( "DataTable.vx", "Py" );
        }
        tFormat.bold = true;
        for ( var row: int = 0; row < MAX_ROWS; row++ ) {
            if ( row != 0 ) {text_arr[row][0].text = row;}
            for ( var col: int = 0; col < nbrColumns; col++ ) {
                if ( row == 0 || col == 0 ) {
                    text_arr[row][col].setTextFormat( tFormat );
                }
            }
        }

        TextFieldUtils.resizeText( text_arr[0][0], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[0][1], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[0][2], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[0][3], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[0][4], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[0][5], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[0][6], TextFieldAutoSize.CENTER );
        TextFieldUtils.resizeText( text_arr[0][7], TextFieldAutoSize.CENTER );
    }


    public function positionButtons(): void {
        addBallButton_sp.x = -0.6 * addBallButton_sp.width - 0.5 * removeBallButton_sp.width;
        addBallButton_sp.y = -0.75 * addBallButton_sp.height;
        removeBallButton_sp.x = 0;
        removeBallButton_sp.y = -0.75 * addBallButton_sp.height;
        moreDataButton_sp.x = 0.5 * removeBallButton_sp.width + 0.8 * moreDataButton_sp.width;
        if ( myModel.isIntro ) {
            moreDataButton_sp.y = invisibleBorder.height + 15;
        }
        else {
            moreDataButton_sp.y = -0.75 * addBallButton_sp.height;
        }

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

    public function displayPartialDataTable( tOrF: Boolean ): void {
        if ( tOrF ) {
            rowWidth = 5.5 * colWidth;
            canvas.x = -rowWidth / 2;
        }
        else {
            rowWidth = nbrColumns * colWidth;
            canvas.x = -rowWidth / 2;
        }
        drawBorder( nbrBalls );
        //hide all but 1st two columns for partial
        for ( var row: int = 0; row < MAX_ROWS; row++ ) {
            if ( row > 0 ) {
                massSlider_arr[row - 1].visible = tOrF;
            }
            for ( var col: int = 2; col < nbrColumns; col++ ) {
                text_arr[row][col].visible = !tOrF;
            }
        }
    }

    public function setNbrDisplayedRows(): void {
        nbrBalls = myModel.nbrBalls;
        drawBorder( nbrBalls );
        for ( var i: int = 0; i < MAX_ROWS; i++ ) {
            rowCanvas_arr[i].visible = i < nbrBalls + 1;
        }
    }

    //ball	mass	x	y	vx	vy	px	py
    public function createTextChangeListeners(): void {
        for ( var row: int = 1; row < MAX_ROWS; row++ ) {
            for ( var col: int = 1; col < nbrColumns; col++ ) {
                //currentBody = i;
                if ( col == 1 ) {
                    text_arr[row][col].addEventListener( Event.CHANGE, changeMassListener );
                }
                else {
                    if ( col == 2 ) {
                        text_arr[row][col].addEventListener( Event.CHANGE, changeXListener );
                    }
                    else {
                        if ( col == 3 ) {
                            text_arr[row][col].addEventListener( Event.CHANGE, changeYListener );
                        }
                        else {
                            if ( col == 4 ) {
                                text_arr[row][col].addEventListener( Event.CHANGE, changeVXListener );
                            }
                            else {
                                if ( col == 5 ) {
                                    text_arr[row][col].addEventListener( Event.CHANGE, changeVYListener );
                                }
                                else {
                                    if ( col == 6 ) {
                                        //do nothing
                                    }
                                    else {
                                        if ( col == 7 ) {
                                            //do nothing
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private function changeMassListener( evt: Event ): void {
        manualUpdating = true;
        var mass: Number = Number( evt.target.text );
        var ballNbr: Number = Number( evt.target.name );  //first ball is ball 1, is Model.ball_arr[0]
        myModel.setMass( ballNbr - 1, mass );
        massSlider_arr[ballNbr - 1].value = mass;
        myMainView.myTableView.ballImage_arr[ballNbr - 1].drawLayer1();  //redraw ballImage for new diameter
        myMainView.myTableView.ballImage_arr[ballNbr - 1].drawLayer1a(); //redraw ballImage for new diameter
        myMainView.myTableView.ballImage_arr[ballNbr - 1].drawLayer4();  //redraw ballImage for new diameter
        manualUpdating = false;
    }

    private function changeXListener( evt: Event ): void {
        manualUpdating = true;
        var xPos: Number = evtTextToNumber( evt );
        var ballNbr: Number = Number( evt.target.name );  //first ball is ball 1, is Model.ball_arr[0]
        myModel.setX( ballNbr - 1, xPos );
        manualUpdating = false;
    }

    private function changeYListener( evt: Event ): void {
        manualUpdating = true;
        var yPos: Number = evtTextToNumber( evt );
        var ballNbr: Number = Number( evt.target.name );  //first ball is ball 1, is Model.ball_arr[0]
        myModel.setY( ballNbr - 1, yPos );
        manualUpdating = false;
    }

    private function changeVXListener( evt: Event ): void {
        manualUpdating = true;
        //var xVel = Number(evt.target.text);
        var xVel: Number = evtTextToNumber( evt );
        var ballNbr: Number = Number( evt.target.name );  //first ball is ball 1, is Model.ball_arr[0]
        myModel.setVX( ballNbr - 1, xVel );
        manualUpdating = false;
    }

    private function changeVYListener( evt: Event ): void {
        manualUpdating = true;
        //var yVel = Number(evt.target.text);
        var yVel: Number = evtTextToNumber( evt );
        var ballNbr: int = Number( evt.target.name );  //first ball is ball 1, is Model.ball_arr[0]
        myModel.setVY( ballNbr - 1, yVel );
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
    }//end textToNumber

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

    public function update(): void {
        setNbrDisplayedRows();
        var row: int;
        var col: int;
        var mass: Number;
        if ( !manualUpdating ) {   //do not update if user is manually filling textFields
            for ( row = 1; row < MAX_ROWS; row++ ) {  //skip header row
                for ( col = 0; col < nbrColumns; col++ ) {
                    if ( col == 0 ) {
                        //do nothing
                    }
                    else {
                        if ( col == 1 ) {            //mass in kg
                            mass = myModel.ball_arr[row - 1].getMass();
                            text_arr[row][col].text = mass.toFixed( 1 ); //round(mass, 1);
                        }
                        else {
                            if ( col == 2 ) {    //x position in m
                                var xPos: Number = myModel.ball_arr[row - 1].position.getX();
                                //trace("DataTable, xPos of ball "+ i +" = "+xPos);
                                text_arr[row][col].text = xPos.toFixed( 3 ); //round(xPos, nbrPlaces);
                            }
                            else {
                                if ( col == 3 ) {    //y position in m
                                    var yPos: Number = myModel.ball_arr[row - 1].position.getY();
                                    text_arr[row][col].text = yPos.toFixed( 3 ); //round(yPos, nbrPlaces);
                                }
                                else {
                                    if ( col == 4 ) {    //v_x in m/s
                                        var xVel: Number = myModel.ball_arr[row - 1].velocity.getX();
                                        text_arr[row][col].text = xVel.toFixed( 3 ); //round(xVel, nbrPlaces);
                                    }
                                    else {
                                        if ( col == 5 ) {    //v_y in m/s
                                            var yVel: Number = myModel.ball_arr[row - 1].velocity.getY();
                                            text_arr[row][col].text = yVel.toFixed( 3 ); //round(yVel, nbrPlaces);
                                        }
                                        else {
                                            if ( col == 6 ) {    //p_x in kg*m/s
                                                //do nothing
                                            }
                                            else {
                                                if ( col == 7 ) {    //p_y in kg*m/s
                                                    //do nothing
                                                }
                                                else {
                                                    trace( "ERROR in DataTable. Incorrect column index. j = " + col );
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if ( sliderUpdating ) {
            for ( row = 0; row < CLConstants.MAX_BALLS; row++ ) {
                mass = myModel.ball_arr[row].getMass();
                text_arr[row + 1][1].text = round( mass, 1 );
            }
        }

        if ( myModel.resetting ) {
            resetMassSliders();
        }

        //update Momenta fields regardless of whether user is manually updating other fields
        for ( row = 1; row < MAX_ROWS; row++ ) {  //skip header row
            mass = myModel.ball_arr[row - 1].getMass();
            xVel = myModel.ball_arr[row - 1].velocity.getX();
            yVel = myModel.ball_arr[row - 1].velocity.getY();
            for ( col = 0; col < nbrColumns; col++ ) {
                if ( col == 6 ) {    //p_x in kg*m/s
                    var xMom: Number = mass * xVel;
                    text_arr[row][col].text = xMom.toFixed( 3 ); //round(xMom, nbrPlaces);
                }
                else {
                    if ( col == 7 ) {    //p_y in kg*m/s
                        var yMom: Number = mass * yVel;
                        text_arr[row][col].text = yMom.toFixed( 3 ); //round(yMom, nbrPlaces);
                    }
                }
            }
        }
    }

    //round decimal number to n places; made obsolete by toFixed() Number function
    private function round( input: Number, nPlaces: int ): Number {
        var result: Number;
        var factor: Number = Math.pow( 10, nPlaces );
        result = Math.round( factor * input ) / factor;
        return result
    }

}
}