// Copyright 2002-2011, University of Colorado
package org.reid.scenic.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import org.reid.scenic.model.Atom;

/**
 * @author Sam Reid
 */
public class AtomView {
    private Atom atom;

    public AtomView( Atom atom ) {
        this.atom = atom;
    }

    public void paint( Graphics2D graphics2D ) {
        graphics2D.setPaint( Color.blue );
        int w = 10;
        graphics2D.fill( new Ellipse2D.Double( atom.position.getX() - w / 2, atom.position.getY() - w / 2, w, w ) );
    }
}
