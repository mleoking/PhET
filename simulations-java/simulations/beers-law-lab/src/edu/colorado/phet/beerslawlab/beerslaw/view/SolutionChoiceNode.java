// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.view.SoluteItemNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ComboBoxNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Control for choosing a solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SolutionChoiceNode extends PhetPNode {

    private final SolutionComboBoxNode comboBoxNode; // keep a reference so we can add observers to ComboBoxNode.selectedItem

    public SolutionChoiceNode( ArrayList<BeersLawSolution> solutions, final Property<BeersLawSolution> currentSolution ) {

        PText labelNode = new PText( MessageFormat.format( Strings.PATTERN_0LABEL, Strings.SOLUTION ) ) {{
            setFont( new PhetFont( BLLConstants.CONTROL_FONT_SIZE ) );
        }};
        addChild( labelNode );

        comboBoxNode = new SolutionComboBoxNode( solutions, currentSolution.get() );
        addChild( comboBoxNode );

        // layout: combo box to right of label, centers vertically aligned
        final double heightDiff = comboBoxNode.getFullBoundsReference().getHeight() - labelNode.getFullBoundsReference().getHeight();
        labelNode.setOffset( 0, Math.max( 0, heightDiff / 2 ) );
        comboBoxNode.setOffset( labelNode.getFullBoundsReference().getMaxX() + 5, Math.min( 0, heightDiff / 2 ) );

        // sync view with model
        currentSolution.addObserver( new VoidFunction1<BeersLawSolution>() {
            public void apply( BeersLawSolution solution ) {
                comboBoxNode.selectedItem.set( solution );
            }
        } );

        // sync model with view
        comboBoxNode.selectedItem.addObserver( new VoidFunction1<BeersLawSolution>() {
            public void apply( BeersLawSolution solution ) {
                currentSolution.set( solution );
            }
        } );
    }

    // Combo box, with custom creation of items (nodes)
    private static class SolutionComboBoxNode extends ComboBoxNode<BeersLawSolution> {
        public SolutionComboBoxNode( ArrayList<BeersLawSolution> solutions, BeersLawSolution selectedSolution ) {
            super( UserComponents.solutionComboBox,
                   new Function1<BeersLawSolution, String>() {
                       public String apply( BeersLawSolution solution ) {
                           return solution.getDisplayName();
                       }
                   },
                   solutions, selectedSolution,
                   new Function1<BeersLawSolution, PNode>() {
                       public PNode apply( final BeersLawSolution solution ) {
                           return new SoluteItemNode( new Property<Color>( solution.saturatedColor ), solution.getDisplayName() );
                       }
                   }
            );
        }
    }
}
