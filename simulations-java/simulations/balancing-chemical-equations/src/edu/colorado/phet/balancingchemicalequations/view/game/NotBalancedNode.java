// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import edu.colorado.phet.balancingchemicalequations.BCEResources;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.control.BalanceChoiceNode.BalanceChoice;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Indicator that equation is not balanced, by any definition of balanced.
 * Frowny face, big "X" and text.
 * Optionally displays a bar chart or balance scale.
 */
public class NotBalancedNode extends GameResultNode {

    private BalanceChoice balanceChoice;

    public NotBalancedNode( final Property<Equation> equationProperty, final Property<Boolean> verboseProperty ) {
        super( false /* smile */ );

        balanceChoice = BalanceChoice.BALANCE_SCALES;

        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                //TODO rebuild this node
            }
        } );

        verboseProperty.addObserver( new SimpleObserver() {
            public void update() {
                //TODO rebuild this node with or without bar charts or balance scales
                System.out.println( "NotBalancedNode.update " + verboseProperty.getValue() );//XXX
            }
        } );
    }

    public void setBalanceChoice( BalanceChoice balanceChoice ) {
        System.out.println( "NotBalanceNode.setBalanceChoice " + balanceChoice );//XXX
        if ( !( balanceChoice == BalanceChoice.BALANCE_SCALES || balanceChoice == BalanceChoice.BAR_CHARTS ) ) {
            throw new IllegalArgumentException( "illegal value for balanceChoice: " + balanceChoice );
        }
        if ( balanceChoice != this.balanceChoice ) {
            this.balanceChoice = balanceChoice;
            //TODO rebuild this node
        }
    }

    @Override
    protected PNode createIconsAndText( PhetFont font ) {
        PNode parentNode = new PNode();

        PImage iconNode = new PImage( BCEResources.getImage( "Heavy-Ballot-X-u2718.png" ) );
        parentNode.addChild( iconNode );

        PText textNode = new PText( BCEStrings.NOT_BALANCED );
        textNode.setFont( font );
        parentNode.addChild( textNode );

        // layout
        iconNode.setOffset( 0, 0 );
        double x = iconNode.getFullBoundsReference().getMaxX() + 2;
        double y = iconNode.getFullBoundsReference().getCenterY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
        textNode.setOffset( x, y );

        return parentNode;
    }
}