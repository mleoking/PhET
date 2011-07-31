/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 7/31/11
 * Time: 3:45 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.view {
import edu.colorado.phet.normalmodes.model.Model1;

import flash.display.Sprite;

public class View1DModes extends Sprite {

    public var myMainView: MainView;		//MainView
    private var myModel1: Model1;			//model for this view
    private var _pixPerMeter: Number;		//scale: number of pixels in 1 meter

    public function View1DModes() {
        this.myMainView = myMainView;
        this.myModel1 = myModel1;
        this.myModel1.registerView( this );
        this.initialize();
    }

    private function initialize():void{

    }//end initialize()


    public function update():void{

    }//end update()
} //end class
} //end package
