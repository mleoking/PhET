/*Copyright, University of Colorado, PhET, 2003.*/
package edu.colorado.phet.common.view.graphics.mousecontrols;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;

/**
 * User: University of Colorado, PhET
 * Date: Oct 9, 2003
 * Time: 1:07:02 AM
 * Copyright (c) Oct 9, 2003 by University of Colorado, PhET
 */
public class BringToFront implements MouseInputListener {
    private GraphicLayerSet graphicTree;
    private PhetGraphic target;

    public BringToFront( GraphicLayerSet graphicTree, PhetGraphic target ) {
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
