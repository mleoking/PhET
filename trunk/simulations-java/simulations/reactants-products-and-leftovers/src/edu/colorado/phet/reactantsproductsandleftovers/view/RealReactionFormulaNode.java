package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays an non-editable formula for a chemical reaction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionFormulaNode extends PComposite {
    
    private static final Font FONT = new PhetFont( 36 );
    private static final Color NAME_COLOR = Color.BLACK;

    private static final double COEFFICIENT_X_SPACING = 10;
    private static final double PLUS_X_SPACING = 30;
    private static final double ARROW_X_SPACING = 20;
    
    private static final Color COEFFICIENT_COLOR = Color.BLACK;
    
    private final ChemicalReaction reaction;
    private final HTMLNode htmlNode;
    
    public RealReactionFormulaNode( ChemicalReaction reaction ) {
        super();
        
        this.reaction = reaction;
        
        htmlNode = new HTMLNode();
        addChild( htmlNode );
        
        update();
    }
    
    /*
     * Note: 
     * This layout algorithm depends on the fact that all terms contain at least 1 uppercase letter.
     * This allows us to align the baselines of HTML-formatted text.
     */
    private void update() {
        
        removeAllChildren();
        
        // determine cap height of the font, using a char that has no descender
        final double capHeight = new NameNode( "T" ).getFullBounds().getHeight();
        
        // left-hand side of the formula (reactants)
        Reactant[] reactants = reaction.getReactants();
        double x = 0;
        double y = 0;
        PNode previousNode = null;
        for ( int i = 0; i < reactants.length; i++ ) {
            
            // plus sign between terms
            PlusNode plusNode = null;
            if ( i > 0 ) {
                plusNode = new PlusNode();
                addChild( plusNode );
                x = previousNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                y = ( capHeight / 2 ) - ( plusNode.getFullBoundsReference().getHeight() / 2 );
                plusNode.setOffset( x, y );
            }
            
            // coefficient 
            CoefficientNode coefficientNode = new CoefficientNode( reactants[i].getCoefficient() );
            addChild( coefficientNode );
            if ( plusNode == null ) {
                coefficientNode.setOffset( 0, 0 );
            }
            else {
                x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                y = 0;
                coefficientNode.setOffset( x,y );
            }
            
            // name
            NameNode nameNode = new NameNode( reactants[i].getName() );
            addChild( nameNode );
            x = coefficientNode.getFullBoundsReference().getMaxX() + COEFFICIENT_X_SPACING;
            y = 0;
            nameNode.setOffset( x, y );
            
            previousNode = nameNode;
        }
        
        // right-pointing arrow
        RightArrowNode arrowNode = new RightArrowNode();
        addChild( arrowNode );
        x = previousNode.getFullBoundsReference().getMaxX() + ARROW_X_SPACING;
        y = ( capHeight / 2 );
        arrowNode.setOffset( x, y );
        previousNode = arrowNode;
        
        // right-hand side of the formula (products)
        Product[] products = reaction.getProducts();
        for ( int i = 0; i < products.length; i++ ) {
            
            // plus sign between terms
            PlusNode plusNode = null;
            if ( i > 0 ) {
                plusNode = new PlusNode();
                addChild( plusNode );
                x = previousNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                y = ( capHeight / 2 ) - ( plusNode.getFullBoundsReference().getHeight() / 2 );
                plusNode.setOffset( x, y );
            }
            
            // coefficient
            CoefficientNode coefficientNode = new CoefficientNode( products[i].getCoefficient() );
            addChild( coefficientNode );
            if ( plusNode == null ) {
                x = previousNode.getFullBoundsReference().getMaxX() + ARROW_X_SPACING;
                y = 0;
                coefficientNode.setOffset( x, y );
            }
            else {
                x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                y = 0;
                coefficientNode.setOffset( x,y );
            }
            
            // name
            NameNode nameNode = new NameNode( products[i].getName() );
            addChild( nameNode );
            x = coefficientNode.getFullBoundsReference().getMaxX() + COEFFICIENT_X_SPACING;
            y = 0;
            nameNode.setOffset( x, y );
            
            previousNode = nameNode;
        }
    }
    
    private static class CoefficientNode extends PText {
        
        public CoefficientNode( int coefficient ) {
            super( String.valueOf( coefficient ) );
            setFont( FONT );
            setTextPaint( COEFFICIENT_COLOR );
        }
    }
    
    private static class NameNode extends HTMLNode {
        
        public NameNode( String html ) {
            super( HTMLUtils.toHTMLString( html ), FONT, NAME_COLOR );
        }
    }
}
