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

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.util.ControlBorderFactory;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

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
        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.getInstance().getString( "ControlPanel.Legend" ) ) );

        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 5, 5, 0 ), 0, 0 );
        // The dipole
        BufferedImage atomImage = null;
        try {
            atomImage = ImageLoader.loadBufferedImage( MriConfig.IMAGE_PATH + "dipole-5-hydrogen.gif" );
            atomImage = BufferedImageUtils.getRotatedImage( atomImage, Math.PI / 2 );
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
        add( new JLabel( SimStrings.getInstance().getString( "NMR.abbreviation" ) ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel(  SimStrings.getInstance().getString( "NMR.definition" )  ), gbc );

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add( new JLabel( SimStrings.getInstance().getString( "MRI.abbreviation" ) ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( SimStrings.getInstance().getString( "MRI.definition" ) ), gbc );

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add( new JLabel( atomIcon ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( SimStrings.getInstance().getString( "ControlPanel.Atom" ) ), gbc );

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        add( new JLabel( arrowIcon ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( SimStrings.getInstance().getString( "ControlPanel.Legend.MagneticField" ) ), gbc );

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel comp = new JLabel( new ImageIcon( getWaveLegendItemImage() ) );
        comp.setBorder( BorderFactory.createLineBorder( Color.lightGray ) );
        add( comp, gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( SimStrings.getInstance().getString( "ControlPanel.Legend.RadioWave" ) ), gbc );
    }

    private BufferedImage getWaveLegendItemImage() {
        try {
            BufferedImage top = ImageLoader.loadBufferedImage( MriConfig.IMAGE_PATH + "wavetop2.gif" );
            BufferedImage bottom = ImageLoader.loadBufferedImage( MriConfig.IMAGE_PATH + "photon2.gif" );
            PCanvas canvas = new PCanvas();
            canvas.setBackground( new Color( 0, 0, 0, 0 ) );
            PImage child1 = new PImage( top );
            canvas.getLayer().addChild( child1 );

            PText text = new PText( SimStrings.getInstance().getString( "ControlPanel.Legend.or" ) );
            canvas.getLayer().addChild( text );

            text.setOffset( child1.getFullBounds().getCenterX() - text.getFullBounds().getWidth() / 2, child1.getFullBounds().getMaxY() );

            PImage child2 = new PImage( bottom );
            canvas.getLayer().addChild( child2 );

            child2.setOffset( child1.getFullBounds().getCenterX() - child2.getFullBounds().getWidth() / 2, text.getFullBounds().getMaxY() );

            canvas.setSize( Math.max( top.getWidth(), bottom.getWidth() ), (int)Math.max( child2.getFullBounds().getMaxY(), text.getFullBounds().getMaxY() ) );
            BufferedImage buf = new BufferedImage( canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
            Graphics2D graphics = buf.createGraphics();
            canvas.paintComponent( graphics );
            graphics.dispose();
            return buf;
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return null;
    }
}
