//CollisionLab M.Dubson Nov 5, 2009
//source code resides in /collision-lab
//main class instance

package {

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
    }//end of constructor

}//end of class
}//end of package