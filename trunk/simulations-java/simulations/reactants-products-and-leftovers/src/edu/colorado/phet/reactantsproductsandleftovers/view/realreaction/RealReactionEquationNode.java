// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.realreaction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.view.IDynamicNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.PlusNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.RightArrowNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays a non-editable equation for a chemical reaction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionEquationNode extends PComposite implements IDynamicNode {
    
    private static final boolean SHOW_COEFFICIENT_BORDERS = false; // puts a border around coefficients
    private static final boolean SHOW_ONE_COEFFICIENTS = true; // shows coefficient values that are 1
    
    private static final Font FONT = new PhetFont( 36 );
    private static final Color NAME_COLOR = Color.BLACK;

    private static final double COEFFICIENT_X_SPACING = 10;
    private static final double PLUS_X_SPACING = 30;
    private static final double ARROW_X_SPACING = 20;
    
    private static final Color COEFFICIENT_COLOR = Color.BLACK;
    private static final double COEFFICIENT_BORDER_MARGIN = 5;
    private static final Stroke COEFFICIENT_BORDER_STROKE = new BasicStroke( 0.5f );
    private static final Color COEFFICIENT_BORDER_COLOR = Color.BLACK;
    
    private final ChemicalReaction reaction;
    
    public RealReactionEquationNode( ChemicalReaction reaction ) {
        super();
        this.reaction = reaction;
        update();
    }
    
    public void cleanup() {
        // nothing to do here
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
            CoefficientNode coefficientNode = null;
            if ( SHOW_ONE_COEFFICIENTS || reactants[i].getCoefficient() != 1 ) {
                coefficientNode = new CoefficientNode( reactants[i].getCoefficient() );
                addChild( coefficientNode );
                if ( plusNode == null ) {
                    coefficientNode.setOffset( 0, 0 );
                }
                else {
                    x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                    y = 0;
                    coefficientNode.setOffset( x, y );
                }
            }
            
            // name
            NameNode nameNode = new NameNode( reactants[i].getName() );
            addChild( nameNode );
            if ( coefficientNode == null ) {
                if ( plusNode == null ) {
                    nameNode.setOffset( 0, 0 );
                }
                else {
                    x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                    y = 0;
                    nameNode.setOffset( x, y );
                }
            }
            else {
                x = coefficientNode.getFullBoundsReference().getMaxX() + COEFFICIENT_X_SPACING;
                y = 0;
                nameNode.setOffset( x, y );
            }
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
            CoefficientNode coefficientNode = null;
            if ( SHOW_ONE_COEFFICIENTS || reactants[i].getCoefficient() != 1 ) {
                coefficientNode = new CoefficientNode( products[i].getCoefficient() );
                addChild( coefficientNode );
                if ( plusNode == null ) {
                    x = arrowNode.getFullBoundsReference().getMaxX() + ARROW_X_SPACING;
                    y = 0;
                    coefficientNode.setOffset( x, y );
                }
                else {
                    x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                    y = 0;
                    coefficientNode.setOffset( x, y );
                }
            }
            
            // name
            NameNode nameNode = new NameNode( products[i].getName() );
            addChild( nameNode );
            if ( coefficientNode == null ) {
                if ( plusNode == null ) {
                    x = arrowNode.getFullBoundsReference().getMaxX() + ARROW_X_SPACING;
                    y = 0;
                    nameNode.setOffset( x, y );
                }
                else {
                    x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                    y = 0;
                    nameNode.setOffset( x, y );
                }
            }
            else {
                x = coefficientNode.getFullBoundsReference().getMaxX() + COEFFICIENT_X_SPACING;
                y = 0;
                nameNode.setOffset( x, y );
            }
            
            previousNode = nameNode;
        }
    }
    
    private static class CoefficientNode extends PComposite {
        
        public CoefficientNode( int coefficient ) {
            super();
            
            PText textNode = new PText( String.valueOf( coefficient ) );
            textNode.setFont( FONT );
            textNode.setTextPaint( COEFFICIENT_COLOR );
            addChild( textNode );
            
            final double x = -COEFFICIENT_BORDER_MARGIN;
            final double y = -COEFFICIENT_BORDER_MARGIN;
            final double w = textNode.getFullBoundsReference().getWidth() + ( 2 * COEFFICIENT_BORDER_MARGIN );
            final double h = textNode.getFullBoundsReference().getHeight() + ( 2 * COEFFICIENT_BORDER_MARGIN );
            PPath borderNode = new PPath( new Rectangle2D.Double( x, y,w,h ) );
            borderNode.setStroke( COEFFICIENT_BORDER_STROKE );
            borderNode.setStrokePaint( COEFFICIENT_BORDER_COLOR );
            if ( SHOW_COEFFICIENT_BORDERS ) {
                addChild( borderNode );
            }
            
            // layout
            textNode.setOffset( 0, 0 );
            borderNode.setOffset( 0, 0 );
        }
    }
    
    // Molecule name
    private static class NameNode extends HTMLNode {
        
        public NameNode( String html ) {
            super( HTMLUtils.toHTMLString( html ), NAME_COLOR, FONT );
        }
    }
}
