/**
 * Class: ManyMoleculesModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.microwaves;

import edu.colorado.phet.microwaves.coreadditions.MessageFormatter;
import edu.colorado.phet.microwaves.coreadditions.collision.Box2D;
import edu.colorado.phet.microwaves.model.WaterMolecule;
import edu.colorado.phet.microwaves.view.WaterMoleculeGraphic;

public class SingleLineOfMoleculesModule2 extends MicrowaveModule {

    public SingleLineOfMoleculesModule2() {
        super( MessageFormatter.format( MicrowavesResources.getString( "ModuleTitle.SingleLineOfMoleculesModule2" ) ) );

        // Put a line of water molecules across the middle of the screen
        Box2D oven = this.getMicrowaveModel().getOven();
        for ( int x = (int) ( oven.getMinX() + WaterMolecule.s_oxygenRadius + WaterMolecule.s_hydrogenRadius * 2 );
              x < (int) ( oven.getMaxX() - WaterMolecule.s_oxygenRadius - WaterMolecule.s_hydrogenRadius * 2 );
              x += WaterMolecule.s_oxygenRadius * 2 + WaterMolecule.s_hydrogenRadius ) {

            WaterMolecule molecule = new WaterMolecule();
            molecule.setLocation( x, 200 );
            molecule.setDipoleOrientation( Math.random() * Math.PI * 2 );
            getMicrowaveModel().addPolarBody( molecule );
            WaterMoleculeGraphic moleculeGraphic = new WaterMoleculeGraphic( molecule, getModelViewTransform() );
            getApparatusPanel().addGraphic( moleculeGraphic, 5 );
            molecule.setVisible( true );
        }
    }
}
