// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.text.MessageFormat;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control for changing solution's concentration.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ConcentrationControlNode extends PNode {

    public ConcentrationControlNode( Property<BeersLawSolution> solution ) {

        final PhetFont font = new PhetFont( BLLConstants.CONTROL_FONT_SIZE );

        // nodes
        PText labelNode = new PhetPText( MessageFormat.format( Strings.PATTERN_0LABEL, Strings.CONCENTRATION ), font );
        PNode sliderNode = new ZeroOffsetNode( new ConcentrationSliderNode( solution ) );
        PNode textFieldNode = new PSwing( new ConcentrationTextField( solution, font ) );
        PNode unitsNode = new ConcentrationUnitsNode( solution, font );

        // rendering order
        PNode parentNode = new PNode();
        parentNode.addChild( labelNode );
        parentNode.addChild( sliderNode );
        parentNode.addChild( textFieldNode );
        parentNode.addChild( unitsNode );
        addChild( new ZeroOffsetNode( parentNode ) );

        // layout
        sliderNode.setOffset( labelNode.getFullBoundsReference().getMaxX() + 8,
                              labelNode.getFullBoundsReference().getCenterY() - ( sliderNode.getFullBoundsReference().getHeight() / 2 ) );
        textFieldNode.setOffset( sliderNode.getFullBoundsReference().getMaxX() + 10,
                                 sliderNode.getFullBoundsReference().getCenterY() - ( textFieldNode.getFullBoundsReference().getHeight() / 2 ) );
        unitsNode.setOffset( textFieldNode.getFullBoundsReference().getMaxX() + 5,
                             textFieldNode.getFullBoundsReference().getCenterY() - ( unitsNode.getFullBoundsReference().getHeight() / 2 ) );
    }
}
