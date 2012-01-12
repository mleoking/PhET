// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.insidemagnets;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 * @author Gary Wysin
 */
public class InsideMagnetsModel {
    Random random = new Random();
    private Lattice<Cell> lattice;
    private IClock clock = new ConstantDtClock( 30 );
    private double time = 0;
    private ImmutableVector2D J = new ImmutableVector2D( -1, -1 );
    private Property<ImmutableVector2D> externalMagneticField = new Property<ImmutableVector2D>( new ImmutableVector2D( 2.1, 0 ) );//Externally applied magnetic field
    private Property<Double> temperature = new Property<Double>( 0.01 );//Beta = 1/temperature
    private Property<ImmutableVector2D> netMagnetizationProperty = new Property<ImmutableVector2D>( new ImmutableVector2D() );
    private SimpleObservable stepListeners = new SimpleObservable();

    public InsideMagnetsModel() {
        lattice = new Lattice<Cell>( 20, 10, new Function0<Cell>() {
            public Cell apply() {
                return new Cell();
            }
        } );
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                update( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    //   Applies the torque equation of motion for XY rotor model spins.
//   Each site has in-plane coordinates (sx,sy), and rotational speed omega.
//   The outputted omega array is omega advanced one time step dt, for
//   Langevin dynamics.  The sx and sy arrays are not modified.
    private void torque( double dt ) {
        double Irot = 1;//rotational inertia
        double gam = 1.0;            /* damping of Langevin dynamics. passed to difeq.*/
        double sigtau = Math.sqrt( 2.0 * gam * Irot * temperature.get() );
        double sigomega = sigtau * Math.sqrt( dt ) / Irot;/* variance of the random velocity changes.	   */
        double dtI = dt / Irot;/* time step divided by rotational inertia. */
        double dtg = dt * gam;/* time step multiplied by damping.  */

        for ( int x = 0; x < getLatticeWidth(); x++ ) {
            for ( int y = 0; y < getLatticeHeight(); y++ ) {
                ArrayList<Point> neighborCells = getLattice().getNeighborCells( new Point( x, y ) );
                ImmutableVector2D sum = new ImmutableVector2D( 0, 0 );
                for ( Point neighborCell : neighborCells ) {
                    sum = getLattice().getValue( neighborCell ).getSpinVector().getAddedInstance( sum );
                }

                double gx = sum.getX() * J.getX() - externalMagneticField.get().getX() - getLattice().getValue( x, y ).bx;
                double gy = sum.getY() * J.getY() - externalMagneticField.get().getY() - getLattice().getValue( x, y ).by;

                //TODO: add boundary conditions
                double sx = getLattice().getValue( x, y ).getSpinVector().getX();
                double sy = getLattice().getValue( x, y ).getSpinVector().getY();
                double omega = getLattice().getValue( x, y ).omega;
                double domega = ( gx * sy - gy * sx ) * dtI;  /* is multiplied by dt, divided by Irot. *//* change in rotational speed of a site. */

                double rangauss = random.nextGaussian();
                if ( gam > 0.001 )  /* include Langevin terms in acceleration equation. */ {
                    domega += sigomega * rangauss - dtg * omega;
                }

                getLattice().getValue( x, y ).omega += domega; /* advance the velocity forward one time step. */
            }
        }
    }

    public void update( double dt ) {
        updateLattice( dt );
        this.time = time + dt;
        stepListeners.notifyObservers();
    }

    private void updateLattice( double dt ) {
        //Update the dipole field everywhere in the lattice
        for ( int x = 0; x < getLatticeWidth(); x++ ) {
            for ( int y = 0; y < getLatticeHeight(); y++ ) {
                dipole_field( x, y );
            }
        }

        double dt2 = 0.5 * dt;
        int kdt = 10;//number of steps before redrawing spins
        for ( int idt = 0; idt < kdt; idt++ ) {
            /* First do a position update over half a time step.
The tmp arrays hold positions at t+0.5*dt.          */
            for ( int x = 0; x < getLatticeWidth(); x++ ) {
                for ( int y = 0; y < getLatticeHeight(); y++ ) {
                    final double tmpSx = getSpin( x, y ).getX() - dt2 * getOmega( x, y ) * getSpin( x, y ).getY();
                    final double tmpSy = getSpin( x, y ).getY() + dt2 * getOmega( x, y ) * getSpin( x, y ).getX();
                    getLattice().getValue( x, y ).sx = tmpSx;
                    getLattice().getValue( x, y ).sy = tmpSy;
                }
            }

            torque( dt );

            /* Next is to find the new angular speeds on the sites, after
  a time interval of dt. The outputted omega is omega(t+dt). */

//  /* Let the positions propagate for another half time step. */
            for ( int x = 0; x < getLatticeWidth(); x++ ) {
                for ( int y = 0; y < getLatticeHeight(); y++ ) {
                    final double tmpSx = getSpin( x, y ).getX() - dt2 * getOmega( x, y ) * getSpin( x, y ).getY();
                    final double tmpSy = getSpin( x, y ).getY() + dt2 * getOmega( x, y ) * getSpin( x, y ).getX();
                    getLattice().getValue( x, y ).sx = tmpSx;
                    getLattice().getValue( x, y ).sy = tmpSy;
                }
            }

            //Normalize the spin vectors
            for ( int x = 0; x < getLatticeWidth(); x++ ) {
                for ( int y = 0; y < getLatticeHeight(); y++ ) {
                    ImmutableVector2D spinVector = getLattice().getValue( x, y ).getSpinVector().getNormalizedInstance();
                    getLattice().getValue( x, y ).sx = spinVector.getX();
                    getLattice().getValue( x, y ).sy = spinVector.getY();
                }
            }
            /* at this point, everything corresponds to the values at t+dt. */
        }

        netMagnetizationProperty.set( getNetMagnetization() );
    }

    private double getOmega( int x, int y ) {
        return getLattice().getValue( new Point( x, y ) ).omega;
    }

    private ImmutableVector2D getSpin( int x, int y ) {
        if ( !getLattice().containsPoint( new Point( x, y ) ) ) {
            System.out.println( "off the lattice: x = " + x + ", y = " + y );
            return new ImmutableVector2D();
        }
        else {
            return getLattice().getValue( new Point( x, y ) ).getSpinVector();
        }
    }

    public IClock getClock() {
        return clock;
    }

    public int getLatticeWidth() {
        return lattice.getWidth();
    }

    public int getLatticeHeight() {
        return lattice.getHeight();
    }

    public Lattice<Cell> getLattice() {
        return lattice;
    }

    public Property<ImmutableVector2D> getExternalMagneticField() {
        return externalMagneticField;
    }

    public Property<Double> getTemperature() {
        return temperature;
    }

    /*
   Calculates the total magnetic field (bx,by) due to all dipoles,
   as would be measured at an observer's position (xobs,yobs).
   D is a parameter related to the magnitudes of the dipoles,
   that determines the relative size of magnetic field they generate.
*/
    void dipole_field( int xobs, int yobs ) {

        double rx, ry, r, r2, r5;
        double dot, tx, ty, sumx, sumy;
        sumx = sumy = 0.0;
        double D = 0.5;

        for ( int x = 0; x < getLatticeWidth(); x++ ) {
            for ( int y = 0; y < getLatticeHeight(); y++ ) {
                Cell cell = getLattice().getValue( x, y );

                /* all distances in units of a lattice constant. */
                rx = (double) ( xobs - x );
                ry = (double) ( yobs - y );
                r2 = rx * rx + ry * ry;
                if ( r2 < 0.1 ) { continue;  /*  avoids the i=j term.   */ }
                r = Math.sqrt( r2 );
                r5 = 1.0 / ( r2 * r2 * r );
                dot = rx * cell.getSpinVector().getX() + ry * cell.getSpinVector().getY();
                tx = r5 * ( 3.0 * dot * rx - r2 * cell.getSpinVector().getX() ); /* term of one dipole. */
                ty = r5 * ( 3.0 * dot * ry - r2 * cell.getSpinVector().getY() );
                sumx += tx;
                sumy += ty;
            }
        }
        getLattice().getValue( xobs, yobs ).bx = D * sumx;
        getLattice().getValue( xobs, yobs ).by = D * sumy;
        return;
    }

    private ImmutableVector2D getNetMagnetization() {
        ImmutableVector2D sum = new ImmutableVector2D( 0, 0 );
        for ( int x = 0; x < getLatticeWidth(); x++ ) {
            for ( int y = 0; y < getLatticeHeight(); y++ ) {
                sum = sum.getAddedInstance( getLattice().getValue( x, y ).getSpinVector() );
            }
        }
        sum = sum.getScaledInstance( 1.0 / ( getLatticeWidth() * getLatticeHeight() ) ).getScaledInstance( getLatticeWidth() );
        return sum;
    }

    public Property<ImmutableVector2D> getNetMagnetizationProperty() {
        return netMagnetizationProperty;
    }

    public void addStepListener( SimpleObserver simpleObserver ) {
        stepListeners.addObserver( simpleObserver );
    }
}
