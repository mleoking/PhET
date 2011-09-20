// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that arranges multiple "floating" control panels in a vertical stack, left justified.
 * Optional "Reset All" button is added.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPControlPanelNode extends PNode {

    private static final int Y_SPACING = 10;

    public MPControlPanelNode( Frame parentFrame, Resettable[] resettables, MPVerticalPanel... panels ) {

        // workaround for #3077
        for ( MPVerticalPanel panel : panels ) {
            for ( Component component : panel.getComponents() ) {
                padComponentWidth( component );
            }
        }

        // Uniform width
        int minWidth = 0;
        for ( MPVerticalPanel panel : panels ) {
            minWidth = (int) Math.max( minWidth, panel.getPreferredSize().getWidth() );
        }

        PNode previousNode = null;
        for ( MPVerticalPanel panel : panels ) {

            // uniform width
            panel.setMinWidth( minWidth );

            // panel node
            PNode panelNode = new ControlPanelNode( panel );
            addChild( panelNode );

            // layout: vertical, left justified
            if ( previousNode != null ) {
                panelNode.setOffset( previousNode.getXOffset(), previousNode.getFullBoundsReference().getMaxY() + Y_SPACING );
            }
            previousNode = panelNode;
        }

        // Reset All button
        if ( resettables != null ) {
            PNode resetAllButtonNode = new MPResetAllButtonNode( resettables, parentFrame );
            addChild( resetAllButtonNode );
            resetAllButtonNode.setOffset( previousNode.getXOffset(), previousNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        }
    }

    /*
     * Workaround for #3077 (PSwing clips labels in floating control panels)
     * Traverse the containment hierarchy and add a small amount of horizontal padding to each component.
     */
    private void padComponentWidth( Component component ) {
        final int padWidth = 5;
        if ( component instanceof Container ) {
            for ( Component child : ( (Container) component ).getComponents() ) {
                padComponentWidth( child );
            }
        }
        component.setPreferredSize( new Dimension( (int) component.getPreferredSize().getWidth() + padWidth, (int) component.getPreferredSize().getHeight() ) );
    }
}
