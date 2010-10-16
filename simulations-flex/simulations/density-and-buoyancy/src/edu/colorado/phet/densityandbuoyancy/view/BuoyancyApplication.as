package edu.colorado.phet.densityandbuoyancy.view {
import mx.containers.Canvas;

public class BuoyancyApplication extends Canvas {
    var buoyancyTabNavigator: BuoyancyTabNavigator;

    public function BuoyancyApplication() {
        super();
        buoyancyTabNavigator = new BuoyancyTabNavigator();
        addChild( buoyancyTabNavigator );
        percentWidth = 100;
        percentHeight = 100;
    }

    public function onApplicationComplete(): void {
        buoyancyTabNavigator.onApplicationComplete();
        var densityAndBuoyancyFlashCommon: BuoyancyFlashCommon = new BuoyancyFlashCommon();
        addChild( densityAndBuoyancyFlashCommon );
        densityAndBuoyancyFlashCommon.init();
    }
}
}