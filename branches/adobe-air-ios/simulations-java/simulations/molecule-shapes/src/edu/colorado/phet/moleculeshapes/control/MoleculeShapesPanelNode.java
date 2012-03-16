// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.BasicStroke;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.umd.cs.piccolo.PNode;

/**
 * Molecule Shapes control panel node with the default settings
 */
public class MoleculeShapesPanelNode extends TitledControlPanelNode {
    public MoleculeShapesPanelNode( PNode content, PNode titleNode ) {
        super( content, titleNode, MoleculeShapesColor.BACKGROUND.get(),
               new BasicStroke( MoleculeShapesConstants.CONTROL_PANEL_BORDER_WIDTH ),
               MoleculeShapesColor.CONTROL_PANEL_BORDER.get() );
        initialize();
    }

    public MoleculeShapesPanelNode( PNode content, String title ) {
        this( content, title, TitledControlPanelNode.DEFAULT_INSET );
    }

    public MoleculeShapesPanelNode( PNode content, String title, int inset ) {
        super( content, title, MoleculeShapesColor.BACKGROUND.get(),
               new BasicStroke( MoleculeShapesConstants.CONTROL_PANEL_BORDER_WIDTH ),
               MoleculeShapesColor.CONTROL_PANEL_BORDER.get(), inset );
        initialize();
    }

    private void initialize() {
        MoleculeShapesColor.BACKGROUND.getProperty().addObserver( new SimpleObserver() {
            public void update() {
                background.setPaint( MoleculeShapesColor.BACKGROUND.get() );
                titleBackground.setPaint( MoleculeShapesColor.BACKGROUND.get() );
                repaint();
            }
        } );
        MoleculeShapesColor.CONTROL_PANEL_BORDER.getProperty().addObserver( new SimpleObserver() {
            public void update() {
                background.setStrokePaint( MoleculeShapesColor.CONTROL_PANEL_BORDER.get() );
            }
        } );
    }
}
