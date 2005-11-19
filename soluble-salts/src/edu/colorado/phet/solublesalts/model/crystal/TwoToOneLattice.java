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

import edu.colorado.phet.solublesalts.model.Ion;
import edu.colorado.phet.solublesalts.model.Sodium;
import edu.colorado.phet.solublesalts.model.Chloride;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.ArrayList;

/**
 * TwoToOneLattice
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TwoToOneLattice extends Lattice {

    private Class oneIonClass;
    private Class twoIonClass;
    private double spacing;

    public TwoToOneLattice( Class oneIonClass, Class twoIonClass, double spacing ) {
        this.oneIonClass = oneIonClass;
        this.twoIonClass = twoIonClass;
        this.spacing = spacing;
    }

    protected List getNeighboringSites( Ion ion, double orientation ) {
        List sites = new ArrayList();
        Point2D p = ion.getPosition();

        // If the parameter ion is an instance of the oneIonClass, it will have four possible
        // open sites
        if( oneIonClass.isInstance( ion ) ) {
            for( int i = 0; i < 4; i++ ) {
                double x = p.getX() + spacing * Math.cos( i * Math.PI / 2 + orientation );
                double y = p.getY() + spacing * Math.sin( i * Math.PI / 2 + orientation );
                Point2D pNew = new Point2D.Double( x, y );
                if( getBounds().contains( pNew ) ) {
                    sites.add( pNew );
                }
            }
        }

        // If the parameter ion is an instance of the twoIonClass, it will have two possible
        // open sites
        if( twoIonClass.isInstance( ion ) ) {
            for( int i = 0; i < 2; i++ ) {
                double x = p.getX() + spacing * Math.cos( i * Math.PI + orientation );
                double y = p.getY() + spacing * Math.sin( i * Math.PI + orientation );
                Point2D pNew = new Point2D.Double( x, y );
                if( getBounds().contains( pNew ) ) {
                    sites.add( pNew );
                }
            }
        }

        System.out.println( "sites.size() = " + sites.size() );
        return sites;
    }


    public static void main( String[] args ) {
        Ion s1 = new Sodium();
        s1.setPosition( 0,0);
        TwoToOneLattice l = new TwoToOneLattice( Sodium.class, Chloride.class, Sodium.RADIUS + Chloride.RADIUS );
        Rectangle2D r = new Rectangle2D.Double( -1000, -1000, 2000, 2000 );
        Crystal c = new Crystal( s1, r, l );
        {
            Chloride ion = new Chloride();
            ion.setPosition( 1, 0 );
            c.addIon( ion );
        }
        {
            Sodium ion = new Sodium();
            ion.setPosition( 15,0 );
            c.addIon( ion );
        }
        {
            Sodium ion = new Sodium();
            ion.setPosition( 15, 0 );
            c.addIon( ion );
        }
    }
}
