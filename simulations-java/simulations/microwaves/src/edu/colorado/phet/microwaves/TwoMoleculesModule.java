/**
 * Class: ManyMoleculesModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.microwaves;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.microwaves.coreadditions.MessageFormatter;
import edu.colorado.phet.microwaves.model.Microwave;
import edu.colorado.phet.microwaves.model.WaterMolecule;
import edu.colorado.phet.microwaves.model.WaterMoleculeWaterMoleculeCollisionExpert;
import edu.colorado.phet.microwaves.view.WaterMoleculeGraphic;


public class TwoMoleculesModule extends MicrowaveModule {

    private int fieldWidth = 1000;
    private int fieldHeight = 700;
    //    private MicrowaveModel model;
    private Microwave muWave;
    private WaterMolecule molecule;


    public TwoMoleculesModule() {
        super( MessageFormatter.format( SimStrings.get( "ModuleTitle.TwoMoleculesModule" ) ) );

        // Put a line of water molecules across the middle of the screen
        WaterMolecule[] ma = new WaterMolecule[10];
        for ( int i = 0; i < 2; i++ ) {
            molecule = new WaterMolecule();
            boolean overlapping = false;
            do {
                molecule.setLocation( 400 + ( i * 12 ), 250 + ( i * -15 ) );

                molecule.setDipoleOrientation( i * Math.PI / 2 );
                for ( int j = 0; j < i; j++ ) {
                    while ( WaterMoleculeWaterMoleculeCollisionExpert.areOverlapping( molecule, ma[j] ) ) {
                        molecule.setLocation( molecule.getLocation().getX(), molecule.getLocation().getY() + 1 );
                    }
                }
                ma[i] = molecule;
                molecule.setOmega( 0.001 * i );
                molecule.setVelocity( -0.000f * i, 0f );
            } while ( overlapping );
            getMicrowaveModel().addPolarBody( molecule );
            WaterMoleculeGraphic moleculeGraphic = new WaterMoleculeGraphic( molecule, getModelViewTransform() );
            getApparatusPanel().addGraphic( moleculeGraphic, 5 );
        }
    }

    public void activate( PhetApplication app ) {
    }

    public void deactivate( PhetApplication app ) {
    }

    //
    // Inner classes
    //

}
