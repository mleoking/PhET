//CollisionLab M.Dubson Nov 5, 2009
//source code resides in /collision-lab
//main class instance

package edu.colorado.phet.collisionlab {

import edu.colorado.phet.flashcommon.controls.Tab;
import edu.colorado.phet.flashcommon.controls.TabBar;
import edu.colorado.phet.flashcommon.FlashCommonCS4;
import edu.colorado.phet.flashcommon.SimStrings;

import edu.colorado.phet.flashcommon.view.PhetIcon;

import flash.display.*;
import flash.events.Event;
import flash.geom.Rectangle;

public class CollisionLab extends Sprite {  //should the main class extend MovieClip or Sprite?

//    This will not compile, because it needs to be reverted to a non-study version before it can be deployed - JO

    public static var isStudyVersion: Boolean = true;

    private var stageW: Number;
    private var stageH: Number;
    private var introModule: IntroModule = new IntroModule();
    private var advancedModule: AdvancedModule;
    private var common: FlashCommonCS4;
    private var tabBar: TabBar;
    private var phetIcon: PhetIcon;

    public function CollisionLab() {
        SimStrings.init( loaderInfo );
        //stage width and height hard-coded for now
        this.stageW = 950;//this.stage.stageWidth;
        this.stageH = 700;//this.stage.stageHeight;
        tabBar = new TabBar();

        var introHolder: Sprite = new Sprite();
        addChild( introHolder );
        introModule.attach( introHolder );

        var introTab: Tab = new Tab( SimStrings.get( "introductionTab", "Introduction" ), tabBar );
        tabBar.addTab( introTab );
        var advancedTab: Tab;
        if ( !isStudyVersion ) {
            advancedModule = new AdvancedModule();

            var advancedHolder: Sprite = new Sprite();
            addChild( advancedHolder );
            advancedModule.attach( advancedHolder );
            advancedHolder.visible = false;

            advancedTab = new Tab( SimStrings.get( "advancedTab", "Advanced" ), tabBar )
            tabBar.addTab( advancedTab );
        }
        tabBar.selectedTab = introTab;
        tabBar.addListener( function(): void {
            if ( tabBar.selectedTab == introTab ) {
                introHolder.visible = true;
                advancedHolder.visible = false;
                introModule.onShow();
                advancedModule.onHide();
            }
            else { // advanced tab
                introHolder.visible = false;
                advancedHolder.visible = true;
                introModule.onHide();
                advancedModule.onShow();
            }
        } );
        addChild( tabBar );

        phetIcon = new PhetIcon();
        phetIcon.x = this.stageW - phetIcon.width - 10; // a bit of padding
        addChild( phetIcon );

        addFlashCommon();
    }

    private function addFlashCommon(): void {
        var ui: Sprite = new Sprite(); // used for FlashCommon UI
        addChild( ui );
        common = FlashCommonCS4.getInstance( ui.stage, this.stageW, this.stageH );//950,700 );
        common.initialize( ui );

        common.addLoadListener( positionButtons );
        stage.addEventListener( Event.RESIZE, function( evt: Event ): void {
            positionButtons();
        } );
        addEventListener( Event.ENTER_FRAME, function( evt: Event ): void {
            positionButtons();
        } );
        positionButtons();
    }

    protected function positionButtons(): void {
        var dimensions: Rectangle = stageDimensions( this );
        if ( common.commonButtons != null ) {
            var height: int = common.commonButtons.getPreferredHeight();
            const x: Number = phetIcon.x - common.commonButtons.width - 20;
            const y: Number = 1 + dimensions.top;
            common.commonButtons.setLocationXY( x, y );
        }
        phetIcon.y = dimensions.top;
        if ( tabBar != null && stage != null ) {
            tabBar.y = dimensions.top;
        }
    }

    public static function stageDimensions( sprite: Sprite ): Rectangle {
        var idealWidth: Number = 950;
        var idealHeight: Number = 700;
        var idealRatio: Number = idealWidth / idealHeight;
        var ratio: Number = sprite.stage.stageWidth / sprite.stage.stageHeight;
        var leftBound: Number;
        var rightBound: Number;
        var topBound: Number;
        var bottomBound: Number;
        if ( ratio < idealRatio ) {
            // width-constrained
            leftBound = 0;
            rightBound = idealWidth;
            topBound = (idealHeight / 2) * (1 - idealRatio / ratio);
            bottomBound = (idealHeight / 2) * (1 + idealRatio / ratio);
        }
        else {
            // height-constrained
            topBound = 0;
            bottomBound = idealHeight;
            leftBound = (idealWidth / 2) * (1 - ratio / idealRatio);
            rightBound = (idealWidth / 2) * (1 + ratio / idealRatio);
        }
        return new Rectangle( leftBound, topBound, rightBound - leftBound, bottomBound - topBound );
    }

}//end of class
}//end of package