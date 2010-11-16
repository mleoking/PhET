package edu.colorado.phet.insidemagnets;

import java.awt.*;
import java.util.ArrayList;
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
    Random random = new Random();
    private Property<Lattice<Cell>> latticeProperty;
    private IClock clock = new ConstantDtClock( 30 );
    private double time = 0;
    private ImmutableVector2D J = new ImmutableVector2D( -1, -1 );
    private Property<ImmutableVector2D> externalMagneticField = new Property<ImmutableVector2D>( new ImmutableVector2D( 2.1, 0 ) );//Externally applied magnetic field
    double Ka = 1.5;         /* anisotropy strength for aniso- boundaries.	 */
    int kdt = 10;             /* number of steps taken before re-drawing spins.   */
    private Property<Double> temperature = new Property<Double>( 0.01 );//Beta = 1/temperature
    private ImmutableVector2D m = new ImmutableVector2D( 0, 0 );//total magnetization

    public InsideMagnetsModel() {
        this.latticeProperty = new Property<Lattice<Cell>>( new Lattice<Cell>( 20, 10, new Function0<Cell>() {
            public Cell apply() {
                return new Cell();
            }
        } ) );
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
        //  int i,nbr1,nbr2,nbr3,nbr4;

        double Irot = 1;//rotational inertia
        double KK = 0.0;    /* boundary anisotropy term. */
        double sigomega;  /* variance of the random velocity changes.	   */
        double dtI;        /* time step divided by rotational inertia. */
        double dtg;        /* time step multiplied by damping.  */
        double domega;    /* change in rotational speed of a site. */

        double gam = 1.0;            /* damping of Langevin dynamics. passed to difeq.*/
        double sigtau = Math.sqrt( 2.0 * gam * Irot * temperature.getValue() );
        sigomega = sigtau * Math.sqrt( dt ) / Irot;
        dtI = dt / Irot;
        dtg = dt * gam;

//  if(bc==2 || bc==3)
        KK = 2.0 * Ka; /* for anisotropic edge forces. */

        for ( int x = 0; x < getLatticeWidth(); x++ ) {
            for ( int y = 0; y < getLatticeHeight(); y++ ) {
//                ArrayList<Cell> neighborCells = getLattice().getNeighborValues( new Point( x, y ) );
                ArrayList<Point> neighborCells = getLattice().getNeighborCells( new Point( x, y ) );
                ImmutableVector2D sum = new ImmutableVector2D( 0, 0 );
                for ( Point neighborCell : neighborCells ) {
                    sum = getLattice().getValue( neighborCell ).getSpinVector().getAddedInstance( sum );
                }

                double gx = sum.getX()*J.getX() - externalMagneticField.getValue().getX() - getLattice().getValue( x, y ).bx;
                double gy = sum.getY()*J.getY() - externalMagneticField.getValue().getY() - getLattice().getValue( x, y ).by;

                //TODO: add boundary conditions
                double sx = getLattice().getValue( x, y ).getSpinVector().getX();
                double sy = getLattice().getValue( x, y ).getSpinVector().getY();
                double omega = getLattice().getValue( x, y ).omega;
                domega = ( gx * sy - gy * sx ) * dtI;  /* is multiplied by dt, divided by Irot. */

                /* if(i==1 || i==2) printf("torque: domega[%d]= %17.10e\n",i,domega); */
                double rangauss = random.nextGaussian();
                if ( gam > 0.001 )  /* include Langevin terms in acceleration equation. */ {
                    domega += sigomega * rangauss - dtg * omega;
                }

                getLattice().getValue( x, y ).omega += domega; /* advance the velocity forward one time step. */
            }
        }
    }
///*

    private ImmutableVector2D randomVector() {
        return new ImmutableVector2D( random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1 );
    }

    public void update( double dt ) {
        latticeProperty.setValue( updateLattice( latticeProperty.getValue(), dt ) );
        latticeProperty.notifyObservers();
        this.time = time + dt;
    }

    private Lattice<Cell> updateLattice( Lattice<Cell> previousLattice, double dt ) {

//          extern double *oldx, *oldy; /* previous spin state. */
//  extern double *tmpx, *tmpy; /* intermediate state.  */
        double dt2;
        int i, idt;
        double r;

        dt2 = 0.5 * dt;

//  for(i=0; i<N; i++)  /* save old state, for erasing old state spins. */
//  {
//    oldx[i]=sx[i];
//    oldy[i]=sy[i];
//  }

        for ( idt = 0; idt < kdt; idt++ ) {

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

            /* Next is to find the new angular speeds on the sites, after
  a time interval of dt. The outputted omega is omega(t+dt). */

//  /* Let the positions propagate for another half time step. */
//     for(i=0; i<N; i++)
//     {
//       sx[i]=tmpx[i]-dt2*omega[i]*tmpy[i];
//       sy[i]=tmpy[i]+dt2*omega[i]*tmpx[i];
//     }
//

//  /* loop to rescale to unit length. */
//     for(i=0; i<N; i++)
//     {
//       r=1.0/sqrt(sx[i]*sx[i]+sy[i]*sy[i]);
//       sx[i] *= r;
//       sy[i] *= r;
//     }

            /* at this point, everything corresponds to the values at t+dt. */
        }
        return getLattice();
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

    private Cell getNewLatticeValue( Point cell, Lattice<Cell> previousLattice, double dt ) {
        return new Cell();
//        if ( cell.getX() == 0 && cell.getY() == 0 ) {
//            return new ImmutableVector2D( 0, Math.cos( time * 2 ) * 3 );
//        }
//        else if (cell.getY() == 0 && cell.getX() == previousLattice.getWidth()/2){
//            return new ImmutableVector2D( Math.sin( time * 3 ) * 3 ,0);
//        }
//        else {
//            ArrayList<ImmutableVector2D> neighbors = previousLattice.getNeighborValues( cell );
//            ImmutableVector2D sum = new ImmutableVector2D();
//            for ( ImmutableVector2D neighbor : neighbors ) {
//                sum = sum.getAddedInstance( neighbor );
//            }
//            final ImmutableVector2D scaledInstance = sum.getScaledInstance( 1.0 / neighbors.size() );
//            return scaledInstance.getAddedInstance( randomVector().getScaledInstance( 0.1 ));
//        }
    }

    public Property<Lattice<Cell>> getLatticeProperty() {
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

    public Lattice<Cell> getLattice() {
        return latticeProperty.getValue();
    }

    public Property<ImmutableVector2D> getExternalMagneticField() {
        return externalMagneticField;
    }

    public Property<Double> getTemperature() {
        return temperature;
    }
}
