﻿package {
import edu.colorado.phet.flashcommon.TextFieldUtils;

import flash.display.*;
import flash.events.*;
import flash.text.*;

import edu.colorado.phet.flashcommon.SimStrings;

public class PlayPauseButtons extends Sprite {
    var myModel: Model;
    var buttonView: PlayButtons;		//library instance
    var paused: Boolean;
    var rewindText: String;			//for internationalization
    var backText: String;
    var playText: String;
    var pauseText: String;
    var stepText: String;

    public function PlayPauseButtons( myModel: Model ) {
        this.myModel = myModel;
        this.buttonView = new PlayButtons();  //library symbol
        this.addChild( this.buttonView );
        this.initializeButtons();
        this.initializeLabels();
        this.paused = true;
    }//end of constructor

    public function initializeButtons(): void {
        this.buttonView.myRewindButton.addEventListener( MouseEvent.MOUSE_DOWN, rewind );
        this.buttonView.myStepBackButton.addEventListener( MouseEvent.MOUSE_DOWN, stepBack );
        this.buttonView.myPlayPauseButton.addEventListener( MouseEvent.MOUSE_DOWN, playPause );
        this.buttonView.myStepButton.addEventListener( MouseEvent.MOUSE_DOWN, step );
        this.buttonView.myRewindButton.buttonMode = true;
        this.buttonView.myPlayPauseButton.buttonMode = true;
        this.buttonView.myStepButton.buttonMode = true;
    }

    private function setPlayText(): void {
        buttonView.playPauseLabel.text = playText;
        TextFieldUtils.resizeText( buttonView.playPauseLabel, TextFieldAutoSize.CENTER );
    }

    private function setPauseText(): void {
        buttonView.playPauseLabel.text = pauseText;
        TextFieldUtils.resizeText( buttonView.playPauseLabel, TextFieldAutoSize.CENTER );
    }

    //must be altered when internationalizing
    public function initializeLabels(): void {
        this.rewindText = SimStrings.get( "PlayPauseButtons.rewind", "Rewind" );
        this.backText = SimStrings.get( "PlayPauseButtons.back", "Back" );
        this.playText = SimStrings.get( "PlayPauseButtons.play", "Play" );
        this.pauseText = SimStrings.get( "PlayPauseButtons.pause", "Pause" );
        this.stepText = SimStrings.get( "PlayPauseButtons.step", "Step" );
        this.buttonView.rewindLabel.text = rewindText;
        TextFieldUtils.resizeText( this.buttonView.rewindLabel, TextFieldAutoSize.RIGHT );
        this.buttonView.stepBackLabel.text = backText;
        TextFieldUtils.resizeText( this.buttonView.stepBackLabel, TextFieldAutoSize.RIGHT );
        setPlayText();
        this.buttonView.stepLabel.text = stepText;
        TextFieldUtils.resizeText( this.buttonView.stepLabel, TextFieldAutoSize.LEFT );
    }

    public function rewind( evt: MouseEvent ): void {
        this.paused = true;
        this.myModel.stopMotion();
        this.buttonView.myPlayPauseButton.gotoAndStop( 1 );
        setPlayText();
        this.myModel.initializePositions();
        this.myModel.updateViews();
    }

    //Need rewind function without mouseEvent argument, to call from Reset All button in ControlPanel
    public function resetAllCalled(): void {
        this.paused = true;
        this.myModel.stopMotion();
        this.buttonView.myPlayPauseButton.gotoAndStop( 1 );
        setPlayText();
        //this.myModel.initializePositions();
    }

    public function playPause( evt: MouseEvent ): void {
        if ( paused ) {
            this.paused = false;
            this.buttonView.myPlayPauseButton.gotoAndStop( 2 );
            setPauseText();
            //                resizeText( txtField:TextField, alignment:String ):void {  //get an error when Object = textField
            this.myModel.startMotion()
        }
        else {
            this.paused = true;
            this.buttonView.myPlayPauseButton.gotoAndStop( 1 );
            setPlayText();
            this.myModel.stopMotion();
        }

    }

    public function step( evt: MouseEvent ): void {
        this.paused = true;
        this.buttonView.myPlayPauseButton.gotoAndStop( 1 );
        setPlayText();
        this.myModel.stopMotion();
        //this.myModel.detectCollision();
        this.myModel.singleStepping = true;
        this.myModel.singleFrame();
        this.myModel.singleStepping = false;
    }

    public function stepBack( evt: MouseEvent ): void {
        this.paused = true;
        this.buttonView.myPlayPauseButton.gotoAndStop( 1 );
        setPlayText();
        this.myModel.stopMotion();
        this.myModel.singleStepping = true;
        this.myModel.backupOneFrame();
        this.myModel.singleStepping = false;
    }


}//end of class
}//end of package