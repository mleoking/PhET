/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 5, 2003
 * Time: 10:09:34 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;
import edu.colorado.phet.idealgas.view.HeavySpeciesGraphic;
import edu.colorado.phet.idealgas.view.LightSpeciesGraphic;

public class PumpMoleculeCmd extends AddModelElementCmd {
    //public class PumpMoleculeCmd implements Command {

    //    protected IdealGasApplication application;
    private GasMolecule molecule;
    private Module module;
    protected Class speciesClass;
    private double initialEnergy;


    //    public PumpMoleculeCmd( IdealGasModel model, double initialEnergy ) {
    //        this( model, model.getCurrentGasSpecies() );
    //        this.initialEnergy = ( initialEnergy == 0 ? DEFAULT_ENERGY : initialEnergy );
    //    }
    //
    //    public PumpMoleculeCmd( IdealGasModel model ) {
    //        this( model, model.getCurrentGasSpecies() );
    //    }
    //
    //    public PumpMoleculeCmd( IdealGasModel model,
    //                            Class speciesClass ) {
    //        super( model, s_gasFactory.create( model,
    //                                           model.getAverageMoleculeEnergy() ));
    //        this.model = model;
    //        this.speciesClass = speciesClass;
    //        this.initialEnergy = DEFAULT_ENERGY;
    //    }

    public PumpMoleculeCmd( IdealGasModel model,
                            GasMolecule molecule,
                            Module module ) {
        super( model, molecule );
        this.molecule = molecule;
        this.module = module;
        this.initialEnergy = DEFAULT_ENERGY;
    }

    public void doIt() {
        super.doIt();
//        molecule.setModel( model );

        PhetGraphic graphic = null;
        if( molecule instanceof HeavySpecies ) {
            graphic = new HeavySpeciesGraphic( module.getApparatusPanel(), molecule );
        }
        else if( molecule instanceof LightSpecies ) {
            graphic = new LightSpeciesGraphic( module.getApparatusPanel(), molecule );
        }
        module.getApparatusPanel().addGraphic( graphic, 10 );
    }

    //
    // Static fields and methods
    //
    protected static final float PI_OVER_2 = (float)Math.PI / 2;
    protected static final float PI_OVER_4 = (float)Math.PI / 4;
    protected static final float MAX_V = -30;

    protected static final float DEFAULT_ENERGY = 15000;
    // Coordinates of the intake port on the box
    protected static final float s_intakePortX = 430 + IdealGasConfig.X_BASE_OFFSET;
    protected static final float s_intakePortY = 400 + IdealGasConfig.Y_BASE_OFFSET;
    // Offset for dithering the initial position of particles pumped into the box
    private static float s_intakePortOffsetY = 1;
}
