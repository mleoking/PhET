// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.beerslawlab.beerslaw.model.ATDetector;
import edu.colorado.phet.beerslawlab.beerslaw.model.ATDetector.ATDetectorMode;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Images;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.view.DebugOriginNode;
import edu.colorado.phet.beerslawlab.common.view.MovableDragHandler;
import edu.colorado.phet.beerslawlab.common.view.TiledBackgroundNode;
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
public class ATDetectorNode extends PhetPNode {

    public ATDetectorNode( ATDetector detector, ModelViewTransform mvt ) {

        // nodes
        PNode bodyNode = new BodyNode( detector, mvt );
        PNode probeNode = new ProbeNode( detector, mvt );
        PNode wireNode = new WireNode();

        // rendering order
        addChild( wireNode );
        addChild( bodyNode );
        addChild( probeNode );

        //NOTE: layout is handled by child nodes observing model elements.
    }

    private static class BodyNode extends PNode {

        private static final double BUTTONS_X_MARGIN = 20;  // specific to image files
        private static final double BUTTONS_Y_OFFSET = 7;  // specific to image files
        private static final double VALUE_X_MARGIN = 30; // specific to image files
        private static final double VALUE_Y_OFFSET = 67; // specific to image files
        private static final DecimalFormat ABSORBANCE_FORMAT = new DecimalFormat( "0.000" );
        private static final DecimalFormat TRANSMITTANCE_FORMAT = new DecimalFormat( "0.0" );
        private static final String NO_VALUE = "-";

        public BodyNode( final ATDetector detector, final ModelViewTransform mvt ) {

            // buttons for changing the detector "mode"
            PNode transmittanceButton = new ModeButton( UserComponents.transmittanceRadioButton, Strings.PERCENT_TRANSMITTANCE, detector.mode, ATDetectorMode.TRANSMITTANCE );
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
            final PNode backgroundNode = new TiledBackgroundNode( bodyWidth, Images.AT_DETECTOR_BODY_LEFT, Images.AT_DETECTOR_BODY_CENTER, Images.AT_DETECTOR_BODY_RIGHT );

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
                        NumberFormat format = ( detector.mode.get() == ATDetectorMode.TRANSMITTANCE ) ? TRANSMITTANCE_FORMAT : ABSORBANCE_FORMAT;
                        valueNode.setText( format.format( value ) );
                        // right justified
                        valueNode.setOffset( backgroundNode.getFullBoundsReference().getMaxX() - valueNode.getFullBoundsReference().getWidth() - VALUE_X_MARGIN,
                                             valueNode.getYOffset() );
                    }
                }
            };
            observer.observe( detector.value, detector.mode );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new MovableDragHandler( UserComponents.detectorBody, detector.body, this, mvt ) );
        }
    }

    private static class ProbeNode extends PNode {

        private static final double PROBE_CENTER_Y_OFFSET = 55; // specific to image file

        public ProbeNode( final ATDetector detector, final ModelViewTransform mvt ) {

            PImage imageNode = new PImage( Images.AT_DETECTOR_PROBE );
            addChild( imageNode );
            imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth()/2, -PROBE_CENTER_Y_OFFSET );

            // show origin, for debugging PROBE_CENTER_OFFSET
            PNode originNode = new DebugOriginNode();
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                addChild( originNode );
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
                    return super.getParametersForAllEvents( event ); //TODO add parameter for whether the probe is in the beam
                }
            } );
        }
    }

    // Wire that connects the probe to the body of the detector.
    private static class WireNode extends PPath {

        public WireNode() {

        }
    }

    // Radio button for changing modes
    private static class ModeButton extends PSwing {
        public ModeButton( IUserComponent userComponent, String text, Property<ATDetectorMode> mode, ATDetectorMode value ) {
            super( new PropertyRadioButton<ATDetectorMode>( userComponent, text, mode, value ) {{
                setFont( new PhetFont( BLLConstants.CONTROL_FONT_SIZE ) );
            }} );
        }
    }
}
