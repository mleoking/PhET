package edu.colorado.phet.calculusgrapher {

import flash.display.Sprite;
import flash.display.Graphics;
import flash.events.*;
import edu.colorado.phet.calculusgrapher.*;
import edu.colorado.phet.flashcommon.*;

public class MainView extends Sprite{
    var myModel:Model;
    var myStageBackground:StageBackground;	//library symbol, necessary to place all graphics on single symbol with hard-coded width & height due to bug in html publish
    var myStageWidth:Number;
    var myStageHeight:Number;
    var yView:View;				//view of function curve
    var dView:View;				//view of derivative of function
    var iView:View;				//view of integral of function
    //var pushPinCup:PushPinCup;
    var controlPanel:ControlPanel;		//library symbol
    var ruler:Ruler;					//library symbol
    var dragMeSign:Sprite;				//library symbol
    var phetLogo:Sprite;				//library symbol
    //var grabbedPin:PushPin;
    //var pinGrabbed:Boolean;
    var smoothOn:Boolean;	//true if smooth graph making is on
    var linearOn:Boolean;	//true if piece-wise linear graph making is on
    var sketchOn:Boolean;

    public function MainView( myModel:Model ) {
        this.myModel = myModel;
        myStageBackground = new StageBackground();
        yView = new View(myModel, this, "original");
        dView = new View(myModel, this, "derivative");
        iView = new View(myModel, this, "integral");
        controlPanel = new ControlPanel(myModel, this);
        phetLogo = new PhETLogo()
        dragMeSign = new DragMe();
        dragMeSign["dragMeText"].text = SimStrings.get("dragMe", "Drag Me!");
        TextFieldUtils.resizeText(dragMeSign["dragMeText"], "left");
        ruler = new Ruler();
    }//end of constructor

    public function initialize():void {
        //myStageBackground.width = this.stage.stageWidth;
        //myStageBackground.height = this.stage.stageHeight;
        myStageWidth = myStageBackground.width;
        myStageHeight = myStageBackground.height;
        this.addChild(myStageBackground);
        this.initialize2();
    }

    public function initialize2():void {
        //trace("h: " + stage.stageHeight + "   w: " + stage.stageWidth);
        this.myStageBackground.addChild(iView);
        this.myStageBackground.addChild(dView);
        this.myStageBackground.addChild(yView);
        this.myStageBackground.addChild(controlPanel);
        this.myStageBackground.addChild(dragMeSign);
        this.myStageBackground.addChild(phetLogo);
        this.myStageBackground.addChild(ruler);
        //trace("myMainView.stage.stageWidth: "+this.stage.stageWidth);
        yView.initialize();
        dView.initialize();
        iView.initialize();
        controlPanel.initialize();
        Util.makeClipDraggable(this.ruler);
        this.ruler.x = this.myStageWidth / 2; //this.stage.stageWidth/2;
        this.ruler.visible = false;
        //pushPinCup.initialize();
        //dView.y = stage.stageHeight/2;
        iView.visible = false;
        this.showDerivativeOnly();
        controlPanel.x = this.myStageWidth - 0.35 * controlPanel.width;//stage.stageWidth - 0.35*controlPanel.width;
        controlPanel.y = 0.5 * myStageHeight; //controlPanel.height;
        phetLogo.x = this.myStageWidth - 1.2 * phetLogo.width;  //stage.stageWidth - 1.2*phetLogo.width;
        phetLogo.y = myStageHeight - 1.0 * phetLogo.height; //stage.stageHeight - 1.0*phetLogo.height;
        //this.drawStageBorder();
        this.flyInDragMe();
    }//end of initialize()

    public function flyInDragMe():void {
        this.dragMeSign.x = 0.45 * this.myStageWidth; //this.stage.stageWidth;
        this.dragMeSign.y = this.yView.y + this.yView.originY;
        //trace("this.yView.y:" + this.yView.y);
    }

    public function drawStageBorder():void {
        var W:Number = this.myStageWidth - 2; //this.parent.stage.stageWidth - 2;
        var H:Number = this.myStageHeight - 2; //this.parent.stage.stageHeight - 2;
        with ( this.myStageBackground.graphics ) {
            clear();
            lineStyle(1, 0xF4800B);
            moveTo(0, 0);
            lineTo(W, 0);
            lineTo(W, H);
            lineTo(0, H);
            lineTo(0, 0);
            moveTo(W / 2, 0);
            lineTo(W / 2, H);
            moveTo(0, H / 2);
            lineTo(W, H / 2);
        }
    }//end of drawStageBorder()

    public function showIntegralOnly():void {
        //trace("showIntegralOnly");
        dView.visible = false;
        iView.visible = true;
        var myHeight:Number = 0.48 * this.myStageHeight;//this.stage.stageHeight;
        iView.setViewHeight(myHeight);
        yView.setViewHeight(myHeight);
        iView.y = 0.035 * this.myStageHeight;//this.stage.stageHeight;
        yView.y = 0.52 * this.myStageHeight;//this.stage.stageHeight;
    }

    public function showDerivativeOnly():void {
        //trace("showDerivativeOnly");
        dView.visible = true;
        iView.visible = false;
        var myHeight:Number = 0.48 * this.myStageHeight;//this.stage.stageHeight;
        yView.setViewHeight(myHeight);
        dView.setViewHeight(myHeight);
        yView.y = 0.035 * this.myStageHeight
        dView.y = 0.52 * this.myStageHeight
    }

    public function showDerivativeAndIntegral():void {
        //trace("showDerivativeAndIntegral");
        dView.visible = true;
        iView.visible = true;
        var myHeight:Number = 0.32 * this.myStageHeight;//this.stage.stageHeight;
        iView.setViewHeight(myHeight);
        yView.setViewHeight(myHeight);
        dView.setViewHeight(myHeight);
        iView.y = 0.05 * this.myStageHeight
        yView.y = 0.35 * this.myStageHeight
        dView.y = 0.65 * this.myStageHeight
    }

    public function showOriginalCurveOnly():void {
        //trace("showOriginalCurveOnly");
        dView.visible = false;
        iView.visible = false;
        var myHeight:Number = 0.70 * this.myStageHeight;//this.stage.stageHeight;
        yView.setViewHeight(myHeight);
        yView.y = (this.myStageHeight / 2) - yView.originY;

    }


}//end of class
}//end of package