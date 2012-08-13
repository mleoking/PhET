// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.shapes.UndoButton;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas.controlPanelStroke;

/**
 * Node that shows a target scoring cell, where a correct fraction can be collected.
 *
 * @author Sam Reid
 */
public class NumberCollectionBoxNode extends PNode {
    public final Fraction fraction;
    private final PhetPPath path;
    private boolean completed;
    private final UndoButton splitButton;
    private FractionNode fractionGraphic;
    private final NumberSceneNode numberSceneNode;

    public NumberCollectionBoxNode( final int numerator, final int denominator, final NumberSceneNode numberSceneNode ) {
        this.numberSceneNode = numberSceneNode;
        this.path = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 120, 120, 30, 30 ), BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, controlPanelStroke, Color.darkGray ) {{

            setStrokePaint( Color.darkGray );
            setStroke( controlPanelStroke );
        }};
        this.fraction = new Fraction( numerator, denominator );
        addChild( this.path );

        splitButton = new UndoButton( Components.shapesCollectionBoxSplitButton ) {{
            scale( 0.8 );
            setOffset( -1, -1 );
            addActionListener( new ActionListener() {
                @Override public void actionPerformed( final ActionEvent e ) {
                    split( true );
                }
            } );
        }};
        splitButton.addInputEventListener( new CursorHandler() );
        splitButton.setVisible( false );
        addChild( splitButton );
    }

    public void split( boolean sendToToolbox ) {
        if ( completed ) {
            completed = false;
            path.setStrokePaint( Color.darkGray );
            path.setStroke( controlPanelStroke );
            splitButton.setVisible( false );
            splitButton.setPickable( false );
            splitButton.setChildrenPickable( false );

            fractionGraphic.setScale( 1.0 );
            fractionGraphic.splitButton.setVisible( true );
            FractionCardNode cardNode = new FractionCardNode( fractionGraphic, numberSceneNode.pairs, numberSceneNode );
            numberSceneNode.addChild( cardNode );

            fractionGraphic = null;

            cardNode.fractionNode.split();
            if ( sendToToolbox ) {
                cardNode.fractionNode.sendFractionSkeletonToToolbox();
            }
            else {
                cardNode.fractionNode.sendFractionSkeletonToCenterOfScreen();
            }

            numberSceneNode.numberCollectionBoxSplit();
            numberSceneNode.updateStacks();
        }
    }

    public void setCompletedFraction( FractionNode fractionGraphic ) {
        this.fractionGraphic = fractionGraphic;
        path.setStrokePaint( Color.darkGray );
        this.completed = true;
        splitButton.setVisible( true );
        splitButton.setPickable( true );
        splitButton.setChildrenPickable( true );
    }

    public boolean isCompleted() { return completed; }
}