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
import edu.colorado.phet.solublesalts.model.ion.Sodium;
import edu.colorado.phet.solublesalts.model.ion.Chlorine;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * PlainCubicLattice
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PlainCubicLattice extends Lattice {
    private static final Random random = new Random( System.currentTimeMillis() );

    private double spacing;

    /**
     *
     * @param spacing
     */
    public PlainCubicLattice( double spacing ) {
        this.spacing = spacing;
    }

    protected List getNeighboringSites( Ion ion, double orientation ) {
        Point2D p = ion.getPosition();
        List sites = new ArrayList();
        for( int i = 0; i < 4; i++ ) {
            double x = p.getX() + spacing * Math.cos( i * Math.PI / 2 + orientation );
            double y = p.getY() + spacing * Math.sin( i * Math.PI / 2 + orientation );
            Point2D pNew = new Point2D.Double( x, y );
            if( getBounds().contains( pNew ) ) {
                sites.add( pNew );
            }
        }
        return sites;
    }


    public static void main( String[] args ) {
        Ion a = new Sodium();
        a.setPosition( 300, 400 );
        Crystal l = new Crystal( new Rectangle2D.Double( 0, 0, 500, 500 ), new PlainCubicLattice( Sodium.RADIUS + Chlorine.RADIUS ) );
        l.addIon( a );
        Ion b = new Chlorine();
        b.setPosition( new Point2D.Double( 290, 400 ) );
        l.addIon( b );

        Ion b2 = new Chlorine();
        b2.setPosition( new Point2D.Double( 290, 400 ) );
        l.addIon( b2 );

        printLattice( l );

//        l.releaseIon( 10 );

        printLattice( l );

//        PlainCubicForm pcf = (PlainCubicForm)l.getForm();
//        List ap = pcf.getNeighboringSites( a.getPosition(), Math.PI / 4 );
//        for( int i = 0; i < ap.size(); i++ ) {
//            Point2D point2D = (Point2D)ap.get(i);
//            System.out.println( "point2D = " + point2D );
//        }
    }

    private static void printLattice( Crystal l ) {
        List ions = l.getIons();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            System.out.println( "ion.getPosition() = " + ion.getPosition() );
        }
    }
}
