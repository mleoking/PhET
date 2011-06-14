// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.view.gun;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Mar 2, 2006
 * Time: 11:27:46 PM
 */

public class OnOffCheckBox extends JCheckBox {
    public OnOffCheckBox( final OnOffItem onOffItem ) {
        super( "", onOffItem.isOn() );
        setVerticalTextPosition( AbstractButton.BOTTOM );
        setHorizontalTextPosition( AbstractButton.CENTER );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onOffItem.setOn( isSelected() );
//                setForeground( new Color( 0, 0, 0, 0 ) );
//                setText( isSelected() ? "On" : "Off" );
//                setForeground( isSelected() ? Color.red : Color.black );
            }
        } );
        setFont( new PhetFont( 13, true ) );
        try {
            ImageIcon on = new ImageIcon( ImageLoader.loadBufferedImage( "quantum-wave-interference/images/button-in-40.gif" ) );
            ImageIcon off = new ImageIcon( ImageLoader.loadBufferedImage( "quantum-wave-interference/images/button-out-40.gif" ) );
            setIcon( new ToggleIcon( on, off ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        setOpaque( false );
    }

    class ToggleIcon implements Icon {
        Icon on;
        Icon off;

        public ToggleIcon( Icon on, Icon off ) {
            this.on = on;
            this.off = off;
        }

        public int getIconHeight() {
            return getCurrentIcon().getIconHeight();
        }

        private Icon getCurrentIcon() {
            return isSelected() ? on : off;
        }

        public int getIconWidth() {
            return getCurrentIcon().getIconHeight();
        }

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            getCurrentIcon().paintIcon( c, g, x, y );
        }
    }

    protected void paintComponent( Graphics g ) {
//        Graphics2D g2 = (Graphics2D)g;
//        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );
    }
}
