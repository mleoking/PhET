/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck;

import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.circuit.JunctionGroup;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Dec 7, 2003
 * Time: 12:35:36 PM
 * Copyright (c) Dec 7, 2003 by Sam Reid
 */
public class ClickToDeselect implements InteractiveGraphic {
    Circuit circuit;

    public ClickToDeselect( Circuit circuit ) {
        this.circuit = circuit;
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return true;
    }

    public void mousePressed( MouseEvent event ) {
    }

    public void mouseDragged( MouseEvent event ) {
    }

    public void mouseMoved( MouseEvent e ) {
    }

    public void deselectAll( Component co ) {
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            circuit.branchAt( i ).setSelected( false );
        }
        JunctionGroup[] jg = circuit.getJunctionGroups();
        for( int i = 0; i < jg.length; i++ ) {
            JunctionGroup junctionGroup = jg[i];
            junctionGroup.setSelected( false );
        }
        co.repaint();
    }

    public void mouseReleased( MouseEvent event ) {
        System.out.println( "ClickToDeselect released." );
        deselectAll( event.getComponent() );
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent event ) {
    }

    public void mouseExited( MouseEvent event ) {
    }

    public void paint( Graphics2D g ) {
    }

    public boolean contains( int x, int y ) {
        return true;
    }

}
