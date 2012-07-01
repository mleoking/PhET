/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/17/12
 * Time: 9:10 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.control {
import edu.colorado.phet.flashcommon.controls.NiceButton2;
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flashcommon.controls.NiceTextField;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;
import edu.colorado.phet.projectilemotionflex.view.MainView;

import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.events.TextEvent;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.CheckBox;
import mx.controls.ComboBox;
import mx.controls.Label;
import mx.controls.TextInput;
import mx.core.FlexSimpleButton;

//control panel for projectile motion flex

public class ControlPanel extends Canvas {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;

    private var background: VBox;
    private var zoomControlBackground: HBox;
    private var buttonBackground: HBox;
    private var projectileComboBox: ComboBox;
    private var choiceList_arr: Array;

    private var angleReadout: NiceTextField;
    private var speedReadout: NiceTextField;
    private var massReadout: NiceTextField;
    private var diameterReadout: NiceTextField;

    private var airResistanceHBox: HBox;
    private var airResistanceCheckBox: CheckBox;
    private var airResistanceLabel: NiceLabel;


    private var fireButton: NiceButton2;
    private var eraseButton: NiceButton2;

    //button strings
    private var fire_str: String;
    private var erase_str: String;
    //projectile strings
    private var userChoice_str: String;
    private var tankshell_str: String;
    private var golfball_str: String;
    private var baseball_str: String;
    private var bowlingball_str: String;
    private var football_str: String;
    private var pumpkin_str: String;
    private var adultHuman_str: String;
    private var piano_str: String;
    private var buick_str: String;
    //input box strings
    private var angle_str: String;
    private var speed_str: String;
    private var mass_str: String;
    private var diameter_str: String;
    //check box string
    private var airResistance_str: String;

    public function ControlPanel( mainView:MainView,  trajectoryModel: TrajectoryModel ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel;
        trajectoryModel.registerView( this );
        this.stageW = mainView.stageW;
        this.stageH = mainView.stageH;
        this.init();
    } //end constructor

    private function init():void{
        this.initializeStrings();

        this.background = new VBox();
        with ( this.background ) {
            setStyle( "backgroundColor", 0x88ff88 );
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x009900 );
            setStyle( "cornerRadius", 10 );
            setStyle( "borderThickness", 4 );
            setStyle( "paddingTop", 15 );
            setStyle( "paddingBottom", 15 );
            setStyle( "paddingRight", 5 );
            setStyle( "paddingLeft", 5 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "right" );
        }

        zoomControlBackground = new HBox();
        buttonBackground = new HBox();

        projectileComboBox = new ComboBox();
        choiceList_arr = new Array();
        choiceList_arr = [ userChoice_str, tankshell_str, golfball_str, baseball_str, bowlingball_str, football_str, pumpkin_str, adultHuman_str, piano_str, buick_str ];
        projectileComboBox.dataProvider = choiceList_arr;

        angleReadout = new NiceTextField( setAngle, angle_str, -90, 180 );
        speedReadout = new NiceTextField( setSpeed, speed_str, 0, 10000 );
        massReadout = new NiceTextField( setMass, mass_str, 0.01, 1000 );
        diameterReadout = new NiceTextField( setDiameter, diameter_str, 0.01, 10 );

        airResistanceHBox = new HBox();
        //airResistanceHBox.setStyle( "horizontalGap", 0 );
        airResistanceCheckBox = new CheckBox();
        airResistanceLabel = new NiceLabel( 12, airResistance_str );
        airResistanceCheckBox.addEventListener( Event.CHANGE , clickAirResistance );



        buttonBackground = new HBox();
        fireButton = new NiceButton2( 60, 25, fire_str, fireProjectile, 0x00ff00, 0x000000 );
        eraseButton = new NiceButton2( 60, 25, erase_str, eraseTrajectories, 0xff0000, 0xffffff );

        //layout of compoenents
        addChild( background );
        background.addChild( projectileComboBox );
        background.addChild( new SpriteUIComponent( angleReadout ) );
        background.addChild( new SpriteUIComponent( speedReadout ) );
        background.addChild( new SpriteUIComponent( massReadout ) );
        background.addChild( new SpriteUIComponent( diameterReadout ) );

        background.addChild( airResistanceHBox );
        airResistanceHBox.addChild( airResistanceCheckBox );
        airResistanceHBox.addChild( new SpriteUIComponent( airResistanceLabel, true ));



        background.addChild( buttonBackground );
        buttonBackground.addChild( new SpriteUIComponent( fireButton, true ) );
        buttonBackground.addChild( new SpriteUIComponent( eraseButton, true ));
        this.update();
    }//end initialize()

//    private function createInputControl( hBox:HBox,  label:Label, label_str:String,  txtInput:TextInput, listener: Function ):void {
//        hBox.addChild( label );
//        hBox.addChild( txtInput );
//        hBox.setStyle( "horizontalGap", 2 );
//        hBox.setStyle( "horizontalAlign", "right" );
//        label.text = label_str;
//        label.truncateToFit = false;
//        label.setStyle( "textAlign", "right" );
//        label.setStyle( "fontSize", 12 );
//        txtInput.setStyle( "right", 5 );
//        txtInput.setStyle( "fontSize", 14 );
//        txtInput.width = 40;
//        txtInput.restrict = "0-9 . \\-" ;
//        //txtInput.setStyle( "", "right")
//        txtInput.addEventListener( Event.CHANGE, listener )
//    }

    private function initializeStrings():void{
        this.fire_str = FlexSimStrings.get( "fire", "Fire" );
        this.erase_str = FlexSimStrings.get( "erase", "Erase" );
        userChoice_str = FlexSimStrings.get( "userChoice", "user choice" );
        tankshell_str = FlexSimStrings.get( "tankshell", "tankshell" );
        golfball_str = FlexSimStrings.get( "golfball", "golfball" );
        baseball_str = FlexSimStrings.get( "baseball", "baseball" );
        bowlingball_str = FlexSimStrings.get( "bowlingball", "bowlingball" );
        football_str = FlexSimStrings.get( "football", "football" );
        pumpkin_str = FlexSimStrings.get( "pumpkin", "pumpkin" );
        adultHuman_str = FlexSimStrings.get( "adultHuman", "adult human" );
        piano_str = FlexSimStrings.get( "piano", "piano" );
        buick_str = FlexSimStrings.get( "buick", "buick" );
        angle_str = FlexSimStrings.get( "angle", "angle(degrees)" );
        speed_str = FlexSimStrings.get( "speed", "initial speed(m/s)" );
        mass_str = FlexSimStrings.get( "mass", "mass(kg)" );
        diameter_str = FlexSimStrings.get( "diameter", "diameter(m)" );
        airResistance_str = FlexSimStrings.get( "airResistance", "Air Resistance" );
    } //end initializeStrings()

    private function formatInputBox( box: TextInput ):void{

    }

    private function fireProjectile():void{
        trajectoryModel.fireCannon();
        mainView.backgroundView.trajectoryView.startTrajectory();
    }

    private function eraseTrajectories():void{
        mainView.backgroundView.trajectoryView.eraseTrajectory();
    }

    private function setAngle( angleInDeg ):void{
        if( angleInDeg > 180 ){
            angleInDeg = 180;
        } else if(angleInDeg < -180 ){
            angleInDeg = -180;
        }
        //trace("angleListener called text is "+ angleInDeg );
        if( angleInDeg < 180 && angleInDeg > -180 && !isNaN( angleInDeg )){
            trajectoryModel.angleInDeg = angleInDeg;
        }
    }

    private function setSpeed( initSpeed ):void{
        trajectoryModel.v0 = initSpeed;
    }

    private function setMass( massInkg ):void{
        trajectoryModel.setMass( massInkg );
    }

    private function setDiameter( diameterInMeters ):void{
       trajectoryModel.setDiameter( diameterInMeters );
        this.mainView.backgroundView.projectileView.drawProjectileInFlight();
        //this.mainView.backgroundView.projectileView.drawProjectileOnGround();
    }

    private function clickAirResistance( evt: Event ):void{
        var selected:Boolean = airResistanceCheckBox.selected;
        trajectoryModel.airResistance = selected;
        mainView.backgroundView.trajectoryView.updateTrajectoryColor();
    }

    public function update():void{
        var angleNbr:Number = trajectoryModel.angleInDeg;
        var speedNbr: Number = trajectoryModel.v0;
        var massNbr: Number = trajectoryModel.projectiles[0].mass;
        var diameterNbr: Number = trajectoryModel.projectiles[0].diameter;
        this.angleReadout.setVal( angleNbr );
        this.speedReadout.setVal( speedNbr );
        this.massReadout.setVal( massNbr );
        this.diameterReadout.setVal ( diameterNbr );

//        var angleStr:String;
//        //round to nearest tenth;
//        angleNbr = Math.round( 10*angleNbr )/10;
//        if( angleNbr%1 != 0 ){
//            this.angle_txt.text = angleNbr.toString();
//        }else{
//            this.angle_txt.text = angleNbr.toString() + ".0";
//        }
//        var initSpeed:Number = trajectoryModel.v0;
//        this.speed_txt.text = initSpeed.toString();


    }//end update()

}//end class
}//end package
