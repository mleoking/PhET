// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas.controlPanelStroke;
import static java.lang.Math.ceil;

/**
 * Node that shows a target scoring cell, where a correct fraction can be collected.
 *
 * @author Sam Reid
 */
public class ShapeCollectionBoxNode extends PNode {
    private final PhetPPath path;
    private boolean completed;
    private final UndoButton splitButton;
    private ContainerNode containerNode;
    private final ShapeSceneNode sceneNode;

    public ShapeCollectionBoxNode( final ShapeSceneNode sceneNode, final MixedFraction maxFraction ) {
        this.sceneNode = sceneNode;
        if ( sceneNode == null ) { throw new RuntimeException( "Null scene" ); }
        double numberShapes = ceil( maxFraction.toDouble() );
        this.path = new PhetPPath( new RoundRectangle2D.Double( 0, 0,

                                                                //room for shape items
                                                                120 * numberShapes +

                                                                //spacing between them
                                                                5 * ( numberShapes - 1 ),
                                                                114, 30, 30 ), BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, controlPanelStroke, Color.darkGray ) {{

            setStrokePaint( Color.darkGray );
            setStroke( controlPanelStroke );
        }};
        addChild( this.path );

        splitButton = new UndoButton( Components.shapesCollectionBoxSplitButton ) {{
            scale( 0.8 );
            setOffset( -1, -1 );
            addActionListener( new ActionListener() {
                @Override public void actionPerformed( final ActionEvent e ) {
                    split();
                }
            } );
        }};
        splitButton.addInputEventListener( new CursorHandler() );
        splitButton.setVisible( false );
        addChild( splitButton );
    }

    public void split() {
        if ( completed ) {
            completed = false;
            path.setStrokePaint( Color.darkGray );
            path.setStroke( controlPanelStroke );
            splitButton.setVisible( false );
            splitButton.setPickable( false );
            splitButton.setChildrenPickable( false );

            sceneNode.addChild( containerNode );

            containerNode.setScale( 1.0 );
            containerNode.addBackSplitButton();
            containerNode.setAllPickable( true );
            containerNode.setInTargetCell( false, 0 );

            //Send the pieces home
            containerNode.splitAll();

            //The blue "break apart" control once a container has been put into the collection box, it will fly back to the floating panel and retain its divisions.  So we just need to have it reset to no divisions when it goes back to the panel.
            containerNode.selectedPieceSize.set( 0 );

            sceneNode.animateContainerNodeToAppropriateLocation( containerNode );
            containerNode = null;

            sceneNode.collectionBoxSplit();
            sceneNode.syncModelFractions();
        }
    }

    public void setCompletedFraction( ContainerNode containerNode ) {
        this.containerNode = containerNode;
        path.setStrokePaint( Color.darkGray );
        this.completed = true;
        splitButton.setVisible( true );
        splitButton.setPickable( true );
        splitButton.setChildrenPickable( true );
    }

    public boolean isCompleted() { return completed; }
}