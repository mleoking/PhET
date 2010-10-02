﻿package{
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
            TextFieldUtils.resizeText( buttonView.playPauseLabel, TextFieldAutoSize.CENTER);
        }

        private function setPauseText():void{
            buttonView.playPauseLabel.text = pauseText;
            TextFieldUtils.resizeText( buttonView.playPauseLabel, TextFieldAutoSize.CENTER);
        }
		
		//must be altered when internationalizing
		public function initializeLabels():void{
			this.resetText = SimStrings.get("PlayPauseButtons.reset","Reset");
			this.backText = SimStrings.get("PlayPauseButtons.back","Back");
			this.playText = SimStrings.get("PlayPauseButtons.play","Play");
			this.pauseText = SimStrings.get("PlayPauseButtons.pause","Pause");
			this.stepText = SimStrings.get("PlayPauseButtons.step","Step");
			this.buttonView.resetLabel.text = resetText;
            TextFieldUtils.resizeText(this.buttonView.resetLabel,TextFieldAutoSize.RIGHT);
			this.buttonView.stepBackLabel.text = backText;
            TextFieldUtils.resizeText(this.buttonView.stepBackLabel,TextFieldAutoSize.RIGHT);
            setPlayText();
			this.buttonView.stepLabel.text = stepText;
            TextFieldUtils.resizeText(this.buttonView.stepLabel,TextFieldAutoSize.LEFT);
		}
		
		public function reset(evt:MouseEvent):void{
			this.paused = true;
			this.myModel.stopMotion();
			this.buttonView.myPlayPauseButton.gotoAndStop(1);
		    setPlayText();
			this.myModel.initializePositions();
			this.myModel.updateViews();
		}
		
		//Need reset function without mouseEvent argument, to call from Reset All button in ControlPanel
		public function resetAllCalled():void{
			this.paused = true;
			this.myModel.stopMotion();
			this.buttonView.myPlayPauseButton.gotoAndStop(1);
		    setPlayText();
			//this.myModel.initializePositions();
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