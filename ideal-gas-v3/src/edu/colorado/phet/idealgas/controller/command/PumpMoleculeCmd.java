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

    //-------------------------------------------------------------
    // Static fields and methods
    //-------------------------------------------------------------

    protected static final float DEFAULT_ENERGY = IdealGasModel.DEFAULT_ENERGY;

    private GasMolecule molecule;
    private Module module;
    protected Class speciesClass;
    private double initialEnergy;
    private IdealGasModel idealGasModel;


    public PumpMoleculeCmd( IdealGasModel model,
                            GasMolecule molecule,
                            Module module ) {
        super( model, molecule );
        this.idealGasModel = model;
        this.molecule = molecule;
        this.module = module;
        this.initialEnergy = DEFAULT_ENERGY;
    }

    public void doIt() {
        super.doIt();

        PhetGraphic graphic = null;
        if( molecule instanceof HeavySpecies ) {
            graphic = new HeavySpeciesGraphic( module.getApparatusPanel(), molecule );
        }
        else if( molecule instanceof LightSpecies ) {
            graphic = new LightSpeciesGraphic( module.getApparatusPanel(), molecule );
        }
        idealGasModel.getBox().addContainedBody( molecule );
        module.getApparatusPanel().addGraphic( graphic, IdealGasConfig.MOLECULE_LAYER );
    }
}
