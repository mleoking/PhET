package edu.colorado.phet.calculusgrapher {

import flash.display.*;
import flash.events.*;
import fl.controls.CheckBox;
import fl.events.*;
import flash.geom.*;
import flash.text.*;
import flash.ui.*;
import edu.colorado.phet.calculusgrapher.*;
import edu.colorado.phet.flashcommon.*;

public class ControlPanel extends Sprite {

    private var myModel:Model;
    private var myMainView:MainView;
    private var showDerivativeCurve:Boolean;
    private var showIntegralCurve:Boolean;
    private var currentSliderValue:Number;
    private var zeroButton:NiceButton;
    private var undoButton:NiceButton;
    private var smoothButton:NiceButton;
    private var pButton_arr:Array;  	//array of PushButtons;
    private var majorTick_arr:Array; 	//array of major Ticks for Slider
    private var freeformIcon:FreeformIcon;
    private var hillIcon:HillIcon;
    private var linearIcon:LinearIcon;
    private var offsetIcon:OffsetIcon;
    private var parabolaIcon:ParabolaIcon;
    private var pedestalIcon:PedestalIcon;
    private var sineIcon:SineIcon;
    private var tiltIcon:TiltIcon;

    private var tFormat:TextFormat;
    public var previewCurve:Sprite;	//for showing preview of curve dent

    public function ControlPanel( myModel:Model, myMainView:MainView ) {
        this.myModel = myModel;
        this.myMainView = myMainView;
        this.previewCurve = new PreviewCurve(myModel);
        this.tFormat = new TextFormat();
    }//end of constructor

    public function initialize():void {
        //trace("initialize called");
        this.controlPanelBackground.width = 140;
        this.controlPanelBackground.height = 440;
        this.tFormat.size = 14;
        this.tFormat.font = "_sans";
        this.makePanelDraggable();

        this.freeformIcon = new FreeformIcon();
        this.hillIcon = new HillIcon();
        this.linearIcon = new LinearIcon();
        this.offsetIcon = new OffsetIcon();
        this.parabolaIcon = new ParabolaIcon();
        this.pedestalIcon = new PedestalIcon();
        this.sineIcon = new SineIcon();
        this.tiltIcon = new TiltIcon();

        this.pButton_arr = new Array(8);
        this.pButton_arr[0] = new PushButton(this.hill_sp, hillIcon, Model.MODE_HILL, PushButton.TOOLTIP_LEFT, setAlterMode);
        this.pButton_arr[1] = new PushButton(this.linear_sp, linearIcon, Model.MODE_LINE, PushButton.TOOLTIP_LEFT, setAlterMode);
        this.pButton_arr[2] = new PushButton(this.pedestal_sp, pedestalIcon, Model.MODE_PEDESTAL, PushButton.TOOLTIP_LEFT, setAlterMode);
        this.pButton_arr[3] = new PushButton(this.parabola_sp, parabolaIcon, Model.MODE_PARABOLA, PushButton.TOOLTIP_LEFT, setAlterMode);
        this.pButton_arr[4] = new PushButton(this.sine_sp, sineIcon, Model.MODE_SINE, PushButton.TOOLTIP_LEFT, setAlterMode);
        this.pButton_arr[5] = new PushButton(this.freeform_sp, freeformIcon, Model.MODE_FREEFORM, PushButton.TOOLTIP_LEFT, setAlterMode);
        this.pButton_arr[6] = new PushButton(this.tilt_sp, tiltIcon, Model.MODE_TILT, PushButton.TOOLTIP_LEFT, setAlterMode);
        this.pButton_arr[7] = new PushButton(this.offset_sp, offsetIcon, Model.MODE_OFFSET, PushButton.TOOLTIP_LEFT, setAlterMode);

        this.majorTick_arr = new Array(3);
        this.majorTick_arr[0] = new SliderMajorTick();
        this.majorTick_arr[1] = new SliderMajorTick();
        this.majorTick_arr[2] = new SliderMajorTick();

        this.zeroButton = new NiceButton(this.zeroButton_sp, 90, zeroCurves);
        this.zeroButton.setLabel(SimStrings.get("zero", "Zero"));
        this.undoButton = new NiceButton(this.undoButton_sp, 90, undoLastChange);
        this.undoButton.setLabel(SimStrings.get("undo", "Undo"));
        this.smoothButton = new NiceButton(this.smoothButton_sp, 90, smoothAllPoints);
        this.smoothButton.setLabel(SimStrings.get("smooth", "Smooth"));

        this.stage.addEventListener(KeyboardEvent.KEY_DOWN, onKeyPressed);
        this.initializeComponents();
        this.myModel.setAlterMode(Model.MODE_HILL);
        this.pButton_arr[0].highlightOn();
        this.myModel.setWidthOfDent(this.currentSliderValue);

        this.showDerivativeCurve = true;
        this.showIntegralCurve = false;
        this.previewScreen.addChild(previewCurve);
        this.myModel.updatePreviewCurve();
    }//end of initialize()

    public function zeroCurves():void {
        this.myModel.copyCurrentY();
        this.myModel.zeroAllCurves();
    }

    public function undoLastChange():void {
        this.myModel.undoLastChange();
    }

    public function smoothAllPoints():void {
        this.myModel.copyCurrentY();
        this.myModel.simpleSmoothAllPoints();
    }

    public function onKeyPressed( evt:KeyboardEvent ):void {
        //trace("evt.keyCode: "+evt.keyCode);
        if ( evt.ctrlKey == true && evt.keyCode == 90 ) { //if cntl-z
            this.undoLastChange();
        }
    }


    public function initializeComponents():void {
        //trace("this.checkBoxDerivative:"+this.checkBoxDerivative);

        this.derivative_cb.addEventListener(MouseEvent.CLICK, showDerivativeCurve);
        this.integral_cb.addEventListener(MouseEvent.CLICK, showIntegralCurve);
        this.showGrid_cb.addEventListener(MouseEvent.CLICK, showGrid);
        this.showRuler_cb.addEventListener(MouseEvent.CLICK, showRuler);

        // the actual check boxes (without labels)
        var integralCheck : CheckBox = this.integral_cb;
        var derivativeCheck : CheckBox = this.derivative_cb;
        var gridCheck : CheckBox = this.showGrid_cb;
        var cursorCheck : CheckBox = this.showRuler_cb;

        // the corresponding labels for the check boxes
        var integralLabel : TextField = this.integral_label;
        var derivativeLabel : TextField = this.derivative_label;
        var gridLabel : TextField = this.grid_label;
        var cursorLabel : TextField = this.cursor_label;

        // set check box label strings
        integralLabel.text = SimStrings.get( "integral", "Integral" );
        derivativeLabel.text = SimStrings.get( "derivative", "Derivative" );
        gridLabel.text = SimStrings.get( "grid", "Grid" );
        cursorLabel.text = SimStrings.get( "cursor", "Cursor" );

        // resize check box label text
        TextFieldUtils.resizeText( integralLabel, "left" );
        TextFieldUtils.resizeText( derivativeLabel, "left" );
        TextFieldUtils.resizeText( gridLabel, "left" );
        TextFieldUtils.resizeText( cursorLabel, "left" );

        // hook up the check box labels so that they also control the check boxes
        emulateCheckBoxLabel( integralLabel, integralCheck );
        emulateCheckBoxLabel( derivativeLabel, derivativeCheck );
        emulateCheckBoxLabel( gridLabel, gridCheck );
        emulateCheckBoxLabel( cursorLabel, cursorCheck );

        this.derivative_cb.setStyle("textFormat", tFormat);
        this.integral_cb.setStyle("textFormat", tFormat);
        this.showGrid_cb.setStyle("textFormat", tFormat);
        this.showRuler_cb.setStyle("textFormat", tFormat);
        this.widthSlider.addEventListener(SliderEvent.CHANGE, setSliderValue);  //widthSlider is symbol on controlPanel symbol
        this.widthSlider.width = 110;
        this.widthSlider.addChild(this.majorTick_arr[0]);
        this.widthSlider.addChild(this.majorTick_arr[1]);
        this.widthSlider.addChild(this.majorTick_arr[2]);
        this.majorTick_arr[0].x = 0;
        this.majorTick_arr[1].x = this.widthSlider.width / 2;
        this.majorTick_arr[2].x = this.widthSlider.width;

        this.currentSliderValue = this.widthSlider.value;
        this.myModel.setWidthOfDent(this.currentSliderValue);
        //trace("this.currentSliderValue:"+this.currentSliderValue)
        var localRef:Object = this;

        function showDerivativeCurve( evt:MouseEvent ):void {
            //trace("derivative checked");
            localRef.showDerivativeCurve = localRef.derivative_cb.selected;
            localRef.updateMainView();
        }

        function showIntegralCurve( evt:MouseEvent ):void {
            //trace("integral checked");
            localRef.showIntegralCurve = localRef.integral_cb.selected;
            localRef.updateMainView();
            myMainView.flyInDragMe();
        }

        function showGrid( evt:MouseEvent ):void {
            var tOrF:Boolean = localRef.showGrid_cb.selected;
            myMainView.iView.grid.visible = tOrF;
            myMainView.yView.grid.visible = tOrF;
            myMainView.dView.grid.visible = tOrF;
        }

        function showRuler( evt:MouseEvent ):void {
            var tOrF:Boolean = localRef.showRuler_cb.selected;
            myMainView.ruler.visible = tOrF;
            myMainView.ruler.visible = tOrF;
            myMainView.ruler.visible = tOrF;
        }

        /*function setSliderValue(evt:SliderEvent):void{
         localRef.currentSliderValue = evt.target.value;
         localRef.myModel.setWidthOfDent(localRef.currentSliderValue);
         localRef.myModel.updatePreviewCurve();
         //localRef.myModel.range = 0.3*Math.pow(1.5, sliderValue);
         //trace("slider sliding, value is " + localRef.currentSliderValue);
         }*/
    }//end of initializeCheckBoxes()

    // hook up the events on a TextField so that it appears like it is the label of the CheckBox.
    // handles mouse over, out, and clicks
    private function emulateCheckBoxLabel( textField : TextField, checkBox : CheckBox ):void {
        textField.addEventListener( MouseEvent.ROLL_OVER, function( evt: Event):void { checkBox.setMouseState( "over" ); } );
        textField.addEventListener( MouseEvent.ROLL_OUT, function( evt: Event):void { checkBox.setMouseState( "up" ); } );
        textField.addEventListener( MouseEvent.MOUSE_DOWN, function( evt: Event):void { checkBox.setMouseState( "down" ); } );
        textField.addEventListener( MouseEvent.MOUSE_UP, function( evt: Event):void { checkBox.setMouseState( "up" ); } );
        textField.addEventListener( MouseEvent.CLICK, function( evt: Event):void { checkBox.dispatchEvent( evt ); } );
    }

    function setAlterMode( key : String ):void {
        //trace("controlPanel.setAlterMode called");
        this.myModel.setAlterMode(key);
        this.myModel.setWidthOfDent(this.currentSliderValue);
        if ( key == Model.MODE_TILT || key == Model.MODE_OFFSET || key == Model.MODE_FREEFORM ) {
            this.widthSlider.visible = false;
        }
        else {
            this.widthSlider.visible = true;
        }
        for ( var i:int = 0; i < this.pButton_arr.length; i++ ) {
            this.pButton_arr[i].myHighlight.visible = false;
        }
    }//end of setAlterMode()

    function setSliderValue( evt:SliderEvent ):void {
        this.currentSliderValue = evt.target.value;
        this.myModel.setWidthOfDent(this.currentSliderValue);
        this.myModel.updatePreviewCurve();
        //localRef.myModel.range = 0.3*Math.pow(1.5, sliderValue);
        //trace("slider sliding, value is " + localRef.currentSliderValue);
    }

    public function updateMainView():void {
        //trace("updateMainView called.  showDerivative is " + this.showDerivativeCurve);
        if ( this.showDerivativeCurve && this.showIntegralCurve ) {
            this.myMainView.showDerivativeAndIntegral();
            //trace("show both");
            //trace(this.myMainView);
        }
        else if ( this.showDerivativeCurve ) {
            this.myMainView.showDerivativeOnly();
            //trace("show deriv only");
        }
        else if ( this.showIntegralCurve ) {
                this.myMainView.showIntegralOnly();
                //trace("show integ only");
            }
            else {
                this.myMainView.showOriginalCurveOnly();
                //trace("show neither");
            }

    }


    public function makePanelDraggable():void {
        var target:Sprite = this.controlPanelBackground.border;
        target.buttonMode = true;
        target.addEventListener(MouseEvent.MOUSE_OVER, onBorder);
        target.addEventListener(MouseEvent.MOUSE_DOWN, startMyDrag);
        this.stage.addEventListener(MouseEvent.MOUSE_MOVE, dragging);
        this.stage.addEventListener(MouseEvent.MOUSE_UP, stopMyDrag);
        target.addEventListener(MouseEvent.MOUSE_OUT, offBorder);
        var localRef = this;
        var clickOffset:Point;

        function onBorder( evt:MouseEvent ):void {
            //trace("overborder");
        }

        function startMyDrag( evt:MouseEvent ):void {
            //trace("start drag");
            clickOffset = new Point(evt.stageX - localRef.x, evt.stageY - localRef.y);//(evt.localX, evt.localY);
            //trace("evt.localX "+evt.localX+"  evt.localY "+evt.localY);
        }

        function stopMyDrag( evt:MouseEvent ):void {
            clickOffset = null;
            //trace("stop drag");
        }

        function dragging( evt:MouseEvent ):void {
            //trace("dragging");

            if ( clickOffset != null ) {//if dragging
                //trace("evt.stageX "+evt.stageX+"  evt.stageY "+evt.stageY);
                localRef.x = evt.stageX - clickOffset.x;
                localRef.y = evt.stageY - clickOffset.y;
                evt.updateAfterEvent();
            }
        }

        function offBorder( evt:MouseEvent ):void {
            //trace("offborder");

        }
    }//end of makePanelDraggable

}//end of class

}//end of package