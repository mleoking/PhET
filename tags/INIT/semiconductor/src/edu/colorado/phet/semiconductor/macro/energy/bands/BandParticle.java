/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.simpleobservable.SimpleObservable;
import edu.colorado.phet.semiconductor.macro.energy.states.Waiting;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 1:54:11 PM
 * Copyright (c) Jan 18, 2004 by Sam Reid
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
    private String force;

    public BandParticle( double x, double y, EnergyCell cell ) {

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

    public void translate( double dx ) {
        this.x += dx;
        updateObservers();
    }

    public boolean isExcited() {
        return excited;
    }

    public void setEnergyCell( EnergyCell newCell ) {
        if( this.cell != null ) {
            this.cell.ownerChanged();
        }
        if( newCell != null ) {
            newCell.ownerChanged();
        }
        this.cell = newCell;
    }

    public double getX() {
        return x;
    }

    public EnergyCell getEnergyCell() {
        return cell;
    }

    public EnergyLevel getEnergyLevel() {
        if( cell == null ) {
            return null;
        }
        return cell.getEnergyLevel();
    }

    public void stepInTime( double v ) {
        lastPosition = getPosition();
        boolean done = this.state.stepInTime( this, v );
        if( done ) {
            this.state = new Waiting();
        }
    }

    public void setPosition( PhetVector loc ) {
        this.x = loc.getX();
        this.y = loc.getY();
        updateObservers();
    }

    public double getDistanceFromOwnedSite() {
        if( cell == null ) {
            return Double.POSITIVE_INFINITY;
        }
        PhetVector site = cell.getPosition();
        return getPosition().getSubtractedInstance( site ).getMagnitude();
    }

    public void setX( double x ) {
        setPosition( new PhetVector( x, getPosition().getY() ) );
    }

    public double getY() {
        return y;
    }

    public void translate( PhetVector dx ) {
        setPosition( getPosition().getAddedInstance( dx ) );
    }

    public BandParticleState getState() {
        return state;
    }

    public void setState( BandParticleState state ) {
        this.state = state;
    }

    public void teleportToCell() {
        if( cell != null ) {
            setPosition( cell.getPosition() );
        }
    }

    public void detach() {
        setEnergyCell( null );
    }

    public boolean isLocatedAtCell() {
        if( cell == null ) {
            return false;
        }
        double dist = getDistanceFromOwnedSite();
        return dist <= .000001;
    }

    public void setExcited( boolean excited ) {
        this.excited = excited;
    }

    public BandSet getBandSet() {
        if( getEnergyCell() == null ) {
            return null;
        }
        return getEnergyLevel().getBand().getBandSet();
    }

    public int getAbsoluteEnergyLevel() {
        return getEnergyLevel().getBandSet().absoluteIndexOf( getEnergyLevel() );
    }

    public Band getBand() {
        if( getEnergyLevel() == null ) {
            return null;
        }
        return getEnergyLevel().getBand();
    }

    public PhetVector getDX() {
        return getPosition().getSubtractedInstance( lastPosition );
    }

    public boolean isMoving() {
        return getState().isMoving();
    }
    //temp for debug
    public void setMessage( String force ) {
        this.force=force;
    }

    public String getMessage() {
        return force;
    }

//    public void setVelocity(PhetVector velocity) {
//        this.velocity=velocity;
//    }

}
