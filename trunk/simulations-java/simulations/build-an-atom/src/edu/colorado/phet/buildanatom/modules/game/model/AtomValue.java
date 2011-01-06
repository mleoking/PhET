package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;

/**
 * Represents one row from the table defined in the design doc (see pools for level 1-3)
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class AtomValue {
    private final int protons;
    private final int neutrons;
    private final int electrons;

    public AtomValue( int protons, int neutrons, int electrons ) {
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

        AtomValue atomValue = (AtomValue) o;

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

    public SimpleAtom toAtom(BuildAnAtomClock clock) {
        assert protons >= 0 && neutrons >= 0 && electrons >= 0;
        return new SimpleAtom( protons, neutrons, electrons );
    }

    public int getMassNumber() {
        return protons+neutrons;
    }

    public int getCharge() {
        return protons-electrons;
    }

    public boolean isNeutral() {
        return getCharge()==0;
    }

    @Override
    public String toString (){
        return new String( "protons: " + protons + ", neutrons: " + neutrons + ", electrons: " + electrons + ", charge: " + getCharge() );
    }
}
