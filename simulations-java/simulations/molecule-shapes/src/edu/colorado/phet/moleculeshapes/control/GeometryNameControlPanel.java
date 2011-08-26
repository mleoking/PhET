// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

public class GeometryNameControlPanel extends TitledControlPanelNode {
    public GeometryNameControlPanel() {
        super( new PNode() {{
                   final PSwing molecularCheckbox = new PSwing( new PropertyCheckBox( "Molecule Geometry", MoleculeShapesApplication.showMolecularShapeName ) {{
                       setFont( new PhetFont( 14 ) );
                       setForeground( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
                   }} ) {{
                       setOffset( 10, 10 );
                   }};
                   addChild( molecularCheckbox );
                   PSwing electronCheckbox = new PSwing( new PropertyCheckBox( "Electron Geometry", MoleculeShapesApplication.showElectronShapeName ) {{
                       setFont( new PhetFont( 14 ) );
                       setForeground( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
                   }} ) {{
                       setOffset( 10, molecularCheckbox.getFullBounds().getMaxY() + 2 );
                   }};
                   addChild( electronCheckbox );
               }},
               "Geometry Name",
               Color.BLACK,
               new BasicStroke( MoleculeShapesConstants.CONTROL_PANEL_BORDER_WIDTH ),
               MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
        setOffset( 0, 10 );
    }
}
