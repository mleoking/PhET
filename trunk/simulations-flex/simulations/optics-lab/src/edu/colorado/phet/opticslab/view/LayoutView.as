/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 5/21/14
 * Time: 4:51 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.opticslab.view {
import edu.colorado.phet.opticslab.model.OpticsModel;

import flash.display.Sprite;

//View of primary play area on which light sources and lenses, etc are "laid out".
//This view acts as the optics bench on which all components are arranged by the user.

public class LayoutView extends Sprite {
    private var myMainView: MainView;
    private var myOpticsModel: OpticsModel;
    private var testSource: LightSourceView;           //for testing graphics
    public function LayoutView( myMainView: MainView, myOpticsModel: OpticsModel ) {
        this.myMainView = myMainView;
        this.myOpticsModel = myOpticsModel;
        this.myOpticsModel.registerView( this );
        this.myOpticsModel.updateViews();
        this.init();
    }

    private function init():void{
        testSource = new LightSourceView( myOpticsModel );
        this.addChild( testSource );
        testSource.x = testSource.y = 50;
        //testSource.x = 50;
        //testSource.y = 50;
    }

    public function update():void{

    }//end of update()
}  //end of class
}  //end of package
