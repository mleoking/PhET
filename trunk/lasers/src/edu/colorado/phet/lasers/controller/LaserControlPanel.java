/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Oct 27, 2004
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.components.PhetSlider;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class LaserControlPanel extends PhetControlPanel {
    private GridBagConstraints gbc;
    private JPanel laserControlPane;

    public LaserControlPanel( final BaseLaserModule module ) {
        super( module );
        laserControlPane = new JPanel( new GridBagLayout() );
        gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                      GridBagConstraints.CENTER,
                                      GridBagConstraints.HORIZONTAL,
                                      new Insets( 0, 0, 0, 0 ),
                                      0, 0 );

        super.setControlPane( laserControlPane );

        addDebugControls( module );

    }

    /**
     * Adds controls for adjusting simulation parameters
     *
     * @param module
     */
    private void addDebugControls( final BaseLaserModule module ) {
        final PhetSlider cheatSlider = new PhetSlider( "Cheat angle", "deg", 0, 20, LaserConfig.PHOTON_CHEAT_ANGLE );
        cheatSlider.setPaintTicks( false );
        cheatSlider.setPaintLabels( false );
        cheatSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                LaserConfig.PHOTON_CHEAT_ANGLE = cheatSlider.getValue();
            }
        } );
        addControl( cheatSlider );

        final PhetSlider cavityHeightSlider = new PhetSlider( "Cavity height", "pixels", 100, 300, module.getCavity().getBounds().getHeight() );
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
        addControl( cavityHeightSlider );

        final PhetSlider cavityWidthSlider = new PhetSlider( "Cavity width", "pixels", 200, 450, module.getCavity().getBounds().getWidth() );
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
        addControl( cavityWidthSlider );

        final PhetSlider lasingThresholdSlider = new PhetSlider( "Lasing threshold", "", 0, 300, LaserConfig.LASING_THRESHOLD );
        lasingThresholdSlider.setPaintLabels( false );
        lasingThresholdSlider.setPaintTicks( false );
        lasingThresholdSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                LaserConfig.LASING_THRESHOLD = (int)lasingThresholdSlider.getValue();
            }
        } );
        addControl( lasingThresholdSlider );

        final JTextField numPhotonsTF = new JTextField( 15 );
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                numPhotonsTF.setText( Integer.toString( ( (BaseLaserModule)module ).getNumPhotons() ) );
            }
        } );
        addControl( new JLabel( "Number of Photons" ) );
        addControl( numPhotonsTF );
    }

    public void addControl( Component component ) {
        gbc.gridy++;
        laserControlPane.add( component, gbc );
    }
}
