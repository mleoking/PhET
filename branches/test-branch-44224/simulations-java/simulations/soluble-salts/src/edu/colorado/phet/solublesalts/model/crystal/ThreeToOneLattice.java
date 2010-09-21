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

import edu.colorado.phet.solublesalts.model.ion.Ion;

/**
 * TwoToOneLattice
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ThreeToOneLattice extends Lattice {

    private Class oneIonClass;
    private Class threeIonClass;

    public ThreeToOneLattice( Class oneIonClass, Class threeIonClass, double spacing ) {
        super( spacing );
        this.oneIonClass = oneIonClass;
        this.threeIonClass = threeIonClass;
        this.spacing = spacing;
    }

    public Object clone() {
        return new ThreeToOneLattice( oneIonClass, threeIonClass, spacing );
    }

    protected int getNumNeighboringSites( Ion ion ) {
        return oneIonClass.isInstance( ion ) ? 6 : 2;
    }

    public static void main( String[] args ) {
//        Ion s1 = new Sodium();
//        s1.setPosition( 0, 0 );
//        ThreeToOneLattice l = new ThreeToOneLattice( Chlorine.class, Sodium.class, Sodium.RADIUS + Chlorine.RADIUS );
//        Rectangle2D r = new Rectangle2D.Double( -1000, -1000, 2000, 2000 );
//        Crystal c = new Crystal( new SolubleSaltsModel(new SwingClock( 1000 / SolubleSaltsConfig.FPS, SolubleSaltsConfig.DT )), l );
////        Crystal c = new Crystal( r, l );
//        c.addIon( s1 );
//        {
//            Chlorine ion = new Chlorine();
//            ion.setPosition( 1, 0 );
//            c.addIon( ion );
//        }
//        {
//            Sodium ion = new Sodium();
//            ion.setPosition( 15, 0 );
//            c.addIon( ion );
//        }
//        {
//            Sodium ion = new Sodium();
//            ion.setPosition( 15, 0 );
//            c.addIon( ion );
//        }
//        {
//            Chlorine ion = new Chlorine();
//            ion.setPosition( 15, 0 );
//            c.addIon( ion );
//        }
//        {
//            Chlorine ion = new Chlorine();
//            ion.setPosition( 15, 0 );
//            c.addIon( ion );
//        }
//        {
//            Chlorine ion = new Chlorine();
//            ion.setPosition( 15, 0 );
//            c.addIon( ion );
//        }
//        {
//            Chlorine ion = new Chlorine();
//            ion.setPosition( 15, 0 );
//            c.addIon( ion );
//        }
//        {
//            Sodium ion = new Sodium();
//            ion.setPosition( 15, 0 );
//            c.addIon( ion );
//        }
//
//        System.out.println( c );
    }
}
