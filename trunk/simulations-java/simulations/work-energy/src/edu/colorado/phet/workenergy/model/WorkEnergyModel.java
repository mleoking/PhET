package edu.colorado.phet.workenergy.model;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.workenergy.WorkEnergyResources;

/**
 * @author Sam Reid
 */
public class WorkEnergyModel {
    private final WorkEnergyObject workEnergyObject = new WorkEnergyObject( WorkEnergyResources.getImage( "crate.gif" ) );
    private final MutableList<Snapshot> snapshots = new MutableList<Snapshot>();
    private IClock clock;

    public WorkEnergyModel( IClock clock ) {
        this.clock = clock;
        workEnergyObject.getPositionProperty().addObserver( new SimpleObserver() {
            public void update() {
                if ( workEnergyObject.getPositionProperty().getValue().getX() > 200 ) {
                    addSnapshot( new Snapshot( workEnergyObject.copy() ) );
                }
            }
        } );
    }

    private void addSnapshot( Snapshot snapshot ) {
        snapshots.add( snapshot );
    }

    public WorkEnergyObject getObject() {
        return workEnergyObject;
    }

    public void stepInTime( double simulationTimeChange ) {
        workEnergyObject.stepInTime( simulationTimeChange );
    }

    public IClock getClock() {
        return clock;
    }
}
