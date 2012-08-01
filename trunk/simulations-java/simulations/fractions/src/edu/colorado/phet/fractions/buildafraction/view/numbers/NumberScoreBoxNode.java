// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.data.List;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas.controlPanelStroke;
import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.INSET;

/**
 * Node that shows a target scoring cell, where a correct fraction can be collected.
 *
 * @author Sam Reid
 */
public class NumberScoreBoxNode extends PNode {
    public final Fraction fraction;
    public final PhetPPath path;
    private boolean completed;
    private PInterpolatingActivity activity;
    private final PImage splitButton;
    private FractionNode fractionGraphic;
    private final PNode rootNode;
    private final BuildAFractionModel model;
    private final NumberSceneNode numberSceneNode;

    public NumberScoreBoxNode( final int numerator, final int denominator, final Property<List<Fraction>> matches, final PNode rootNode, final BuildAFractionModel model, final NumberSceneNode numberSceneNode ) {
        this.rootNode = rootNode;
        this.model = model;
        this.numberSceneNode = numberSceneNode;
        this.path = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 120, 120, 30, 30 ), BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, controlPanelStroke, Color.darkGray ) {{

            setStrokePaint( Color.darkGray );
            setStroke( controlPanelStroke );
        }};
        this.fraction = new Fraction( numerator, denominator );
        addChild( this.path );

        splitButton = new PImage( Images.SPLIT_BLUE ) {{
            setOffset( INSET, INSET );
        }};
        splitButton.addInputEventListener( new CursorHandler() );
        splitButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( final PInputEvent event ) {
                split();
            }
        } );
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

            fractionGraphic.setScale( 1.0 );
            fractionGraphic.splitButton.setVisible( true );
            FractionCardNode cardNode = new FractionCardNode( fractionGraphic, rootNode, numberSceneNode.pairList, model, numberSceneNode );
            numberSceneNode.addChild( cardNode );

            fractionGraphic = null;

            cardNode.fractionNode.split();
            cardNode.fractionNode.sendFractionSkeletonToToolbox();

            numberSceneNode.numberScoreBoxSplit();
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