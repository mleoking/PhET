//MainView contains all views, acts as mediator, communication hub for views
package edu.colorado.phet.resonance {
//import edu.colorado.phet.flashcommon.FlashCommonCS4;

import flash.display.*;

import flash.events.Event;

import mx.containers.Canvas;

//import flash.geom.ColorTransform;

public class MainView extends Canvas {
    var myShakerModel: ShakerModel;
    var myShakerView: ShakerView;
    var myPlayPauseButtons: PlayPauseButtons;
    //var ruler:VerticalRuler;
    var myControlPanel: ControlPanel;
    var phetLogo: Sprite;
    var stageH: Number;
    var stageW: Number;

    public function MainView( myShakerModel: ShakerModel, stageW: Number, stageH: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this.myShakerModel = myShakerModel;
        this.myShakerView = new ShakerView( this, myShakerModel );

        this.myShakerView.x = 0.40 * stageW;
        this.myShakerView.y = 0.6 * stageH;

        this.myPlayPauseButtons = new PlayPauseButtons( this, myShakerModel );

        this.myPlayPauseButtons.x = this.myShakerView.x;
        this.myPlayPauseButtons.y = 0.9 * stageH; //this.myShakerView.y + this.myPlayPauseButtons.height;

        //this.ruler = new VerticalRuler( this, this.myShakerView );
        //this.ruler.x = 100;
        //this.ruler.y = 100;

        this.myControlPanel = new ControlPanel( this, myShakerModel );

        //this.myControlPanel.right = 10;    //does not work, "right" is a style property
        //this.myControlPanel.setStyle("right", 10);    //this works, but forces the control panel on the far right
        this.myControlPanel.x = 0.8 * stageW; //- 3 * this.myControlPanel.width;
        this.myControlPanel.y = 0.1 * stageH;

        this.phetLogo = new PhetIcon();

        this.phetLogo.x = stageW - 1.5 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;

        this.addChild( new SpriteUIComponent( myPlayPauseButtons ) );
        this.addChild( new SpriteUIComponent( myShakerView ) );
        //this.addChild( new SpriteUIComponent( ruler ));
        this.addChild( myControlPanel );
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();

    }//end of constructor

    //called from ControlPanel, so must not include any controlPanel setters
    public function setNbrResonators( nbrR: int ): void {
        this.myShakerModel.setNbrResonators( nbrR );
        this.myShakerView.setNbrResonators( nbrR );
    }

    public function initializeAll(): void {
        this.setNbrResonators( 1 );
        this.myControlPanel.setNbrResonatorsExternally( 1 );
        this.myControlPanel.setResonatorIndex(1);
        this.myControlPanel.setPresetComboBoxExternally( 3 );
        this.myControlPanel.setGravityExternally( false );
        this.myControlPanel.setDampingExternally( 1 );   //max is 5
        this.myControlPanel.setRulerCheckBoxExternally( false );
        this.myShakerView.initializeShakerControls();
        this.myPlayPauseButtons.unPauseExternally();
        this.myPlayPauseButtons.setSliderExternally(1);
        //this.setNbrResonators(2);
    }

    private function waitForGraphicsLoad(): void {
        //trace("MainView.momentumView.scale_slider.height = "+this.momentumView.scale_slider.height);
    }

}//end of class
}//end of package