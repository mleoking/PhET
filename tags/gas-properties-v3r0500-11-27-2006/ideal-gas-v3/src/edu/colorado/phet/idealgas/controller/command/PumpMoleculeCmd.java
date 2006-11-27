/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetGraphicsModule;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;
import edu.colorado.phet.idealgas.view.HeavySpeciesGraphic;
import edu.colorado.phet.idealgas.view.LightSpeciesGraphic;

import java.util.Random;

public class PumpMoleculeCmd extends AddModelElementCmd {

    //-------------------------------------------------------------
    // Static fields and methods
    //-------------------------------------------------------------

    protected static final float DEFAULT_ENERGY = IdealGasModel.DEFAULT_ENERGY;

    private GasMolecule molecule;
    private PhetGraphicsModule module;
    protected Class speciesClass;
    private IdealGasModel idealGasModel;
    static private Random random = new Random();


    /**
     *
     * @param model
     * @param molecule
     * @param module
     */
    public PumpMoleculeCmd( IdealGasModel model,
                            GasMolecule molecule,
                            PhetGraphicsModule module ) {
        super( model, molecule );
        this.idealGasModel = model;
        this.molecule = molecule;
        this.module = module;
    }

    /**
     *
     */
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

        // todo: !!!!!!! this should be done in a different way!!!!
//        molecule.setInBox( true);

        // Randomize the placement of the graphic above and below the MOLECULE_LAYER. This
        // gives the scene depth when objects like the thermometer are placed at the MOLECULE_LAYER
        double dLayer = 1 * ( random.nextBoolean() ? 1 : 0 );
        module.getApparatusPanel().addGraphic( graphic, IdealGasConfig.MOLECULE_LAYER + dLayer );
    }
}
