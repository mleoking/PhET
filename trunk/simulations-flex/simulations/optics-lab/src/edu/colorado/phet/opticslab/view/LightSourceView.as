/**
 * Created by Dubson on 5/24/2014.
 * View of LightSource
 */
package edu.colorado.phet.opticslab.view {
import edu.colorado.phet.opticslab.model.OpticsModel;

import flash.display.Graphics;

import flash.display.Sprite;

public class LightSourceView extends Sprite {
    //private var myMainView: MainView;
    private var myOpticsModel: OpticsModel;
    private var sourceHolder: Sprite;       //like a flashlight, holds a bunch of rays

    public function LightSourceView( opticsModel: OpticsModel ) {
        //myMainView = mainView;
        myOpticsModel = opticsModel;
        this.sourceHolder = new Sprite();
        myOpticsModel.registerView( this );
        init();
    }//end constructor

    private function init():void{
        //this.sourceHolder = new Sprite();
        drawSourceHolder();
        this.addChild( sourceHolder );


    }//end init()

    private function drawSourceHolder():void{
        var g: Graphics = sourceHolder.graphics;
        g.clear();
        g.lineStyle( 5, 0xff0000 );
        g.beginFill( 0x0000ff );
        //g.moveTo( 0, 0 );
        //g.lineTo( 50, 50 )
        g.drawCircle( 0, 0, 20 );
        g.endFill();


    }//end drawSourceHolder;

    public function update():void{

    }//end update
}//end class
}//end package
