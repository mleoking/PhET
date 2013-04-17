//View and Controller of ball in TableView
//BallImage has 6 sprite layers,
// 1: colored ball, on bottom, not grabbable
// 1a: momentum arrow, not grabbable
// 2: velocity arrow, not grabbable
// 2a: ball number label
// 3: arrowHead indicator (shows location of arrowHead when arrow length is small
// 4: transparent disk for dragging ball, to set position
// 5: transparent arrow head, for dragging velocity arrow, to set velocity, on top

package edu.colorado.phet.collisionlab.view {
import edu.colorado.phet.collisionlab.constants.CLConstants;
import edu.colorado.phet.collisionlab.control.DataTable;
import edu.colorado.phet.collisionlab.model.Ball;
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.TwoVector;

import edu.colorado.phet.flashcommon.SimStrings;

import flash.display.*;
import flash.events.*;
import flash.filters.*;
import flash.geom.Point;
import flash.text.*;

public class BallImage extends Sprite {
    public static const ballColor_arr: Array = new Array(
            0xff0000, // red
            0x009900, // green
            0x0000ff, // blue
            0xff00ff, // magenta
            0xffff00, // yellow
            0, 0, 0, 0, 0 // others that were already specified. shortening might break this?
            );			//array of uint for colors of balls

    public var myModel: Model;
    public var myTableView: TableView;
    public var myBall: Ball;
    public var ballIndex: int;			//index labels ball 1, 2, 3,
    public var ballBody: Sprite;
    public var pArrowImage: Arrow;				//momentum arrow, not grabbable
    public var vArrowImage: Arrow;				//velocity arrow, not grabbable
    public var ballHandle: Sprite; // this is the "draggable" target for the ball
    private var velocityReadoutText: TextField;
    private var momentumReadoutText: TextField;
    public var arrowHeadIndicator: Sprite; 		//shows user where tip of arrow head is
    public var arrowHeadHandle: Sprite;			//user grabs this handle to set velocity with mouse
    public var arrowShown: Boolean;				//true if velocity arrow visible
    public var tFieldBallNbr: TextField;		//label = ball number
    public var xEqString: String;				//"x = "  All text must be programmatically set for internationalization
    public var yEqString: String;				//"y = "
    public var showValues: Boolean = false;

    private var mouseOverBallHandle: Boolean = false;
    private var mouseDownOnArrow: Boolean = false;

    private static const arrowDragRadius: Number = 15; // radius of the arrow "handle" and visible marker is

    public function BallImage( myModel: Model, indx: int, myTableView: TableView ) {
        this.myModel = myModel;
        this.myTableView = myTableView;
        this.ballIndex = indx;
        this.myBall = this.myModel.ball_arr[this.ballIndex];
        this.ballBody = new Sprite();
        this.vArrowImage = new Arrow( indx );
        this.vArrowImage.setScale( 100 );  //normal scale is 50
        this.vArrowImage.setColor( CLConstants.COLOR_VELOCITY_ARROW );
        this.pArrowImage = new Arrow( indx );
        this.pArrowImage.setScale( 110 );
        this.pArrowImage.setColor( CLConstants.COLOR_MOMENTUM_ARROW );
        this.pArrowImage.setShaftWidth( 13 );
        this.pArrowImage.setMaxHeadLength( 20 );
        this.showPArrow( false );
        this.ballHandle = new Sprite();
        this.arrowHeadIndicator = new Sprite();
        this.arrowHeadHandle = new Sprite();
        this.tFieldBallNbr = new TextField();
        var outline: GlowFilter = new GlowFilter( 0x000000, 1.0, 2.0, 2.0, 10 ); //outline for ball number text for better visibility
        outline.quality = BitmapFilterQuality.MEDIUM;
        var ballNbr: String = String( 1 + this.ballIndex );
        this.tFieldBallNbr.text = ballNbr;
        this.tFieldBallNbr.filters = [outline];
        this.xEqString = "x = ";
        this.yEqString = "y = ";

        var ballLabelTextFormat: TextFormat = new TextFormat();
        ballLabelTextFormat.font = "Arial";
        ballLabelTextFormat.bold = true;
        ballLabelTextFormat.color = 0xffffff;
        ballLabelTextFormat.size = 20;

        var ballReadoutTextFormat: TextFormat = new TextFormat();
        ballReadoutTextFormat.bold = true;
        ballReadoutTextFormat.font = "Arial";
        ballReadoutTextFormat.color = 0x000000;
        ballReadoutTextFormat.size = 12;
        ballReadoutTextFormat.align = TextFormatAlign.CENTER;

        // TODO: better sizes on readouts for i18n. not provided by Flash CS4.

        velocityReadoutText = new TextField();
        velocityReadoutText.width = 100;
        velocityReadoutText.height = 35;
        velocityReadoutText.backgroundColor = 0xFFFFFF;
        velocityReadoutText.background = true;
        velocityReadoutText.borderColor = 0x000000;
        velocityReadoutText.border = true;
        velocityReadoutText.selectable = false;
        velocityReadoutText.defaultTextFormat = ballReadoutTextFormat;

        momentumReadoutText = new TextField();
        momentumReadoutText.width = 120;
        momentumReadoutText.height = 35;
        momentumReadoutText.backgroundColor = 0xFFFFFF;
        momentumReadoutText.background = true;
        momentumReadoutText.borderColor = 0x000000;
        momentumReadoutText.border = true;
        momentumReadoutText.selectable = false;
        momentumReadoutText.defaultTextFormat = ballReadoutTextFormat;

        setReadoutsVisible( false ); // don't show them initially

        ballHandle.addEventListener( MouseEvent.MOUSE_OVER, function( evt: MouseEvent ): void {
            mouseOverBallHandle = true;
            updateShowValuesVisibility();
        } );

        ballHandle.addEventListener( MouseEvent.MOUSE_OUT, function( evt: MouseEvent ): void {
            mouseOverBallHandle = false;
            updateShowValuesVisibility();
        } );

        this.tFieldBallNbr.defaultTextFormat = ballLabelTextFormat;
        this.setLayerDepths();
        this.drawLayer1();
        this.drawLayer1a();
        this.drawLayer2();
        this.drawLayer2a();
        this.drawLayer3();
        this.drawLayer4();
        this.drawLayer5();
        this.makeBallDraggable();
        this.makeArrowDraggable();
        this.arrowShown = true;
    }

    private function updateShowValuesVisibility(): void {
        // if showValues == true, it is already visible. don't toggle
        if ( !showValues ) {
            // NOTE: never show readouts unless "Show Values" is on
            //setReadoutsVisible( mouseOverBallHandle || mouseDownOnArrow );
        }
    }

    private function setLayerDepths(): void {
        this.myTableView.addBallChild( this );
        this.addChild( this.ballBody );
        this.addChild( this.arrowHeadIndicator );
        this.addChild( this.pArrowImage );
        this.addChild( this.vArrowImage );
        this.addChild( this.tFieldBallNbr );
        this.addChild( this.ballHandle );
        this.addChild( this.arrowHeadHandle );
        addChild( velocityReadoutText );
        addChild( momentumReadoutText );
    }

    public function drawLayer1(): void {
        var g: Graphics = this.ballBody.graphics;
        var currentColor: uint = BallImage.ballColor_arr[this.ballIndex];
        var radius: Number = this.myBall.getRadius();
        g.clear();
        g.lineStyle( 1, 0x000000, 1, false );
        g.beginFill( currentColor );
        var viewRadius: Number = radius * CLConstants.PIXELS_PER_METER;
        g.drawCircle( 0, 0, viewRadius );
        g.endFill();

        var readoutPaddingFromBall: Number = 5;

        // TODO: reposition readouts from here
        velocityReadoutText.x = -velocityReadoutText.width / 2;
        velocityReadoutText.y = -viewRadius - velocityReadoutText.height - readoutPaddingFromBall;

        momentumReadoutText.x = -momentumReadoutText.width / 2;
        momentumReadoutText.y = viewRadius + readoutPaddingFromBall;
    }

    public function updateReadouts(): void {
        var vCaption: String;
        var vReadout: String;
        var pCaption: String;
        var pReadout: String;
        if ( myModel.isIntro ) {
            vCaption = SimStrings.get( "ShowValues.velocityCaption", "Velocity (m/s)" );
            vReadout = SimStrings.get( "ShowValues.velocityReadout", "v = {0}", [this.myModel.ball_arr[this.ballIndex].velocity.getX().toFixed( 2 )] );
            pCaption = SimStrings.get( "ShowValues.momentumCaption", "Momentum (kg m/s)" );
            pReadout = SimStrings.get( "ShowValues.momentumReadout", "p = {0}", [this.myModel.ball_arr[this.ballIndex].momentum.getX().toFixed( 2 )] );
        }
        else {
            vCaption = SimStrings.get( "ShowValues.speedCaption", "Speed (m/s)" );
            vReadout = SimStrings.get( "ShowValues.speedReadout", "| v | = {0}", [this.myModel.ball_arr[this.ballIndex].velocity.getLength().toFixed( 2 )] );
            pCaption = SimStrings.get( "ShowValues.momentumCaption", "Momentum (kg m/s)" );
            pReadout = SimStrings.get( "ShowValues.absoluteMomentumReadout", "| p | = {0}", [this.myModel.ball_arr[this.ballIndex].momentum.getLength().toFixed( 2 )] );
        }
        velocityReadoutText.text = vCaption + "\n" + vReadout;
        momentumReadoutText.text = pCaption + "\n" + pReadout;
    }

    public function setReadoutsVisible( visible: Boolean ): void {
        velocityReadoutText.visible = visible;
        momentumReadoutText.visible = visible;
    }

    public function setShowValues( showValues: Boolean ): void {
        this.showValues = showValues;
        setReadoutsVisible( showValues );
    }

    public function drawLayer1a(): void {
        this.pArrowImage.setArrow( this.myModel.ball_arr[this.ballIndex].momentum );
        //trace("velocityY: "+this.myModel.ball_arr[this.ballIndex].velocity.getY());
        this.pArrowImage.setText( "" );

        updateReadouts();
    }

    public function drawLayer2(): void {
        this.vArrowImage.setArrow( this.myModel.ball_arr[this.ballIndex].velocity );
        //trace("velocityY: "+this.myModel.ball_arr[this.ballIndex].velocity.getY());
        this.vArrowImage.setText( "" );

        updateReadouts();
    }

    public function drawLayer2a(): void {
        var ballNbr: int = this.ballIndex + 1;
        var ballNbr_str: String = String( ballNbr );
        this.tFieldBallNbr.text = ballNbr_str;
        this.tFieldBallNbr.autoSize = TextFieldAutoSize.LEFT;
        this.tFieldBallNbr.height = 15;
        this.tFieldBallNbr.x = -this.tFieldBallNbr.width / 2;
        this.tFieldBallNbr.y = -this.tFieldBallNbr.height / 2;
    }

    public function drawLayer3(): void {
        var g: Graphics = this.arrowHeadIndicator.graphics;

        // draw the "Velocity" arrow head label
        g.clear();
        g.lineStyle( 2, 0x666666 );
        g.drawCircle( 0, 0, arrowDragRadius );
        var arrowHeadLabel: TextField = new TextField();
        arrowHeadLabel.autoSize = "left";
        arrowHeadLabel.text = "V";
        arrowHeadLabel.textColor = 0x666666;
        var fmt: TextFormat = new TextFormat();
        fmt.font = "Arial";
        fmt.size = 20;
        arrowHeadLabel.setTextFormat( fmt );

        // center label
        arrowHeadLabel.x = -arrowHeadLabel.width / 2;
        arrowHeadLabel.y = -arrowHeadLabel.height / 2;
        arrowHeadIndicator.addChild( arrowHeadLabel );
        this.arrowHeadIndicator.visible = false;
    }

    public function drawLayer4(): void {    //ballHandle
        var g: Graphics = this.ballHandle.graphics;
        var currentColor: uint = 0xffff00;
        var alpha1: Number = 0;
        var r: Number = this.myBall.getRadius();
        g.clear();
        g.beginFill( currentColor, alpha1 );
        g.drawCircle( 0, 0, r * CLConstants.PIXELS_PER_METER );
        g.endFill();
    }

    public function drawLayer5(): void {  //arrowHeadHandle
        var g: Graphics = this.arrowHeadHandle.graphics;
        var currentColor: uint = 0xffffff;
        var alpha1: Number = 0;
        g.clear();
        g.beginFill( currentColor, alpha1 );
        g.drawCircle( 1, 0, arrowDragRadius );
        g.endFill();
        this.arrowHeadHandle.x = this.vArrowImage.getTipX();
        this.arrowHeadHandle.y = this.vArrowImage.getTipY();
    }


    public function makeBallDraggable(): void {
        var target: Sprite = this.ballHandle;
        var thisBallImage: BallImage = this;
        target.buttonMode = true;
        var indx: int = ballIndex;
        var modelRef: Model = this.myModel;
        var H: Number = modelRef.borderHeight;
        var W: Number = modelRef.borderWidth;
        var ballX: Number;	//current ball coordinates in meters
        var ballY: Number;

        var overBall: Boolean = false;
        var dragging: Boolean = false;

        //target.addEventListener(MouseEvent.MOUSE_OVER, bringToTop);
        target.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        target.addEventListener( MouseEvent.MOUSE_OVER, function( e: MouseEvent ): void {
            overBall = true
            checkHighlight();
        } );
        target.addEventListener( MouseEvent.MOUSE_OUT, function( e: MouseEvent ): void {
            overBall = false
            checkHighlight();
        } );
        var theStage: Object = thisBallImage.myTableView.canvas;//target.parent;
        var clickOffset: Point;

        function checkHighlight(): void {
            if ( overBall || dragging ) {
                thisBallImage.myTableView.myMainView.myDataTable.setPositionHighlight( thisBallImage.ballIndex, true );
            }
            else {
                thisBallImage.myTableView.myMainView.myDataTable.setPositionHighlight( thisBallImage.ballIndex, false );
            }
        }

        function startTargetDrag( evt: MouseEvent ): void {
            //next two lines bring selected ball to top, so velocity arrow visible
            //and bring C.M. icon to top, so not hidden behind any ball
            // TODO: this seems to be messing up everything. why do we have a ball layer in TableView?
            thisBallImage.myTableView.addBallChild( thisBallImage );
            //problem with localX, localY if sprite is rotated.
            clickOffset = new Point( evt.localX, evt.localY );
            dragging = true;
            checkHighlight();
            thisBallImage.myTableView.myTrajectories.dragging = true;
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            if ( clickOffset != null ) {
                clickOffset = null;
                thisBallImage.myModel.separateAllBalls();
            }
            dragging = false;
            checkHighlight();
            thisBallImage.myTableView.myTrajectories.dragging = false;
        }

        function dragTarget( evt: MouseEvent ): void {
            if ( clickOffset != null ) {  //if dragging
                //adjust x position
                thisBallImage.x = theStage.mouseX - clickOffset.x;
                ballX = thisBallImage.x / CLConstants.PIXELS_PER_METER;
                //edges of border, beyond which center of ball may not go
                var leftEdge: Number = thisBallImage.myBall.getRadius();
                var rightEdge: Number = W - thisBallImage.myBall.getRadius();
                var topEdge: Number = H / 2 - thisBallImage.myBall.getRadius();
                var bottomEdge: Number = -H / 2 + thisBallImage.myBall.getRadius();
                if ( modelRef.borderOn ) {
                    if ( ballX < leftEdge ) {
                        ballX = leftEdge;
                    }
                    else {
                        if ( ballX > rightEdge ) {
                            ballX = rightEdge;
                        }
                    }
                }
                modelRef.setX( indx, ballX );
                //if not in 1DMode, adjust y position
                if ( !thisBallImage.myModel.oneDMode ) {
                    thisBallImage.y = theStage.mouseY - clickOffset.y;
                    ballY = H / 2 - thisBallImage.y / CLConstants.PIXELS_PER_METER;
                    if ( modelRef.borderOn ) {
                        if ( ballY < bottomEdge ) {
                            ballY = bottomEdge;
                        }
                        else {
                            if ( ballY > topEdge ) {
                                ballY = topEdge;
                            }
                        }
                    }
                    modelRef.setY( indx, ballY );
                }
                if ( modelRef.atInitialConfig ) {
                    modelRef.initPos[indx].setXY( ballX, ballY );
                }
                modelRef.updateViews();
                evt.updateAfterEvent();
            }
        }
    }


    public function makeArrowDraggable(): void {
        var target: Sprite = this.arrowHeadHandle;
        var thisBallImage: BallImage = this;
        var thisArrowImage: Arrow = this.vArrowImage;

        target.buttonMode = true;
        var indx: int = ballIndex;
        var modelRef: Model = this.myModel;
        var H: Number = modelRef.borderHeight;

        target.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        target.addEventListener( MouseEvent.MOUSE_OVER, showVelocity );
        target.addEventListener( MouseEvent.MOUSE_OUT, unshowVelocity );
        var theStage: Object = thisBallImage;//target.parent;
        var clickOffset: Point;


        function startTargetDrag( evt: MouseEvent ): void {
            //problem with localX, localY if sprite is rotated.
            thisBallImage.myTableView.addBallChild( thisBallImage );
            clickOffset = new Point( evt.localX, evt.localY );

            mouseDownOnArrow = true;
            updateShowValuesVisibility();
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            //trace("stop dragging");
            clickOffset = null;

            mouseDownOnArrow = false;
            updateShowValuesVisibility();
        }

        function dragTarget( evt: MouseEvent ): void {
            if ( clickOffset != null ) {  //if dragging
                //adjust x-component of velocity
                //following line is ratio of arrowHeadIndicator position to tip-of-arrow position, measured from origin at tail of arrow.
                //keeps the handle on the center of the arrow head rather than on the tip of the arrow head
                var ratio: Number = (thisArrowImage.lengthInPix + thisArrowImage.headL) / (thisArrowImage.lengthInPix + 0.2 * thisArrowImage.headL);
                //trace("ratio before trap: "+ratio);
                if ( isNaN( ratio ) ) {
                    ratio = 1;
                    //trace("ratio set to 1 because is was NaN");
                }
                //trace("ratio after trap: "+ratio);
                target.x = theStage.mouseX;// - clickOffset.x;
                //thisBallImage.arrowHeadHandle.x = target.x;
                thisBallImage.arrowHeadIndicator.x = target.x;
                var velocityX: Number = (target.x * ratio) / thisBallImage.vArrowImage.scale;
                //trace("velocityX: "+velocityX);

                modelRef.setVX( indx, velocityX );
                //if not in 1DMode, set y-component of velocity
                if ( !modelRef.oneDMode ) {
                    target.y = theStage.mouseY;// - clickOffset.y;
                    //thisBallImage.arrowHeadHandle.y = target.y;
                    thisBallImage.arrowHeadIndicator.y = target.y;
                    var velocityY: Number = -(target.y * ratio) / thisBallImage.vArrowImage.scale;
                    modelRef.setVY( indx, velocityY );
                }
                else {
                    target.y = 0;// - clickOffset.y;
                    thisBallImage.arrowHeadHandle.y = target.y;
                    //thisBallImage.arrowHeadIndicator.y = target.y;
                    velocityY = -(target.y * ratio) / thisBallImage.vArrowImage.scale;
                    modelRef.setVY( indx, velocityY );
                }
                thisBallImage.setVisibilityOfArrowHeadIndicator();
                if ( modelRef.atInitialConfig ) {
                    modelRef.initVel[indx].setXY( velocityX, velocityY );
                }
                modelRef.updateViews();
                thisBallImage.vArrowImage.setArrow( modelRef.ball_arr[indx].velocity );
                evt.updateAfterEvent();
            }
        }


        function showVelocity( evt: MouseEvent ): void {
            thisBallImage.myTableView.myMainView.myDataTable.setVelocityHighlight( thisBallImage.ballIndex, true );
        }

        function unshowVelocity( evt: MouseEvent ): void {
            thisBallImage.myTableView.myMainView.myDataTable.setVelocityHighlight( thisBallImage.ballIndex, false );
        }
    }


    public function setVisibilityOfArrowHeadIndicator(): void {
        var ballRadiusInPix: Number = CLConstants.PIXELS_PER_METER * this.myBall.getRadius();
        var velInPix: Number = this.vArrowImage.lengthInPix//Math.sqrt(target.x*target.x + target.y*target.y);
        //var rInPix:Number = thisBallImage.pixelsPerMeter*thisBallImage.myBall.getRadius();
        //trace("distInPix: "+distInPix+"   r:"+rInPix);
        //this.arrowHeadIndicator.visible = velInPix < ballRadiusInPix && this.arrowShown;
        arrowHeadIndicator.visible = this.arrowShown && !myModel.playing;
    }

    public function showArrow( tOrF: Boolean ): void {
        if ( tOrF ) {  //if arrows shown
            this.arrowShown = true;
            this.vArrowImage.visible = true;
            this.setVisibilityOfArrowHeadIndicator();
            this.arrowHeadHandle.visible = true;
        }
        else {  //if arrow not shown
            this.arrowShown = false;
            this.vArrowImage.visible = false;
            this.arrowHeadIndicator.visible = false;
            this.arrowHeadHandle.visible = false;
        }
    }

    public function showPArrow( tOrF: Boolean ): void {
        if ( tOrF ) {  //if arrows shown
            //this.pArrowShown = true;
            this.pArrowImage.visible = true;
        }
        else {  //if arrow not shown
            //this.pArrowShown = false;
            this.pArrowImage.visible = false;
        }
    }

    //update both velocity and momentum arrows on ball images
    public function updateVelocityArrow(): void {
        var vel: TwoVector = this.myModel.ball_arr[this.ballIndex].velocity;
        var mom: TwoVector = this.myModel.ball_arr[this.ballIndex].getMomentum(); //momentum;
        //if(this.ballIndex == 0){
        //trace("ballImage.myModel.ball_arr[0].velocity.y = "+this.myModel.ball_arr[0].velocity.getY());
        //}
        //this.myModel.updateViews();
        this.vArrowImage.setArrow( vel );
        this.pArrowImage.setArrow( mom );
        var scaleFactor: Number = this.vArrowImage.scale;
        //following line is ratio of arrowHeadIndicator position to tip-of-arrow position, measured from origin at tail of arrow.
        var thisArrowImage: Arrow = this.vArrowImage;
        var ratio: Number = (thisArrowImage.lengthInPix + thisArrowImage.headL) / (thisArrowImage.lengthInPix + 0.2 * thisArrowImage.headL);
        if ( isNaN( ratio ) ) {ratio = 1;}
        ratio = 1;
        //trace("on updateVelocityArrow(), ratio is "+ratio);
        this.arrowHeadIndicator.x = scaleFactor * vel.getX() / ratio;
        this.arrowHeadIndicator.y = -scaleFactor * vel.getY() / ratio;
        this.arrowHeadHandle.x = scaleFactor * vel.getX() / ratio;
        this.arrowHeadHandle.y = -scaleFactor * vel.getY() / ratio;
        this.setVisibilityOfArrowHeadIndicator();
        updateReadouts();
    }

}
}