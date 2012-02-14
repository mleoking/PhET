// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ComboBoxNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Control for choosing a solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionChoiceNode extends PhetPNode {

    private static final PhetFont LABEL_FONT = new PhetFont( BLLConstants.CONTROL_FONT_SIZE );
    private static final PhetFont ITEM_FONT = new PhetFont( BLLConstants.CONTROL_FONT_SIZE );

    private final SolutionComboBoxNode comboBoxNode; // keep a reference so we can add observers to ComboBoxNode.selectedItem

    public SolutionChoiceNode( ArrayList<BeersLawSolution> solutions, final Property<BeersLawSolution> currentSolution ) {
        this( Strings.SOLUTION, solutions, currentSolution,
              new Function1<BeersLawSolution, String>() {
                  public String apply( BeersLawSolution solution ) {
                      return solution.solute.name;
                  }
              } );
    }

    public SolutionChoiceNode( String label, ArrayList<BeersLawSolution> solutions, final Property<BeersLawSolution> currentSolution, Function1<BeersLawSolution,String> solutionToString ) {

        PText labelNode = new PText( MessageFormat.format( Strings.PATTERN_0LABEL, label ) ) {{
            setFont( LABEL_FONT );
        }};
        addChild( labelNode );

        comboBoxNode = new SolutionComboBoxNode( solutions, currentSolution.get(), solutionToString );
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
        public SolutionComboBoxNode( ArrayList<BeersLawSolution> solutions, BeersLawSolution selectedSolution, final Function1<BeersLawSolution,String> solutionToString ) {
            super( UserComponents.solutionComboBox,
                   new Function1<BeersLawSolution, String>() {
                       public String apply( BeersLawSolution solution ) {
                           return solution.getDisplayName();
                       }
                   },
                   solutions, selectedSolution,
                   new Function1<BeersLawSolution, PNode>() {
                       public PNode apply( final BeersLawSolution solution ) {
                           return new SoluteItemNode( solution.getSaturatedColor(), solution.getDisplayName() );
                       }
                   }
            );
        }
    }

    //TODO same as in SoluteChoiceNode, pull up to top-level common class
    // A solute item in the combo box
    private static class SoluteItemNode extends PComposite {
        public SoluteItemNode( final Color color, final String label ) {

            // solute color chip
            PPath colorNode = new PPath( new Rectangle2D.Double( 0, 0, 20, 20 ) );
            colorNode.setPaint( color );
            colorNode.setStroke( null );
            addChild( colorNode );

            // solute label
            HTMLNode labelNode = new HTMLNode();
            labelNode.setHTML( label );
            labelNode.setFont( ITEM_FONT );
            addChild( labelNode );

            // layout, color chip to left of label, centers vertically aligned
            colorNode.setOffset( 0, Math.max( 0, ( labelNode.getFullBoundsReference().getHeight() - colorNode.getFullBoundsReference().getHeight() ) / 2 ) );
            labelNode.setOffset( colorNode.getFullBoundsReference().getMaxX() + 5,
                                 Math.max( 0, ( colorNode.getFullBoundsReference().getHeight() - labelNode.getFullBoundsReference().getHeight() ) / 2 ) );
        }
    }

}
