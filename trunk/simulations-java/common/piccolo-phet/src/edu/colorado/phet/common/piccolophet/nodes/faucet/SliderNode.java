// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.faucet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.PFrame;

/**
 * Rewrite for SliderNode, should work at different orientations and support tick labels, etc.
 * See #1767
 *
 * @author Sam Reid
 */
public class SliderNode extends PNode {
    public SliderNode( final double min, final SettableProperty<Double> value, final double max ) {
        final int trackHeight = 3;
        final Rectangle2D.Double trackPath = new Rectangle2D.Double( 0, 0, 200, trackHeight );
        final PhetPPath trackNode = new PhetPPath( trackPath, new BasicStroke( 1 ), Color.black );
        addChild( trackNode );
        addChild( new KnobNode( new KnobNode.ColorScheme( new Color( 115, 217, 255 ) ) ) {{
//        addChild( new PhetPPath( new Ellipse2D.Double( 0, 0, 20, 20 ), Color.blue ) {{
            value.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    double viewX = new Function.LinearFunction( min, max, trackNode.getFullBounds().getMinX(), trackNode.getFullBounds().getWidth() ).evaluate( value );
                    setOffset( viewX - getFullBounds().getWidth() / 2, trackNode.getFullBounds().getHeight() / 2 - getFullBounds().getHeight() / 2 );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {

                private Point2D startPoint;
                public Double startValue;

                @Override public void mousePressed( PInputEvent event ) {
                    super.mousePressed( event );
                    startPoint = event.getPositionRelativeTo( SliderNode.this.getParent() );
                    startValue = value.get();
                }

                @Override public void mouseDragged( PInputEvent event ) {
                    Point2D point = event.getPositionRelativeTo( SliderNode.this.getParent() );
                    final ImmutableVector2D vector = new ImmutableVector2D( startPoint, point );

                    Point2D leftGlobal = trackNode.localToGlobal( new Point2D.Double( 0, 0 ) );
                    Point2D rightGlobal = trackNode.localToGlobal( new Point2D.Double( trackNode.getFullBounds().getWidth(), 0 ) );
                    final ImmutableVector2D unitVector = new ImmutableVector2D( leftGlobal, rightGlobal ).getNormalizedInstance();
                    double viewDelta = vector.dot( unitVector );

                    double modelDelta = ( max - min ) / trackNode.getFullBounds().getWidth() * viewDelta;
                    value.set( MathUtil.clamp( min, startValue + modelDelta, max ) );
                }
            } );
        }} );
    }

    public static void main( String[] args ) {
        new PFrame( "test", false, new PCanvas() {{
            getLayer().addChild( new SliderNode( 0, new Property<Double>( 0.0 ), 100 ) {{
                setOffset( 100, 100 );
//                rotate( Math.PI / 6 );
            }} );
            setPanEventHandler( null );
        }} ) {{
            setSize( 800, 600 );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }}.setVisible( true );
    }
}