/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.graphics.mousecontrols;

import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;

/**
 * BringToFront
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class BringToFront implements MouseInputListener {
    private CompositeGraphic graphicTree;
    private Graphic target;

    public BringToFront( CompositeGraphic graphicTree, Graphic target ) {
        this.graphicTree = graphicTree;
        this.target = target;
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
        graphicTree.moveToTop( target );
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    public void mouseDragged( MouseEvent e ) {
    }

    public void mouseMoved( MouseEvent e ) {
    }


}
