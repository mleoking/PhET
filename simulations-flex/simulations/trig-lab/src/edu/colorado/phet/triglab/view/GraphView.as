/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:25 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.triglab.model.TrigModel;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;

import flash.display.Graphics;

import flash.display.Sprite;

public class GraphView extends Sprite{

    private var myMainView: MainView;
    private var myTrigModel: TrigModel;
    private var axesGraph: Sprite;
    private var cosGraph: Sprite;
    private var sinGraph: Sprite;


    public function GraphView( myMainView: MainView,  myTrigModel: TrigModel ) {
        this.myMainView = myMainView;
        this.myTrigModel = myTrigModel;

        this.myTrigModel.registerView( this );
        this.initializeStrings();
        this.init();
        this.myTrigModel.updateViews();
    }

    private function initializeStrings():void{
        //this.paused_str = FlexSimStrings.get( "paused", "PAUSED" );
    }

    private function init():void{

    }

    public function update():void{

    }//end of update()
}//end of class
}//end of package
