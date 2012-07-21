// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.buildafunction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.functions.buildafunction.DraggableToken.Listener;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class FunctionMirrorNode extends PNode {
    private final ArrayList<Vector2D> points = new ArrayList<Vector2D>();
    private final PhetPPath linePath;
    private final DraggableToken token;

    public FunctionMirrorNode( final DraggableToken token ) {
        this.token = token;
        linePath = new PhetPPath( new BasicStroke( 1 ), Color.black );
        addChild( linePath );

        //X is the input variable the user is dragging through.  This node is a smaller mirror of the same thing.
        final DraggableToken copy = new DraggableToken( token.value, null );
        token.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( final PropertyChangeEvent evt ) {
                final Vector2D v = Vector2D.v( token.getXOffset() + token.getFullBounds().getWidth() / 2, token.getYOffset() + token.getFullBounds().getHeight() / 2 );
                points.add( v );
                if ( points.size() == 1 ) {
                    linePath.moveTo( (float) v.x, (float) v.y );
                }
                else {
                    linePath.lineTo( (float) v.x, (float) v.y );
                }
                copy.setOffset( token.getXOffset(), token.getYOffset() );
            }
        } );
        addChild( copy );

        token.addListener( new Listener() {
            public void functionApplied( final DraggableToken t, final FunctionBoxWithText function ) {
                FunctionBoxWithText a = new FunctionBoxWithText( function.getText() ) {
                    {
                        setOffset( function.getXOffset(), function.getYOffset() );
                    }

                    @Override public Object evaluate( final Object v ) {
                        return v;
                    }
                };
                addChild( a );
            }
        } );
    }

    public Object evaluate( final Object v ) {
        return token.evaluateAsFunction( v );
    }
}