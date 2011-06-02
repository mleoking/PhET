/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/1/11
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes {
import flash.display.Sprite;

import mx.containers.Canvas;

public class MainView extends Canvas {
    var myModel: Model;
    var myView: View;
    var myPlayPauseButtons: PlayPauseButtons;
    //var ruler:VerticalRuler;
    var myControlPanel: ControlPanel;
    var phetLogo: Sprite;
    var stageH: Number;
    var stageW: Number;

    //Internalized strings are located at:


    public function MainView( myModel: Model, stageW: Number, stageH: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this.myModel = myModel;
        this.myView = new View( this, myModel );

        this.myView.x = 0.40 * stageW;
        this.myView.y = 0.6 * stageH;

        this.myPlayPauseButtons = new PlayPauseButtons( this, myModel );

        this.myPlayPauseButtons.x = this.myView.x;
        this.myPlayPauseButtons.y = 0.9 * stageH; //this.myShakerView.y + this.myPlayPauseButtons.height;

        this.myControlPanel = new ControlPanel( this, myModel );

        //this.myControlPanel.right = 10;    //does not work, "right" is a style property
        //this.myControlPanel.setStyle("right", 10);    //this works, but forces the control panel on the far right
        this.myControlPanel.x = 0.8 * stageW; //- 3 * this.myControlPanel.width;
        this.myControlPanel.y = 0.1 * stageH;

        this.phetLogo = new PhetIcon();

        this.phetLogo.x = stageW - 1.5 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;

        this.addChild( new SpriteUIComponent( myPlayPauseButtons ) );
        this.addChild( new SpriteUIComponent( myView ) );
        //this.addChild( new SpriteUIComponent( ruler ));
        this.addChild( myControlPanel );
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();

    }//end of constructor



    public function initializeAll(): void {
//        this.myControlPanel.setNbrResonatorsExternally( 1 );
//        this.myControlPanel.setResonatorIndex(1);
//        this.myControlPanel.setPresetComboBoxExternally( 3 );
//        this.myControlPanel.setGravityExternally( false );
//        this.myControlPanel.setDampingExternally( 1 );   //max is 5
//        this.myControlPanel.setRulerCheckBoxExternally( false );
        this.myView.initializeControls();
        this.myPlayPauseButtons.unPauseExternally();
        this.myPlayPauseButtons.setSliderExternally(1);
        //this.setNbrResonators(2);
    }


}//end of class
} //end of package
