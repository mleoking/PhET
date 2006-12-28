/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.util.ControlBorderFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * MriLegend
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriLegend extends JPanel {

    public MriLegend() {
        super( new GridBagLayout() );
        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "ControlPanel.Legend" ) ) );

        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 5, 5, 0 ), 0, 0 );
        // The dipole
        BufferedImage atomImage = null;
        try {
            atomImage = ImageLoader.loadBufferedImage( MriConfig.IMAGE_PATH + "dipole-5-hydrogen.gif" );
            atomImage = BufferedImageUtils.getRotatedImage( atomImage, -Math.PI / 2 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        ImageIcon atomIcon = new ImageIcon( atomImage );

        // The arrow
        double length = 25;
        double sign = 1;
        Arrow arrow = new Arrow( new Point2D.Double( 0, ( length * sign ) / 2 ),
                                 new Point2D.Double( 0, ( length * -sign ) / 2 ),
                                 length / 2, length / 2, length / 4, .5, true );
        Area arrowArea = new Area( arrow.getShape() );
        BufferedImage arrowImage = new BufferedImage( (int)arrowArea.getBounds().getWidth(),
                                                      (int)arrowArea.getBounds().getHeight() + 2,
                                                      BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = (Graphics2D)arrowImage.getGraphics();
        g2.translate( arrowImage.getWidth() / 2,
                      arrowImage.getHeight() / 2 );
        g2.setColor( Color.black );
        g2.draw( arrowArea );
        g2.dispose();
        ImageIcon arrowIcon = new ImageIcon( arrowImage );

        // Lay out the panel
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add( new JLabel( "NMR" ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( "Nuclear magnetic resonance" ), gbc );

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add( new JLabel( "MRI" ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( "Magnetic resonance imaging" ), gbc );

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add( new JLabel( atomIcon ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( SimStrings.get( "ControlPanel.Atom" ) ), gbc );

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        add( new JLabel( arrowIcon ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( SimStrings.get( "ControlPanel.MagneticField" ) ), gbc );
    }
}
