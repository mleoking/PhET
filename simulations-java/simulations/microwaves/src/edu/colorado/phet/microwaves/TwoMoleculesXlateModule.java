/**
 * Class: ManyMoleculesModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.microwaves;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.microwaves.coreadditions.MessageFormatter;
import edu.colorado.phet.microwaves.model.Microwave;
import edu.colorado.phet.microwaves.model.WaterMolecule;
import edu.colorado.phet.microwaves.view.WaterMoleculeGraphic;

public class TwoMoleculesXlateModule extends MicrowaveModule {

    private int fieldWidth = 1000;
    private int fieldHeight = 700;
    //    private MicrowaveModel model;
    private Microwave muWave;
    private WaterMolecule molecule;


    public TwoMoleculesXlateModule() {
        super( MessageFormatter.format( SimStrings.get( "ModuleTitle.TwoMoleculesXlateModule" ) ) );

//        getMicrowaveModel().removeAllModelElements();
//        Box2D oven = new Box2D( new Point2D.Double( 100, 100 ),
//                                new Point2D.Double( 800, 600 ) );
//        Box2D oven = new Box2D( new Point2D.Double( 350, 210 ),
//                                new Point2D.Double( 500, 300 ) );
//        getMicrowaveModel().setOven( oven );
//        getApparatusPanel().addGraphic( new OvenGraphic( oven ), 1 );

        // Put a line of water molecules across the middle of the screen
        molecule = new WaterMolecule();
        molecule.setLocation( 250, 255 );
        molecule.setDipoleOrientation( Math.PI / 4 );
        molecule.setVelocity( 0, 0 );
        getMicrowaveModel().addPolarBody( molecule );
        WaterMoleculeGraphic moleculeGraphic = new WaterMoleculeGraphic( molecule, getModelViewTransform() );
        getApparatusPanel().addGraphic( moleculeGraphic, 5 );

        molecule = new WaterMolecule();
        molecule.setLocation( 450, 275 );
        molecule.setDipoleOrientation( 0 );
        molecule.setVelocity( -0.08f, 0.0f );
        getMicrowaveModel().addPolarBody( molecule );
        moleculeGraphic = new WaterMoleculeGraphic( molecule, getModelViewTransform() );
        getApparatusPanel().addGraphic( moleculeGraphic, 5 );
    }

    public void activate( PhetApplication app ) {

        // Create and display a dialog with strip charts for the microwave intensity
        // and the orientation of the water molecule
        JFrame frame = app.getApplicationView().getPhetFrame();
        app.getApplicationModel().setRunning( false );
    }

    public void deactivate( PhetApplication app ) {
    }

    //
    // Inner classes
    //

}
