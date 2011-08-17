// Copyright 2002-2011, University of Colorado
package org.reid.scenic.view;

import java.awt.Graphics2D;

import org.reid.scenic.model.Atom;
import org.reid.scenic.model.Model;

/**
 * @author Sam Reid
 */
public class View {
    private Model model;

    public View( Model model ) {
        this.model = model;
    }

    public void paint( Graphics2D graphics2D ) {
        for ( Atom atom : model.atoms ) {
            new AtomView( atom ).paint( graphics2D );
        }
        new ButtonView( model.buttonModel ).paint( graphics2D );
    }
}
