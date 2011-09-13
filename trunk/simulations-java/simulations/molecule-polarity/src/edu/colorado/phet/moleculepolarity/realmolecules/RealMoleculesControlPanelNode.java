// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.MPResetAllButtonNode;
import edu.colorado.phet.moleculepolarity.common.control.MPVerticalPanel;
import edu.colorado.phet.moleculepolarity.common.control.SurfaceControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ViewControlPanel;
import edu.colorado.phet.moleculepolarity.common.model.MPModel;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.umd.cs.piccolo.PNode;

/**
 * Floating control panel for the "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesControlPanelNode extends PNode {

    public RealMoleculesControlPanelNode( MPModel model, final ViewProperties viewProperties, Frame parentFrame ) {

        // Swing panels
        MPVerticalPanel viewControlPanel = new ViewControlPanel( viewProperties, true, false, true, true, MPStrings.BOND_DIPOLES );
        MPVerticalPanel surfaceControlPanel = new SurfaceControlPanel( viewProperties.isosurfaceType );

        // Uniform width
        int minWidth = (int) Math.max( viewControlPanel.getPreferredSize().getWidth(), surfaceControlPanel.getPreferredSize().getWidth() );
        viewControlPanel.setMinWidth( minWidth );
        surfaceControlPanel.setMinWidth( minWidth );

        // Panel nodes
        PNode viewControlNode = new ControlPanelNode( viewControlPanel );
        addChild( viewControlNode );
        PNode surfaceControlNode = new ControlPanelNode( surfaceControlPanel );
        addChild( surfaceControlNode );
        PNode resetAllButtonNode = new MPResetAllButtonNode( new Resettable[] { model, viewProperties }, parentFrame );
        addChild( resetAllButtonNode );

        // layout: vertical, left justified
        final int ySpacing = 10;
        viewControlNode.setOffset( 0, 0 );
        surfaceControlNode.setOffset( viewControlNode.getXOffset(), viewControlNode.getFullBoundsReference().getMaxY() + ySpacing );
        resetAllButtonNode.setOffset( viewControlNode.getXOffset(), surfaceControlNode.getFullBoundsReference().getMaxY() + ySpacing );
    }
}
