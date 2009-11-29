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
		private var tFormat:TextFormat;
		//private var changeNbrBallButtons:ChangeNbrBallButtons;	//libary symbol instance
		
		public function ControlPanel(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.myMainView.addChild(this);
			this.nbrBalls = this.myModel.nbrBalls;
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
			var nbrString:String = String(this.nbrBalls);
			this.changeNbrBallButtons.nbrReadout.text = nbrString;
			//this.background.border.buttonMode = true;
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
			//this.velocity_rb.addEventListener(MouseEvent.CLICK, showVelocityArrows);
			//this.momentum_rb.addEventListener(MouseEvent.CLICK, showMomentumArrows);
			//this.none_rb.addEventListener(MouseEvent.CLICK, showNoArrows);
			this.oneD_rb.addEventListener(MouseEvent.CLICK, oneDModeOn);
			this.twoD_rb.addEventListener(MouseEvent.CLICK, oneDModeOff);
			this.showVelocities_cb.addEventListener(MouseEvent.CLICK, showVelocityArrows);
			this.showCM_cb.addEventListener(MouseEvent.CLICK, showCM);
			this.reflectingBorder_cb.addEventListener(MouseEvent.CLICK, borderOnOrOff);
			this.showPaths_cb.addEventListener(MouseEvent.CLICK, showOrErasePaths);
			this.timeRateSlider.addEventListener(SliderEvent.CHANGE, setTimeRate);
			this.elasticitySlider.addEventListener(SliderEvent.CHANGE, setElasticity);
			
		}
		
		public function oneDModeOn(evt:MouseEvent):void{
			this.myModel.setOneDMode(true);
		}
		public function oneDModeOff(evt:MouseEvent):void{
			this.myModel.setOneDMode(false);
		}
		
		public function showVelocityArrows(evt:MouseEvent):void{
			this.myMainView.myTableView.showArrowsOnBallImages(evt.target.selected);
		}
		
		private function showCM(evt:MouseEvent):void{
			this.myMainView.myTableView.CM.visible = evt.target.selected;
			//trace("ControlPanel.showCM: " + evt.target.selected);
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
		public function setTimeRate(evt:SliderEvent):void{
			//trace("time slider: "+evt.target.value);
			this.myModel.setTimeRate(evt.target.value);
		}
		
		public function setElasticity(evt:SliderEvent):void{
			this.myModel.setElasticity(evt.target.value);
			//trace("e slider: "+evt.target.value);
		}
		
		public function addBall(evt:MouseEvent):void{
			this.myModel.addBall();
			var nbrString = String(this.myModel.nbrBalls);
			this.changeNbrBallButtons.nbrReadout.text = nbrString;
		}
		
		public function removeBall(evt:MouseEvent):void{
			this.myModel.removeBall();
			var nbrString = String(this.myModel.nbrBalls);
			this.changeNbrBallButtons.nbrReadout.text = nbrString;
		}
		
		//may not be necessary, since this is a controller, not a view
		public function update():void{
									  
		}
		
	}//end of class
}//end of package