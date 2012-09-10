// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.buildafraction.view.CollectionBoxNode;
import edu.colorado.phet.fractions.buildafraction.view.shapes.UndoButton;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

import static edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas.controlPanelStroke;

/**
 * Node that shows a target scoring cell, where a correct fraction can be collected.
 *
 * @author Sam Reid
 */
public class NumberCollectionBoxNode extends CollectionBoxNode {
    public final MixedFraction mixedFraction;
    private final PhetPPath path;
    private boolean completed;
    private final UndoButton undoButton;
    private FractionNode fractionGraphic;
    private final NumberSceneNode numberSceneNode;

    public NumberCollectionBoxNode( final MixedFraction mixedFraction, final NumberSceneNode numberSceneNode, final ObservableProperty<Boolean> enabled ) {
        this.numberSceneNode = numberSceneNode;
        this.path = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 120, 120, ARC, ARC ), BACKGROUND, STROKE, DISABLED_STROKE_PAINT ) {{
            if ( !enabled.get() ) {
                setTransparency( FADED_OUT );
            }
            enabled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( final Boolean matched ) {
                    if ( matched ) {
                        setStrokePaint( ENABLED_STROKE_PAINT );
                        animateToTransparency( 1f, FADE_TIME );
                    }
                    else {
                        setStrokePaint( DISABLED_STROKE_PAINT );
                        animateToTransparency( FADED_OUT, FADE_TIME );
                    }
                }
            } );
        }};

        this.mixedFraction = mixedFraction;
        addChild( this.path );

        undoButton = new UndoButton( Components.collectionBoxUndoButton ) {{
            scale( 0.8 );
            setOffset( -1, -1 );
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    undo( true );
                }
            } );
        }};
        undoButton.addInputEventListener( new CursorHandler() );
        undoButton.setVisible( false );
        addChild( undoButton );
    }

    public void undo( boolean sendToToolbox ) {
        if ( completed ) {
            completed = false;
            path.setStrokePaint( Color.darkGray );
            path.setStroke( controlPanelStroke );
            undoButton.setVisible( false );
            undoButton.setPickable( false );
            undoButton.setChildrenPickable( false );

            fractionGraphic.setScale( 1.0 );
            fractionGraphic.undoButton.setVisible( true );
            FractionCardNode cardNode = new FractionCardNode( fractionGraphic, numberSceneNode.pairs, numberSceneNode );
            numberSceneNode.addChild( cardNode );

            fractionGraphic = null;

            cardNode.fractionNode.undo();
            if ( sendToToolbox ) {
                cardNode.fractionNode.animateToToolbox();
            }
            else {
                cardNode.fractionNode.animateToCenterOfScreen();
            }

            numberSceneNode.numberCollectionBoxUndone();
            numberSceneNode.updateStacks();
        }
    }

    public void setCompletedFraction( FractionNode fractionGraphic ) {
        this.fractionGraphic = fractionGraphic;
        path.setStrokePaint( Color.darkGray );
        this.completed = true;
        undoButton.setVisible( true );
        undoButton.setPickable( true );
        undoButton.setChildrenPickable( true );
    }

    public boolean isCompleted() { return completed; }
}