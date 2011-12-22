// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * PNode that contains a slider and a textual title and allows the user to
 * control the level of some parameter.
 *
 * @author John Blanco
 */
public class CellParameterController extends PNode {
    private static final Font LABEL_FONT = new PhetFont( 14 );

    public CellParameterController( String labelText, Color color ) {
        PNode content = new VBox(
                new PText( labelText ) {{
                    setFont( LABEL_FONT );
                }},
                new HSliderNode( 0, 100, new Property<Double>( 0.0 ) )
        );

        addChild( new ControlPanelNode( content, color ) );

    }
}
