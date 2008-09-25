/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import edu.colorado.phet.semiconductor.macro.energy.states.Waiting;
import edu.colorado.phet.semiconductor.util.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.model.ModelElement;
import edu.colorado.phet.semiconductor.phetcommon.model.simpleobservable.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 1:54:11 PM
 */
public class BandParticle extends SimpleObservable implements ModelElement {
    double x;
    double y;
    private EnergyCell cell;
    private BandParticleState state;
    static int static_index = 0;
    private int index;
    private boolean excited;
    private PhetVector lastPosition = new PhetVector();

    public BandParticle( double x, double y, EnergyCell cell ) {
//        if (cell==null){
//            throw new RuntimeException("Null cell");
//        }
        this.x = x;
        this.y = y;
        setEnergyCell( cell );
        this.state = new Waiting();
        this.index = static_index;
        lastPosition = getPosition();
        static_index++;
    }

    public BandParticle( double x, double y ) {
        this( x, y, null );
    }

    public BandParticle( EnergyCell energyCell ) {
        this( energyCell.getX(), energyCell.getEnergy(), energyCell );
    }

    public BandParticle( PhetVector pos ) {
        this( pos.getX(), pos.getY() );
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return getIndex() + "";
    }

    public PhetVector getPosition() {
        return new PhetVector( x, y );
    }

    public boolean isExcited() {
        return excited;
    }

    public void setEnergyCell( EnergyCell newCell ) {
//        if( this.cell != null ) {
//            this.cell.ownerChanged();
//        }
//        if( newCell != null ) {
//            newCell.ownerChanged();
//        }
        this.cell = newCell;
    }

    public EnergyCell getEnergyCell() {
        return cell;
    }

    public EnergyLevel getEnergyLevel() {
        if ( cell == null ) {
            return null;
        }
        return cell.getEnergyLevel();
    }

    public void stepInTime( double v ) {
        lastPosition = getPosition();
        boolean done = this.state.stepInTime( this, v );
        if ( done ) {
            this.state = new Waiting();
        }
    }

    public void setPosition( PhetVector loc ) {
        this.x = loc.getX();
        this.y = loc.getY();
        updateObservers();
    }

    public double getDistanceFromOwnedSite() {
        if ( cell == null ) {
            return Double.POSITIVE_INFINITY;
        }
        PhetVector site = cell.getPosition();
        return getPosition().getSubtractedInstance( site ).getMagnitude();
    }

    public double getY() {
        return y;
    }

    public BandParticleState getState() {
        return state;
    }

    public void setState( BandParticleState state ) {
        this.state = state;
    }

    public boolean isLocatedAtCell() {
        if ( cell == null ) {
            return false;
        }
        double dist = getDistanceFromOwnedSite();
//        System.out.println( "dist = " + dist );
        return dist <= .000001;
    }

    public void setExcited( boolean excited ) {
        this.excited = excited;
    }

    public SemiconductorBandSet getBandSet() {
        if ( getEnergyCell() == null ) {
            return null;
        }
        return getEnergyLevel().getBand().getBandSet();
    }

    public Band getBand() {
        if ( getEnergyLevel() == null ) {
            return null;
        }
        return getEnergyLevel().getBand();
    }

    public PhetVector getDX() {
        return getPosition().getSubtractedInstance( lastPosition );
    }

//    public String getMessage() {
//        return force;
//    }

//    public void setVelocity(PhetVector velocity) {
//        this.velocity=velocity;
//    }

}
