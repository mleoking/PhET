/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.tests;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 23, 2005
 * Time: 8:48:02 AM
 * Copyright (c) May 23, 2005 by Sam Reid
 */

public class TestPanelLayout {
    public static void main( String[] args ) {
        JPanel panel = new JPanel();
        panel.add( new JButton( "test1" ) );
        panel.add( new JButton( "test2" ) );
        panel.setBorder( BorderFactory.createTitledBorder( "Hello!" ) );

        Dimension dim = panel.getPreferredSize();
        System.out.println( "dim = " + dim );
        System.out.println( "panel.getSize( ) = " + panel.getSize() );

    }
}
