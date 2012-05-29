package edu.colorado.phet.collisionlab.control {
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.flashcommon.SimStrings;
import edu.colorado.phet.flashcommon.TextFieldUtils;

import flash.display.*;
import flash.events.*;
import flash.text.*;

public class PlayPauseButtons extends Sprite {
    var myModel: Model;
    var buttonView: PlayButtons;		//library instance
    var paused: Boolean;
    var rewindText: String;			//for internationalization
    var backText: String;
    var playText: String;
    var pauseText: String;
    var stepText: String;
    var backEnabled: Boolean = true;

    public function PlayPauseButtons( myModel: Model ) {
        this.myModel = myModel;
        this.buttonView = new PlayButtons();  //library symbol
        this.addChild( this.buttonView );
        this.initializeButtons();
        this.initializeLabels();
        this.paused = true;
        myModel.registerView( this );

        this.buttonView.myStepBackButton.visible = false;
        this.buttonView.stepBackLabel.visible = false;
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
        this.rewindText = SimStrings.get( "PlayPauseButtons.rewind", "Restart" );
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

    public function updateBackEnabled( resetting: Boolean ): void {
        backEnabled = myModel.time > 0 && myModel.e == 1 && !resetting;

        // grey-out the button and label
        var alpha: Number = backEnabled ? 1 : 0.5;
        this.buttonView.stepBackLabel.alpha = alpha;
        this.buttonView.myStepBackButton.alpha = alpha;
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
        updateBackEnabled( true );
        this.buttonView.myPlayPauseButton.gotoAndStop( 1 );
        setPlayText();
        //this.myModel.initializePositions();
    }

    public function playPause( evt: MouseEvent ): void {
        if ( paused ) {
            setToPlaying();
            //                resizeText( txtField:TextField, alignment:String ):void {  //get an error when Object = textField
            this.myModel.startMotion()
        }
        else {
            setToPaused();
            this.myModel.stopMotion();
        }
    }

    private function setToPaused(): void {
        this.paused = true;
        this.buttonView.myPlayPauseButton.gotoAndStop( 1 );
        setPlayText();
    }

    private function setToPlaying(): void {
        this.paused = false;
        this.buttonView.myPlayPauseButton.gotoAndStop( 2 );
        setPauseText();
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
        if( !backEnabled ) {
            return;
        }
        this.paused = true;
        this.buttonView.myPlayPauseButton.gotoAndStop( 1 );
        setPlayText();
        this.myModel.stopMotion();
        this.myModel.singleStepping = true;
        this.myModel.backupOneFrame();
        this.myModel.singleStepping = false;
    }

    public function update(): void { // called when model changes (basically)

        // sync the state to the model
        if ( paused && myModel.playing ) {
            setToPlaying();
        }
        if ( !paused && !myModel.playing ) {
            setToPaused();
        }

        updateBackEnabled( false );
    }

}//end of class
}//end of package