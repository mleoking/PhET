//View of "Pool Table" containing balls
package edu.colorado.phet.collisionlab.view {
import edu.colorado.phet.collisionlab.constants.CLConstants;
import edu.colorado.phet.collisionlab.control.PlayPauseButtons;
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.Util;
import edu.colorado.phet.flashcommon.SimStrings;
import edu.colorado.phet.collisionlab.CollisionLab;

import fl.controls.*;
import fl.events.*;

import flash.display.*;
import flash.events.*;
import flash.text.*;

public class TableView extends Sprite {
    public var myTrajectories: Trajectories;  //Sprite showing trajectories (paths) of balls
    public var CM: CenterOfMass;              //library symbol
    public var playButtons: PlayPauseButtons; //class to hold library symbol, contains dynamic text strings
    public var timeRate_slider: Slider;       //adjusts rate at which time passes
    public var ballImage_arr: Array;					//array of ball images
    public var ballLayer: Sprite; // holds all of the balls
    public var showingPaths: Boolean;                //true if paths are shown
    public var myMainView: MainView;			      //mediator and container of views
    public var canvas: Sprite;					      //background on which everything is placed

    private var myModel: Model;
    private var border: Sprite;                       //reflecting border
    private var invisibleBorder: Sprite;              //handle for dragging
    private var borderColor: uint;                    //color of border 0xrrggbb
    private var timeText: TextField;                  //label containing current time
    private var totKEText: TextField;                 //label showing total KE of particles
    private var timeRateText: TextField;			//label above timeRate slider
    private var ballLabels: Array;				//array of ball labels: 1, 2, 3, ...
    private var xOffset: Number;					//x of upper left corner of canvas
    private var yOffset: Number;					//y of upper left corner of canvas

    public function TableView( myModel: Model, myMainView: MainView ) {
        this.myModel = myModel;
        this.myMainView = myMainView;
        CM = new CenterOfMass();	//library symbol
        canvas = new Sprite();
        myMainView.addChild( this );
        addChild( this.canvas );
        xOffset = 10;  //position of table border relative to origin of stage
        yOffset = 30;
        canvas.x = xOffset;
        canvas.y = yOffset;
        border = new Sprite();
        invisibleBorder = new Sprite();
        playButtons = new PlayPauseButtons( myModel );
        timeRate_slider = new Slider();
        setupSlider();
        canvas.addChild( border );
        canvas.addChild( invisibleBorder );
        canvas.addChild( playButtons );
        makeTimeLabel();
        makeTotKELabel();
        makeTimeRateLabel();
        canvas.addChild( timeRate_slider );
        myModel.registerView( this );
        showingPaths = false;
        myTrajectories = new Trajectories( myModel, this );
        //canvas.addChild(myTrajectories);
        border.addChild( myTrajectories );
        drawBorder();  //drawBorder() also calls positionLabels() and drawInvisibleBorder()
        ballLayer = new Sprite();
        canvas.addChild( ballLayer );
        createBallImages();
        canvas.addChild( CM );
        CM.mouseEnabled = false;
        if ( myModel.nbrBalls == 1 ) {
            CM.visible = false;
        }
        Util.makePanelDraggableWithBorder( this, invisibleBorder );
        update();

        // TODO: put this in reset! Where is that?
        setTotalKEVisible( false );
    }

    public function reset(): void {
        setTotalKEVisible( false );
        reDrawBorder();
        playButtons.resetAllCalled();
        showArrowsOnBallImages( true );
        showPArrowsOnBallImages( false );
        CM.visible = true;
        myTrajectories.pathsOff();
        timeRate_slider.value = myModel.timeRate;
        for each ( var ball: BallImage in ballImage_arr ) {
            ball.setShowValues( false );
        }
    }

    public function drawBorder(): void {
        var W: Number = myModel.borderWidth * CLConstants.PIXELS_PER_METER;
        var H: Number = myModel.borderHeight * CLConstants.PIXELS_PER_METER;
        var thickness: Number = 6;  //border thickness in pixels
        var del: Number = thickness / 2;
        //trace("width: "+W+"    height: "+H);
        var g: Graphics = border.graphics;
        g.clear();
        if ( myModel.borderOn ) {
            // now we only draw the border if the border is "on".

            if ( myModel.borderOn ) {
                g.lineStyle( thickness, 0xFF0000 );
            }
            else {
                g.lineStyle( thickness, 0xffcccc );
            }
            //        var x0: Number = 0;
            //        var y0: Number = 0;
            if ( myModel.isIntro ) {
                const largeConst: Number = 5000;
                g.beginFill( 0xccffcc );
                g.moveTo( -largeConst, -del );
                g.lineTo( W + largeConst, -del );
                g.lineTo( W + largeConst, +H + del );
                g.lineTo( -largeConst, +H + del );
                g.lineTo( -largeConst, -del );
                g.endFill();
            }
            else {
                g.beginFill( 0xccffcc );
                g.moveTo( -del, -del );
                g.lineTo( W + del, -del );
                g.lineTo( W + del, +H + del );
                g.lineTo( -del, +H + del );
                g.lineTo( -del, -del );
                g.endFill();
            }
        }

        //position playButtons
        playButtons.x = W / 2;
        playButtons.y = H + playButtons.height / 2;

        drawInvisibleBorder();

        timeText.text = getTimeText( myModel.time.toFixed( 2 ) );
        totKEText.text = getKEText( myModel.getTotalKE().toFixed( 2 ) );
        timeRateText.text = " " + SimStrings.get( "TableView.timeRate", "Sim Speed" ) + " ";

        //position Time Label
        //timeText.width=165;//to improve support for i18n
        timeText.x = W - timeText.width;
        timeText.y = H + 10;
        //trace("timeText.width = "+timeText.width);

        //position slider
        timeRate_slider.x = W - timeRate_slider.width - timeText.width - 5;
        //        ; //- 1.7*timeText.width;
        timeRate_slider.y = H + 30;

        //position slider label
        timeRateText.y = H + 10;
        timeRateText.x = timeRate_slider.x + (timeRate_slider.width / 2) - (timeRateText.width / 2);

        //positon KE label
        if ( myModel.isIntro ) {
            // intro tab. position KE at top middle
            totKEText.x = W / 2 - totKEText.width / 2;
            totKEText.y = -20;
        }
        else {
            // advanced tab
            totKEText.x = 0;//0.5*totKEText.width;//30; //
            totKEText.y = H + 10;
        }
        //            totKEText.border=true;//to help visualize layout
        totKEText.width = 165;//to improve support for i18n
        //trace("drawBorder() called. totKEText.width = "+totKEText.width);

    }//end of drawBorder();

    //called when 1D/2D mode is switched
    public function reDrawBorder(): void {
        drawBorder();
        if ( myModel.oneDMode ) {
            var oneDH: Number = CLConstants.BORDER_HEIGHT_1D / 2;
            var twoDH: Number = CLConstants.BORDER_HEIGHT_2D / 2;
            canvas.y = yOffset + CLConstants.PIXELS_PER_METER * (twoDH - oneDH);
        }
        else {
            canvas.y = yOffset;
        }
        update();
    }

    //invisible border is grabbable
    public function drawInvisibleBorder(): void {  //grayed-out border when Reflecting Border is OFF
        var W: Number = myModel.borderWidth * CLConstants.PIXELS_PER_METER;
        var H: Number = myModel.borderHeight * CLConstants.PIXELS_PER_METER;
        var thickness: Number = 6;  //border thickness in pixels
        var del: Number = thickness / 2;
        //trace("width: "+W+"    height: "+H);
        var g: Graphics = invisibleBorder.graphics;
        g.clear();
        g.lineStyle( thickness, 0xffffff, 0 );
        g.moveTo( -del, -del );
        g.lineTo( W + del, -del );
        g.lineTo( W + del, +H + del );
        g.lineTo( -del, +H );
        g.lineTo( -del, -del );
    }//end of drawInvisibleBorder();


    public function makeTimeLabel(): void {
        timeText = new TextField();
        timeText.text = getTimeText( Number( 0 ).toFixed( 2 ) );
        timeText.selectable = false;
        timeText.autoSize = TextFieldAutoSize.LEFT;
        var tFormat: TextFormat = new TextFormat();
        tFormat.font = "Arial";
        tFormat.bold = true;
        tFormat.color = 0x000000;
        tFormat.size = 16;
        timeText.defaultTextFormat = tFormat;
        //timeText.setTextFormat(tFormat);
        canvas.addChild( timeText );
    }

    public function setTotalKEVisible( visible: Boolean ): void {
        totKEText.visible = visible;
    }

    public function makeTotKELabel(): void {
        //following two strings should be set by internationalizer
        totKEText = new TextField();
        totKEText.text = getKEText( myModel.getTotalKE().toFixed( 2 ) ); //text is set in update
        totKEText.selectable = false;
        //			totKEText.autoSize = TextFieldAutoSize.RIGHT;
        var tFormat: TextFormat = new TextFormat();
        tFormat.font = "Arial";
        tFormat.bold = true;
        tFormat.color = 0x000000;
        tFormat.size = 14;
        totKEText.defaultTextFormat = tFormat;
        //timeText.setTextFormat(tFormat);
        canvas.addChild( totKEText );
    }

    public function makeTimeRateLabel(): void {
        //following two strings should be set by internationalizer
        timeRateText = new TextField();
        timeRateText.text = " " + SimStrings.get( "TableView.timeRate", "Sim Speed" ) + " ";
        timeRateText.selectable = false;
        timeRateText.autoSize = "left";
        //			timeRateText.autoSize = TextFieldAutoSize.RIGHT;
        var tFormat: TextFormat = new TextFormat();
        tFormat.font = "Arial";
        tFormat.bold = true;
        tFormat.color = 0x000000;
        tFormat.italic = true;
        tFormat.size = 12;
        timeRateText.defaultTextFormat = tFormat;
        //timeText.setTextFormat(tFormat);
        canvas.addChild( timeRateText );
    }

    private function setupSlider(): void {
        timeRate_slider.getChildAt( 1 ).width = CollisionLab.KNOB_SIZE;
        timeRate_slider.getChildAt( 1 ).height = CollisionLab.KNOB_SIZE;
        timeRate_slider.direction = SliderDirection.HORIZONTAL;
        timeRate_slider.minimum = 0.01;
        timeRate_slider.maximum = 1;
        timeRate_slider.snapInterval = 0.02;
        timeRate_slider.value = 0.5;
        myModel.setTimeRate( 0.5 );
        timeRate_slider.liveDragging = true;
        timeRate_slider.width = 100;
        timeRate_slider.addEventListener( Event.CHANGE, setTimeRate );
    }

    public function setTimeRate( evt: SliderEvent ): void {
        //trace("time slider: "+evt.target.value);
        myModel.setTimeRate( evt.target.value );
    }

    //called once, at startup
    public function createBallImages(): void {
        ballImage_arr = new Array( CLConstants.MAX_BALLS );
        for ( var i: int = 0; i < CLConstants.MAX_BALLS; i++ ) {
            var ballImage: BallImage = new BallImage( myModel, i, this );
            ballImage_arr[i] = ballImage;
            ballImage.x = CLConstants.PIXELS_PER_METER * myModel.ball_arr[i].position.getX();
            ballImage.y = CLConstants.PIXELS_PER_METER * myModel.ball_arr[i].position.getY();
            if ( i >= myModel.nbrBalls ) {
                ballImage_arr[i].visible = false;
            }
        }//end for
        //update(); //to make extra balls invisible
    }//end of createBallImages()

    //show velocity arrows on ball images
    public function showArrowsOnBallImages( tOrF: Boolean ): void {
        for ( var i: int = 0; i < CLConstants.MAX_BALLS; i++ ) {
            if ( tOrF ) {
                ballImage_arr[i].showArrow( true );
            }
            else {
                ballImage_arr[i].showArrow( false );
            }
        }
    }//end showArrowsOnBallImages()

    //show Momentum arrows on ball images
    public function showPArrowsOnBallImages( tOrF: Boolean ): void {
        for ( var i: int = 0; i < CLConstants.MAX_BALLS; i++ ) {
            if ( tOrF ) {
                ballImage_arr[i].showPArrow( true );
            }
            else {
                ballImage_arr[i].showPArrow( false );
            }
        }
    }//end showPArrowsOnBallImages()


    public function update(): void {
        if ( myModel.nbrBallsChanged ) {
            for ( var i: int = 0; i < myModel.nbrBalls; i++ ) {
                ballImage_arr[i].visible = true;
            }
            for ( i = myModel.nbrBalls; i < CLConstants.MAX_BALLS; i++ ) {
                ballImage_arr[i].visible = false;
            }
            myTrajectories.updateNbrPaths();
            myTrajectories.erasePaths();
        }

        for ( i = 0; i < myModel.nbrBalls; i++ ) {
            ballImage_arr[i].setVisibilityOfArrowHeadIndicator();
        }

        var yMax: Number = myModel.borderHeight / 2;  //recall origin is set at y = borderHeight/2
        for ( i = 0; i < myModel.nbrBalls; i++ ) {
            ballImage_arr[i].x = CLConstants.PIXELS_PER_METER * myModel.ball_arr[i].position.getX();
            ballImage_arr[i].y = CLConstants.PIXELS_PER_METER * (yMax - myModel.ball_arr[i].position.getY());
            ballImage_arr[i].updateVelocityArrow();
        }

        if ( showingPaths ) {
            myTrajectories.drawStep();
        }

        if ( myModel.atInitialConfig || myModel.resetting ) {
            myTrajectories.erasePaths();
            //myModel.atInitialConfig = false;
        }

        timeText.text = getTimeText( myModel.time.toFixed( 2 ) );
        totKEText.text = getKEText( myModel.getTotalKE().toFixed( 2 ) );

        CM.x = CLConstants.PIXELS_PER_METER * myModel.CM.x;
        CM.y = CLConstants.PIXELS_PER_METER * (yMax - myModel.CM.y);
    }

    function getKEText( keValue: String ): String {
        return SimStrings.get( "TableView.kineticEnergy", "Kinetic Energy = {0} J", [keValue] );
    }

    function getTimeText( time: String ): String {
        return SimStrings.get( "TableView.time", "Time = {0} s", [time] );
    }

    public function addBallChild( ball: BallImage ): void {
        ballLayer.addChild( ball );
    }

    public function removeBallChild( ball: BallImage ): void {
        ballLayer.removeChild( ball );
    }
}//end of class

}//end of package