// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.workenergy.WorkEnergyResources;

/**
 * @author Sam Reid
 */
public class WorkEnergyModel {
    private final WorkEnergyObject workEnergyObject = new WorkEnergyObject( WorkEnergyResources.getImage( "crate.gif" ), 1 );
    private final MutableList<Snapshot> snapshots = new MutableList<Snapshot>();
    private ConstantDtClock clock;
    public static final double DEFAULT_DT = 30 / 1000.0;

    public WorkEnergyModel() {
        this.clock = new ConstantDtClock( 30, DEFAULT_DT );
        workEnergyObject.getPositionProperty().addObserver( new SimpleObserver() {
            public void update() {
                if ( workEnergyObject.getPositionProperty().get().getX() > 200 ) {
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
//        System.out.println( "simulationTimeChange = " + simulationTimeChange );
        workEnergyObject.stepInTime( simulationTimeChange );
    }

    public ConstantDtClock getClock() {
        return clock;
    }

    public void resetAll() {
        clock.resetSimulationTime();
        clock.setDt( DEFAULT_DT );
        workEnergyObject.reset();
//        snapshots.clear();
    }
}
