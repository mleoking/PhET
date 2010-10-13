package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.view.modes.IntroObjectMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;

public class BuoyancyIntroContainer extends BuoyancyContainer {
    public function BuoyancyIntroContainer() {
    }

    override public function createCustomObjectMode( canvas: AbstractDBCanvas ): Mode {
        return new IntroObjectMode( canvas );
    }
}
}