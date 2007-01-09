package edu.colorado.phet.rotation.controls;

import edu.colorado.phet.rotation.RotationLookAndFeel;
import edu.colorado.phet.rotation.util.GraphicsUtil;
import edu.colorado.phet.rotation.util.UnicodeUtil;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:03:31 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class SymbolKey extends JPanel {
    private GridBagConstraints gridBagConstraints;

    public SymbolKey() {
        setLayout( new GridBagLayout() );
        gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        JLabel label = new JLabel( "Symbol Key:" );
        label.setFont( RotationLookAndFeel.getControlPanelTitleFont() );
        add( label, gridBagConstraints );

        addItem( UnicodeUtil.THETA, "Angle" );
        addItem( UnicodeUtil.OMEGA, "Angular Velocity" );
        addItem( UnicodeUtil.ALPHA, "Angular Acceleration" );
        addItem( "x, y", "Position" );
        addItem( "v", "Velocity" );
        addItem( "a", "Acceleration" );
        setBorder( BorderFactory.createLineBorder( Color.lightGray ) );
    }

    private void addItem( String theta, String s ) {
        JLabel label = new JLabel( theta + " = " + s ) {
            protected void paintComponent( Graphics g ) {
                boolean aa = GraphicsUtil.antialias( g, true );
                super.paintComponent( g );
                GraphicsUtil.antialias( g, aa );
            }
        };
        label.setFont( RotationLookAndFeel.getLegendItemFont() );
        add( label, gridBagConstraints );
    }
}
