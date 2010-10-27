package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.flexcommon.PhetLogoButton;

import mx.containers.Canvas;

public class BuoyancyApplication extends Canvas {
    private var buoyancyTabNavigator: BuoyancyTabNavigator;

    public function BuoyancyApplication() {
        super();
        buoyancyTabNavigator = new BuoyancyTabNavigator();
        addChild( buoyancyTabNavigator );
        percentWidth = 100;
        percentHeight = 100;
    }

    public function onApplicationComplete(): void {
        buoyancyTabNavigator.onApplicationComplete();
        const tabHeight: Number = buoyancyTabNavigator.getTabAt( 0 ).height;
        var phetLogoButton: PhetLogoButton = new PhetLogoButton( tabHeight );
        var densityAndBuoyancyFlashCommon: BuoyancyFlashCommon = new BuoyancyFlashCommon( phetLogoButton.width, tabHeight );
        addChild( densityAndBuoyancyFlashCommon );
        densityAndBuoyancyFlashCommon.init();

        phetLogoButton.setStyle( "right", 0 );
        phetLogoButton.setStyle( "top", 0 );
        addChild( phetLogoButton );
    }
}
}