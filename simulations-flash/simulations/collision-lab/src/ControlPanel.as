package{
	
	import flash.display.*;
	import flash.events.*;
	import fl.events.*;
	import flash.text.*;
	import flash.ui.*;
	
	public class ControlPanel extends Sprite{
		private var myModel:Model;
		private var myMainView:MainView;
		
		public function ControlPanel(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.myMainView.addChild(this);
			//this.myModel.registerView(this);
			this.initialize();
			this.initializeComponents();
		}//end of constructor
		
		public function initialize():void{
			
		}
		
		public function initializeComponents():void{
			
			this.timeRateSlider.addEventListener(SliderEvent.CHANGE, setTimeRate);
			this.elasticitySlider.addEventListener(SliderEvent.CHANGE, setElasticity);
			
		}
		
		public function setTimeRate(evt:SliderEvent):void{
			//trace("time slider: "+evt.target.value);
			this.myModel.setTimeRate(evt.target.value);
		}
		
		public function setElasticity(evt:SliderEvent):void{
			trace("e slider: "+evt.target.value);
		}
		
		//may not be necessary, since this is a controller, not a view
		public function update():void{
									  
		}
		
	}//end of class
}//end of package