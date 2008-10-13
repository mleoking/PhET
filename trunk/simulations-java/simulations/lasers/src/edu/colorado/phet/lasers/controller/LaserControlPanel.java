/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

public class LaserControlPanel extends ControlPanel {
    private GridBagConstraints gbc;
    private JPanel laserControlPane;

    public LaserControlPanel( final BaseLaserModule module ) {
        super( module );
        laserControlPane = new JPanel( new GridBagLayout() );
        gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                      GridBagConstraints.CENTER,
                                      GridBagConstraints.HORIZONTAL,
                                      new Insets( 3, 0, 3, 0 ),
                                      0, 0 );
        super.addFullWidth( laserControlPane );
//        addDebugControls( module );
//        addControl( new LasersLegend() );
    }

    public Component addControl( Component component ) {
        gbc.gridy++;
        laserControlPane.add( component, gbc );
        return component;
    }

    /**
     * Adds controls for adjusting simulation parameters
     *
     * @param module
     */
    private void addDebugControls( final BaseLaserModule module ) {

        JButton btn = new JButton( "Debug controls" );
        btn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JDialog dlg = new JDialog( PhetApplication.instance().getPhetFrame(),
                                           "Debug controls", false );
                dlg.setContentPane( new DebugPanel( module ) );
                dlg.pack();
                dlg.setVisible( true );
            }
        } );
        addControl( btn );
    }

    private class DebugPanel extends JPanel {
        DebugPanel( final BaseLaserModule module ) {
            super( new GridBagLayout() );
            JPanel panel = this;
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                             GridBagConstraints.NORTHWEST,
                                                             GridBagConstraints.HORIZONTAL,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            JFrame dlg = new JFrame( "Parameter Adjustments" );
            dlg.setContentPane( panel );
            dlg.setUndecorated( true );
            dlg.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );

            final ModelSlider cheatSlider = new ModelSlider( "Cheat angle", "deg", 0, 20, LasersConfig.PHOTON_CHEAT_ANGLE );
            cheatSlider.setPaintTicks( false );
            cheatSlider.setPaintLabels( false );
            cheatSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    LasersConfig.PHOTON_CHEAT_ANGLE = cheatSlider.getValue();
                }
            } );
            panel.add( cheatSlider, gbc );

            final ModelSlider cavityHeightSlider = new ModelSlider( "Cavity height", "pixels", 100, 300, module.getCavity().getBounds().getHeight() );
            cavityHeightSlider.setPaintTicks( false );
            cavityHeightSlider.setPaintLabels( false );
            cavityHeightSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    Rectangle2D bounds = module.getCavity().getBounds();
                    double height = cavityHeightSlider.getValue();
                    double midY = bounds.getMinY() + bounds.getHeight() / 2;
                    module.getCavity().setBounds( bounds.getMinX(), midY - height / 2,
                                                  bounds.getMaxX(), midY + height / 2 );
                }
            } );
            panel.add( cavityHeightSlider, gbc );

            final ModelSlider cavityWidthSlider = new ModelSlider( "Cavity width", "pixels", 200, 450, module.getCavity().getBounds().getWidth() );
            cavityWidthSlider.setPaintTicks( false );
            cavityWidthSlider.setPaintLabels( false );
            cavityWidthSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    Rectangle2D bounds = module.getCavity().getBounds();
                    double width = cavityWidthSlider.getValue();
                    double midX = bounds.getMinX() + bounds.getWidth() / 2;
                    module.getCavity().setBounds( midX - width / 2, bounds.getMinY(),
                                                  midX + width / 2, bounds.getMaxY() );
                }
            } );
            panel.add( cavityWidthSlider, gbc );

            final ModelSlider lasingThresholdSlider = new ModelSlider( "Lasing threshold", "", 0, 300, LasersConfig.LASING_THRESHOLD );
            lasingThresholdSlider.setPaintLabels( false );
            lasingThresholdSlider.setPaintTicks( false );
            lasingThresholdSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    LasersConfig.LASING_THRESHOLD = (int) lasingThresholdSlider.getValue();
                }
            } );
            panel.add( lasingThresholdSlider, gbc );

            final JTextField numPhotonsTF = new JTextField( 15 );
            module.getModel().addModelElement( new ModelElement() {
                public void stepInTime( double dt ) {
                    numPhotonsTF.setText( Integer.toString( ( (BaseLaserModule) module ).getNumPhotons() ) );
                }
            } );
            panel.add( new JLabel( "Number of Photons" ), gbc );
            panel.add( numPhotonsTF, gbc );

            DecimalFormat decFmt = new DecimalFormat( "#" );
            final ModelSlider aveSlider = new ModelSlider( "Averaging period", "msec", 0, 10000, 0, decFmt );
            aveSlider.setPaintLabels( false );
            aveSlider.setNumMajorTicks( 10 );
            aveSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setEnergyLevelsAveragingPeriod( aveSlider.getValue() );
                }
            } );
            aveSlider.setValue( module.getEnerglyLevelsAveragingPeriod() );
            panel.add( aveSlider, gbc );

            final ModelSlider kaboomThresholdSlider = new ModelSlider( "Meltdown threshold", "Photons", 0, 500, LasersConfig.KABOOM_THRESHOLD );
            kaboomThresholdSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    LasersConfig.KABOOM_THRESHOLD = (int) kaboomThresholdSlider.getValue();
                }
            } );
            panel.add( kaboomThresholdSlider, gbc );
        }
    }
}
