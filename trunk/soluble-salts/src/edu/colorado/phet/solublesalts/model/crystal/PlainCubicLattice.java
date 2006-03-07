/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.crystal;

import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.ion.Chlorine;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.Sodium;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

/**
 * PlainCubicLattice
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PlainCubicLattice extends Lattice {
//public class PlainCubicLattice extends Lattice {
    private static final Random random = new Random(System.currentTimeMillis());

    /**
     * @param spacing
     */
    public PlainCubicLattice(double spacing) {
        super(spacing);
    }

    public Object clone() {
        return new PlainCubicLattice(this.spacing);
    }

    protected int getNumNeighboringSites(Ion ion) {
        return 4;
    }

    public static void main(String[] args) {
        Ion a = new Sodium();
        a.setPosition(300, 500);
        Crystal l = new Crystal(new SolubleSaltsModel(), new PlainCubicLattice(10));
//        Crystal l = new Crystal( new SolubleSaltsModel(), new PlainCubicLattice( Sodium.RADIUS + Chlorine.RADIUS ) );
        l.addIon(a);

        Ion b = new Chlorine();
        b.setPosition(new Point2D.Double(307, 498));
        l.addIonNextToIon(b, a);

        Ion b2 = new Chlorine();
        b2.setPosition(new Point2D.Double(297, 495));
        l.addIonNextToIon(b2, a);

        Ion a2 = new Sodium();
        a2.setPosition(302, 490);
        l.addIonNextToIon(a2, b);

        Ion b3 = new Chlorine();
        b3.setPosition(new Point2D.Double(300, 490));
        l.addIonNextToIon(b3, a2);

        Ion a3 = new Sodium();
        a3.setPosition(290, 410);
        l.addIonNextToIon(a3, b3);

        printLattice(l);

//        l.releaseIon( 10 );

//        printLattice( l );

//        PlainCubicForm pcf = (PlainCubicForm)l.getForm();
//        List ap = pcf.getNeighboringSites( a.getPosition(), Math.PI / 4 );
//        for( int i = 0; i < ap.size(); i++ ) {
//            Point2D point2D = (Point2D)ap.get(i);
//            System.out.println( "point2D = " + point2D );
//        }
    }

    private static void printLattice(Crystal l) {
        List ions = l.getIons();
        for (int i = 0; i < ions.size(); i++) {
            Ion ion = (Ion) ions.get(i);
            System.out.println("ion.getPosition() = " + ion.getPosition());
        }
    }
}
