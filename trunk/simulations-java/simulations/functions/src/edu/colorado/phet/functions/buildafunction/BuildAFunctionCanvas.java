package edu.colorado.phet.functions.buildafunction;

import fj.F;
import fj.data.List;

import java.awt.Color;

import edu.colorado.phet.functions.buildafunction.DraggableToken.Context;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class BuildAFunctionCanvas extends AbstractFunctionCanvas implements Context {
    public BuildAFunctionCanvas() {
        //Set a really light blue because there is a lot of white everywhere
        setBackground( new Color( 236, 251, 251 ) );
        addChild( new DraggableToken( 2, this ) );
        final DraggableToken x = new DraggableToken( new LinearFunction( 1, 0 ), this );
        addChild( x );

        addChild( new FunctionBoxWithText( "*2" ) {
            {
                setOffset( 200, 200 );
            }

            @Override public Object evaluate( final Object v ) {
                if ( v instanceof Integer ) {
                    return (Integer) v * 2;
                }
                else if ( v instanceof LinearFunction ) {
                    return ( (LinearFunction) v ).times( 2 );
                }
                else {
                    return null;
                }
            }
        } );
        addChild( new FunctionBoxWithText( "+1" ) {
            {
                setOffset( 600, 200 );
            }

            @Override public Object evaluate( final Object v ) {
                if ( v instanceof Integer ) { return (Integer) v + 1; }
                else if ( v instanceof LinearFunction ) {return ( (LinearFunction) v ).plus( 1 );}
                else { return null; }
            }
        } );

        addChild( new FunctionBoxWithText( "Mirror" ) {
            private final FunctionMirrorNode mirror;

            {
                setOffset( ( 200 + 600 ) / 2, 275 + 175 );
                mirror = new FunctionMirrorNode( x ) {{
                    scale( FunctionBox.DEFAULT_WIDTH / STAGE_SIZE.width );
                }};
                addChild( mirror );
            }

            @Override public Object evaluate( final Object v ) {
                return mirror.evaluate( v );
            }
        } );
    }

    public List<FunctionBoxWithText> getFunctionBoxes() {
        List<FunctionBoxWithText> list = List.nil();
        for ( Object child : rootNode.getChildrenReference() ) {
            if ( child instanceof FunctionBoxWithText ) {
                list = list.snoc( (FunctionBoxWithText) child );
            }
        }
        return list;
    }

    public void dragged( final DraggableToken node, final PInputEvent event ) {
        final PDimension delta = event.getDeltaRelativeTo( node.getParent() );

        //Only allow left-right motion within a function
        final List<FunctionBoxWithText> intersected = getFunctionBoxes().filter( new F<FunctionBoxWithText, Boolean>() {
            @Override public Boolean f( final FunctionBoxWithText functionBox ) {
                return functionBox.getGlobalFullBounds().intersects( node.getGlobalFullBounds() );
            }
        } );
        if ( intersected.isNotEmpty() ) {
            //if passed over center line, then apply the function.
            double previousX = node.getGlobalFullBounds().getCenterX();
            node.translate( delta.width, 0 );
            double newX = node.getGlobalFullBounds().getCenterX();
            final FunctionBoxWithText head = intersected.head();
            double line = head.getGlobalFullBounds().getCenterX();
            if ( previousX < line && newX >= line ) {
                node.applyFunction( head );
            }
        }
        else {
            node.translate( delta.width, delta.height );
        }
    }
}