/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view.util;

import edu.colorado.phet.common.view.util.GraphicsState;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * RotatedTextLabel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

/**
 * Class for labels of the y axes
 */
public class RotatedTextLabel extends JPanel {
    private static Font myFont;

    static {
        // Set up the defaultFont we want
        JLabel dummyLabel = new JLabel();
        Font defaultFont = dummyLabel.getFont();
        myFont = new Font( defaultFont.getFontName(), defaultFont.getStyle(), defaultFont.getSize() );
    }

    private String label;
    private int x = 15;
    private int y = 90;

    public RotatedTextLabel( String label ) {
        super( null );
        this.label = "   " + label;
        setPreferredSize( new Dimension( x, y ) );
    }

    public void paint( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
        GraphicsState gs = new GraphicsState( g2 );
        JLabel dummyLabel = new JLabel();
        Font font = dummyLabel.getFont();
        Font f = new Font( font.getFontName(), font.getStyle(), font.getSize() + 2 );
        int y = (int)getSize().getHeight();

        AffineTransform at = new AffineTransform();
        at.setToRotation( -Math.PI / 2.0, x, y );
        g2.transform( at );
        g2.setFont( myFont );
        g2.drawString( label, x, y );
        gs.restoreGraphics();
    }
}
