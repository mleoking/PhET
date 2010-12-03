package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.flashcommon.StageHandler;
import edu.colorado.phet.flexcommon.PhetLogoButton;

import flash.display.Stage;

import mx.containers.Canvas;

public class BuoyancyApplication extends Canvas {
    private var buoyancyTabNavigator: BuoyancyTabNavigator;

    public function BuoyancyApplication() {
        super();
        buoyancyTabNavigator = new BuoyancyTabNavigator();
        addChild( buoyancyTabNavigator );
        percentWidth = 100;
        percentHeight = 100;

        StageHandler.addStageCreationListener( function( stage: Stage ): void {
            buoyancyTabNavigator.onApplicationComplete();

            const tabHeight: Number = buoyancyTabNavigator.getTabAt( 0 ).height;
            trace( "2nd time: buoyancyTabNavigator.getTabAt( 0 ).height = " + buoyancyTabNavigator.getTabAt( 0 ).height );
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