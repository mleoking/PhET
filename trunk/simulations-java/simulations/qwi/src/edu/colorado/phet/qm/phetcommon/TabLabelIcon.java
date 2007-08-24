/*  */
package edu.colorado.phet.qm.phetcommon;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 28, 2006
 * Time: 8:32:12 PM
 */

public class TabLabelIcon extends ImageIcon {
    public TabLabelIcon( String name ) {
        super( new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB ) );
        JLabel example = new JLabel( name ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                Object origAA = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, origAA );
            }
        };
        example.setText( name );
        example.setFont( new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 18 ) );
        setImage( new PSwing( example ).toImage() );
    }
}
