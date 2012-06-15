
/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/1/11
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.view {
import edu.colorado.phet.flashcommon.view.PhetIcon;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;

import flash.display.Sprite;

import mx.containers.Canvas;

//main view and communications hub for Radiating Charge sim
public class MainView extends Canvas {

    public var phetLogo: Sprite;
    public var stageH: Number;
    public var stageW: Number;

    //Internalized strings are located at:
    //

    public function MainView( stageW: Number, stageH: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;

        this.phetLogo = new PhetIcon();
        this.phetLogo.x = stageW - 2.0 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;

        //trace( "stageW:" + stageW + "   stageH:" +stageH );

        //phetLogo now in tab area
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();
    }//end of constructor


    public function initializeAll(): void {
//        this.myModel.pauseSim();
        //stage.quality = StageQuality.LOW;

    }//end of initializeAll()



}//end of class
} //end of package
