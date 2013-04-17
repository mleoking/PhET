// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.model;

import edu.colorado.phet.buildanatom.modules.game.model.SimpleAtom;

/**
 * This class represents an atom whose configuration (i.e. the number of
 * protons, neutrons, and electrons that comprise it) is defined at
 * construction and cannot be changed.  The atom does not create instances of
 * the subatomic particles that comprise it - it only maintains numerical
 * values for the quantity of each type of constituent particle.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class ImmutableAtom implements IAtom {

    private final int protons;
    private final int neutrons;
    private final int electrons;

    public ImmutableAtom( int protons, int neutrons, int electrons ) {
        this.protons = protons;
        this.neutrons = neutrons;
        this.electrons = electrons;
    }

    public int getNumProtons() {
        return protons;
    }

    public int getNumNeutrons() {
        return neutrons;
    }

    public int getNumElectrons() {
        return electrons;
    }

    @Override
    public boolean equals( Object o ) {
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        ImmutableAtom atomValue = (ImmutableAtom) o;

        if ( electrons != atomValue.electrons ) {
            return false;
        }
        if ( neutrons != atomValue.neutrons ) {
            return false;
        }
        if ( protons != atomValue.protons ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = protons;
        hash = hash * 31 + neutrons;
        hash = hash * 31 + electrons;
        return hash;
    }

    public SimpleAtom toAtom( BuildAnAtomClock clock ) {
        assert protons >= 0 && neutrons >= 0 && electrons >= 0;
        return new SimpleAtom( protons, neutrons, electrons );
    }

    public int getMassNumber() {
        return protons + neutrons;
    }

    public int getCharge() {
        return protons - electrons;
    }

    public boolean isNeutral() {
        return getCharge() == 0;
    }

    @Override
    public String toString() {
        return new String( "protons: " + protons + ", neutrons: " + neutrons + ", electrons: " + electrons + ", charge: " + getCharge() );
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.buildanatom.model.IAtom#getFormattedCharge()
     */
    public String getFormattedCharge() {
        if ( getCharge() <= 0 ) {
            return "" + getCharge();
        }
        else {
            return "+" + getCharge();
        }
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.buildanatom.model.IAtom#getName()
     */
    public String getName() {
        return AtomIdentifier.getName( this );
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.buildanatom.model.IAtom#getSymbol()
     */
    public String getSymbol() {
        return AtomIdentifier.getSymbol( this );
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.buildanatom.model.IAtom#isStable()
     */
    public boolean isStable() {
        return AtomIdentifier.isStable( this );
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.buildanatom.model.IAtom#getAtomicMass()
     */
    public double getAtomicMass() {
        return AtomIdentifier.getAtomicMass( this );
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.buildanatom.model.IAtom#getNaturalAbundance()
     */
    public double getNaturalAbundance() {
        return AtomIdentifier.getNaturalAbundance( this );
    }
}
