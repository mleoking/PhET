// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.common.control.ModelControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.TestControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ViewControlPanel;
import edu.colorado.phet.moleculepolarity.common.view.EFieldPlateNode.NegativeEFieldPlateNode;
import edu.colorado.phet.moleculepolarity.common.view.EFieldPlateNode.PositiveEFieldPlateNode;
import edu.colorado.phet.moleculepolarity.common.view.JmolViewerNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesCanvas extends MPCanvas {

    private static final Dimension JMOL_VIEWER_SIZE = new Dimension( 400, 400 );

    public RealMoleculesCanvas( RealMoleculesModel model, ViewProperties viewProperties, Frame parentFrame ) {
        super();

        JmolViewerNode viewerNode = new JmolViewerNode( model.getMolecules().get( 0 ), getBackground(), JMOL_VIEWER_SIZE );
        addChild( viewerNode );

        PNode modelControlsNode = new ControlPanelNode( new ModelControlPanel( viewProperties.modelRepresentation ) );
        addChild( modelControlsNode );

        PNode viewControlsNode = new ControlPanelNode( new ViewControlPanel( viewProperties ) );
        addChild( viewControlsNode );

        PNode testControlsNode = new ControlPanelNode( new TestControlPanel( model.eField.enabled ) );
        addChild( testControlsNode );

        Resettable[] resettables = new Resettable[] { model, viewProperties };
        PNode resetAllButtonNode = new ResetAllButtonNode( resettables, parentFrame, 16, Color.BLACK, Color.YELLOW );
        addChild( resetAllButtonNode );

        NegativeEFieldPlateNode negativeEFieldPlateNode = new NegativeEFieldPlateNode( model.eField );
        addChild( negativeEFieldPlateNode );

        PositiveEFieldPlateNode positiveEFieldPlateNode = new PositiveEFieldPlateNode( model.eField );
        addChild( positiveEFieldPlateNode );

        // layout
        {
            final double xSpacing = 50;
            final double ySpacing = 10;
            negativeEFieldPlateNode.setOffset( 30, 60 - PNodeLayoutUtils.getOriginYOffset( negativeEFieldPlateNode ) );
            viewerNode.setOffset( negativeEFieldPlateNode.getFullBoundsReference().getMaxX() + xSpacing, negativeEFieldPlateNode.getYOffset() );
            positiveEFieldPlateNode.setOffset( viewerNode.getFullBoundsReference().getMaxX() + xSpacing, negativeEFieldPlateNode.getYOffset() );
            modelControlsNode.setOffset( positiveEFieldPlateNode.getFullBoundsReference().getMaxX() + xSpacing, 100 );
            viewControlsNode.setOffset( modelControlsNode.getXOffset(), modelControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            testControlsNode.setOffset( modelControlsNode.getXOffset(), viewControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            resetAllButtonNode.setOffset( modelControlsNode.getXOffset(), testControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
        }
    }
}
