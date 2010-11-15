//CollisionLab M.Dubson Nov 5, 2009
//source code resides in /collision-lab
//main class instance

package edu.colorado.phet.collisionlab {

import edu.colorado.phet.collisionlab.constants.CollisionLabConstants;
import edu.colorado.phet.collisionlab.control.Tab;
import edu.colorado.phet.collisionlab.control.TabBar;
import edu.colorado.phet.flashcommon.SimStrings;

import flash.display.*;

public class CollisionLab extends Sprite {  //should the main class extend MovieClip or Sprite?
    var stageW: Number;
    var stageH: Number;
    var introModule: IntroModule = new IntroModule();

    public function CollisionLab() {
        SimStrings.init( loaderInfo );
        //stage width and height hard-coded for now
        this.stageW = 950;//this.stage.stageWidth;
        this.stageH = 700;//this.stage.stageHeight;
        introModule.attach( this );
        var tabBar: TabBar = new TabBar( CollisionLabConstants.BACKGROUND_COLOR, 0xFFFFBB );
        var introTab: Tab = new Tab( "Introduction", tabBar );
        tabBar.addTab( introTab );
        var advancedTab: Tab = new Tab( "Advanced", tabBar );
        tabBar.addTab( advancedTab );
        tabBar.selectedTab = introTab;
        addChild( tabBar );
    }//end of constructor

}//end of class
}//end of package