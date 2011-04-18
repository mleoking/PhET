//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.flashcommon.ApplicationLifecycle;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.PhetLogoButton;
import edu.colorado.phet.flexcommon.PhetTabNavigator;

import mx.containers.Canvas;

//REVIEW top-level class type is inconsistent (based on whether sim has tabs?) DensityContainer vs BuoyancyApplication - let's standardize, also in XML
/**
 * Main application entry point for the Buoyancy sim.
 *
 * This is linked from Buoyancy.mxml, and sets up the modules with tabs. Since Density has no tabs, it does not have a
 * corresponding application class, but directly references its container.
 */
public class BuoyancyApplication extends Canvas {
    public function BuoyancyApplication() {
        super();

        var introModule: BuoyancyModule = new BuoyancyModule( FlexSimStrings.get( "buoyancy.tab.intro", "Intro" ), new BuoyancyIntroCanvas() );
        var playgroundModule: BuoyancyModule = new BuoyancyModule( FlexSimStrings.get( "buoyancy.tab.playground", "Buoyancy Playground" ), new BuoyancyPlaygroundCanvas() );

        var tabNavigator: PhetTabNavigator = new PhetTabNavigator( [ introModule, playgroundModule ] );

        addChild( tabNavigator );
        percentWidth = 100;
        percentHeight = 100;

        setStyle( "backgroundColor", 0xf0f0f0 ); // background of tab bar!

        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            tabNavigator.onApplicationComplete();

            const tabHeight: Number = tabNavigator.getTabAt( 0 ).height;
            trace( "2nd time: buoyancyTabNavigator.getTabAt( 0 ).height = " + tabNavigator.getTabAt( 0 ).height );
            var phetLogoButton: PhetLogoButton = new PhetLogoButton( tabHeight );
            var densityAndBuoyancyFlashCommon: BuoyancyFlashCommon = new BuoyancyFlashCommon( phetLogoButton.width, tabHeight );
            addChild( densityAndBuoyancyFlashCommon );
            densityAndBuoyancyFlashCommon.init();

            phetLogoButton.setStyle( "right", 0 );
            phetLogoButton.setStyle( "top", 0 );
            addChild( phetLogoButton );
        } );
    }

    protected override function createChildren(): void {
        super.createChildren();
        trace( "BACC: " + systemManager );
        trace( "BACC.stage: " + stage );
        trace( "BACC.systemmanager.stage: " + systemManager.stage );
        trace( "BACC.systemmanager.stageW: " + systemManager.stage.width );
        trace( "BACC.systemmanager.stageH: " + systemManager.stage.height );
    }

}
}

import edu.colorado.phet.densityandbuoyancy.view.BuoyancyCanvas;
import edu.colorado.phet.flexcommon.Module;

class BuoyancyModule extends Module {
    private var container: BuoyancyCanvas;

    function BuoyancyModule( title: String, canvas: BuoyancyCanvas ) {
        super( title, canvas );
        this.container = canvas;
    }

    override public function get running(): Boolean {
        return container.running;
    }

    override public function set running( r: Boolean ): void {
        container.running = r;
    }
}