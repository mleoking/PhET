// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.conductivity.macro.bands;


import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.conductivity.macro.bands.states.MoveTo;
import edu.colorado.phet.conductivity.macro.bands.states.Speed;
import edu.colorado.phet.conductivity.macro.bands.states.Waiting;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            EnergyCell, BandParticleState, EnergyLevel

public class BandParticle extends SimpleObservable
        implements ModelElement {

    public BandParticle( double d, double d1, EnergyCell energycell ) {
        x = d;
        y = d1;
        cell = energycell;
        if ( energycell.hasOwner() && energycell.getOwner() != this ) {
            throw new RuntimeException( "Wrong owner." );
        }
        else {
            energycell.setOwner( this );
            state = new Waiting();
            return;
        }
    }

    public MutableVector2D getPosition() {
        return new MutableVector2D( x, y );
    }

    public double getX() {
        return x;
    }

    public EnergyCell getEnergyCell() {
        return cell;
    }

    public EnergyLevel getEnergyLevel() {
        return cell.getEnergyLevel();
    }

    public MoveTo moveTo( EnergyCell energycell, final double speed ) {
        return moveTo( energycell, new Speed() {

            public double getSpeed() {
                return speed;
            }

        } );
    }

    public MoveTo moveTo( EnergyCell energycell, Speed speed ) {
        cell.detach( this );
        energycell.setOwner( this );
        state = new MoveTo( energycell, speed );
        cell = energycell;
        return (MoveTo) state;
    }

    public void stepInTime( double d ) {
        state = state.stepInTime( this, d );
    }

    public void setPosition( MutableVector2D phetvector ) {
        x = phetvector.getX();
        y = phetvector.getY();
        notifyObservers();
    }

    public double getDistanceFromOwnedSite() {
        MutableVector2D phetvector = cell.getPosition();
        return getPosition().minus( phetvector ).magnitude();
    }

    public void detach() {
        if ( cell != null ) {
            cell.detach( this );
        }
        cell = null;
    }

    public void setX( double d ) {
        setPosition( new MutableVector2D( d, getPosition().getY() ) );
    }

    public void pairPropagate( BandParticle bandparticle, Speed speed ) {
        if ( getDistanceFromOwnedSite() < 0.0001D && bandparticle.getDistanceFromOwnedSite() < 0.0001D ) {
            EnergyCell energycell = bandparticle.getEnergyCell();
            EnergyCell energycell1 = getEnergyCell();
            cell.detach( this );
            bandparticle.cell.detach( bandparticle );
            energycell.setOwner( this );
            energycell1.setOwner( bandparticle );
            state = new MoveTo( energycell, speed );
            bandparticle.state = new MoveTo( energycell1, speed );
            cell = energycell;
            bandparticle.cell = energycell1;
        }
    }

    double x;
    double y;
    private EnergyCell cell;
    private BandParticleState state;
}
