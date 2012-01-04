// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.beerslawlab.BLLResources.Images;
import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.BLLSimSharing;
import edu.colorado.phet.beerslawlab.BLLSimSharing.Objects;
import edu.colorado.phet.beerslawlab.model.ConcentrationMeter;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Concentration meter, with probe.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationMeterNode extends PhetPNode {

    public ConcentrationMeterNode( ConcentrationMeter meter ) {

        // nodes
        BodyNode bodyNode = new BodyNode( meter );
        ProbeNode probeNode = new ProbeNode( meter );
        WireNode wireNode = new WireNode( meter, new ImmutableVector2D( bodyNode.getFullBoundsReference().getWidth() / 2, bodyNode.getFullBoundsReference().getHeight() / 2 ) );

        // rendering order
        addChild( wireNode );
        addChild( bodyNode );
        addChild( probeNode );

        //NOTE: layout is handled by child nodes observing model elements.
    }

    // Meter body, origin at upper left.
    private static class BodyNode extends PNode {

        private static final String VALUE_PATTERN = "0.00000";
        private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( VALUE_PATTERN );
        private static final String NO_VALUE = "-";

        // image-specific locations and dimensions
        private static final double TITLE_Y_OFFSET = 10;
        private static final PBounds VALUE_BOUNDS = new PBounds( 24, 45, 114, 50 );
        private static final double BEVEL_WIDTH = 12;

        public BodyNode( final ConcentrationMeter meter ) {

            // nodes
            PImage imageNode = new PImage( Images.CONCENTRATION_METER_BODY );
            PText titleNode = new PText( Strings.CONCENTRATION ) {{
                setTextPaint( Color.WHITE );
                setFont( new PhetFont( Font.BOLD, 18 ) );
            }};
            final PText valueNode = new PText( createDisplayText( VALUE_PATTERN ) ) {{
                setFont( new PhetFont( 18 ) );
            }};

            // scale title and value to fit in meter
            final double titleScale = ( imageNode.getFullBoundsReference().getWidth() - 2 * BEVEL_WIDTH ) / titleNode.getFullBoundsReference().getWidth();
            if ( titleScale < 1 ) {
                titleNode.setScale( titleScale );
            }
            final double valueScale = VALUE_BOUNDS.getWidth() / valueNode.getFullBoundsReference().getWidth();
            if ( valueScale < 1 ) {
                valueNode.setScale( valueScale );
            }

            // rendering order
            addChild( imageNode );
            addChild( titleNode );
            addChild( valueNode );

            // layout
            titleNode.setOffset( ( imageNode.getFullBoundsReference().getWidth() - titleNode.getFullBoundsReference().getWidth() ) / 2, TITLE_Y_OFFSET );
            //NOTE: value layout will be done when value is set, to maintain right justification

            // body location
            meter.body.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D location ) {
                    setOffset( location.toPoint2D() );
                }
            } );

            // displayed value
            meter.addValueObserver( new SimpleObserver() {
                public void update() {
                    Double value = meter.getValue();
                    if ( value == null ) {
                        valueNode.setText( NO_VALUE );
                        // centered
                        valueNode.setOffset( VALUE_BOUNDS.getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 ),
                                             VALUE_BOUNDS.getCenterY() - ( valueNode.getFullBoundsReference().getHeight() / 2 ) );
                    }
                    else {
                        // eg, "0.23400 M"
                        valueNode.setText( createDisplayText( VALUE_FORMAT.format( value ) ) );
                        // right justified
                        valueNode.setOffset( VALUE_BOUNDS.getMaxX() - valueNode.getFullBoundsReference().getWidth() - 2,
                                             VALUE_BOUNDS.getCenterY() - ( valueNode.getFullBoundsReference().getHeight() / 2 ) );
                    }
                }
            } );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new MovableDragHandler( Objects.CONCENTRATION_METER_BODY, meter.body, this ) );
        }

        private static String createDisplayText( String value ) {
            return MessageFormat.format( Strings.PATTERN_0VALUE_1UNITS, value, Strings.MOLAR );
        }
    }

    // Meter probe, origin at geometric center.
    private static class ProbeNode extends PNode {

        public ProbeNode( final ConcentrationMeter meter ) {

            PImage imageNode = new PImage( Images.CONCENTRATION_METER_PROBE ) {{
                scale( 0.5 );
            }};
            addChild( imageNode );
            imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, -imageNode.getFullBoundsReference().getHeight() / 2 );

            // body location
            meter.probe.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D location ) {
                    setOffset( location.toPoint2D() );
                }
            } );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new MovableDragHandler( Objects.CONCENTRATION_METER_PROBE, meter.probe, this,
                                                           // sim-sharing parameters
                                                           new Function0<Parameter[]>() {
                                                               public Parameter[] apply() {
                                                                   return new Parameter[] { new Parameter( BLLSimSharing.Parameters.IS_IN_SOLUTION, meter.probe.isInSolution() ) };
                                                               }
                                                           } ) );
        }
    }

    private static class WireNode extends PPath {

        public WireNode( final ConcentrationMeter meter, final ImmutableVector2D bodyCenterOffset ) {
            setStrokePaint( Color.BLACK );
            setStroke( new BasicStroke( 6f ) );

            SimpleObserver observer = new SimpleObserver() {
                public void update() {
                    //TODO end of wire is visible in transparent center of probe
                    setPathTo( new Line2D.Double( meter.probe.getX(), meter.probe.getY(),
                                                  meter.body.getX() + bodyCenterOffset.getX(), meter.body.getY() + bodyCenterOffset.getY() ) );
                }
            };
            meter.body.location.addObserver( observer );
            meter.probe.location.addObserver( observer );
        }
    }
}
