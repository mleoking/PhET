// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.slider;

import java.awt.Paint;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.PFrame;

import static edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode.DEFAULT_TRACK_LENGTH;
import static edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode.DEFAULT_TRACK_THICKNESS;

/**
 * Horizontal slider that uses only Piccolo, i.e. there is no Swing involved.
 * Implementation note: This uses composition on a rotated VSliderNode for its implementation so there is no duplication of code.
 *
 * @author Sam Reid
 */
public class HSliderNode extends SliderNode {

    private final VSliderNode node;
    public final double trackLength;

    public HSliderNode( final IUserComponent userComponent, final double min, final double max, final SettableProperty<Double> value ) {
        this( userComponent, min, max, value, new Property<Boolean>( true ) );
    }

    public HSliderNode( final IUserComponent userComponent, final double min, final double max, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        this( userComponent, min, max, DEFAULT_TRACK_THICKNESS, DEFAULT_TRACK_LENGTH, value, enabled );
    }

    public HSliderNode( final IUserComponent userComponent, final double min, final double max, double trackThickness, double trackLength, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        super( userComponent, min, max, value );
        this.trackLength = trackLength;

        //Create the vertical node that will be rotated by 90 degrees and delegated to
        node = new VSliderNode( userComponent, min, max, trackThickness, trackLength, value, enabled ) {

            protected Point2D.Double createEndPoint() {
                return new Point2D.Double( trackNode.getFullBounds().getWidth(), 0 );
            }

            {
                rotate( Math.PI / 2 );
            }
        };
        addChild( new ZeroOffsetNode( node ) );
    }

    @Override public void setTrackFillPaint( final Paint paint ) {
        node.setTrackFillPaint( paint );
    }

    //Adds a label to the slider node
    public void addLabel( double value, PNode x ) {
        //Wrap in a zero offset node for rotating the label
        //Wrap in another layer for setting the offset independently of the rotation
        final ZeroOffsetNode label = new ZeroOffsetNode( new ZeroOffsetNode( x ) {{
            rotate( -Math.PI / 2 );
        }} );
        node.rootNode.addChild( label );
        label.setOffset( node.knobNode.getFullBounds().getWidth() / 2 + node.trackThickness / 2, node.getViewY( value ) - label.getFullBounds().getHeight() / 2 );
        node.adjustOrigin();
    }

    public static void main( String[] args ) {

        //Init sim sharing manager so it doesn't exception when trying to record messages
        String[] myArgs = { "-study" };
        SimSharingManager.init( new PhetApplicationConfig( myArgs, "myProject" ) );
//        PDebug.debugBounds = true;

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PFrame( "test", false, new PCanvas() {{

                    //Emulate sim environment as closely as possible for debugging.
                    setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
                    setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
                    setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );

                    Property<Double> sliderValue = new Property<Double>( 0.0 ) {{
                        addObserver( new VoidFunction1<Double>() {
                            public void apply( Double newValue ) {
                                System.out.println( "sliderValue = " + newValue );
                            }
                        } );
                    }};

                    final HSliderNode sliderNode = new HSliderNode( new UserComponent( "mySlider" ), 0, 100, DEFAULT_TRACK_THICKNESS, DEFAULT_TRACK_LENGTH, sliderValue, new Property<Boolean>( true ) ) {{
                        addLabel( 0.0, new PhetPText( "None" ) );
                        addLabel( 100.0, new PhetPText( "Lots" ) );
                        setOffset( 150, 250 );
                    }};
                    getLayer().addChild( sliderNode );

                    setPanEventHandler( null );
                }} ) {{
                    setSize( 800, 600 );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }
}