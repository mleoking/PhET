package edu.colorado.phet.insidemagnets;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.Function0;

/**
 * @author Sam Reid
 */
public class InsideMagnetsModel {
    private Property<Lattice<ImmutableVector2D>> latticeProperty;
    private IClock clock = new ConstantDtClock( 30 );
    private double time = 0;

    public InsideMagnetsModel() {
        this.latticeProperty = new Property<Lattice<ImmutableVector2D>>( new Lattice<ImmutableVector2D>( 20, 10, new Function0<ImmutableVector2D>() {
            public ImmutableVector2D apply() {
                return randomVector();
            }
        } ) );
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                update( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    private ImmutableVector2D randomVector() {
        return new ImmutableVector2D( random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1 );
    }

    public void update( double dt ) {
        latticeProperty.setValue( updateLattice( latticeProperty.getValue(), dt ) );
        this.time = time + dt;
    }

    Random random = new Random();

    private Lattice<ImmutableVector2D> updateLatticeRandom( Lattice<ImmutableVector2D> value, double dt ) {
        Lattice<ImmutableVector2D> newLattice = new Lattice<ImmutableVector2D>( value.getWidth(), value.getHeight(), new Function0<ImmutableVector2D>() {
            public ImmutableVector2D apply() {
                return randomVector();
            }
        } );
        return newLattice;
    }

    private Lattice<ImmutableVector2D> updateLattice( Lattice<ImmutableVector2D> previousLattice, double dt ) {
        HashMap<Point, ImmutableVector2D> map = new HashMap<Point, ImmutableVector2D>();
        for ( Point point : previousLattice.getLocations() ) {
            map.put( point, getNewLatticeValue( point, previousLattice, dt ) );
        }
        Lattice<ImmutableVector2D> newLattice = new Lattice<ImmutableVector2D>( previousLattice.getWidth(), previousLattice.getHeight(), map );
        return newLattice;
    }

    private ImmutableVector2D getNewLatticeValue( Point cell, Lattice<ImmutableVector2D> previousLattice, double dt ) {
        if ( cell.getX() == 0 && cell.getY() == 0 ) {
            return new ImmutableVector2D( 0, Math.cos( time * 2 ) * 3 );
        }
        else if (cell.getY() == 0 && cell.getX() == previousLattice.getWidth()/2){
            return new ImmutableVector2D( Math.sin( time * 3 ) * 3 ,0);
        }
        else {
            ArrayList<ImmutableVector2D> neighbors = previousLattice.getNeighborValues( cell );
            ImmutableVector2D sum = new ImmutableVector2D();
            for ( ImmutableVector2D neighbor : neighbors ) {
                sum = sum.getAddedInstance( neighbor );
            }
            final ImmutableVector2D scaledInstance = sum.getScaledInstance( 1.0 / neighbors.size() );
            return scaledInstance.getAddedInstance( randomVector().getScaledInstance( 0.1 ));
        }
    }

    public Property<Lattice<ImmutableVector2D>> getLatticeProperty() {
        return latticeProperty;
    }

    public IClock getClock() {
        return clock;
    }

    public int getLatticeWidth() {
        return latticeProperty.getValue().getWidth();
    }

    public int getLatticeHeight() {
        return latticeProperty.getValue().getHeight();
    }
}
