// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.Kit;
import edu.colorado.phet.common.piccolophet.nodes.kit.KitSelectionNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Displays a 3D view for molecules that are "real" versions of the currently visible VSEPR model
 */
public class RealMoleculeNode extends PNode {

    private PText labelText( String str ) {
        return new PText( str ) {{
            setFont( new PhetFont( 14 ) );
            setTextPaint( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
        }};
    }

    public RealMoleculeNode() {
        // padding, and make sure we have the width
        addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Double( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ), new Color( 0, 0, 0, 0 ) ) );

        addChild( new KitSelectionNode<PSwing>( new Property<Integer>( 0 ),
                                                new Kit<PSwing>( labelText( "Water" ), new PSwing( new RealMoleculePanel( RealMolecule.WATER ) ) ),
                                                new Kit<PSwing>( labelText( "Ethane" ), new PSwing( new RealMoleculePanel( RealMolecule.ETHANE ) ) ) ) );
    }
}
