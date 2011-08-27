// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;

import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.umd.cs.piccolo.PNode;

public class MoleculeShapesPanelNode extends TitledControlPanelNode {
    public MoleculeShapesPanelNode( PNode content, String title ) {
        super( content, title, Color.BLACK, new BasicStroke( MoleculeShapesConstants.CONTROL_PANEL_BORDER_WIDTH ), MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
    }
}
