// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import fj.F;
import fj.F2;
import fj.data.List;

import java.awt.Color;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Pie;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.pieset.SliceNodeArgs;
import edu.colorado.phet.fractionsintro.intro.view.pieset.WaterGlassNodeFactory;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.RepresentationControlPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractions.FractionsResources.RESOURCES;
import static edu.colorado.phet.fractions.util.Cache.cache;
import static edu.colorado.phet.fractionsintro.intro.view.CakeNode.cropSides;
import static edu.colorado.phet.fractionsintro.intro.view.Representation.*;
import static fj.Ord.doubleOrd;

/**
 * Canvas for "Fractions Intro" sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroCanvas extends AbstractFractionsCanvas {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") public FractionsIntroCanvas( final FractionsIntroModel model ) {

        final RepresentationControlPanel representationControlPanel = new RepresentationControlPanel( model.representation ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullWidth() / 2, INSET );
        }};
        addChild( representationControlPanel );

        //Cake set with click-to-toggle (not draggable) cakes
//        addChild( new CakeSetFractionNode( model.containerSet, model.representation.valueEquals( Representation.CAKE ) ) {{
//            setOffset( INSET + -10, representationControlPanel.getFullBounds().getMaxY() + 100 - 40 );
//        }} );

        //Number line
        addChild( new NumberLineNode( model.numerator, model.denominator, model.representation.valueEquals( NUMBER_LINE ) ) {{
            setOffset( INSET + 10, representationControlPanel.getFullBounds().getMaxY() + 100 + 15 );
        }} );

        //Show the pie set node when pies are selected
        addChild( new RepresentationNode( model.representation, PIE, new PieSetNode( model.pieSet, rootNode ) ) );

        //For horizontal bars
        addChild( new RepresentationNode( model.representation, HORIZONTAL_BAR, new PieSetNode( model.horizontalBarSet, rootNode ) ) );

        //For vertical bars
        addChild( new RepresentationNode( model.representation, VERTICAL_BAR, new PieSetNode( model.verticalBarSet, rootNode ) ) );

        //For debugging cakes
        addChild( new RepresentationNode( model.representation, CAKE, new PieSetNode( model.cakeSet, rootNode ) ) );

        final Cache<Integer, BufferedImage> cakeImages = cache( new F<Integer, BufferedImage>() {
            @Override public BufferedImage f( Integer denominator ) {
                return cropSides( RESOURCES.getImage( "cake/cake_" + denominator + "_" + 1 + ".png" ) );
            }
        } );
        final Cache<Integer, BufferedImage> cakeGridImages = cache( new F<Integer, BufferedImage>() {
            @Override public BufferedImage f( Integer denominator ) {
                return RESOURCES.getImage( "cake/cake_grid_" + denominator + ".png" );
            }
        } );

        //For draggable cakes
        addChild( new RepresentationNode( model.representation, CAKE, new PieSetNode( model.cakeSet, rootNode, new F<SliceNodeArgs, PNode>() {
            @Override public PNode f( final SliceNodeArgs a ) {
                return new PImage( cakeImages.f( a.denominator ) ) {{
                    setOffset( a.slice.shape().getBounds2D().getCenterX() - getFullBounds().getWidth() / 2, a.slice.shape().getBounds2D().getCenterY() - getFullBounds().getHeight() / 2 );
                }};
            }
        } ) {
            @Override protected PNode createEmptyCellsNode( PieSet state ) {

                PNode node = new PNode();

                //Show the pie cells
                for ( final Pie pie : state.pies ) {
                    final List<ImmutableRectangle2D> rectangles = pie.cells.map( new F<Slice, ImmutableRectangle2D>() {
                        @Override public ImmutableRectangle2D f( Slice slice ) {
                            return new ImmutableRectangle2D( slice.shape().getBounds2D() );
                        }
                    } );
                    final ImmutableRectangle2D union = rectangles.tail().foldLeft( new F2<ImmutableRectangle2D, ImmutableRectangle2D, ImmutableRectangle2D>() {
                        @Override public ImmutableRectangle2D f( ImmutableRectangle2D a, ImmutableRectangle2D b ) {
                            return a.union( b );
                        }
                    }, rectangles.head() );

                    node.addChild( new PImage( cakeGridImages.f( state.denominator ) ) {{
                        double fudgeY = getFullBounds().getHeight() * 0.2;
                        setOffset( union.getCenter().getX() - getFullBounds().getWidth() / 2, union.getCenter().getY() - getFullBounds().getHeight() / 2 - fudgeY );
                    }} );
                }
                return node;
            }
        } ) );

        //For debugging water glasses region management
//        addChild( new RepresentationNode( model.representation, WATER_GLASSES ) {{
//            addChild( new PieSetNode( model.waterGlassSet, rootNode ) );
//        }} );

        //For water glasses
        addChild( new RepresentationNode( model.representation, WATER_GLASSES, new PieSetNode( model.waterGlassSet, rootNode, new WaterGlassNodeFactory() ) {
            @Override protected PNode createEmptyCellsNode( PieSet state ) {
                PNode node = new PNode();
                //Show the beakers
                for ( final Pie pie : state.pies ) {
                    final List<Double> centers = pie.cells.map( new F<Slice, Double>() {
                        @Override public Double f( Slice s ) {
                            return s.shape().getBounds2D().getMinY();
                        }
                    } );

                    //TODO: Could read from cache like WaterGlassNodeFactory instead of creating new each time to improve performance
                    node.addChild( new WaterGlassNode( state.countFilledCells( pie ), state.denominator ) {{
                        setOffset( pie.cells.index( 0 ).shape().getBounds2D().getX(), centers.minimum( doubleOrd ) );
                    }} );
                }
                return node;
            }
        } ) );

        ZeroOffsetNode fractionEqualityPanel = new ZeroOffsetNode( new FractionControlNode( model.numerator, model.denominator, model.maximum ) ) {{
            setOffset( 35, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
        addChild( fractionEqualityPanel );

        final ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setConfirmationEnabled( false );
        }};
        addChild( resetAllButtonNode );

        resetAllButtonNode.setOffset( STAGE_SIZE.width - resetAllButtonNode.getFullBounds().getWidth() - INSET, STAGE_SIZE.height - resetAllButtonNode.getFullBounds().getHeight() - INSET );

        //Spinner to change the maximum allowed value
        MaxSpinner maxSpinner = new MaxSpinner( model.maximum ) {{

            //Center above reset all button
            setOffset( resetAllButtonNode.getFullBounds().getCenterX() - getFullWidth() / 2, resetAllButtonNode.getFullBounds().getY() - getFullHeight() - INSET );
        }};
        addChild( maxSpinner );
    }
}