package{
	
	import flash.display.*;
	import flash.events.*;
	import fl.events.*;
	import flash.text.*;
	import flash.ui.*;
	import fl.controls.*;
	import fl.managers.StyleManager;
	
	public class ControlPanel extends Sprite{
		private var myModel:Model;
		private var myMainView:MainView;
		private var nbrBalls;
		private var maxNbrBalls;
		private var tFormat:TextFormat;
		private var resetButton:NiceButton;
		private var showCMOn:Boolean;
		//private var changeNbrBallButtons:ChangeNbrBallButtons;	//libary symbol instance
		
		public function ControlPanel(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.myMainView.addChild(this);
			this.nbrBalls = this.myModel.nbrBalls;
			this.maxNbrBalls = this.myModel.maxNbrBalls;
			this.tFormat = new TextFormat();
			//this.myModel.registerView(this);
			//this.changeNbrBallButtons = new ChangeNbrBallButtons();
			this.initialize();
			this.initializeComponents();
		}//end of constructor
		
		public function initialize():void{
			this.changeNbrBallButtons.addBallButton.addEventListener(MouseEvent.MOUSE_DOWN, addBall);
			this.changeNbrBallButtons.removeBallButton.addEventListener(MouseEvent.MOUSE_DOWN, removeBall);
			this.changeNbrBallButtons.addBallButton.buttonMode = true;
			this.changeNbrBallButtons.removeBallButton.buttonMode = true;
			this.resetButton = new NiceButton(this.resetButton_sp, 80, resetAll);
			var nbrString:String = String(this.nbrBalls);
			this.changeNbrBallButtons.nbrReadout.text = nbrString;
			this.elasticityValueLabel.text = "1.00";
			//this.background.border.buttonMode = true;
			if(this.myModel.nbrBalls == 1){
				this.changeNbrBallButtons.removeBallButton.visible = false;
			}
			this.showCMOn = true;
			Util.makePanelDraggableWithBorder(this, this.background.border);
		}
		
		public function initializeComponents():void{
			this.tFormat.size = 12;
			this.tFormat.font = "Arial";
			StyleManager.setStyle("textFormat",tFormat);
			//var tField:TextField = new TextField();
			//tField.autoSize = TextFieldAutoSize.LEFT;
			//StyleManager.setStyle("autoSize",TextFieldAutoSize.LEFT);
			this.oneD_rb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.twoD_rb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.showVelocities_cb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.showCM_cb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.reflectingBorder_cb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.showPaths_cb.textField.autoSize = TextFieldAutoSize.LEFT;
			this.oneD_rb.addEventListener(MouseEvent.CLICK, oneDModeOn);
			this.twoD_rb.addEventListener(MouseEvent.CLICK, oneDModeOff);
			this.showVelocities_cb.addEventListener(MouseEvent.CLICK, showVelocityArrows);
			this.showCM_cb.addEventListener(MouseEvent.CLICK, showCM);
			this.reflectingBorder_cb.addEventListener(MouseEvent.CLICK, borderOnOrOff);
			this.showPaths_cb.addEventListener(MouseEvent.CLICK, showOrErasePaths);
			this.sound_cb.addEventListener(MouseEvent.CLICK, soundOnOrOff);
			this.timeRateSlider.addEventListener(SliderEvent.CHANGE, setTimeRate);
			this.elasticitySlider.addEventListener(SliderEvent.CHANGE, setElasticity);
			
		}
		
		public function oneDModeOn(evt:MouseEvent):void{
			this.myModel.setOneDMode(true);
		}
		public function oneDModeOff(evt:MouseEvent):void{
			this.myModel.setOneDMode(false);
		}
		
		private function resetAll():void{
			this.myModel.resetAll();
			this.twoD_rb.selected = true;
			var nbrBalls_str:String = String(this.myModel.nbrBalls);
			this.changeNbrBallButtons.nbrReadout.text = nbrBalls_str;
			this.nbrBalls = this.myModel.nbrBalls;
			this.changeNbrBallButtons.addBallButton.visible = true;
			if(this.nbrBalls == 1){
				this.changeNbrBallButtons.removeBallButton.visible = false;
				this.myMainView.myTableView.CM.visible = false;
			}
			this.timeRateSlider.value = 0.5;
			this.myModel.setTimeRate(0.5);
			this.elasticitySlider.value = 1;
			this.myModel.setElasticity(1);
		}
		
		public function showVelocityArrows(evt:MouseEvent):void{
			this.myMainView.myTableView.showArrowsOnBallImages(evt.target.selected);
		}
		
		private function showCM(evt:MouseEvent):void{
			this.showCMOn = evt.target.selected;
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
		
		public function setTimeRate(evt:SliderEvent):void{
			//trace("time slider: "+evt.target.value);
			this.myModel.setTimeRate(evt.target.value);
		}
		
		public function setElasticity(evt:SliderEvent):void{
			trace("elasticity = "+evt.target.value)
			this.myModel.setElasticity(evt.target.value);
			var e_str:String = String(evt.target.value);
			this.elasticityValueLabel.text = e_str;
			//trace("e slider: "+evt.target.value);
		}
		
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
		
		//may not be necessary, since this is a controller, not a view
		public function update():void{
									  
		}
		
	}//end of class
}//end of package