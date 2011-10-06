// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.slider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.PFrame;

/**
 * Horizontal slider that uses only Piccolo, i.e. there is no Swing involved.
 *
 * @author Sam Reid
 */
public class HSliderNode extends SliderNode {
    private PhetPPath trackNode;
    private KnobNode knobNode;

    // Root node to which all other nodes should be added.  This is done to
    // allow the offset of this node to be at (0, 0).  Use this when
    // subclassing if you want to keep the origin at (0, 0).
    protected PNode rootNode = new PNode();

    public HSliderNode( final double min, final double max, final SettableProperty<Double> value ) {
        this( min, max, value, new Property<Boolean>( true ) );
    }

    public HSliderNode( final double min, final double max, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        super( min, max, value, enabled );
        addChild( rootNode );

        final int trackHeight = 5;

        final Rectangle2D.Double trackPath = new Rectangle2D.Double( 0, 0, 200, trackHeight );

        trackNode = new PhetPPath( trackPath, new GradientPaint( 0, trackHeight / 2, Color.white, 0, trackHeight, Color.gray, false ), new BasicStroke( 1 ), new GradientPaint( 0, 0, Color.gray, 0, trackHeight, Color.black, false ) );
        rootNode.addChild( trackNode );
        knobNode = new KnobNode( KnobNode.DEFAULT_SIZE, new KnobNode.ColorScheme( new Color( 115, 217, 255 ) ) ) {{
            enabled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean enabled ) {
                    setEnabled( enabled );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {

                private Point2D startPoint;
                public Double startValue;

                @Override public void mousePressed( PInputEvent event ) {
                    super.mousePressed( event );
                    startPoint = event.getPositionRelativeTo( HSliderNode.this );
                    startValue = value.get();
                }

                @Override public void mouseDragged( PInputEvent event ) {
                    Point2D point = event.getPositionRelativeTo( HSliderNode.this );
                    final ImmutableVector2D vector = new ImmutableVector2D( startPoint, point );

                    final ImmutableVector2D unitVector = new ImmutableVector2D( 1, 0 );
                    double viewDelta = vector.dot( unitVector );

                    double modelDelta = ( max - min ) / trackNode.getFullBounds().getWidth() * viewDelta;
                    value.set( MathUtil.clamp( min, startValue + modelDelta, max ) );
                }
            } );
        }};

        SimpleObserver updateKnobPosition = new SimpleObserver() {
            public void update() {
                double viewX = getViewX( value.get() );
                knobNode.setOffset( viewX - knobNode.getFullBounds().getWidth() / 2,
                                    trackNode.getFullBounds().getCenterY() - knobNode.getFullBounds().getHeight() / 2 );
            }
        };
        value.addObserver( updateKnobPosition );

        //Create an invisible rectangle that will account for where the knob could be
        //This is so that the knob won't overlap other layout elements that are positioned using this node's full bounds
        Rectangle2D leftKnobRect = getKnobRect( min );
        Rectangle2D rightKnobRect = getKnobRect( max );
        PhetPPath knobBackground = new PhetPPath( leftKnobRect.createUnion( rightKnobRect ), null, null, null ) {{
            setPickable( false );
            setChildrenPickable( false );
        }};
        rootNode.addChild( knobBackground );

        //Add the knob itself
        rootNode.addChild( knobNode );

        adjustOrigin();
    }

    private Rectangle2D getKnobRect( double value ) {
        Rectangle2D leftKnobRect = knobNode.getFullBounds();
        final double x = getViewX( value ) - knobNode.getFullBounds().getWidth() / 2;
        final double y = trackNode.getFullBounds().getHeight() / 2 - knobNode.getFullBounds().getHeight() / 2;
        leftKnobRect.setRect( x, y, leftKnobRect.getWidth(), leftKnobRect.getHeight() );
        return leftKnobRect;
    }

    protected double getViewX( double value ) {
        return new Function.LinearFunction( min, max, trackNode.getFullBounds().getMinX(), trackNode.getFullBounds().getMaxX() ).evaluate( value );
    }

    //Add a label to appear under the slider at the specified location
    public void addLabel( double value, PNode label ) {
        label.setOffset( getViewX( value ) - label.getFullBounds().getWidth() / 2, knobNode.getFullBounds().getMaxY() );
        rootNode.addChild( label );

        adjustOrigin();
    }

    /**
     * Adjust the origin of this node so that it is at (0, 0) in screen
     * coordinates.  This will only work if all items have been added to the
     * root node.
     */
    protected void adjustOrigin() {
        removeAllChildren();
        addChild( new ZeroOffsetNode( rootNode ) );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PFrame( "test", false, new PCanvas() {{
                    Property<Double> sliderValue = new Property<Double>( 0.0 );
                    final HSliderNode sliderNode = new HSliderNode( 0, 100, sliderValue ) {{
                        addLabel( 0.0, new PhetPText( "Low" ) );
                        addLabel( 100.0, new PhetPText( "High" ) );
                        setOffset( 150, 250 );
                    }};
                    getLayer().addChild( sliderNode );

                    new Timer( 1000, new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            sliderNode.addLabel( 0.0, new PhetPText( "A much longer label" ) );
                        }
                    } ).start();

                    getLayer().addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 100 ) ) {{
                        setOffset( 150, 250 );
                    }} );

                    getLayer().addChild( new PhetPPath( sliderNode.getFullBounds() ) );
                    setPanEventHandler( null );
                }} ) {{
                    setSize( 800, 600 );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }

}