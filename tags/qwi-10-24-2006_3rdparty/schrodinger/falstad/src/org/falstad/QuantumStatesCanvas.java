/* Copyright 2004, Sam Reid */
package org.falstad;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 9:15:26 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */
class QuantumStatesCanvas extends Canvas {
    QuantumStatesFrame pg;

    QuantumStatesCanvas( QuantumStatesFrame p ) {
        pg = p;
    }

    public Dimension getPreferredSize() {
        return new Dimension( 300, 400 );
    }

    public void update( Graphics g ) {
        pg.updateQuantumStates( g );
    }

    public void paint( Graphics g ) {
        pg.updateQuantumStates( g );
    }
}
