// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.common.control.IsosurfaceControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeControlNode;
import edu.colorado.phet.moleculepolarity.common.control.TestControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ViewControlPanel;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.colorado.phet.moleculepolarity.common.view.JmolViewerNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.NegativePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.PeriodicTableNode;
import edu.colorado.phet.moleculepolarity.common.view.PositivePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.IsosurfaceType;
import edu.colorado.phet.moleculepolarity.developer.JmolScriptNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesCanvas extends MPCanvas {

    private static final Dimension JMOL_VIEWER_SIZE = new Dimension( 400, 400 );

    public RealMoleculesCanvas( RealMoleculesModel model, final ViewProperties viewProperties, Frame parentFrame ) {
        super();

        final JmolViewerNode viewerNode = new JmolViewerNode( model.getMolecules().get( 0 ), getBackground(), JMOL_VIEWER_SIZE );
        addChild( viewerNode );

        PNode viewControlsNode = new ControlPanelNode( new ViewControlPanel( viewProperties, true, false, true ) );
        addChild( viewControlsNode );

        PNode isosurfaceControlsNode = new ControlPanelNode( new IsosurfaceControlPanel( viewProperties.isosurfaceType ) );
        addChild( isosurfaceControlsNode );

        PNode testControlsNode = new ControlPanelNode( new TestControlPanel( model.eField.enabled ) );
        addChild( testControlsNode );

        Resettable[] resettables = new Resettable[] { model, viewProperties };
        PNode resetAllButtonNode = new ResetAllButtonNode( resettables, parentFrame, 16, Color.BLACK, Color.YELLOW );
        addChild( resetAllButtonNode );

        PNode negativePlateNode = new NegativePlateNode( model.eField );
        addChild( negativePlateNode );

        PNode positivePlateNode = new PositivePlateNode( model.eField );
        addChild( positivePlateNode );

        PeriodicTableNode periodicTableNode = new PeriodicTableNode();
        addChild( periodicTableNode );

        MoleculeControlNode moleculeComboBox = new MoleculeControlNode( model.getMolecules() );
        addChild( moleculeComboBox );


        JmolScriptNode scriptNode = new JmolScriptNode( viewerNode );
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            addChild( scriptNode );
        }

        // layout
        {
            final double xSpacing = 50;
            final double ySpacing = 10;
            negativePlateNode.setOffset( 30, 30 - PNodeLayoutUtils.getOriginYOffset( negativePlateNode ) );
            viewerNode.setOffset( negativePlateNode.getFullBoundsReference().getMaxX() + xSpacing, negativePlateNode.getYOffset() );
            positivePlateNode.setOffset( viewerNode.getFullBoundsReference().getMaxX() + xSpacing, negativePlateNode.getYOffset() );
            periodicTableNode.setOffset( viewerNode.getFullBoundsReference().getCenterX() - ( periodicTableNode.getFullBoundsReference().getWidth() / 2 ),
                                         viewerNode.getFullBoundsReference().getMaxY() + 20 );
            moleculeComboBox.setOffset( viewerNode.getFullBoundsReference().getCenterX() - ( moleculeComboBox.getFullBoundsReference().getWidth() / 2 ),
                                        viewerNode.getFullBoundsReference().getMinY() - moleculeComboBox.getFullBoundsReference().getHeight() - 30 );
            viewControlsNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + xSpacing, viewerNode.getYOffset() );
            isosurfaceControlsNode.setOffset( viewControlsNode.getXOffset(), viewControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            testControlsNode.setOffset( isosurfaceControlsNode.getXOffset(), isosurfaceControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            resetAllButtonNode.setOffset( isosurfaceControlsNode.getXOffset(), testControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            scriptNode.setOffset( resetAllButtonNode.getXOffset(), resetAllButtonNode.getFullBoundsReference().getMaxY() + ySpacing );

        }

        // synchronize viewer with view properties
        {
            viewProperties.bondDipolesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    viewerNode.setBondDipolesVisible( visible );
                }
            } );

            viewProperties.molecularDipoleVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    viewerNode.setMolecularDipoleVisible( visible );
                }
            } );

            viewProperties.isosurfaceType.addObserver( new VoidFunction1<IsosurfaceType>() {
                public void apply( IsosurfaceType isosurfaceType ) {
                    viewerNode.setIsosurfaceType( isosurfaceType );
                }
            } );

            viewProperties.partialChargesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    viewerNode.setPartialChargeVisible( visible );
                }
            } );

            viewProperties.atomLabelsVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    viewerNode.setAtomLabelsVisible( visible );
                }
            } );

            moleculeComboBox.addSelectedItemObserver( new VoidFunction1<Molecule3D>() {
                public void apply( Molecule3D molecule ) {
                    viewerNode.setMolecule( molecule );
                }
            } );
        }
    }
}
