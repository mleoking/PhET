package edu.colorado.phet.workenergy.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class WorkEnergyModel {
    private final WorkEnergyObject workEnergyObject = new WorkEnergyObject();
    private final MutableList<Snapshot> snapshots = new MutableList<Snapshot>();

    public WorkEnergyModel() {
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
}
