//MainView contains all views, acts as mediator, communication hub for views
package edu.colorado.phet.resonance {
//import edu.colorado.phet.flashcommon.FlashCommonCS4;

import flash.display.*;

//import flash.geom.ColorTransform;

public class MainView extends Sprite {
    var myShakerModel: ShakerModel;
    var myShakerView: ShakerView;
    var myControlPanel: ControlPanel;
    var phetLogo: Sprite;
    var stageH: Number;
    var stageW: Number;

    public function MainView( myShakerModel: ShakerModel, stageW: Number, stageH: Number ) {
        this.stageH = stageH;
        this.stageW = stageW;
        this.myShakerModel = myShakerModel;
        this.myShakerView = new ShakerView( this, myShakerModel );
        this.addChild( myShakerView );
        this.myShakerView.x = 0.40 * stageW;
        this.myShakerView.y = 0.6 * stageH;

        this.myControlPanel = new ControlPanel( this, myShakerModel );
        this.addChild( myControlPanel );
        this.myControlPanel.x = stageW - 1.1 * this.myControlPanel.width;
        this.myControlPanel.y = 0.1 * stageH;

        this.phetLogo = new PhetIcon();
        this.addChild( phetLogo );
        this.phetLogo.x = stageW - 1.5 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;

    }//end of constructor

    public function setNbrResonators( nbrR: int ): void {
        this.myShakerModel.setNbrResonators( nbrR );
        this.myShakerView.setNbrResonators( nbrR );
    }

    //called from ..
    public function initialize(): void {
        trace( "myMainView initialize called" );
        //trace("this.msView.stage"+this.msView.stage)
        //this.phetLogo = new PhETLogo();
        //this.addChild(this.phetLogo);
        //this.myShakerView = new ShakerView(myShakerModel, this);
        //this.controlPanel = new ControlPanel(myShakerModel, this);
        //this.waitForGraphicsLoad();
        //this.myShakerModel.resetAll();
        //addFlashCommon();
        //trace("stageW: "+stageW+"   stageH: "+stageH);
    }//end of initialize()

    private function waitForGraphicsLoad(): void {
        //trace("MainView.momentumView.scale_slider.height = "+this.momentumView.scale_slider.height);
    }

}//end of class
}//end of package