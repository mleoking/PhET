/**
 * Class: ManyMoleculesModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.microwaves;

import edu.colorado.phet.microwaves.coreadditions.collision.Box2D;
import edu.colorado.phet.microwaves.model.WaterMolecule;
import edu.colorado.phet.microwaves.view.WaterMoleculeGraphic;

public class ManyMoleculesModule extends MicrowaveModule {

    public ManyMoleculesModule() {
        super( MicrowavesResources.getString( "ModuleTitle.ManyMoleculesModule" ) );

        // Put a bunch of water molecules randomly on the screen. Make sure they don't overlap
        // so the collision mechanics stay sane
        WaterMolecule[] molecules = new WaterMolecule[MicrowavesConfig.NUM_WATER_MOLECULES_PLACED_RANDOMLY];
        Box2D oven = getMicrowaveModel().getOven();

        for ( int i = 0; i < MicrowavesConfig.NUM_WATER_MOLECULES_PLACED_RANDOMLY; i++ ) {
            WaterMolecule molecule = new WaterMolecule();
            double x = -1;
            while ( x < oven.getMinX() + WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2
                    || x > oven.getMaxX() - ( WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2 ) ) {
                x = Math.random() * oven.getMaxX();
            }
            double y = -1;
            while ( y < oven.getMinY() + WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2
                    || y > oven.getMaxY() - ( WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2 ) ) {
                y = Math.random() * oven.getMaxY();
            }
            molecule.setLocation( x, y );
            molecules[i] = molecule;

            molecule.setDipoleOrientation( Math.random() * Math.PI * 2 );
            getMicrowaveModel().addPolarBody( molecule );
            WaterMoleculeGraphic moleculeGraphic = new WaterMoleculeGraphic( molecule, getModelViewTransform() );
            getApparatusPanel().addGraphic( moleculeGraphic, 5 );
        }
    }
}
