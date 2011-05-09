// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.sandwich;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.IntegerSpinnerNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeListener;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.IDynamicNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.PlusNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.RightArrowNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.SubstanceImageNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * Node that displays the equation for a sandwich.
 * Coefficients may or may not be editable, depending on a compile-time flag.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichEquationNode extends PhetPNode implements IDynamicNode {
    
    private static final PhetFont COEFFICIENT_FONT = new PhetFont( 24 );
    private static final PhetFont NO_REACTION_FONT = new PhetFont( 24 );
    
    private static final int IMAGE_X_SPACING = 6;
    private static final int TERM_X_SPACING = 20;
    
    private final ChemicalReaction reaction;
    private final PNode arrowNode;
    private final ArrayList<PNode> lhsCoefficientNodes, lhsImageNodes, lhsPlusNodes, rhsCoefficientNodes, rhsImageNodes, rhsPlusNodes;
    private final ArrayList<ReactantChangeListener> reactantChangeListeners;
    private final ChangeListener reactionChangeListener;
    private final PText noReactionNode;
    
    public SandwichEquationNode( SandwichShopModel model ) {
        super();
        
        this.reaction = model.getReaction();
        
        // image change listener, for sandwich image
        PropertyChangeListener imageChangeListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( PImage.PROPERTY_IMAGE ) ) {
                    updateLayout();
                }
            }
        };
        
        // left side (reactants)
        lhsCoefficientNodes = new ArrayList<PNode>();
        lhsImageNodes = new ArrayList<PNode>();
        lhsPlusNodes = new ArrayList<PNode>();
        reactantChangeListeners = new ArrayList<ReactantChangeListener>();
        Reactant[] reactants = reaction.getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            
            final Reactant reactant = reactants[i];
            
            // coefficient spinner
            final IntegerSpinnerNode spinnerNode = new IntegerSpinnerNode( SandwichShopModel.getCoefficientRange() );
            spinnerNode.scale( 2 ); // setting font size would be preferable, but doesn't change size of up/down arrows on Mac
            spinnerNode.setValue( reactant.getCoefficient() );
            spinnerNode.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    reactant.setCoefficient( spinnerNode.getValue() );
                }
            } );
            addChild( spinnerNode );
            lhsCoefficientNodes.add( spinnerNode );
            
            // update spinner when reactant changes
            ReactantChangeListener listener = new ReactantChangeAdapter() {
                @Override
                // user has control over bread, meat and cheese coefficients
                public void coefficientChanged() {
                    spinnerNode.setValue( reactant.getCoefficient() );
                }
            };
            reactant.addReactantChangeListener( listener );
            reactantChangeListeners.add( listener );
            
            // image
            final SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
            imageNode.scale( RPALConstants.EQUATION_IMAGE_SCALE );
            addChild( imageNode );
            lhsImageNodes.add( imageNode );

            // plus sign
            if ( i < reactants.length - 1 ) {
                PNode plusNode = new PlusNode();
                addChild( plusNode );
                lhsPlusNodes.add( plusNode );
            }
        }
        
        // arrow
        arrowNode = new RightArrowNode();
        addChild( arrowNode );

        // right side (products)
        rhsCoefficientNodes = new ArrayList<PNode>();
        rhsImageNodes = new ArrayList<PNode>();
        rhsPlusNodes = new ArrayList<PNode>();
        Product[] products = reaction.getProducts();
        for ( int i = 0; i < products.length; i++ ) {
            
            final Product product = products[i];
            
            // coefficient display
            final PText coefficientNode = new PText( String.valueOf( product.getCoefficient() ) );
            coefficientNode.setFont( COEFFICIENT_FONT );
            addChild( coefficientNode );
            rhsCoefficientNodes.add( coefficientNode );
            
            // image
            final SubstanceImageNode imageNode = new SubstanceImageNode( product );
            imageNode.addPropertyChangeListener( imageChangeListener );
            imageNode.scale( RPALConstants.EQUATION_IMAGE_SCALE );
            addChild( imageNode );
            rhsImageNodes.add( imageNode );
            
             // plus sign
            if ( i < products.length - 1 ) {
                PNode plusNode = new PlusNode();
                addChild( plusNode );
                rhsPlusNodes.add( plusNode );
            }
        }
        
        noReactionNode = new PText( RPALStrings.LABEL_NO_SANDWICH );
        noReactionNode.setFont( NO_REACTION_FONT );
        addChild( noReactionNode );
        
        reactionChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
               updateVisibility();
            }
        };
        reaction.addChangeListener( reactionChangeListener );
     
        updateVisibility();
        updateLayout();
    }
    
    /**
     * Cleans up all listeners that could cause memory leaks.
     */
    public void cleanup() {
        // reaction listeners
        reaction.removeChangeListener( reactionChangeListener );
        // reactant listeners
        Reactant[] reactants = reaction.getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            reactants[i].removeReactantChangeListener( reactantChangeListeners.get( i ) );
        }
    }
    
    /*
     * Controls visibility of parts of the right-hand side of the equation,
     * depending on whether we have a valid reaction.
     * If the reaction is invalid, the right-hand side should simply say "no reaction".
     */
    private void updateVisibility() {
        boolean isReaction = reaction.isReaction();
        noReactionNode.setVisible( !isReaction );
        for ( PNode node : rhsCoefficientNodes ) {
            node.setVisible( isReaction );
        }
        for ( PNode node : rhsImageNodes ) {
            node.setVisible( isReaction );
        }
        for ( PNode node : rhsPlusNodes ) {
            node.setVisible( isReaction );
        }
        updateLayout();
    }

    private void updateLayout() {
        
        PNode previousNode = null;
        double spinnerYOffset = 0;
        
        // title
        double x = 0;
        double y = 0;
        
        // left side
        for ( int i = 0; i < lhsCoefficientNodes.size(); i++ ) {
            
            // coefficient
            PNode coefficientNode = lhsCoefficientNodes.get( i );
            if ( i == 0 ) {
                // below title
                x = 0;
                y = 0;
                coefficientNode.setOffset( x, y );
                spinnerYOffset = y;
            }
            else {
                // to right of previous term
                x = previousNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
                coefficientNode.setOffset( x, spinnerYOffset );
            }
            
            // image
            PNode imageNode = lhsImageNodes.get( i );
            x = coefficientNode.getFullBoundsReference().getMaxX() + IMAGE_X_SPACING;
            y = coefficientNode.getFullBoundsReference().getCenterY() - ( imageNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( imageNode );
            imageNode.setOffset( x, y );
            
            // plus sign 
            if ( i < lhsCoefficientNodes.size() - 1 ) {
                PNode plusNode = lhsPlusNodes.get( i );
                x = imageNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
                y = imageNode.getFullBoundsReference().getCenterY() - ( plusNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( plusNode );
                plusNode.setOffset( x, y );
                previousNode = plusNode;
            }
            else {
                previousNode = imageNode;
            }
        }
        
        // arrow
        x = previousNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
        y = previousNode.getFullBoundsReference().getCenterY() - ( arrowNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( arrowNode );
        arrowNode.setOffset( x, y );
        previousNode = arrowNode;
        
        // right side
        for ( int i = 0; i < rhsCoefficientNodes.size(); i++ ) {
            
            // coefficient
            PNode coefficientNode = rhsCoefficientNodes.get( i );
            x = previousNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
            y = previousNode.getFullBoundsReference().getCenterY() - ( coefficientNode.getFullBoundsReference().getHeight() / 2 ) - - PNodeLayoutUtils.getOriginYOffset( coefficientNode );
            coefficientNode.setOffset( x, y );
            
            // "no reaction" label
            if ( i == 0 ) {
                y = previousNode.getFullBoundsReference().getCenterY() - ( noReactionNode.getFullBoundsReference().getHeight() / 2 ) - - PNodeLayoutUtils.getOriginYOffset( noReactionNode );
                noReactionNode.setOffset( x, y );
            }
            
            // image
            PNode imageNode = rhsImageNodes.get( i );
            x = coefficientNode.getFullBoundsReference().getMaxX() + IMAGE_X_SPACING - PNodeLayoutUtils.getOriginXOffset( imageNode );
            y = coefficientNode.getFullBoundsReference().getCenterY() - ( imageNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( imageNode );
            imageNode.setOffset( x, y );
            
            // plus
            if ( i < rhsCoefficientNodes.size() - 1 ) {
                PNode plusNode = rhsPlusNodes.get( i );
                x = imageNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
                y = imageNode.getFullBoundsReference().getCenterY() - ( plusNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( plusNode );
                plusNode.setOffset( x, y );
                previousNode = plusNode;
            }
            else {
                previousNode = imageNode;
            }
        }
    }
}
