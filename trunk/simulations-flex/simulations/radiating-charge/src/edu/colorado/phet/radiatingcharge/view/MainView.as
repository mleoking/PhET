
/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/1/11
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.view {
import edu.colorado.phet.flashcommon.controls.Tab;
import edu.colorado.phet.flashcommon.controls.TabBar;
import edu.colorado.phet.radiatingcharge.*;
import edu.colorado.phet.radiatingcharge.model.FieldModel;

import org.aswing.event.ModelEvent;


import edu.colorado.phet.flashcommon.view.PhetIcon;
import edu.colorado.phet.radiatingcharge.util.SpriteUIComponent;

import flash.display.Sprite;

import mx.containers.Canvas;
import mx.controls.sliderClasses.Slider;

public class MainView extends Canvas {

    public var myFieldModel:FieldModel;
//    public var myControlPanel:ControlPanel;

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
        this.myFieldModel = new FieldModel(this);

//        var oneDHolder:Canvas = new Canvas();
//        var twoDHolder:Canvas = new Canvas();
//        addChild( oneDHolder );
//        addChild( twoDHolder  );

//        tabBar = new TabBar();
//        var oneDTab: Tab = new Tab( "1 Dimension  ", tabBar );
//        var twoDTab: Tab = new Tab( "2 Dimensions  ", tabBar );
//        tabBar.addTab( oneDTab );
//        tabBar.addTab( twoDTab );
//
//        tabBar.selectedTab = oneDTab;
//
//        tabBar.addListener( function(): void {
//            if ( tabBar.selectedTab == oneDTab ) {
//                set1DOr2D( 1 );
//            }
//            else { // advanced tab
//                set1DOr2D( 2 );
//            }
//        } );
//
//        this.addChild( new SpriteUIComponent( tabBar ));



        this.phetLogo = new PhetIcon();

        this.phetLogo.x = stageW - 2.0 * this.phetLogo.width;
        this.phetLogo.y = 0;// stageH - 1.5 * this.phetLogo.height;



        //phetLogo now in tab area
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();
    }//end of constructor






    public function initializeAll(): void {
//        this.myModel.pauseSim();

    }



}//end of class
} //end of package
