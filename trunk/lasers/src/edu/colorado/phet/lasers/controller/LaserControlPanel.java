/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Oct 27, 2004
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.components.PhetSlider;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
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

        final PhetSlider cheatSlider = new PhetSlider( "Cheat angle", "deg", 0, 20, 0 );
        cheatSlider.setPaintTicks( false );
        cheatSlider.setPaintLabels( false );
        cheatSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                LaserConfig.PHOTON_CHEAT_ANGLE = cheatSlider.getValue();
            }
        } );
        addControl( cheatSlider );

        final PhetSlider cavityHeightSlider = new PhetSlider( "Cavity height", "pixels", 100, 300, module.getCavity().getBounds().getHeight() );
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
    }

    public void addControl( Component component ) {
        gbc.gridy++;
        laserControlPane.add( component, gbc );
    }
}
