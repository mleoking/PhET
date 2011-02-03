//CollisionLab M.Dubson Nov 5, 2009
//source code resides in /collision-lab
//main class instance

package edu.colorado.phet.collisionlab {

import edu.colorado.phet.collisionlab.constants.CLConstants;
import edu.colorado.phet.collisionlab.control.Tab;
import edu.colorado.phet.collisionlab.control.TabBar;
import edu.colorado.phet.flashcommon.FlashCommonCS4;
import edu.colorado.phet.flashcommon.SimStrings;

import flash.display.*;
import flash.events.Event;

public class CollisionLab extends Sprite {  //should the main class extend MovieClip or Sprite?
    private var stageW: Number;
    private var stageH: Number;
    private var introModule: IntroModule = new IntroModule();
    private var advancedModule: AdvancedModule = new AdvancedModule();
    private var common: FlashCommonCS4;

    public function CollisionLab() {
        SimStrings.init( loaderInfo );
        //stage width and height hard-coded for now
        this.stageW = 950;//this.stage.stageWidth;
        this.stageH = 700;//this.stage.stageHeight;
        var tabBar: TabBar = new TabBar();

        var introHolder: Sprite = new Sprite();
        addChild( introHolder );
        introModule.attach( introHolder );

        var advancedHolder: Sprite = new Sprite();
        addChild( advancedHolder );
        advancedModule.attach( advancedHolder );
        advancedHolder.visible = false;

        var introTab: Tab = new Tab( SimStrings.get( "introductionTab", "Introduction" ), tabBar );
        tabBar.addTab( introTab );
        var advancedTab: Tab = new Tab( SimStrings.get( "advancedTab", "Advanced" ), tabBar );
        tabBar.addTab( advancedTab );
        tabBar.selectedTab = introTab;
        tabBar.addListener( function(): void {
            if ( tabBar.selectedTab == introTab ) {
                introHolder.visible = true;
                advancedHolder.visible = false;
                introModule.onShow();
                advancedModule.onHide();
            }
            else { // advanced tab
                introHolder.visible = false;
                advancedHolder.visible = true;
                introModule.onHide();
                advancedModule.onShow();
            }
        } );
        addChild( tabBar );

        addFlashCommon();
    }

    private function addFlashCommon(): void {
        var ui: Sprite = new Sprite(); // used for FlashCommon UI
        addChild( ui );
        common = FlashCommonCS4.getInstance( ui.stage, this.stageW, this.stageH );//950,700 );
        common.initialize( ui );

        common.addLoadListener( positionButtons );
        stage.addEventListener( Event.RESIZE, function( evt: Event ): void {
            positionButtons();
        } );
        positionButtons();
    }

    protected function positionButtons(): void {
        if ( common.commonButtons == null ) {
            return;
        }
        var height: int = common.commonButtons.getPreferredHeight();
        const x: Number = 5;
        const y: Number = 700 - height - 5;
        common.commonButtons.setLocationXY( x, y );
    }

}//end of class
}//end of package