/**
 * Class: WaterMoleculeWaterMoleculeCollisionExpert
 * Package: edu.colorado.phet.microwave.model
 * Author: Another Guy
 * Date: Aug 25, 2003
 */
package edu.colorado.phet.microwaves.test;


import edu.colorado.phet.microwaves.model.WaterMolecule;
import edu.colorado.phet.microwaves.model.WaterMoleculeWaterMoleculeCollisionExpert;

public class WaterMoleculeWaterMolecuelContactExpertTest {

    public static void main( String[] args ) {
        for ( int i = 0; i < 2; i++ ) {
            WaterMolecule molecule = new WaterMolecule();
            molecule.setLocation( 400 + ( i * 40 ), 250 + ( i * 20 ) );
            molecule.setDipoleOrientation( i * Math.PI / 2 );
        }

        WaterMoleculeWaterMoleculeCollisionExpert detector = new WaterMoleculeWaterMoleculeCollisionExpert();

        WaterMolecule molecule1 = new WaterMolecule();
        molecule1.setLocation( 400, 250 );
        WaterMolecule molecule2 = new WaterMolecule();
        molecule1.setLocation( 400, 250 );

        detector.areInContact( molecule1, molecule2 );
    }
}
