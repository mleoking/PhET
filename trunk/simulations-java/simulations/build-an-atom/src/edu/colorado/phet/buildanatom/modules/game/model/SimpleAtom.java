// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.IAtom;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * An atom that simply tracks the quantities of the various subatomic
 * particles, but doesn't actually create modeled instances of them.  This can
 * be used in many cases where no subatomic particles are needed.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class SimpleAtom extends SimpleObservable implements IDynamicAtom {

    private int numProtons = 0;
    private int numNeutrons = 0;
    private int numElectrons = 0;

    /**
     * Default constructor.
     */
    public SimpleAtom(){
        // Use the default values.
    }

    /**
     * Constructor.
     */
    public SimpleAtom( int numProtons, int numNeutrons, int numElectrons ) {
        this.numProtons = numProtons;
        this.numNeutrons = numNeutrons;
        this.numElectrons = numElectrons;
    }

    public SimpleAtom( ImmutableAtom atomConfig ){
        this( atomConfig.getNumProtons(), atomConfig.getNumNeutrons(), atomConfig.getNumElectrons() );
    }

    public static String getSymbol( int protonCount ) {
        return AtomIdentifier.getSymbol( protonCount );
    }

    public int getNumNeutrons() {
        return numNeutrons;
    }

    public void setNumNeutrons( int numNeutrons ) {
        if ( this.numNeutrons != numNeutrons ){
            this.numNeutrons = numNeutrons;
            notifyObservers();
        }
    }

    public int getNumElectrons() {
        return numElectrons;
    }

    public void setNumElectrons( int numElectrons ) {
        if ( this.numElectrons != numElectrons ){
            this.numElectrons = numElectrons;
            notifyObservers();
        }
    }

    public int getNumProtons() {
        return numProtons;
    }

    public void setNumProtons( int numProtons ) {
        if ( this.numProtons != numProtons ) {
            this.numProtons = numProtons;
            notifyObservers();
        }
    }

    public void setConfiguration( IAtom atom ) {
        if ( this.numProtons != atom.getNumProtons() || this.numNeutrons != atom.getNumNeutrons() || this.numElectrons != atom.getNumElectrons() ) {
            this.numProtons = atom.getNumProtons();
            this.numNeutrons = atom.getNumNeutrons();
            this.numElectrons = atom.getNumElectrons();
            notifyObservers();
        }
    }

    public int getCharge() {
        return getNumProtons() - getNumElectrons();
    }

    public String getFormattedCharge() {
        if (getCharge() <= 0){
            return "" + getCharge();
        }
        else{
            return "+" + getCharge();
        }
    }

    public String getSymbol(){
        return getSymbol( getNumProtons() );
    }

    public String getName() {
        return AtomIdentifier.getName( this );
    }

    public boolean isStable() {
        return AtomIdentifier.isStable( this );
    }

    public int getNumParticles() {
        return getMassNumber()+getNumElectrons();
    }

    public void reset(){
        setNumProtons( 0 );
        setNumNeutrons( 0 );
        setNumElectrons( 0 );
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.buildanatom.model.IAtom#getAtomicNumber()
     */
    public int getMassNumber() {
        return getNumProtons() + getNumNeutrons();
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.buildanatom.model.IDynamicAtom#toImmutableAtom()
     */
    public ImmutableAtom toImmutableAtom() {
        return new ImmutableAtom( getNumProtons(), getNumNeutrons(), getNumElectrons());
    }

    public double getAtomicMass(){
        return AtomIdentifier.getAtomicMass( this );
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.buildanatom.model.IAtom#getNaturalAbundance()
     */
    public double getNaturalAbundance() {
        return AtomIdentifier.getNaturalAbundance( this );
    }
}