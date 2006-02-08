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

import java.awt.geom.Rectangle2D;

/**
 * TwoToOneLattice
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TwoToOneLattice extends Lattice_new_new {
//public class TwoToOneLattice extends Lattice {

    private Class oneIonClass;
    private Class twoIonClass;

    public TwoToOneLattice( Class oneIonClass, Class twoIonClass, double spacing ) {
        super( spacing );
        this.oneIonClass = oneIonClass;
        this.twoIonClass = twoIonClass;
    }

    public Object clone() {
        return new TwoToOneLattice( oneIonClass, twoIonClass, spacing );
    }

    protected int getNumNeighboringSites( Ion ion ) {
//        int cnt = oneIonClass.isInstance( ion ) ? 4 : 2;
//        if( cnt == 2 ) {
//            System.out.println( "TwoToOneLattice.getNumNeighboringSites" );
//        }
//        return cnt;
        return oneIonClass.isInstance( ion ) ? 4 : 2;
    }

    /**
     * Overides the parent class behavior by filtering out sites that could not be occupied around
     * ions of the twoIonClass. This is the most straightforward way to handle keeping the ration
     * right, I think: return all four surrounding sites in getNeighboringSite(), then filter out
     * the appropriate ones here.
     *
     * @param ion
     * @param ionsInLattice
     * @param orientation
     * @return
     */
//    public List getOpenNeighboringSites( Ion ion, List ionsInLattice, double orientation ) {
//        List neighboringSites = getNeighboringSites( ion, orientation );
//        List openSites = super.getOpenNeighboringSites( neighboringSites, ionsInLattice );
//
//        // If the parameter ion is an instance of the twoIonClass, and one of its neighboringSites
//        // is occupied, the the only open site is the one opposite that occupied site
//        if( twoIonClass.isInstance( ion ) && openSites.size() != neighboringSites.size() ) {
//            // Do a sanity check to see that there aren't 2 or more neighboring sites occupied.
//            int numOccupiedSites = neighboringSites.size() - openSites.size();
//            if( numOccupiedSites > 2 ) {
//                throw new RuntimeException( "too many neighboring sites occupied" );
//            }
//            // Find the occupied site
//            openSites = new ArrayList();
//            if( numOccupiedSites == 1 ) {
//                for( int i = 0; i < neighboringSites.size(); i++ ) {
//                    Point2D p = (Point2D)neighboringSites.get( i );
//                    if( isSiteOccupied( p, ionsInLattice ) ) {
//                        double x = ion.getPosition().getX() + ( ion.getPosition().getX() - p.getX() );
//                        double y = ion.getPosition().getY() + ( ion.getPosition().getY() - p.getY() );
//                        openSites.add( new Point2D.Double( x, y ) );
//                    }
//                }
//            }
//
//        }
//        return openSites;
//    }


    public static void main( String[] args ) {
        Ion s1 = new Sodium();
        s1.setPosition( 0, 0 );
        TwoToOneLattice l = new TwoToOneLattice( Chlorine.class, Sodium.class, Sodium.RADIUS + Chlorine.RADIUS );
        Rectangle2D r = new Rectangle2D.Double( -1000, -1000, 2000, 2000 );
        Crystal c = new Crystal( new SolubleSaltsModel(), l );
        c.addIon( s1 );
        {
            Chlorine ion = new Chlorine();
            ion.setPosition( 1, 0 );
            c.addIon( ion );
        }
        {
            Sodium ion = new Sodium();
            ion.setPosition( 15, 0 );
            c.addIon( ion );
        }
        {
            Sodium ion = new Sodium();
            ion.setPosition( 15, 0 );
            c.addIon( ion );
        }
        {
            Chlorine ion = new Chlorine();
            ion.setPosition( 15, 0 );
            c.addIon( ion );
        }
        {
            Chlorine ion = new Chlorine();
            ion.setPosition( 15, 0 );
            c.addIon( ion );
        }
        {
            Chlorine ion = new Chlorine();
            ion.setPosition( 15, 0 );
            c.addIon( ion );
        }
        {
            Chlorine ion = new Chlorine();
            ion.setPosition( 15, 0 );
            c.addIon( ion );
        }
        {
            Sodium ion = new Sodium();
            ion.setPosition( 15, 0 );
            c.addIon( ion );
        }

        System.out.println( c );
    }
}
