/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.IdealGasResources;
import edu.colorado.phet.idealgas.collision.SphereSphereExpert;
import edu.colorado.phet.idealgas.controller.DiffusionModule;
import edu.colorado.phet.idealgas.controller.MovableWallsModule;
import edu.colorado.phet.idealgas.model.SimulationClock;

/**
 * OptionsMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class OptionsMenu extends JMenu {
    private PhetApplication application;
    private DiffusionModule diffusionModule;
    private MovableWallsModule movableWallModule;

    public OptionsMenu( PhetApplication application, SimulationClock simulationClock ) {
        super( "Options" );
        setMnemonic( 'O' );
        this.application = application;
        diffusionModule = new DiffusionModule( simulationClock );
        movableWallModule = new MovableWallsModule( simulationClock );

//        this.add( new AdvancedMenu() );
//        this.add( new AdvancedPanelsMI() );
        this.add( new MoleculeInteractionsMI() );
        this.add( new AddHeatFromFloorMI() );
    }

    //----------------------------------------------------------------
    // Menu item classes
    //----------------------------------------------------------------
    private class AdvancedMenu extends JMenu {
        public AdvancedMenu() {
            super( "Advanced Options" );
            this.add( new AdvancedPanelsMI() );
            this.add( new MoleculeInteractionsMI() );
        }
    }

    private class AdvancedPanelsMI extends JCheckBoxMenuItem {
        public AdvancedPanelsMI() {
            super( IdealGasResources.getString( "OptionsMenu.Advanced_panels" ), false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( AdvancedPanelsMI.this.isSelected() ) {
                        application.addModule( movableWallModule );
                        application.addModule( diffusionModule );
                    }
                    else {
                        application.removeModule( movableWallModule );
                        application.removeModule( diffusionModule );
                    }
                }
            } );
        }
    }

    private class MoleculeInteractionsMI extends JCheckBoxMenuItem {
        public MoleculeInteractionsMI() {
            super( IdealGasResources.getString( "MeasurementControlPanel.Molecules_interact" ), true );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    SphereSphereExpert.setIgnoreGasMoleculeInteractions( !MoleculeInteractionsMI.this.isSelected() );
                }
            } );
        }
    }

    private class AddHeatFromFloorMI extends JCheckBoxMenuItem {
        public AddHeatFromFloorMI() {
            super( IdealGasResources.getString( "OptionsMenu.Add_remove_heat_from_floor_only" ), false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    IdealGasConfig.HEAT_ONLY_FROM_FLOOR = isSelected();
                }
            } );
        }
    }

}
