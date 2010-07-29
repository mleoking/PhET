package{
import edu.colorado.phet.flashcommon.TextFieldUtils;

import flash.display.*;
	import flash.events.*;
	import flash.text.*;
    import edu.colorado.phet.flashcommon.SimStrings;
	
	public class PlayPauseButtons extends Sprite{
		var myModel:Model;
		var buttonView:PlayButtons;		//library instance
		var paused:Boolean;
		var resetText:String;			//for internationalization
		var backText:String;
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
			this.buttonView.myStepBackButton.addEventListener(MouseEvent.MOUSE_DOWN, stepBack);
			this.buttonView.myPlayPauseButton.addEventListener(MouseEvent.MOUSE_DOWN, playPause);
			this.buttonView.myStepButton.addEventListener(MouseEvent.MOUSE_DOWN, step);
			this.buttonView.myResetButton.buttonMode = true;
			this.buttonView.myPlayPauseButton.buttonMode = true;
			this.buttonView.myStepButton.buttonMode = true;
		}

        private function setPlayText():void{
            buttonView.playPauseLabel.text = playText;
            TextFieldUtils.resizeText( buttonView.playPauseLabel, TextFieldUtils.CENTER);
        }

        private function setPauseText():void{
            buttonView.playPauseLabel.text = pauseText;
            TextFieldUtils.resizeText( buttonView.playPauseLabel, TextFieldUtils.CENTER);
        }
		
		//must be altered when internationalizing
		public function initializeLabels():void{
			this.resetText = SimStrings.get("PlayPauseButtons.reset","Reset");
			this.backText = "Back";
			this.playText = SimStrings.get("PlayPauseButtons.play","Play");
			this.pauseText = "Pause";
			this.stepText = "Step";
			this.buttonView.resetLabel.text = resetText;
			this.buttonView.stepBackLabel.text = backText;
            setPlayText();
			this.buttonView.stepLabel.text = stepText;
		}
		
		public function reset(evt:MouseEvent):void{
			this.paused = true;
			this.myModel.stopMotion();
			this.buttonView.myPlayPauseButton.gotoAndStop(1);
		    setPlayText();
			this.myModel.initializePositions();
		}
		public function playPause(evt:MouseEvent):void{
			if(paused){
				this.paused = false;
				this.buttonView.myPlayPauseButton.gotoAndStop(2);
				setPauseText();
//                resizeText( txtField:TextField, alignment:String ):void {  //get an error when Object = textField
				this.myModel.startMotion()
			}else{
				this.paused = true;
				this.buttonView.myPlayPauseButton.gotoAndStop(1);
				setPlayText();
				this.myModel.stopMotion();
			}
			
		}
		public function step(evt:MouseEvent):void{
			this.paused = true;
			this.buttonView.myPlayPauseButton.gotoAndStop(1);
			setPlayText();
			this.myModel.stopMotion();
			//this.myModel.detectCollision();
			this.myModel.singleFrame();
		}
		
		public function stepBack(evt:MouseEvent):void{
			this.paused = true;
			this.buttonView.myPlayPauseButton.gotoAndStop(1);
			setPlayText();
			this.myModel.stopMotion();
			this.myModel.backupOneFrame();
		}
		
		
	}//end of class
}//end of package