//View of "Pool Table" containing balls
package edu.colorado.phet.collisionlab.view {
import edu.colorado.phet.collisionlab.constants.CLConstants;
import edu.colorado.phet.collisionlab.control.PlayPauseButtons;
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.Util;
import edu.colorado.phet.flashcommon.SimStrings;

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
    public var ballColor_arr:Array = new Array(
            0xff0000, // red
            0x009900, // green
            0x0000ff, // blue
            0xff00ff, // magenta
            0xffff00, // yellow
            0, 0, 0, 0, 0 // others that were already specified. shortening might break this?
            );			//array of uint for colors of balls

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
        this.CM = new CenterOfMass();	//library symbol
        this.canvas = new Sprite();
        this.myMainView.addChild( this );
        this.addChild( this.canvas );
        this.xOffset = 10;  //position of table border relative to origin of stage
        this.yOffset = 30;
        this.canvas.x = xOffset;
        this.canvas.y = yOffset;
        this.border = new Sprite();
        this.invisibleBorder = new Sprite();
        this.playButtons = new PlayPauseButtons( this.myModel );
        this.timeRate_slider = new Slider();
        this.setupSlider();
        this.canvas.addChild( this.border );
        this.canvas.addChild( this.invisibleBorder );
        this.canvas.addChild( this.playButtons );
        this.makeTimeLabel();
        this.makeTotKELabel();
        this.makeTimeRateLabel();
        this.canvas.addChild( this.timeRate_slider );
        this.myModel.registerView( this );
        this.showingPaths = false;
        this.myTrajectories = new Trajectories( this.myModel, this );
        //this.canvas.addChild(this.myTrajectories);
        this.border.addChild( this.myTrajectories );
        this.drawBorder();  //drawBorder() also calls positionLabels() and drawInvisibleBorder()
        //this.ballColor_arr = new Array( 10 );  //start with 10 colors
        this.createBallColors();
        //this.createBallImages2();
        ballLayer = new Sprite();
        this.canvas.addChild( ballLayer );
        this.createBallImages();
        this.canvas.addChild( this.CM );
        if ( this.myModel.nbrBalls == 1 ) {
            this.CM.visible = false;
        }
        Util.makePanelDraggableWithBorder( this, this.invisibleBorder );
        this.update();
        //this.drawBorder();
        //this.ballImageTest = new BallImage(this.myModel, 2, this);
        //this.myModel.startMotion();

        // TODO: put this in reset! Where is that?
        setTotalKEVisible( false );
    }//end of constructor

    public function drawBorder(): void {
        var W: Number = this.myModel.borderWidth * CLConstants.PIXELS_PER_METER;
        var H: Number = this.myModel.borderHeight * CLConstants.PIXELS_PER_METER;
        var thickness: Number = 6;  //border thickness in pixels
        var del: Number = thickness / 2;
        //trace("width: "+W+"    height: "+H);
        var g: Graphics = this.border.graphics;
        g.clear();
        if ( this.myModel.borderOn ) {
            // now we only draw the border if the border is "on".

            if ( this.myModel.borderOn ) {
                g.lineStyle( thickness, 0xFF0000 );
            }
            else {
                g.lineStyle( thickness, 0xffcccc );
            }
            //        var x0: Number = 0;
            //        var y0: Number = 0;
            if ( this.myModel.isIntro ) {
                const largeConst:Number = 5000;
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
        this.playButtons.x = W / 2;
        this.playButtons.y = H + this.playButtons.height / 2;

        this.drawInvisibleBorder();

        this.timeText.text = getTimeText( this.myModel.time.toFixed( 2 ) );
        this.totKEText.text = getKEText( this.myModel.getTotalKE().toFixed( 2 ) );
        this.timeRateText.text = SimStrings.get( "TableView.timeRate", "Sim Speed" );

        //position Time Label
        //this.timeText.width=165;//to improve support for i18n
        this.timeText.x = W - this.timeText.width;
        this.timeText.y = H + 10;
        //trace("this.timeText.width = "+this.timeText.width);

        //position slider
        this.timeRate_slider.x = W - this.timeRate_slider.width - this.timeText.width - 5;
        //        ; //- 1.7*this.timeText.width;
        this.timeRate_slider.y = H + 30;

        //position slider label
        this.timeRateText.y = H + 10;
        this.timeRateText.x = this.timeRate_slider.x + 0.5 * this.timeRate_slider.width / 2;

        //positon KE label
        if ( myModel.isIntro ) {
            // intro tab. position KE at top middle
            totKEText.x = W / 2 - totKEText.width / 2;
            totKEText.y = -20;
        }
        else {
            // advanced tab
            this.totKEText.x = 0;//0.5*this.totKEText.width;//30; //
            this.totKEText.y = H + 10;
        }
        //            this.totKEText.border=true;//to help visualize layout
        this.totKEText.width = 165;//to improve support for i18n
        //trace("drawBorder() called. this.totKEText.width = "+this.totKEText.width);

    }//end of drawBorder();

    //called when 1D/2D mode is switched
    public function reDrawBorder(): void {
        this.drawBorder();
        if ( this.myModel.oneDMode ) {
            var oneDH: Number = CLConstants.BORDER_HEIGHT_1D / 2;
            var twoDH: Number = CLConstants.BORDER_HEIGHT_2D / 2;
            this.canvas.y = yOffset + CLConstants.PIXELS_PER_METER * (twoDH - oneDH);
        }
        else {
            this.canvas.y = yOffset;
        }
        this.update();
    }

    //invisible border is grabbable
    public function drawInvisibleBorder(): void {  //grayed-out border when Reflecting Border is OFF
        var W: Number = this.myModel.borderWidth * CLConstants.PIXELS_PER_METER;
        var H: Number = this.myModel.borderHeight * CLConstants.PIXELS_PER_METER;
        var thickness: Number = 6;  //border thickness in pixels
        var del: Number = thickness / 2;
        //trace("width: "+W+"    height: "+H);
        var g: Graphics = this.invisibleBorder.graphics;
        g.clear();
        g.lineStyle( thickness, 0xffffff, 0 );
        g.moveTo( -del, -del );
        g.lineTo( W + del, -del );
        g.lineTo( W + del, +H + del );
        g.lineTo( -del, +H );
        g.lineTo( -del, -del );
    }//end of drawInvisibleBorder();


    public function makeTimeLabel(): void {
        this.timeText = new TextField();
        this.timeText.text = getTimeText( Number( 0 ).toFixed( 2 ) );
        this.timeText.selectable = false;
        this.timeText.autoSize = TextFieldAutoSize.LEFT;
        var tFormat: TextFormat = new TextFormat();
        tFormat.font = "Arial";
        tFormat.bold = true;
        tFormat.color = 0x000000;
        tFormat.size = 16;
        this.timeText.defaultTextFormat = tFormat;
        //this.timeText.setTextFormat(tFormat);
        this.canvas.addChild( this.timeText );
    }

    public function setTotalKEVisible( visible: Boolean ): void {
        this.totKEText.visible = visible;
    }

    public function makeTotKELabel(): void {
        //following two strings should be set by internationalizer
        this.totKEText = new TextField();
        this.totKEText.text = getKEText( myModel.getTotalKE().toFixed( 2 ) ); //text is set in update
        this.totKEText.selectable = false;
        //			this.totKEText.autoSize = TextFieldAutoSize.RIGHT;
        var tFormat: TextFormat = new TextFormat();
        tFormat.font = "Arial";
        tFormat.bold = true;
        tFormat.color = 0x000000;
        tFormat.size = 14;
        this.totKEText.defaultTextFormat = tFormat;
        //this.timeText.setTextFormat(tFormat);
        this.canvas.addChild( this.totKEText );
    }

    public function makeTimeRateLabel(): void {
        //following two strings should be set by internationalizer
        this.timeRateText = new TextField();
        this.timeRateText.text = SimStrings.get( "TableView.timeRate", "Sim Speed" );
        this.timeRateText.selectable = false;
        //			this.timeRateText.autoSize = TextFieldAutoSize.RIGHT;
        var tFormat: TextFormat = new TextFormat();
        tFormat.font = "Arial";
        tFormat.bold = true;
        tFormat.color = 0x000000;
        tFormat.size = 12;
        this.timeRateText.defaultTextFormat = tFormat;
        //this.timeText.setTextFormat(tFormat);
        this.canvas.addChild( this.timeRateText );
    }

    private function setupSlider(): void {
        this.timeRate_slider.direction = SliderDirection.HORIZONTAL;
        this.timeRate_slider.minimum = 0.01;
        this.timeRate_slider.maximum = 1;
        this.timeRate_slider.snapInterval = 0.02;
        this.timeRate_slider.value = 0.5;
        this.myModel.setTimeRate( 0.5 );
        this.timeRate_slider.liveDragging = true;
        this.timeRate_slider.width = 100;
        this.timeRate_slider.addEventListener( Event.CHANGE, setTimeRate );
    }

    public function setTimeRate( evt: SliderEvent ): void {
        //trace("time slider: "+evt.target.value);
        this.myModel.setTimeRate( evt.target.value );
    }

    public function createBallColors(): void {
        this.ballColor_arr[0] = 0xff0000;
        this.ballColor_arr[1] = 0x009900;
        this.ballColor_arr[2] = 0x0000ff;
        this.ballColor_arr[3] = 0xff00ff;
        this.ballColor_arr[4] = 0xffff00;

    }

    //called once, at startup
    public function createBallImages(): void {
        this.ballImage_arr = new Array( CLConstants.MAX_BALLS );
        for ( var i: int = 0; i < CLConstants.MAX_BALLS; i++ ) {
            var ballImage: BallImage = new BallImage( this.myModel, i, this );
            this.ballImage_arr[i] = ballImage;
            ballImage.x = CLConstants.PIXELS_PER_METER * this.myModel.ball_arr[i].position.getX();
            ballImage.y = CLConstants.PIXELS_PER_METER * this.myModel.ball_arr[i].position.getY();
            if ( i >= this.myModel.nbrBalls ) {
                ballImage_arr[i].visible = false;
            }
        }//end for
        //this.update(); //to make extra balls invisible
    }//end of createBallImages()

    //show velocity arrows on ball images
    public function showArrowsOnBallImages( tOrF: Boolean ): void {
        for ( var i: int = 0; i < CLConstants.MAX_BALLS; i++ ) {
            if ( tOrF ) {
                this.ballImage_arr[i].showArrow( true );
            }
            else {
                this.ballImage_arr[i].showArrow( false );
            }
        }
    }//end showArrowsOnBallImages()

    //show Momentum arrows on ball images
    public function showPArrowsOnBallImages( tOrF: Boolean ): void {
        for ( var i: int = 0; i < CLConstants.MAX_BALLS; i++ ) {
            if ( tOrF ) {
                this.ballImage_arr[i].showPArrow( true );
            }
            else {
                this.ballImage_arr[i].showPArrow( false );
            }
        }
    }//end showPArrowsOnBallImages()


    public function update(): void {
        if ( this.myModel.nbrBallsChanged ) {
            for ( var i: int = 0; i < myModel.nbrBalls; i++ ) {
                this.ballImage_arr[i].visible = true;
            }
            for ( i = myModel.nbrBalls; i < CLConstants.MAX_BALLS; i++ ) {
                this.ballImage_arr[i].visible = false;
            }
            this.myTrajectories.updateNbrPaths();
            this.myTrajectories.erasePaths();
        }


        var yMax: Number = this.myModel.borderHeight / 2;  //recall origin is set at y = borderHeight/2
        for ( i = 0; i < myModel.nbrBalls; i++ ) {
            ballImage_arr[i].x = CLConstants.PIXELS_PER_METER * this.myModel.ball_arr[i].position.getX();
            ballImage_arr[i].y = CLConstants.PIXELS_PER_METER * (yMax - this.myModel.ball_arr[i].position.getY());
            ballImage_arr[i].updateVelocityArrow();
        }

        if ( this.showingPaths ) {
            this.myTrajectories.drawStep();
        }

        if ( this.myModel.atInitialConfig || this.myModel.resetting ) {
            this.myTrajectories.erasePaths();
            //this.myModel.atInitialConfig = false;
        }

        this.timeText.text = getTimeText( this.myModel.time.toFixed( 2 ) );
        this.totKEText.text = getKEText( this.myModel.getTotalKE().toFixed( 2 ) );

        this.CM.x = CLConstants.PIXELS_PER_METER * this.myModel.CM.x;
        this.CM.y = CLConstants.PIXELS_PER_METER * (yMax - this.myModel.CM.y);
    }

    function getKEText( keValue: String ): String {
        return SimStrings.get( "TableView.kineticEnergy", "Kinetic Energy = {0} J", [keValue] );
    }

    function getTimeText( time: String ): String {
        return SimStrings.get( "TableView.time", "Time = {0} s", [time] );
    }
}//end of class

}//end of package