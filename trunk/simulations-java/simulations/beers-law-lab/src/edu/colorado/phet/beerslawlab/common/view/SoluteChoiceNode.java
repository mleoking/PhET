// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.model.Solute;
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
 * Control for choosing a solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoluteChoiceNode extends PhetPNode {

    private static final PhetFont LABEL_FONT = new PhetFont( BLLConstants.CONTROL_FONT_SIZE );
    private static final PhetFont ITEM_FONT = new PhetFont( BLLConstants.CONTROL_FONT_SIZE );

    private final SoluteComboBoxNode comboBoxNode; // keep a reference so we can add observers to ComboBoxNode.selectedItem

    public SoluteChoiceNode( ArrayList<Solute> solutes, final Property<Solute> currentSolute ) {
        this( Strings.SOLUTE, solutes, currentSolute,
              new Function1<Solute, String>() {
                  public String apply( Solute solute ) {
                      return solute.name;
                  }
              } );
    }

    public SoluteChoiceNode( String label, ArrayList<Solute> solutes, final Property<Solute> currentSolute, Function1<Solute,String> soluteToString ) {

        PText labelNode = new PText( MessageFormat.format( Strings.PATTERN_0LABEL, label ) ) {{
            setFont( LABEL_FONT );
        }};
        addChild( labelNode );

        comboBoxNode = new SoluteComboBoxNode( solutes, currentSolute.get(), soluteToString );
        addChild( comboBoxNode );

        // layout: combo box to right of label, centers vertically aligned
        final double heightDiff = comboBoxNode.getFullBoundsReference().getHeight() - labelNode.getFullBoundsReference().getHeight();
        labelNode.setOffset( 0, Math.max( 0, heightDiff / 2 ) );
        comboBoxNode.setOffset( labelNode.getFullBoundsReference().getMaxX() + 5, Math.min( 0, heightDiff / 2 ) );

        // when the combo box selection changes, update the current molecule
        comboBoxNode.selectedItem.addObserver( new VoidFunction1<Solute>() {
            public void apply( Solute solute ) {
                currentSolute.set( solute );
            }
        } );

        // when the current molecule changes, update the combo box selection
        currentSolute.addObserver( new VoidFunction1<Solute>() {
            public void apply( Solute solute ) {
                comboBoxNode.selectedItem.set( solute );
            }
        } );
    }

    // Combo box, with custom creation of items (nodes)
    private static class SoluteComboBoxNode extends ComboBoxNode<Solute> {
        public SoluteComboBoxNode( ArrayList<Solute> solutes, Solute selectedSolute, final Function1<Solute,String> soluteToString ) {
            super( UserComponents.soluteComboBox,
                   new Function1<Solute, String>() {
                       public String apply( Solute solute ) {
                           return solute.name;
                       }
                   },
                   solutes, selectedSolute,
                   new Function1<Solute, PNode>() {
                       public PNode apply( final Solute solute ) {
                           return new SoluteItemNode( solute.solutionColor.getMax(), soluteToString.apply( solute ) );
                       }
                   }
            );
        }
    }

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
