package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Strings;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents;

/**
 * Module for tab 1 "Tug of War"
 *
 * @author Sam Reid
 */
public class TugOfWarModule extends SimSharingPiccoloModule implements Resettable {
    public TugOfWarModule() {
        super( UserComponents.tugOfWarTab, Strings.TUG_OF_WAR, new ConstantDtClock() );
        setSimulationPanel( new TugOfWarCanvas( this, getClock() ) );
        setClockControlPanel( null );
        setLogoPanel( null );
        setControlPanel( null );
    }

    @Override public void reset() {
        super.reset();
        setSimulationPanel( new TugOfWarCanvas( this, getClock() ) );
    }
}