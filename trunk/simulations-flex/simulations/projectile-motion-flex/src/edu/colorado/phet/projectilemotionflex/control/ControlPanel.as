/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/17/12
 * Time: 9:10 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.control {
import edu.colorado.phet.flashcommon.controls.NiceButton2;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.projectilemotionflex.model.TrajectoryModel;
import edu.colorado.phet.projectilemotionflex.view.MainView;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.ComboBox;

//control panel for projectile motion flex

public class ControlPanel extends Canvas {
    private var mainView: MainView;
    private var trajectoryModel: TrajectoryModel;
    private var stageW: Number;
    private var stageH: Number;

    private var background: VBox;
    private var buttonBackground: HBox;
    private var selectProjectile: ComboBox;
    private var choiceList_arr: Array;

    private var fireButton: NiceButton2;
    private var eraseButton: NiceButton2;

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

    public function ControlPanel( mainView:MainView,  trajectoryModel: TrajectoryModel ) {
        this.mainView = mainView;
        this.trajectoryModel = trajectoryModel;
        this.stageW = mainView.stageW;
        this.stageH = mainView.stageH;
        this.initialize();
    } //end constructor

    private function initialize():void{
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
            setStyle( "horizontalAlign", "center" );
        }
        this.buttonBackground = new HBox();

        this.selectProjectile = new ComboBox();
        choiceList_arr = new Array();
        choiceList_arr = [ userChoice_str, tankshell_str, golfball_str, baseball_str, bowlingball_str, football_str, pumpkin_str, adultHuman_str, piano_str, buick_str ];
        selectProjectile.dataProvider = choiceList_arr;
        this.fireButton = new NiceButton2( 60, 25, fire_str, fireProjectile, 0x00ff00, 0x000000 );
        this.eraseButton = new NiceButton2( 60, 25, erase_str, eraseTrajectories, 0xff0000, 0xffffff );
    }

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
    } //end initializeStrings()

    private function fireProjectile():void{

    }

    private function eraseTrajectories():void{

    }

}//end class
}//end package
