// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColors;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.umd.cs.piccolo.PNode;

/**
 * Molecule Shapes control panel node with the default settings
 */
public class MoleculeShapesPanelNode extends TitledControlPanelNode {
    public MoleculeShapesPanelNode( PNode content, PNode titleNode ) {
        super( content, titleNode, MoleculeShapesColors.BACKGROUND.get(),
               new BasicStroke( MoleculeShapesConstants.CONTROL_PANEL_BORDER_WIDTH ),
               MoleculeShapesColors.CONTROL_PANEL_BORDER.get() );
        initialize();
    }

    public MoleculeShapesPanelNode( PNode content, String title ) {
        super( content, title, MoleculeShapesColors.BACKGROUND.get(),
               new BasicStroke( MoleculeShapesConstants.CONTROL_PANEL_BORDER_WIDTH ),
               MoleculeShapesColors.CONTROL_PANEL_BORDER.get() );
        initialize();
    }

    private void initialize() {
        MoleculeShapesColors.BACKGROUND.getProperty().addObserver( new SimpleObserver() {
            public void update() {
                background.setPaint( MoleculeShapesColors.BACKGROUND.get() );
                titleBackground.setPaint( MoleculeShapesColors.BACKGROUND.get() );
                repaint();
            }
        } );
        MoleculeShapesColors.CONTROL_PANEL_BORDER.getProperty().addObserver( new SimpleObserver() {
            public void update() {
                background.setStrokePaint( MoleculeShapesColors.CONTROL_PANEL_BORDER.get() );
                repaint(); // TODO: debug this repaint issue. really becoming an issue
            }
        } );
    }
}
