// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.oneatom;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.colorado.phet.moleculepolarity.control.ElectronegativityControlNode;
import edu.colorado.phet.moleculepolarity.control.ModelPanel;
import edu.colorado.phet.moleculepolarity.control.TestPanel;
import edu.colorado.phet.moleculepolarity.control.ViewPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the "One Atom" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OneAtomCanvas extends MPCanvas {

    public OneAtomCanvas( OneAtomModel model, ViewProperties viewProperties, Frame parentFrame ) {
        super();

        ElectronegativityControlNode enControlA = new ElectronegativityControlNode( model.getAtomA(), MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        ElectronegativityControlNode enControlB = new ElectronegativityControlNode( model.getAtomB(), MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        addChild( enControlA );
        addChild( enControlB );

        PNode modelControlsNode = new ControlPanelNode( new PSwing( new ModelPanel( viewProperties.modelRepresentation ) ) );
        addChild( modelControlsNode );

        PNode viewControlsNode = new ControlPanelNode( new PSwing( new ViewPanel( viewProperties ) ) );
        addChild( viewControlsNode );

        PNode testControlsNode = new ControlPanelNode( new PSwing( new TestPanel( model.eFieldEnabled ) ) );
        addChild( testControlsNode );

        Resettable[] resettables = new Resettable[] { model, viewProperties };
        PNode resetAllButtonNode = new ResetAllButtonNode( resettables, parentFrame, 20, Color.BLACK, Color.YELLOW );
        addChild( resetAllButtonNode );

        // layout
        {
            enControlA.setOffset( 100, 100 );
            enControlB.setOffset( 400, 100 );
            modelControlsNode.setOffset( 600, 100 );
            viewControlsNode.setOffset( 600, 250 );
            testControlsNode.setOffset( 600, 400 );
            resetAllButtonNode.setOffset( 600, 550 );
        }
    }
}
