package{
    import edu.colorado.phet.flashcommon.SimStrings;	
	import edu.colorado.phet.flashcommon.TextFieldUtils;	
	import flash.display.*;
	import flash.events.*;
	import fl.events.*;
	import flash.text.*;
	import flash.ui.*;
	import fl.controls.*;
	import fl.managers.StyleManager;
	
	//List of strings in this class for internationalization:
	//This class ControlPanel associated with library movieclip controlPanel 
	//Here are all the dynamic text strings and their english values
	//controlPanel.oneD_rb.label = "1 Dimension"
	//controlPanel.twoD_rb.label = "2 Dimensions"
	//controlPanel.resetButton_sp.label_txt  = "Reset All"  //This is instance of class NiceButton
	//controlPanel.showVelocities_cb.label = "Show vlocities"
	//controlPanel.showCM_cb.label = "Show C.M."
	//controlPanel.reflectingBorder_cb.label = "Reflecting Border"
	//controlPane.momentaDiagram
	//controlPanel.showPaths_cb.label = "Show Paths"
	//controlPanel.sound_cb.label = "Sound" 
	//controlPanel.timeLabel = "time"
	//controlPanel.slowLabel = "slow"
	//controlPanel.fastLabel = "fast"
	//controlPanel.elasticityLabel = "elasticity"
	//controlPanel.elasticityValueLabel = numeric value is set by code, do not internalize this string
	//controlPanel.zeroPercentLabel = "0%"
	//controlPanel.oneHundredPercentLabel = "100%"
	
	//This class is associated with Flash Library Symbol controlPanel, so "this" is the controlPanel Library Symbol 
	
	public class ControlPanel extends Sprite{
		private var myModel:Model;
		private var myMainView:MainView;
		private var nbrBalls;
		//private var maxNbrBalls;
		private var tFormat:TextFormat;
		private var resetButton:NiceButton;
		public var showCMOn:Boolean;
		
		
		public function ControlPanel(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.myMainView.addChild(this);
			this.nbrBalls = this.myModel.nbrBalls;
			//this.maxNbrBalls = this.myModel.maxNbrBalls;
			this.tFormat = new TextFormat();
			//this.myModel.registerView(this);
			//this.changeNbrBallButtons = new ChangeNbrBallButtons();
			this.initialize();
			this.initializeComponents();
			//this.initializeStrings();
		}//end of constructor
		
		public function initialize():void{
			//this.changeNbrBallButtons.addBallButton.addEventListener(MouseEvent.MOUSE_DOWN, addBall);
			//this.changeNbrBallButtons.removeBallButton.addEventListener(MouseEvent.MOUSE_DOWN, removeBall);
			//this.changeNbrBallButtons.addBallButton.buttonMode = true;
			//this.changeNbrBallButtons.removeBallButton.buttonMode = true;
			this.resetButton = new NiceButton(this.resetButton_sp, 80, resetAll);
			//var nbrString:String = String(this.nbrBalls);
			//this.changeNbrBallButtons.nbrReadout.text = nbrString;
			this.elasticityValueLabel.text = "1.00";
			//this.background.border.buttonMode = true;
			//if(this.myModel.nbrBalls == 1){
				//this.changeNbrBallButtons.removeBallButton.visible = false;
			//}
			this.showCMOn = true;
			Util.makePanelDraggableWithBorder(this, this.background.border);
		}
		
		public function initializeComponents():void{
			this.tFormat.size = 12;
			this.tFormat.font = "Arial";
			StyleManager.setStyle("textFormat",tFormat);

            initializeStrings();

			this.oneD_rb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.twoD_rb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.showVelocities_cb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.showMomentumVectors_cb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.showCM_cb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.reflectingBorder_cb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.showPaths_cb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.oneD_rb.addEventListener(MouseEvent.CLICK, oneDModeOn);
			this.twoD_rb.addEventListener(MouseEvent.CLICK, oneDModeOff);
			this.showVelocities_cb.addEventListener(MouseEvent.CLICK, showVelocityArrows);
			this.showMomentumVectors_cb.addEventListener(MouseEvent.CLICK, showMomentumArrows);
			this.showCM_cb.addEventListener(MouseEvent.CLICK, showCM);
			this.reflectingBorder_cb.addEventListener(MouseEvent.CLICK, borderOnOrOff);
			this.showMomenta_cb.addEventListener(MouseEvent.CLICK, momentaDiagramOnOrOff);
			this.showPaths_cb.addEventListener(MouseEvent.CLICK, showOrErasePaths);
			this.sound_cb.addEventListener(MouseEvent.CLICK, soundOnOrOff);
			//this.timeRateSlider.addEventListener(SliderEvent.CHANGE, setTimeRate);
			this.elasticitySlider.addEventListener(SliderEvent.CHANGE, setElasticity);
			
		}
		
        
//<string key="ControlPanel.showCenterOfMass" value="Show C.M."/>
//    <string key="ControlPanel.reflectingBorder" value="Reflecting Border"/>
//    <string key="ControlPanel.showPaths" value="Show Paths"/>
//    <string key="ControlPanel.sound" value="Sound"/>
            
		public function initializeStrings():void{
            TextFieldUtils.initLabelButtonI18NLeft("ControlPanel.1d","1 Dimension",oneD_txt, oneD_rb);
            TextFieldUtils.initLabelButtonI18NLeft("ControlPanel.2d","2 Dimensions",twoD_txt, twoD_rb);
            TextFieldUtils.initLabelButtonI18NLeft("ControlPanel.showVelocities","Velocity Vectors", showVelocities_label, showVelocities_cb);
            TextFieldUtils.initLabelButtonI18NLeft("ControlPanel.showMomentumVectors","Momentum Vectors", showMomentumVectors_label, showMomentumVectors_cb);
			TextFieldUtils.initLabelButtonI18NLeft("ControlPanel.showCenterOfMass","Center of Mass", showCM_label, showCM_cb);
            TextFieldUtils.initLabelButtonI18NLeft("ControlPanel.reflectingBorder","Reflecting Border", reflectingBorder_label, reflectingBorder_cb);
            TextFieldUtils.initLabelButtonI18NLeft("ControlPanel.momentaDiagram","Momenta Diagram", showMomenta_label, showMomenta_cb);
			TextFieldUtils.initLabelButtonI18NLeft("ControlPanel.showPaths","Show Paths",showPaths_label, showPaths_cb);
            TextFieldUtils.initLabelButtonI18NLeft("ControlPanel.sound","Sound",sound_label, sound_cb);
            
            resetButton.setLabel(SimStrings.get("ControlPanel.resetAll","Reset All"));
            // this.timeLabel.text = SimStrings.get("ControlPanel.timeRate","Time Rate");
            //this.slowLabel.text = SimStrings.get("ControlPanel.slow","slow");
            //this.fastLabel.text = SimStrings.get("ControlPanel.fast","fast");
            this.elasticityLabel.text = SimStrings.get("ControlPanel.elasticity","Elasticity");
            this.zeroPercentLabel.text = SimStrings.get("ControlPanel.zeroPercent","0%");
            this.oneHundredPercentLabel.text = SimStrings.get("ControlPanel.oneHundredPercent","100%");

            //TODO: JO: needs resizing and extracting labels of the components out
		}//end of initializeStrings()
		
		public function oneDModeOn(evt:MouseEvent):void{
			this.myModel.setOneDMode(true);
			this.myMainView.myTableView.myTrajectories.setBorderHeight();
			this.myMainView.myTableView.myTrajectories.erasePaths();
			
			this.myMainView.myTableView.reDrawBorder();
		}
		public function oneDModeOff(evt:MouseEvent):void{
			this.myModel.setOneDMode(false);
			this.myMainView.myTableView.myTrajectories.setBorderHeight();
			this.myMainView.myTableView.myTrajectories.erasePaths();
			
			this.myMainView.myTableView.reDrawBorder();
		}
		
		public function momentaDiagramOnOrOff(evt:MouseEvent):void{
			//trace(this.showCMOn = evt.target.selected);
			//var momentaDiagramOnOrOff:Boolean = evt.target.selected;
			this.myMainView.momentumView.visible = evt.target.selected;
		}
		
		private function resetAll():void{
			this.myModel.resetAll();
			this.myMainView.myTableView.reDrawBorder();
			this.myMainView.myDataTable.checkBallNbrLimits();
			this.myMainView.myTableView.playButtons.resetAllCalled();
			this.twoD_rb.selected = true;
			//var nbrBalls_str:String = String(this.myModel.nbrBalls);
			//this.changeNbrBallButtons.nbrReadout.text = nbrBalls_str;
			//this.nbrBalls = this.myModel.nbrBalls;
			//this.changeNbrBallButtons.addBallButton.visible = true;
			//if(this.nbrBalls == 1){
				//this.changeNbrBallButtons.removeBallButton.visible = false;
				//this.myMainView.myTableView.CM.visible = false;
			//}
			//this.timeRateSlider.value = 0.5;
			//this.myModel.setTimeRate(0.5);
			this.elasticitySlider.value = 1;
			this.myModel.setElasticity(1);
		}
		
		public function showVelocityArrows(evt:MouseEvent):void{
			this.myMainView.myTableView.showArrowsOnBallImages(evt.target.selected);
		}
		
		private function showMomentumArrows(evt:MouseEvent):void{
			//trace("show Momentum Arrows is " + evt.target.selected);
			this.myMainView.myTableView.showPArrowsOnBallImages(evt.target.selected);
		}
		
		private function showCM(evt:MouseEvent):void{
			this.showCMOn = evt.target.selected;
			this.cmIcon.visible = evt.target.selected;
			if(myModel.nbrBalls > 1){
				this.myMainView.myTableView.CM.visible = evt.target.selected;
			}else{
				this.myMainView.myTableView.CM.visible = false;
			}
			//trace("this.showCMOn: "+this.showCMOn);
		}
		
		private function borderOnOrOff(evt:MouseEvent):void{
			this.myModel.setReflectingBorder(evt.target.selected);
			this.myMainView.myTableView.drawBorder();
			//trace("ControlPanel.borderOnOrOff: " + evt.target.selected);
		}
		
		public function showOrErasePaths(evt:MouseEvent):void{
			//trace("ControlPanel.showOrErasePaths.evt.target.selected: "+evt.target.selected);
			if(evt.target.selected){
				this.myMainView.myTableView.myTrajectories.pathsOn();
			}else{
				this.myMainView.myTableView.myTrajectories.pathsOff();
			}
		}
		
		public function soundOnOrOff(evt:MouseEvent):void{
			this.myModel.soundOn = evt.target.selected;
		}
		
		//Time Rate slider moved to TableView
		//public function setTimeRate(evt:SliderEvent):void{
			//trace("time slider: "+evt.target.value);
			//this.myModel.setTimeRate(evt.target.value);
		//}
		
		public function setElasticity(evt:SliderEvent):void{
			trace("elasticity = "+evt.target.value)
			this.myModel.setElasticity(evt.target.value);
			var e_str:String = evt.target.value.toFixed(2);//String(evt.target.value);
			this.elasticityValueLabel.text = e_str;
			//trace("e slider: "+evt.target.value);
		}
		
		//functions addBall and removeBall are obsolete; these functions now in DataTable.as
		/*
		public function addBall(evt:MouseEvent):void{
			this.myModel.addBall();
			this.nbrBalls = this.myModel.nbrBalls;
			var nbrBalls_str:String = String(this.myModel.nbrBalls);
			this.changeNbrBallButtons.nbrReadout.text = nbrBalls_str;
			if(this.nbrBalls == this.maxNbrBalls){
				this.changeNbrBallButtons.addBallButton.visible = false;
			}
			if(this.nbrBalls > 1){
				this.changeNbrBallButtons.removeBallButton.visible = true;
				if(this.showCMOn){
					this.myMainView.myTableView.CM.visible = true;
				}
			}
			
		}
		
		public function removeBall(evt:MouseEvent):void{
			this.myModel.removeBall();
			this.nbrBalls = this.myModel.nbrBalls;
			var nbrBalls_str:String = String(this.myModel.nbrBalls);
			this.changeNbrBallButtons.nbrReadout.text = nbrBalls_str;
			if(this.nbrBalls < this.maxNbrBalls){
				this.changeNbrBallButtons.addBallButton.visible = true;
			}
			if(this.nbrBalls == 1){
				this.changeNbrBallButtons.removeBallButton.visible = false;
				this.myMainView.myTableView.CM.visible = false;
			}
		}//end removeBall()
		*/
		
		//may not be necessary, since this is a controller, not a view
		public function update():void{
									  
		}
		
	}//end of class
}//end of package