package edu.colorado.phet.buildanatom.modules.game.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

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

    public int getProtons() {
        return protons;
    }

    public int getNeutrons() {
        return neutrons;
    }

    public int getElectrons() {
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


    public Atom toAtom() {
        BuildAnAtomClock clock=new BuildAnAtomClock();
        if ( protons < 0 ) {
            System.out.println( "protons = "+protons );
        }
        if ( neutrons < 0 ) {
            System.out.println( "neutrons = "+neutrons );
        }
        if ( electrons < 0 ) {
            System.out.println( "electrons = "+electrons );
        }
        Atom atom = new Atom( new Point2D.Double( 0, 0 ));
        for ( int i = 0; i < protons; i++ ) {
            atom.addProton( new Proton( clock ) );
        }
        for ( int i = 0; i < neutrons; i++ ) {
            atom.addNeutron( new Neutron( clock ) );
        }
        for ( int i = 0; i < electrons; i++ ) {
            atom.addElectron( new Electron( clock ) );
        }
        clock.start();//TODO: memory leak and makes particles animate in an unphysical way
        return atom;
    }

    public int getMassNumber() {
        return protons+neutrons;
    }

    public int getCharge() {
        return protons-electrons;
    }
}
