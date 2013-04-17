// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.buildafraction.view.CollectionBoxNode;
import edu.colorado.phet.fractions.buildafraction.view.UndoButton;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

import static edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas.CONTROL_PANEL_STROKE;
import static java.lang.Math.ceil;

/**
 * Node that shows a target scoring cell, where a correct fraction can be collected.
 *
 * @author Sam Reid
 */
public class ShapeCollectionBoxNode extends CollectionBoxNode {
    private final PhetPPath path;
    private boolean completed;
    private final UndoButton undoButton;
    private ContainerNode containerNode;
    private final ShapeSceneNode sceneNode;
    public final double scaleFactor;

    public ShapeCollectionBoxNode( final ShapeSceneNode sceneNode, final MixedFraction mixedFraction, final ObservableProperty<Boolean> enabled ) {
        this.sceneNode = sceneNode;
        if ( sceneNode == null ) { throw new RuntimeException( "Null scene" ); }
        double numberOfShapes = ceil( mixedFraction.toDouble() );
        scaleFactor = sceneNode.isMixedNumbers() ? 0.6 : 1.0;

        double mixedNumbersReductionSize = sceneNode.isMixedNumbers() ? 24 : 0;
        this.path = new PhetPPath( new RoundRectangle2D.Double( 0, 0,

                                                                //room for shape items
                                                                120 * numberOfShapes * scaleFactor +

                                                                //spacing between them
                                                                5 * ( numberOfShapes - 1 ) / 2 * scaleFactor - mixedNumbersReductionSize,
                                                                114, ARC, ARC ), BACKGROUND, STROKE, DISABLED_STROKE_PAINT ) {{

            if ( !enabled.get() ) {
                setTransparency( FADED_OUT );
            }

            //Fade in if in the scene graph, or immediately set the transparency if out of the scene graph (for example if the user started a level and then came back to it).
            enabled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( final Boolean matched ) {
                    if ( matched ) {
                        setStrokePaint( ENABLED_STROKE_PAINT );
                        if ( getRoot() == null ) { setTransparency( 1f ); }
                        else { animateToTransparency( 1f, FADE_TIME ); }
                    }
                    else {
                        setStrokePaint( DISABLED_STROKE_PAINT );
                        if ( getRoot() == null ) {setTransparency( FADED_OUT );}
                        else { animateToTransparency( FADED_OUT, FADE_TIME ); }
                    }
                }
            } );
        }};
        addChild( this.path );

        undoButton = new UndoButton( Components.collectionBoxUndoButton ) {{
            scale( 0.8 );
            setOffset( -1, -1 );
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    undo();
                }
            } );
        }};
        undoButton.addInputEventListener( new CursorHandler() );
        undoButton.setVisible( false );
        addChild( undoButton );
    }

    //Send the ContainerNode and all of its pieces back to the toolbox.
    public void undo() {
        if ( completed ) {
            completed = false;
            path.setStroke( CONTROL_PANEL_STROKE );
            undoButton.setVisible( false );
            undoButton.setPickable( false );
            undoButton.setChildrenPickable( false );

            sceneNode.addChild( containerNode );

            containerNode.setScale( 1.0 );
            containerNode.addBackUndoButton();
            containerNode.setAllPickable( true );

            //Have to start animating back before changing the "target cell" flag, because that flag is used to determine whether it is "inPlayArea" for purposes of choosing location.
            sceneNode.animateContainerNodeToAppropriateLocation( containerNode );
            containerNode.setInCollectionBox( false, 0 );

            //Send the pieces home
            containerNode.undoAll();

            //The blue "break apart" control once a container has been put into the collection box, it will fly back to the floating panel and retain its divisions.  So we just need to have it reset to no divisions when it goes back to the panel.
            containerNode.selectedPieceSize.set( 0 );

            containerNode.resetNumberOfContainers();

            containerNode = null;

            sceneNode.collectionBoxUndone();
            sceneNode.syncModelFractions();
        }
    }

    //The user has completed a challenge by dragging a container node into this matching ShapeCollectionBoxNode.
    public void setCompletedFraction( ContainerNode containerNode ) {
        this.containerNode = containerNode;
        this.completed = true;
        undoButton.setVisible( true );
        undoButton.setPickable( true );
        undoButton.setChildrenPickable( true );
    }

    public boolean isCompleted() { return completed; }
}