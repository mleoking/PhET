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

import edu.colorado.phet.idealgas.model.*;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * PumpControlPanel
 * <p/>
 * A JPanel with spinners that make gas molecules come into the box from the pump.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PumpControlPanel extends SpeciesSelectionPanel implements Pump.Listener {
    private PressureSensingBox box;

    public PumpControlPanel( IdealGasModule module, GasSource gasSource, String[] speciesNames ) {
        super( module, speciesNames );
        module.getPump().addListener( this );
        this.box = module.getBox();
    }

    protected void createMolecule( Class moleculeClass ) {
        getModule().pumpGasMolecules( 1, moleculeClass );
    }

    protected void removeMolecule( Class moleculeClass ) {
        getModule().removeGasMolecule( moleculeClass );
    }

    protected int getHeavySpeciesCnt() {
        return getModule().getHeavySpeciesCnt();
    }

    protected int getLightSpeciesCnt() {
        return getModule().getLightSpeciesCnt();
    }

    //--------------------------------------------------------------
    // Pump.Listener implementation
    //--------------------------------------------------------------

    public void moleculesAdded( Pump.MoleculeEvent event ) {
        Class species = event.getSpecies();
        if( HeavySpecies.class.isAssignableFrom( species ) ) {
            getHeavySpinner().setValue( new Integer( getModule().getHeavySpeciesCnt() ) );
        }
        if( LightSpecies.class.isAssignableFrom( species ) ) {
            getLightSpinner().setValue( new Integer( getModule().getLightSpeciesCnt() ) );
        }
    }

    public void moleculeAdded( GasMolecule molecule ) {
        molecule.addObserver( new MoleculeRemover( molecule ) );
    }

    private class MoleculeRemover implements GasMolecule.Observer {
        GasMolecule molecule;
        boolean isInBox = true;
        boolean init;
        MoleculeCountSpinner spinner;
        Point2D p = new Point2D.Double();
        Rectangle2D b = new Rectangle2D.Double();

        MoleculeRemover( GasMolecule molecule ) {
            this.molecule = molecule;
            if( HeavySpecies.class.isAssignableFrom( molecule.getClass() ) ) {
                this.spinner = getHeavySpinner();
            }
            if( LightSpecies.class.isAssignableFrom( molecule.getClass() ) ) {
                this.spinner = getLightSpinner();
            }
        }

        public void removedFromSystem() {
            update();
        }

        /**
         * Handles molecules leaving and re-entering the box
         */
        public void update() {
//            SwingUtilities.invokeLater(  new Runnable() {
//                public void run() {

//                    int padding = 5;
//                    b.setRect( box.getBoundsInternal().getX() - padding,
//                               box.getBoundsInternal().getY() - padding,
//                               box.getBoundsInternal().getWidth() + padding * 2,
//                               box.getBoundsInternal().getHeight() + padding * 2 );

                    b = box.getBoundsInternal();
                    if( !b.contains( molecule.getPosition() ) && isInBox ) {
                        isInBox = false;
                        spinner.decrementValue();
                    }
                    if( b.contains( molecule.getPosition() ) && !isInBox ) {
                        isInBox = true;
                        spinner.incrementValue();
                    }
//                }
//            } );
        }
    }
}
