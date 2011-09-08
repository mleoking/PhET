// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharingtestsim;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.simsharing.ImageFactory;
import edu.colorado.phet.common.phetcommon.simsharing.SerializableBufferedImage;
import edu.colorado.phet.common.phetcommon.simsharing.SimsharingApplication;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * @author Sam Reid
 */
public class SimSharingTestSim extends PiccoloPhetApplication implements SimsharingApplication<SimSharingTestSimState> {
    public static final String PROJECT_NAME = "simsharing-test-sim";
    public SimSharingTestModule module;
    public ImageFactory imageFactory = new ImageFactory();

    public SimSharingTestSim( PhetApplicationConfig config ) {
        super( config );
        module = new SimSharingTestModule();
        addModule( module );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, PROJECT_NAME, SimSharingTestSim.class );
    }

    public void setTeacherMode( boolean b ) {
    }

    public SimSharingTestSimState getState() {
        return new SimSharingTestSimState( System.currentTimeMillis(),
                                           new SerializableBufferedImage( imageFactory.getThumbnail( getPhetFrame(), 200 ) ),
                                           module.position.get() );
    }

    public void setState( SimSharingTestSimState state ) {
        module.position.set( state.position );
    }

    public void addModelSteppedListener( final VoidFunction0 updateSharing ) {
        module.getClock().addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateSharing.apply();
            }
        } );
    }

    public boolean isPaused() {
        return module.getClock().isPaused();
    }

    public void setPlayButtonPressed( boolean b ) {
    }
}