/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.molecularreactions.model.collision.Spring;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * SpringTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpringTest {

    public static void main( String[] args ) {
        Spring spring = new Spring( 1, 100 );
        Point2D springFixedEnd = new Point2D.Double( 100, 0 );
        SimpleMolecule body = new MoleculeA();
        DecimalFormat format = new DecimalFormat( "#.00" );

        body.setVelocity( 1, 0 );

        for( int i = 0; i < 20; i++ ) {
            body.stepInTime( 1 );
            double springLength = springFixedEnd.getX() - ( body.getPosition().getX() + body.getPositionPrev().getX() ) / 2;
            double f = -spring.getForce( springLength );
//            body.setAcceleration( f / body.getMass(), 0 );

            double dx = body.getPosition().getX() - body.getPositionPrev().getX();
            double dPe = spring.getEnergy( springLength );
//            double dPe = spring.getEnergy( spring.getRestingLength() - body.getPosition().getX() );
            double ke = body.getKineticEnergy();
            System.out.println( "ke = " + format.format(ke) + "\tdPe = " + format.format(dPe));
            if( dPe < ke ) {
                ke -= dPe;
                double vx = Math.sqrt( 2 * ke / body.getMass() );
                System.out.println( "vx = " + vx );
                body.setVelocity( vx, 0 );
            }
            else {
                System.out.println( "SpringTest.main" );
                body.setVelocity( -body.getVelocity().getX(), 0 );
            }

//            System.out.println( "body.getPosition() = " + body.getPosition() + "\t\tbody.getV" );
            System.out.println( "f = " + format.format( f )
                                + "\tbody ke = " + format.format( body.getKineticEnergy() )
                                + "\t\tspring pe = " + format.format( spring.getEnergy( springLength ) )
                                + "\t\ttotal energy = " + format.format( ( body.getKineticEnergy() + spring.getEnergy( springLength ) ) ) );
        }
    }
}
