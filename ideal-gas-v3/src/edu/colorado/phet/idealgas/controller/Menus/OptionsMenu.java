/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller.menus;

import edu.colorado.phet.collision.SphereSphereExpert;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.controller.DiffusionModule;
import edu.colorado.phet.idealgas.controller.MovableWallsModule;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public OptionsMenu( PhetApplication application ) {
        super( "Options" );
        setMnemonic( 'O' );
        this.application = application;
        diffusionModule = new DiffusionModule( application.getApplicationModel().getClock() );
        movableWallModule = new MovableWallsModule( application.getApplicationModel().getClock() );

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
            super( SimStrings.get("OptionsMenu.Advanced_panels"), false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( AdvancedPanelsMI.this.isSelected() ) {
                        application.getModuleManager().addModule( movableWallModule );
                        application.getModuleManager().addModule( diffusionModule );
                    }
                    else {
                        application.getModuleManager().removeModule( movableWallModule );
                        application.getModuleManager().removeModule( diffusionModule );
                    }
                }
            } );
        }
    }

    private class MoleculeInteractionsMI extends JCheckBoxMenuItem {
        public MoleculeInteractionsMI() {
            super( SimStrings.get( "MeasurementControlPanel.Molecules_interact" ), true );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    SphereSphereExpert.setIgnoreGasMoleculeInteractions( !MoleculeInteractionsMI.this.isSelected() );
                }
            } );
        }
    }

    private class AddHeatFromFloorMI extends JCheckBoxMenuItem {
        public AddHeatFromFloorMI() {
            super( SimStrings.get( "OptionsMenu.Add_remove_heat_from_floor_only"), false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    IdealGasConfig.HEAT_ONLY_FROM_FLOOR = isSelected();
                }
            } );
        }
    }

}
