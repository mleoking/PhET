package edu.colorado.phet.functions.intro;

import fj.data.List;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.functions.buildafunction.UnaryFunctionNode;
import edu.colorado.phet.functions.buildafunction.ValueContext;
import edu.colorado.phet.functions.buildafunction.ValueNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public abstract class Scene extends PNode implements ValueContext {
    private final List<UnaryFunctionNode> unaryFunctionNodeList;
    private final List<ValueNode> targetNodes;
    private final SceneContext sceneContext;

    protected Scene( List<ValueNode> valueNodes, List<UnaryFunctionNode> unaryFunctionNodeList, List<ValueNode> targetNodes, SceneContext sceneContext ) {
        this.unaryFunctionNodeList = unaryFunctionNodeList;
        this.targetNodes = targetNodes;
        this.sceneContext = sceneContext;

        for ( ValueNode targetNode : targetNodes ) {
            addChild( targetNode );
        }
        for ( ValueNode valueNode : valueNodes ) {
            addChild( valueNode );
        }
        for ( UnaryFunctionNode unaryFunctionNode : unaryFunctionNodeList ) {
            addChild( unaryFunctionNode );
        }
    }

    @Override public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
        UnaryFunctionNode functionNode = unaryFunctionNodeList.head();
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
                valueNode.applyFunction( functionNode.function );
            }
        }

        else {
            System.out.println( "2" );
            valueNode.translate( delta.width, delta.height );
        }
    }

    @Override public void mouseReleased( final ValueNode valueNode ) {
        ValueNode targetNode = targetNodes.head();
        if ( valueNode.getGlobalFullBounds().intersects( targetNode.getGlobalFullBounds() ) && valueNode.getNumberRotations() % 4 == 1 ) {
            valueNode.setStrokePaint( Color.red );
            valueNode.centerFullBoundsOnPoint( targetNode.getFullBounds().getCenterX(), targetNode.getFullBounds().getCenterY() );
            sceneContext.showNextButton();
        }
    }
}