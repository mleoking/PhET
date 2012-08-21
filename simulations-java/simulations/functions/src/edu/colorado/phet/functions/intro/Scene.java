package edu.colorado.phet.functions.intro;

import fj.F;
import fj.Unit;
import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.functionaljava.FJUtils;
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

    public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
        UnaryFunctionNode functionNode = unaryFunctionNodeList.sort( FJUtils.ord( new F<UnaryFunctionNode, Double>() {
            @Override public Double f( final UnaryFunctionNode n ) {
                return n.getGlobalFullBounds().getCenter2D().distance( valueNode.getGlobalFullBounds().getCenter2D() );
            }
        } ) ).head();
        PBounds valueBounds = valueNode.getGlobalFullBounds();
        PBounds functionBounds = functionNode.getGlobalFullBounds();
        boolean leftBefore = valueNode.getGlobalFullBounds().getCenterX() < functionNode.getGlobalFullBounds().getCenterX();
        double distanceFromOpening = functionBounds.getMinX() - valueBounds.getMaxX();
        PBounds functionBox = new PBounds( functionBounds.getMinX() - 50, functionBounds.getY(), functionBounds.getWidth() + 50, functionBounds.getHeight() );

        //Move through function
        if ( functionBox.intersects( valueBounds ) ) {
            //Move toward opening
            if ( distanceFromOpening > 0 && distanceFromOpening < 50 ) {
                double valueCenterY = valueBounds.getCenterY();
                double functionCenterY = functionBounds.getCenterY();
                double deltaY = functionCenterY - valueCenterY;
                LinearFunction f = new LinearFunction( 0, 50, 1, 0 );
                valueNode.translate( Math.max( delta.width, 0 ), f.evaluate( distanceFromOpening ) * deltaY );
            }

            //Move through
            else if ( valueBounds.getMinX() < functionBounds.getMaxX() && valueBounds.getMaxX() > functionBounds.getMinX() ) {
                double valueCenterY = valueBounds.getCenterY();
                double functionCenterY = functionBounds.getCenterY();
                double deltaY = functionCenterY - valueCenterY;
                valueNode.translate( Math.max( 0, delta.width ), deltaY );
            }
            else {
                valueNode.translate( delta.width, delta.height );
            }
            boolean leftAfter = valueNode.getGlobalFullBounds().getCenterX() < functionNode.getGlobalFullBounds().getCenterX();
            if ( leftBefore && !leftAfter ) {
                valueNode.applyFunction( functionNode.function );
            }
        }

        else {
            valueNode.translate( delta.width, delta.height );
        }
    }

    public void mouseReleased( final ValueNode valueNode ) {
        //find the nearest target node, and see if the value matches
        ValueNode targetNode = targetNodes.sort( FJUtils.ord( new F<ValueNode, Double>() {
            @Override public Double f( final ValueNode t ) {
                return t.getGlobalFullBounds().getCenter2D().distance( valueNode.getGlobalFullBounds().getCenter2D() );
            }
        } ) ).head();
        if ( valueNode.getGlobalFullBounds().intersects( targetNode.getGlobalFullBounds() ) && valueNode.getCurrentValue().equals( targetNode.getCurrentValue() ) ) {
            valueNode.setStrokePaint( Color.red );
            valueNode.centerFullBoundsOnPoint( targetNode.getFullBounds().getCenterX(), targetNode.getFullBounds().getCenterY() );
            sceneContext.showNextButton();
        }
    }

    public static List<ValueNode> toStack( int stackSize, final F<Unit, ValueNode> f ) {
        ArrayList<ValueNode> valueNodes = new ArrayList<ValueNode>();
        for ( int i = 0; i < stackSize; i++ ) {
            ValueNode valueNode = f.f( Unit.unit() );
            valueNode.setOffset( 84.37223042836038 + i * 3, 315.3914327917278 - i * 3 );
            valueNodes.add( valueNode );
        }
        return List.iterableList( valueNodes );
    }
}