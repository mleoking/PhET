package{
	import flash.display.*;
	import flash.events.*;
	import flash.text.*;
	
	public class PlayPauseButtons extends Sprite{
		var myModel:Model;
		var buttonView:PlayButtons;		//library instance
		var paused:Boolean;
		var resetText:String;			//for internationalization
		var playText:String;  			
		var pauseText:String;
		var stepText:String;
		
		public function PlayPauseButtons(myModel:Model){
			this.myModel = myModel;
			this.buttonView = new PlayButtons();  //library symbol
			this.addChild(this.buttonView);
			this.initializeButtons();
			this.initializeLabels();
			this.paused = true;
		}//end of constructor
		
		public function initializeButtons():void{
			this.buttonView.myResetButton.addEventListener(MouseEvent.MOUSE_DOWN, reset);
			this.buttonView.myPlayPauseButton.addEventListener(MouseEvent.MOUSE_DOWN, playPause);
			this.buttonView.myStepButton.addEventListener(MouseEvent.MOUSE_DOWN, step);
			this.buttonView.myResetButton.buttonMode = true;
			this.buttonView.myPlayPauseButton.buttonMode = true;
			this.buttonView.myStepButton.buttonMode = true;
		}
		
		//must be altered when internationalizing
		public function initializeLabels():void{
			this.resetText = "Reset";
			this.playText = "Play";
			this.pauseText = "Pause";
			this.stepText = "Step";
			this.buttonView.resetLabel.text = resetText;
			this.buttonView.playPauseLabel.text = playText;
			this.buttonView.stepLabel.text = stepText;
		}
		
		public function reset(evt:MouseEvent):void{
			this.paused = true;
			this.myModel.stopMotion();
			this.buttonView.myPlayPauseButton.gotoAndStop(1);
				this.buttonView.playPauseLabel.text = playText;
			this.myModel.initializePositions();
		}
		public function playPause(evt:MouseEvent):void{
			if(paused){
				this.paused = false;
				this.buttonView.myPlayPauseButton.gotoAndStop(2);
				this.buttonView.playPauseLabel.text = pauseText;
				this.myModel.startMotion()
			}else{
				this.paused = true;
				this.buttonView.myPlayPauseButton.gotoAndStop(1);
				this.buttonView.playPauseLabel.text = playText;
				this.myModel.stopMotion();
			}
			
		}
		public function step(evt:MouseEvent):void{
			this.paused = true;
			this.buttonView.myPlayPauseButton.gotoAndStop(1);
			this.buttonView.playPauseLabel.text = playText;
			this.myModel.stopMotion();
			this.myModel.singleFrame();
		}
		
		
	}//end of class
}//end of package