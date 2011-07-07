/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 5/31/11
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes {
import edu.colorado.phet.flashcommon.controls.Tab;
import edu.colorado.phet.flashcommon.controls.TabBar;
import edu.colorado.phet.normalmodes.util.SpriteUIComponent;
import edu.colorado.phet.normalmodes.view.MainView;

import flash.display.Sprite;

import mx.containers.Canvas;



public class NormalModes extends Canvas {

    private var tabBar: TabBar;
    public function NormalModes( w: Number, h: Number ) {
        //percentWidth = 100;
        //percentHeight = 100;
        //this.addChild( new MainView( w, h ) );
        var oneDHolder:Canvas = new Canvas();
        var twoDHolder:Canvas = new Canvas();
        addChild( oneDHolder );
        addChild( twoDHolder  );

        tabBar = new TabBar();
        var oneDTab: Tab = new Tab( " 1 D  ", tabBar );
        var twoDTab: Tab = new Tab( " 2 D  ", tabBar );
        tabBar.addTab( oneDTab );
        tabBar.addTab( twoDTab );

        tabBar.selectedTab = oneDTab;

        tabBar.addListener( function(): void {
            if ( tabBar.selectedTab == oneDTab ) {
                oneDHolder.visible = true;
                twoDHolder.visible = false;

            }
            else { // advanced tab
                oneDHolder.visible = false;
                twoDHolder.visible = true;

            }
        } );

        this.addChild( new SpriteUIComponent( tabBar ));


    }
}
}
