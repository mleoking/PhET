/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/2/13
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.triglab.model.TrigModel;

import flash.display.Sprite;

//view of readout panel which displays current angle in rads or degrees, values of sine, cos, tangent
public class ReadoutView extends Sprite {
    private var myMainView: MainView;
    private var myTrigModel: TrigModel;
    private var readoutPanel: Sprite;

    public function ReadoutView( myMainView: MainView,  myTrigModel: TrigModel ) {
        this.myMainView = myMainView;
        this.myTrigModel = myTrigModel;
        this.myTrigModel.registerView( this );
        this.readoutPanel = new Sprite();
        this.addChild( this.readoutPanel );
        this.initialize();
    }

    private function initialize():void{
        myTrigModel.registerView( this );
    }

    public function update():void{

    }
} //end of class
} //end of package
