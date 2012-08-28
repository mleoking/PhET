// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import fj.F;
import fj.Unit;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PadBoundsNode;
import edu.colorado.phet.functions.FunctionsResources.Images;
import edu.colorado.phet.functions.buildafunction.TwoInputFunctionNode;
import edu.colorado.phet.functions.buildafunction.UnaryFunctionNode;
import edu.colorado.phet.functions.buildafunction.ValueNode;
import edu.colorado.phet.functions.intro.view.NavigationBar.ShapeIcon;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.getRotatedImage;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;
import static edu.colorado.phet.common.piccolophet.nodes.FaceNode.*;
import static edu.colorado.phet.functions.FunctionsResources.Images.ROTATE_RIGHT;
import static edu.colorado.phet.functions.intro.Scene.toStack;
import static edu.colorado.phet.functions.model.Functions.*;
import static edu.colorado.phet.functions.model.Type.*;
import static fj.data.List.list;
import static java.awt.Color.lightGray;
import static java.lang.Math.PI;
import static java.lang.Math.toRadians;

/**
 * @author Sam Reid
 */
public class Scenes {
    public static F<ShapeValue, PNode> imageNode( final Image image ) {
        return new F<ShapeValue, PNode>() {
            @Override public PNode f( final ShapeValue graphic ) {
                return new PImage( getRotatedImage( toBufferedImage( image ), -PI / 2 + graphic.getNumRotations() * PI / 2 ) );
            }
        };
    }

    private static final F<ShapeValue, PNode> smile = imageNode( new PadBoundsNode( createFaceNode() ).toImage() );

    public static FaceNode createFaceNode() {return new FaceNode( 60, HEAD_PAINT, new BasicStroke( 1 ), lightGray, EYE_PAINT, MOUTH_PAINT );}

    private static final F<ShapeValue, PNode> triangle = imageNode( new ShapeIcon().toImage() );

    //copied from fractions
    private static Shape triangle( final double length, final Vector2D tip, final Vector2D direction ) {
        return new DoubleGeneralPath() {{
            moveTo( tip.toPoint2D() );
            lineTo( tip.plus( direction.getRotatedInstance( toRadians( 90 + 60 ) ).times( length ) ).toPoint2D() );
            lineTo( tip.plus( direction.getRotatedInstance( toRadians( -90 - 60 ) ).times( length ) ).toPoint2D() );
            lineTo( tip.toPoint2D() );
        }}.getGeneralPath();
    }

    @SuppressWarnings("unchecked") public static final List<F<IntroCanvas, Scene>> introScenes = List.<F<IntroCanvas, Scene>>list(

            //Level 1: Apply a single function
            new F<IntroCanvas, Scene>() {
                @Override public Scene f( final IntroCanvas introCanvas ) {
                    return new Scene( introCanvas, toStack( 3, new F<Unit, ValueNode>() {
                        @Override public ValueNode f( final Unit unit ) {
                            return new ValueNode( introCanvas, new ShapeValue( 0, smile ), new BasicStroke( 1 ), Color.white, Color.black, Color.black );
                        }
                    } ),
                                      List.list( new UnaryFunctionNode( new PImage( BufferedImageUtils.multiScaleToWidth( ROTATE_RIGHT, 60 ) ), false, ROTATE_GRAPHIC_RIGHT, SHAPE, SHAPE, 390.72378138847836, 294.298375184638 ) ),
                                      Scene.createTargetNodeList( introCanvas, list( new ShapeValue( 1, smile ) ) ) );
                }
            },

            //Level 2: Apply same function twice
            new F<IntroCanvas, Scene>() {
                @Override public Scene f( final IntroCanvas introCanvas ) {
                    return new Scene( introCanvas, toStack( 3, new F<Unit, ValueNode>() {
                        @Override public ValueNode f( final Unit unit ) {
                            return new ValueNode( introCanvas, new ShapeValue( -1, smile ), new BasicStroke( 1 ), Color.white, Color.black, Color.black );
                        }
                    } ),
                                      List.list( new UnaryFunctionNode( new PImage( BufferedImageUtils.multiScaleToWidth( ROTATE_RIGHT, 60 ) ), false, ROTATE_GRAPHIC_RIGHT, SHAPE, SHAPE, 390.72378138847836, 294.298375184638 ) ),
                                      Scene.createTargetNodeList( introCanvas, list( new ShapeValue( 1, smile ) ) ) );
                }
            },

            //Level 3: Apply different functions, one after the other
            new F<IntroCanvas, Scene>() {
                @Override public Scene f( final IntroCanvas introCanvas ) {
                    return new Scene( introCanvas, toStack( 3, new F<Unit, ValueNode>() {
                        @Override public ValueNode f( final Unit unit ) {
                            return new ValueNode( introCanvas, new ShapeValue( -1, triangle ), new BasicStroke( 1 ), Color.white, Color.black, Color.black );
                        }
                    } ),
                                      List.list( new UnaryFunctionNode( new PImage( BufferedImageUtils.multiScaleToWidth( ROTATE_RIGHT, 60 ) ), false, ROTATE_GRAPHIC_RIGHT, SHAPE, SHAPE, 390.72378138847836, 294.298375184638 ) ),
                                      Scene.createTargetNodeList( introCanvas, list( new ShapeValue( 1, triangle ) ) ) );
                }
            },


            //First string level
            new F<IntroCanvas, Scene>() {
                @Override public Scene f( final IntroCanvas canvas ) {
                    return new Scene( canvas, toStack( 4, new F<Unit, ValueNode>() {
                        @Override public ValueNode f( final Unit unit ) {
                            return new ValueNode( canvas, "hello", new BasicStroke( 1 ), Color.white, Color.black, Color.black );
                        }
                    } ),
                                      list( new UnaryFunctionNode( new PImage( BufferedImageUtils.multiScaleToWidth( Images.LEFT_RIGHT_ARROW, 80 ) ), false, STRING_REVERSE, TEXT, TEXT, 390.72378138847836, 294.298375184638 - 80 ),
                                            new UnaryFunctionNode( new PImage( BufferedImageUtils.multiScaleToWidth( Images.COPY, 50 ) ), false, STRING_DOUBLE, TEXT, TEXT, 390.72378138847836, 444.298375184638 - 80 )
                                      ),
                                      Scene.createTargetNodeList( canvas, list( "olleh", "hellohello" ) )
                    );
                }
            },

            //Apply different functions in different orders to achieve 2 goals.

            new F<IntroCanvas, Scene>() {
                @Override public Scene f( final IntroCanvas canvas ) {
                    return new Scene( canvas, toStack( 4, new F<Unit, ValueNode>() {
                        @Override public ValueNode f( final Unit unit ) {
                            return new ValueNode( canvas, 3, new BasicStroke( 1 ), Color.white, Color.black, Color.black );
                        }
                    } ),
                                      list( new UnaryFunctionNode( "\u27152", false, INTEGER_TIMES_2, NUMBER, NUMBER, 390.72378138847836, 294.298375184638 - 80 ),
                                            new UnaryFunctionNode( "+1", false, INTEGER_PLUS_1, NUMBER, NUMBER, 390.72378138847836, 444.298375184638 - 80 )
                                      ),
                                      Scene.createTargetNodeList( canvas, list( 4, 6, 8 ) )
                    );
                }
            },
            new F<IntroCanvas, Scene>() {
                @Override public Scene f( final IntroCanvas canvas ) {
                    return new Scene( canvas, toStack( 4, new F<Unit, ValueNode>() {
                        @Override public ValueNode f( final Unit unit ) {
                            return new ValueNode( canvas, 2, new BasicStroke( 1 ), Color.white, Color.black, Color.black );
                        }
                    } ),
                                      list( new UnaryFunctionNode( "^2", false, INTEGER_POWER_2, NUMBER, NUMBER, 390.72378138847836, 294.298375184638 - 80 ),
                                            new UnaryFunctionNode( "-1", false, INTEGER_MINUS_1, NUMBER, NUMBER, 390.72378138847836, 444.298375184638 - 80 )
                                      ),
                                      Scene.createTargetNodeList( canvas, list( 4, 9, 16 ) )
                    );
                }
            }
    );

    @SuppressWarnings("unchecked") public static final List<F<IntroCanvas, Scene>> binaryScenes = List.<F<IntroCanvas, Scene>>list(

            //Level 1: Apply a single function
            new F<IntroCanvas, Scene>() {
                @Override public Scene f( final IntroCanvas introCanvas ) {
                    return new Scene( introCanvas, toStack( 3, new F<Unit, ValueNode>() {
                        @Override public ValueNode f( final Unit unit ) {
                            return new ValueNode( introCanvas, new ShapeValue( 0, smile ), new BasicStroke( 1 ), Color.white, Color.black, Color.black );
                        }
                    } ),
                                      List.<UnaryFunctionNode>list(),
                                      list( new TwoInputFunctionNode( "+", false, SHAPE, 418.8478581979311, 224.9926144756274 ) ),
                                      Scene.createTargetNodeList( introCanvas, list( new ShapeValue( 1, smile ) ) ) );
                }
            }
    );

}