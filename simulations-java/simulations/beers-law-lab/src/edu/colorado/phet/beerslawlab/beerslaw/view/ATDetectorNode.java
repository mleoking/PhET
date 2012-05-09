// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.beerslawlab.beerslaw.model.ATDetector;
import edu.colorado.phet.beerslawlab.beerslaw.model.ATDetector.ATDetectorMode;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Images;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.ParameterKeys;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.view.DebugOriginNode;
import edu.colorado.phet.beerslawlab.common.view.MovableDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.HorizontalTiledNode;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.simsharing.NonInteractiveEventHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Detector for absorbance (A) and transmittance (T).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ATDetectorNode extends PhetPNode {

    public ATDetectorNode( ATDetector detector, ModelViewTransform mvt ) {

        // nodes
        PNode bodyNode = new BodyNode( detector, mvt );
        PNode probeNode = new ProbeNode( detector, mvt );
        PNode wireNode = new WireNode( bodyNode, probeNode );

        // rendering order
        addChild( wireNode );
        addChild( bodyNode );
        addChild( probeNode );

        //NOTE: layout is handled by child nodes observing model elements.
    }

    // The body of the detector, where A and T values are displayed.
    private static class BodyNode extends PNode {

        private static final double BUTTONS_X_MARGIN = 20;  // specific to image files
        private static final double BUTTONS_Y_OFFSET = 17;  // specific to image files
        private static final double VALUE_X_MARGIN = 30; // specific to image files
        private static final double VALUE_Y_OFFSET = 87; // specific to image files
        private static final DecimalFormat ABSORBANCE_FORMAT = new DecimalFormat( "0.00" );
        private static final DecimalFormat TRANSMITTANCE_FORMAT = new DecimalFormat( "0.00" );
        private static final String NO_VALUE = "-";

        public BodyNode( final ATDetector detector, final ModelViewTransform mvt ) {

            // buttons for changing the detector "mode"
            PNode transmittanceButton = new ModeButton( UserComponents.transmittanceRadioButton, Strings.TRANSMITTANCE, detector.mode, ATDetectorMode.TRANSMITTANCE );
            PNode absorbanceButton = new ModeButton( UserComponents.absorbanceRadioButton, Strings.ABSORBANCE, detector.mode, ATDetectorMode.ABSORBANCE );

            // group the radio buttons
            PNode buttonsNode = new PNode();
            buttonsNode.addChild( transmittanceButton );
            buttonsNode.addChild( absorbanceButton );
            absorbanceButton.setOffset( transmittanceButton.getXOffset(),
                                        transmittanceButton.getFullBoundsReference().getMaxY() + 1 );

            // value display
            final PText valueNode = new PText( NO_VALUE );
            valueNode.setFont( new PhetFont( 24 ) );

            // background image, sized to fit
            double bodyWidth = buttonsNode.getFullBoundsReference().getWidth() + ( 2 * BUTTONS_X_MARGIN );
            final PNode backgroundNode = new HorizontalTiledNode( bodyWidth, Images.AT_DETECTOR_BODY_LEFT, Images.AT_DETECTOR_BODY_CENTER, Images.AT_DETECTOR_BODY_RIGHT );

            // rendering order
            addChild( backgroundNode );
            addChild( buttonsNode );
            addChild( valueNode );

            // layout
            buttonsNode.setOffset( BUTTONS_X_MARGIN, BUTTONS_Y_OFFSET );
            valueNode.setOffset( VALUE_X_MARGIN, VALUE_Y_OFFSET );

            // body location
            detector.body.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D location ) {
                    setOffset( mvt.modelToView( location.toPoint2D() ) );
                }
            } );

            // update the value display
            RichSimpleObserver observer = new RichSimpleObserver() {
                public void update() {
                    Double value = detector.value.get();
                    if ( value == null ) {
                        valueNode.setText( NO_VALUE );
                        // centered
                        valueNode.setOffset( backgroundNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 ),
                                             valueNode.getYOffset() );
                    }
                    else {
                        String text;
                        if ( detector.mode.get() == ATDetectorMode.TRANSMITTANCE ) {
                            text = MessageFormat.format( Strings.PATTERN_0PERCENT, TRANSMITTANCE_FORMAT.format( value ) );
                        }
                        else {
                            text = ABSORBANCE_FORMAT.format( value );
                        }
                        valueNode.setText( text );
                        // right justified
                        valueNode.setOffset( backgroundNode.getFullBoundsReference().getMaxX() - valueNode.getFullBoundsReference().getWidth() - VALUE_X_MARGIN,
                                             valueNode.getYOffset() );
                    }
                }
            };
            observer.observe( detector.value, detector.mode );

            addInputEventListener( new NonInteractiveEventHandler( UserComponents.detectorBody ) );
        }
    }

    // The probe, whose position indicates where the measurement is being made.
    private static class ProbeNode extends PNode {

        private static final double PROBE_CENTER_Y_OFFSET = 55; // specific to image file

        public ProbeNode( final ATDetector detector, final ModelViewTransform mvt ) {

            PImage imageNode = new PImage( Images.AT_DETECTOR_PROBE );
            addChild( imageNode );
            imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, -PROBE_CENTER_Y_OFFSET );

            // show origin and vertical "diameter" line, for debugging PROBE_CENTER_OFFSET
            PNode originNode = new DebugOriginNode();
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                addChild( originNode );
                addChild( new PPath() {{
                    setStrokePaint( Color.RED );
                    double viewDiameter = mvt.modelToViewY( detector.probe.sensorDiameter );
                    setPathTo( new Line2D.Double( 0, -viewDiameter / 2, 0, viewDiameter / 2 ) );
                }} );
            }

            // probe location
            detector.probe.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D location ) {
                    setOffset( mvt.modelToView( location.toPoint2D() ) );
                }
            } );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new MovableDragHandler( UserComponents.detectorProbe, detector.probe, this, mvt ) {
                @Override public ParameterSet getParametersForAllEvents( PInputEvent event ) {
                    return super.getParametersForAllEvents( event ).with( ParameterKeys.inBeam, detector.probeInBeam() );
                }
            } );
        }
    }

    // Wire that connects the probe to the body of the detector.
    public class WireNode extends PPath {
        public WireNode( final PNode bodyNode, final PNode probeNode ) {
            setStroke( new BasicStroke( 8, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1f ) );
            setStrokePaint( Color.GRAY );

            // Update when bounds of the body or probe change
            final PropertyChangeListener listener = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {

                    // connect to left center of body
                    final double bodyConnectionX = bodyNode.getFullBoundsReference().getMinX();
                    final double bodyConnectionY = bodyNode.getFullBounds().getCenterY();

                    // connect to bottom center of probe
                    final double probeConnectionX = probeNode.getFullBoundsReference().getCenterX();
                    final double probeConnectionY = probeNode.getFullBoundsReference().getMaxY();

                    // cubic curve
                    final double controlPointOffset = 60;
                    setPathTo( new CubicCurve2D.Double( bodyConnectionX, bodyConnectionY,
                                                        bodyConnectionX - controlPointOffset, bodyConnectionY,
                                                        probeConnectionX, probeConnectionY + controlPointOffset,
                                                        probeConnectionX, probeConnectionY ) );
                }
            };
            probeNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
            bodyNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
        }
    }

    // Radio button for changing modes
    private static class ModeButton extends PSwing {
        public ModeButton( IUserComponent userComponent, String text, Property<ATDetectorMode> mode, ATDetectorMode value ) {
            super( new PropertyRadioButton<ATDetectorMode>( userComponent, text, mode, value ) {{
                setOpaque( false );
                setForeground( Color.WHITE );
                setFont( new PhetFont( Font.BOLD, BLLConstants.CONTROL_FONT_SIZE ) );
            }} );
        }
    }
}
