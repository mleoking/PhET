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

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.StopwatchPanel;
import edu.colorado.phet.idealgas.controller.DiffusionModule;
import edu.colorado.phet.idealgas.controller.MovableWallsModule;
import edu.colorado.phet.idealgas.model.GasMolecule;

import javax.swing.*;
import java.awt.*;
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

        this.add( new AdvancedMenu() );
    }

    //----------------------------------------------------------------
    // Menu item classes
    //----------------------------------------------------------------
    private class AdvancedMenu extends JMenu {
        public AdvancedMenu() {
            super( "Advanced Options" );
            this.add( new AdvancedPanels() );
            this.add( new MoleculeInteractions() );
            this.add( new Stopwatch() );
        }
    }

    private class AdvancedPanels extends JCheckBoxMenuItem {
        public AdvancedPanels() {
            super( "Advanced Panels", false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( AdvancedPanels.this.isSelected() ) {
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

    private class MoleculeInteractions extends JCheckBoxMenuItem {
        public MoleculeInteractions() {
            super( SimStrings.get( "MeasurementControlPanel.Molecules-interact" ), true );

            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    GasMolecule.enableParticleParticleInteractions( MoleculeInteractions.this.isSelected() );
                }
            } );
        }
    }

    private class Stopwatch extends JCheckBoxMenuItem {
        private StopwatchPanel stopwatchPanel;

        public Stopwatch() {
            super( "Stopwatch", false );

            ApplicationModel appModel = application.getApplicationModel();
            stopwatchPanel = new StopwatchPanel( appModel.getClock() );
            addActionListener( new ActionListener() {
                PhetFrame frame = application.getPhetFrame();

                public void actionPerformed( ActionEvent e ) {
                    if( isSelected() ) {
                        frame.getClockControlPanel().add( stopwatchPanel, BorderLayout.WEST );
                        frame.getClockControlPanel().revalidate();
                    }
                    else {
                        frame.getClockControlPanel().remove( stopwatchPanel );
                        frame.getClockControlPanel().revalidate();
                    }
                }
            } );
        }
    }
}
