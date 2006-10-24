/* Copyright 2004, Sam Reid */
package org.falstad;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 9:14:03 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */
class QuantumBoxCanvas extends Canvas {
    QuantumBoxFrame pg;

    QuantumBoxCanvas( QuantumBoxFrame p ) {
        pg = p;
    }

    public Dimension getPreferredSize() {
        return new Dimension( 300, 400 );
    }

    public void update( Graphics g ) {
        pg.updateQuantumBox( g );
    }

    public void paint( Graphics g ) {
        pg.updateQuantumBox( g );
    }
}
