/**
 * Class: ManyMoleculesModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.microwaves;

import edu.colorado.phet.microwaves.coreadditions.MessageFormatter;
import edu.colorado.phet.microwaves.model.WaterMolecule;
import edu.colorado.phet.microwaves.view.WaterMoleculeGraphic;

public class OneMoleculeModule extends MicrowaveModule {

    public OneMoleculeModule() {
        super( MessageFormatter.format( MicrowavesResources.getString( "ModuleTitle.OneMoleculeModule" ) ) );

        WaterMolecule molecule = new WaterMolecule();

        molecule.setLocation( getMicrowaveModel().getOven().getMinX() + ( getMicrowaveModel().getOven().getMaxX() - getMicrowaveModel().getOven().getMinX() ) / 2,
                              getMicrowaveModel().getOven().getMinY() + ( getMicrowaveModel().getOven().getMaxY() - getMicrowaveModel().getOven().getMinY() ) / 2 );

        molecule.setDipoleOrientation( 0 );
//        molecule.setDipoleOrientation( 3 * Math.PI / 2 );
//            molecule.setOmega( -0.01 );
//            molecule.setAlpha( 0.0001);
//        molecule.setVelocity( 0.1f, 0f );
        getMicrowaveModel().addPolarBody( molecule );
        WaterMoleculeGraphic moleculeGraphic = new WaterMoleculeGraphic( molecule, getModelViewTransform() );
        getApparatusPanel().addGraphic( moleculeGraphic, 5 );

    }
}
