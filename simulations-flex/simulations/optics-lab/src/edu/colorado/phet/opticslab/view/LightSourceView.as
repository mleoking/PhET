/**
 * Created by Dubson on 5/24/2014.
 * View of LightSource
 */
package edu.colorado.phet.opticslab.view {
import edu.colorado.phet.opticslab.model.OpticsModel;

import flash.display.Sprite;

public class LightSourceView extends Sprite {
    private var myMainView: MainView;
    private var myOpticsModel: OpticsModel;
    public function LightSourceView( mainView: MainView, opticsModel: OpticsModel ) {
        myMainView = mainView;
        myOpticsModel = opticsModel;
        myOpticsModel.registerView( this );
        init();
    }//end constructor

    private function init():void{
        drawLightSource();



    }//end init()

    private function drawLightSource():void{

    }

    public function update():void{

    }//end update
}//end class
}//end package
