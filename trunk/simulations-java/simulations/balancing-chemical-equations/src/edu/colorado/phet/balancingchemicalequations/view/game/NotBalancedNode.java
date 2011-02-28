// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEResources;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.control.BalancedRepresentationChoiceNode.BalancedRepresentation;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.balancingchemicalequations.model.OneProductEquation.WaterEquation;
import edu.colorado.phet.balancingchemicalequations.view.BalanceScalesNode;
import edu.colorado.phet.balancingchemicalequations.view.BarChartsNode;
import edu.colorado.phet.balancingchemicalequations.view.HorizontalAligner;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Indicator that equation is not balanced, by any definition of balanced.
 * Frowny face, big "X" and text.
 * Optionally displays a bar chart or balance scale.
 */
public class NotBalancedNode extends GameResultNode {

    private final ArrayList<VoidFunction0> sizeChangedListeners;
    private BalancedRepresentation balanceRepresentation;
    private final Property<Equation> equationProperty;
    private final SimpleObserver coefficientsObserver;

    private Equation equation;

    public NotBalancedNode( final Property<Equation> equationProperty, final Property<Boolean> verboseProperty ) {
        super( false /* smile */ );

        this.sizeChangedListeners = new ArrayList<VoidFunction0>();
        balanceRepresentation = BalancedRepresentation.BALANCE_SCALES;
        this.equationProperty = equationProperty;

        equation = equationProperty.getValue();
        coefficientsObserver = new SimpleObserver() {
            public void update() {
                updateNode();
            }
        };

        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                equation.removeCoefficientsObserver( coefficientsObserver );
                NotBalancedNode.this.equation = equationProperty.getValue();
                equation.addCoefficientsObserver( coefficientsObserver );
                updateNode();
            }
        } );

        verboseProperty.addObserver( new SimpleObserver() {
            public void update() {
                updateNode();
            }
        } );

        updateNode();
    }

    @Override
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        updateNode();
    }

    protected void updateNode() {
        if ( getVisible() ) {
            super.updateNode();
            notifySizeChanged();
        }
    }

    public void addSizeChangedListener( VoidFunction0 listener ) {
        sizeChangedListeners.add( listener );
    }

    public void removeSizeChangedListener( VoidFunction0 listener ) {
        sizeChangedListeners.remove( listener );
    }

    private void notifySizeChanged() {
        if ( sizeChangedListeners != null ) {
            ArrayList<VoidFunction0> listenersCopy = new ArrayList<VoidFunction0>( sizeChangedListeners );
            for ( VoidFunction0 listener : listenersCopy ) {
                listener.apply();
            }
        }
    }

    public void setBalancedRepresentation( BalancedRepresentation balancedRepresentation ) {
        assert( balancedRepresentation != BalancedRepresentation.NONE );
        if ( balancedRepresentation != this.balanceRepresentation ) {
            this.balanceRepresentation = balancedRepresentation;
            updateNode();
        }
    }

    @Override
    protected PNode createIconsAndText( PhetFont font ) {
        PNode parentNode = new PNode();

        PNode iconAndTextNode = new PNode();
        parentNode.addChild( iconAndTextNode );

        PImage iconNode = new PImage( BCEResources.getImage( "Heavy-Ballot-X-u2718.png" ) );
        iconAndTextNode.addChild( iconNode );

        PText textNode = new PText( BCEStrings.NOT_BALANCED );
        textNode.setFont( font );
        iconAndTextNode.addChild( textNode );

        PNode balanceRepresentationNode = null;
        if ( balanceRepresentation != null ) {
            HorizontalAligner aligner = new HorizontalAligner( new Dimension( 475, 400 ), 90 ); //XXX constants
            if ( balanceRepresentation == BalancedRepresentation.BALANCE_SCALES ) {
                balanceRepresentationNode = new BalanceScalesNode( equationProperty, aligner );
            }
            else  if ( balanceRepresentation == BalancedRepresentation.BAR_CHARTS ) {
                balanceRepresentationNode = new BarChartsNode( equationProperty, aligner );
            }
        }

        // layout
        iconNode.setOffset( 0, 0 );
        double x = iconNode.getFullBoundsReference().getMaxX() + 2;
        double y = iconNode.getFullBoundsReference().getCenterY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
        textNode.setOffset( x, y );
        if ( balanceRepresentationNode != null ) {
            x = iconAndTextNode.getFullBoundsReference().getCenterX() - ( balanceRepresentationNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( balanceRepresentationNode );
            y = parentNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( balanceRepresentationNode ) + 25;
            balanceRepresentationNode.setOffset( x, y );
            parentNode.addChild( balanceRepresentationNode );
        }

        return parentNode;
    }

    public static void main( String[] args ) {

        PhetPCanvas canvas = new PhetPCanvas( BCEConstants.CANVAS_RENDERING_SIZE );
        canvas.setPreferredSize( new Dimension( 1024, 768 ) );

        Equation equation = new WaterEquation();
        EquationTerm[] reactants = equation.getReactants();
        for ( EquationTerm term : reactants ) {
            term.setActualCoefficient( 15 );
        }
        EquationTerm[] products = equation.getProducts();
        for ( EquationTerm term : products ) {
            term.setActualCoefficient( 15 );
        }
        Property<Equation> equationProperty = new Property<Equation>( equation );

        NotBalancedNode node = new NotBalancedNode( equationProperty, new Property<Boolean>( true ) );
        node.setBalancedRepresentation( BalancedRepresentation.BAR_CHARTS );
        node.setOffset( 200, 200 );
        canvas.addWorldChild( node );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible( true );
    }
}