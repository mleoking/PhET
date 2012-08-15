// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.functions.FunctionsResources.Images;
import edu.colorado.phet.functions.buildafunction.UnaryNumberFunctionNode;
import edu.colorado.phet.functions.buildafunction.ValueNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
class Scene1 extends Scene {

    private final UnaryNumberFunctionNode functionNode;
    private ValueNode valueNode;
    private final ValueNode targetNode;
    private IntroCanvas introCanvas;

    public static final F<Object, Object> rotateRight = new F<Object, Object>() {
        @Override public Object f( final Object o ) {
            Key key = (Key) o;
            return key.rotateRight();
        }
    };

    Scene1( final IntroCanvas introCanvas ) {
        this.introCanvas = introCanvas;
        targetNode = new ValueNode( introCanvas, new Key( 1 ),
                                    new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10, 10 }, 0 ), new Color( 0, 0, 0, 0 ), Color.gray, Color.black ) {{
            setOffset( 903.9881831610056, 318.4047267355978 );
        }};
        addChild( targetNode );

        for ( int i = 0; i < 3; i++ ) {
            final int finalI = i;
            valueNode = new ValueNode( introCanvas, new Key( 0 ), new BasicStroke( 1 ), Color.white, Color.black, Color.black ) {{
                setOffset( 84.37223042836038 + finalI * 3, 315.3914327917278 - finalI * 3 );
            }};
            addChild( valueNode );
        }

        functionNode = new UnaryNumberFunctionNode( new PImage( BufferedImageUtils.multiScaleToWidth( Images.ROTATE_RIGHT, 50 ) ), false ) {{
            setOffset( 390.72378138847836, 294.298375184638 );
        }};
        addChild( functionNode );
    }

    public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
        PBounds valueBounds = valueNode.getGlobalFullBounds();
        PBounds functionBounds = functionNode.getGlobalFullBounds();
        boolean leftBefore = valueNode.getGlobalFullBounds().getCenterX() < functionNode.getGlobalFullBounds().getCenterX();
        double distanceFromOpening = functionBounds.getMinX() - valueBounds.getMaxX();
        PBounds functionBox = new PBounds( functionBounds.getMinX() - 50, functionBounds.getY(), functionBounds.getWidth() + 50, functionBounds.getHeight() );

        //Move through function
        if ( functionBox.intersects( valueBounds ) ) {
            System.out.println( "1" );
            //Move toward opening
            if ( distanceFromOpening > 0 && distanceFromOpening < 50 ) {
                System.out.println( "a" );
                double valueCenterY = valueBounds.getCenterY();
                double functionCenterY = functionBounds.getCenterY();
                double deltaY = functionCenterY - valueCenterY;
                LinearFunction f = new LinearFunction( 0, 50, 1, 0 );
                valueNode.translate( Math.max( delta.width, 0 ), f.evaluate( distanceFromOpening ) * deltaY );
            }

            //Move through
            else if ( valueBounds.getMinX() < functionBounds.getMaxX() && valueBounds.getMaxX() > functionBounds.getMinX() ) {
                System.out.println( "b" );
                double valueCenterY = valueBounds.getCenterY();
                double functionCenterY = functionBounds.getCenterY();
                double deltaY = functionCenterY - valueCenterY;
                valueNode.translate( Math.max( 0, delta.width ), deltaY );
            }
            else {
                System.out.println( "c" );
                valueNode.translate( delta.width, delta.height );
            }
            boolean leftAfter = valueNode.getGlobalFullBounds().getCenterX() < functionNode.getGlobalFullBounds().getCenterX();
            if ( leftBefore && !leftAfter ) {
                valueNode.applyFunction( rotateRight );
            }
        }

        else {
            System.out.println( "2" );
            valueNode.translate( delta.width, delta.height );
        }

//            System.out.println( "LB = " + leftBefore + ", leftAfter = " + leftAfter );
    }

    public void mouseReleased( final ValueNode valueNode ) {
        if ( valueNode.getGlobalFullBounds().intersects( targetNode.getGlobalFullBounds() ) && valueNode.getNumberRotations() % 4 == 1 ) {
            valueNode.setStrokePaint( Color.red );
            valueNode.centerFullBoundsOnPoint( targetNode.getFullBounds().getCenterX(), targetNode.getFullBounds().getCenterY() );
            introCanvas.showNextButton();
        }
    }
}