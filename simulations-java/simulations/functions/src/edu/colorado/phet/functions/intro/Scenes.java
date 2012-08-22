// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import fj.F;
import fj.Unit;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.functions.FunctionsResources.Images;
import edu.colorado.phet.functions.buildafunction.UnaryFunctionNode;
import edu.colorado.phet.functions.buildafunction.ValueNode;
import edu.colorado.phet.functions.model.Functions;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.functions.model.Functions.*;
import static fj.data.List.list;

/**
 * @author Sam Reid
 */
public class Scenes {
    public static final F<IntroCanvas, Scene> level1 = new F<IntroCanvas, Scene>() {
        @Override public Scene f( final IntroCanvas introCanvas ) {
            return new Scene( introCanvas, Scene.toStack( 3, new F<Unit, ValueNode>() {
                @Override public ValueNode f( final Unit unit ) {
                    return new ValueNode( introCanvas, new Graphic( 0 ), new BasicStroke( 1 ), Color.white, Color.black, Color.black );
                }
            } ),
                              List.list( new UnaryFunctionNode( new PImage( BufferedImageUtils.multiScaleToWidth( Images.ROTATE_RIGHT, 60 ) ), false, Functions.ROTATE_GRAPHIC_RIGHT, 390.72378138847836, 294.298375184638 ) ),
                              Scene.createTargetNodeList( introCanvas, list( new Graphic( 1 ) ) ) );
        }
    };
    public static final F<IntroCanvas, Scene> level2 = new F<IntroCanvas, Scene>() {
        @Override public Scene f( final IntroCanvas introCanvas ) {
            return new Scene( introCanvas, Scene.toStack( 3, new F<Unit, ValueNode>() {
                @Override public ValueNode f( final Unit unit ) {
                    return new ValueNode( introCanvas, new Graphic( 0 ), new BasicStroke( 1 ), Color.white, Color.black, Color.black );
                }
            } ),
                              List.list( new UnaryFunctionNode( new PImage( BufferedImageUtils.multiScaleToWidth( Images.ROTATE_RIGHT, 60 ) ), false, Functions.ROTATE_GRAPHIC_RIGHT, 390.72378138847836, 294.298375184638 ) ),
                              Scene.createTargetNodeList( introCanvas, list( new Graphic( 1 ) ) ) );
        }
    };
    public static final F<IntroCanvas, Scene> level3 = new F<IntroCanvas, Scene>() {
        @Override public Scene f( final IntroCanvas canvas ) {
            return new Scene( canvas, Scene.toStack( 4, new F<Unit, ValueNode>() {
                @Override public ValueNode f( final Unit unit ) {
                    return new ValueNode( canvas, 3, new BasicStroke( 1 ), Color.white, Color.black, Color.black );
                }
            } ),
                              list( new UnaryFunctionNode( "\u27152", false, INTEGER_TIMES_2, 390.72378138847836, 294.298375184638 - 80 ),
                                    new UnaryFunctionNode( "+1", false, INTEGER_PLUS_1, 390.72378138847836, 444.298375184638 - 80 )
                              ),
                              Scene.createTargetNodeList( canvas, list( 4, 6, 8 ) )
            );
        }
    };

    public static final F<IntroCanvas, Scene> level4 = new F<IntroCanvas, Scene>() {
        @Override public Scene f( final IntroCanvas canvas ) {
            return new Scene( canvas, Scene.toStack( 4, new F<Unit, ValueNode>() {
                @Override public ValueNode f( final Unit unit ) {
                    return new ValueNode( canvas, 2, new BasicStroke( 1 ), Color.white, Color.black, Color.black );
                }
            } ),
                              list( new UnaryFunctionNode( "^2", false, INTEGER_POWER_2, 390.72378138847836, 294.298375184638 - 80 ),
                                    new UnaryFunctionNode( "-1", false, INTEGER_MINUS_1, 390.72378138847836, 444.298375184638 - 80 )
                              ),
                              Scene.createTargetNodeList( canvas, list( 4, 6, 8 ) )
            );
        }
    };

    @SuppressWarnings("unchecked") public static final List<F<IntroCanvas, Scene>> scenes = List.<F<IntroCanvas, Scene>>list( level1, level2, level3, level4 );
}