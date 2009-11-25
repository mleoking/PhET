package{
	
	import flash.display.*;
	import flash.events.*;
	import fl.events.*;
	import flash.text.*;
	import flash.ui.*;
	import fl.controls.*;
	
	public class ControlPanel extends Sprite{
		private var myModel:Model;
		private var myMainView:MainView;
		private var nbrBalls;
		//private var changeNbrBallButtons:ChangeNbrBallButtons;	//libary symbol instance
		
		public function ControlPanel(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.myMainView.addChild(this);
			this.nbrBalls = this.myModel.nbrBalls;
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
		}
		
		public function initializeComponents():void{
			this.velocity_rb.addEventListener(MouseEvent.CLICK, showVelocityArrows);
			this.momentum_rb.addEventListener(MouseEvent.CLICK, showMomentumArrows);
			this.none_rb.addEventListener(MouseEvent.CLICK, showNoArrows)
			this.showPaths_cb.addEventListener(MouseEvent.CLICK, showOrErasePaths);
			this.timeRateSlider.addEventListener(SliderEvent.CHANGE, setTimeRate);
			this.elasticitySlider.addEventListener(SliderEvent.CHANGE, setElasticity);
			
		}
		
		public function showVelocityArrows(evt:MouseEvent):void{
			if(evt.target.selected){
				this.myMainView.myTableView.showArrowsOnBallImages(true);
			}
			//trace("velocity rb clicked. target is "+evt.target.selected);
		}
		public function showMomentumArrows(evt:MouseEvent):void{
			trace("momentum rb clicked.target is "+evt.target.selected);
		}
		public function showNoArrows(evt:MouseEvent):void{
			if(evt.target.selected){
				this.myMainView.myTableView.showArrowsOnBallImages(false);
			}
			//trace("no rb clicked.target is "+evt.target.selected);
		}
		
		public function showOrErasePaths(evt:MouseEvent){
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