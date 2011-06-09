// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.drag.WorldLocationDragHandler;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.CapacitanceMeter;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.PlateChargeMeter;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.StoredEnergyMeter;
import edu.colorado.phet.capacitorlab.view.meters.ZoomButtonNode.ZoomInButtonNode;
import edu.colorado.phet.capacitorlab.view.meters.ZoomButtonNode.ZoomOutButtonNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Abstract base class for all bar meters.
 * Origin is at the upper-left corner of the "track" that the bar moves in.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BarMeterNode extends PhetPNode {

    public static class CapacitanceMeterNode extends BarMeterNode {
        public CapacitanceMeterNode( CapacitanceMeter meter, CLModelViewTransform3D mvt, String label ) {
            super( meter, mvt, CLPaints.CAPACITANCE, label, "0.00", CLConstants.CAPACITANCE_METER_VALUE_EXPONENT, CLStrings.FARADS );
        }
    }

    public static class PlateChargeMeterNode extends BarMeterNode {

        public PlateChargeMeterNode( PlateChargeMeter meter, CLModelViewTransform3D mvt, String label ) {
            super( meter, mvt, CLPaints.POSITIVE_CHARGE, label, "0.00", CLConstants.PLATE_CHARGE_METER_VALUE_EXPONENT, CLStrings.COULOMBS );
        }

        // This meter displays absolute value, and changes color to indicate positive or negative charge.
        @Override
        protected void setValue( double value ) {
            super.setValue( Math.abs( value ) );
            setBarColor( ( value >= 0 ) ? CLPaints.POSITIVE_CHARGE : CLPaints.NEGATIVE_CHARGE );
        }
    }

    public static class StoredEnergyMeterNode extends BarMeterNode {
        public StoredEnergyMeterNode( StoredEnergyMeter meter, CLModelViewTransform3D mvt, String label ) {
            super( meter, mvt, CLPaints.STORED_ENERGY, label, "0.00", CLConstants.STORED_ENERGY_METER_VALUE_EXPONENT, CLStrings.JOULES );
        }
    }

    // track
    private static final PDimension TRACK_SIZE = new PDimension( 50, 200 );
    private static final Color TRACK_FILL_COLOR = Color.WHITE;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );

    // bar
    private static final Color BAR_STROKE_COLOR = TRACK_STROKE_COLOR;
    private static final Stroke BAR_STROKE = TRACK_STROKE;

    // ticks
    private static final int NUMBER_OF_TICKS = 10;
    private static final double MAJOR_TICK_MARK_LENGTH = 5;
    private static final double MINOR_TICK_MARK_LENGTH = 5;
    private static final Color TICK_MARK_COLOR = TRACK_STROKE_COLOR;
    private static final Stroke TICK_MARK_STROKE = TRACK_STROKE;
    private static final boolean MINOR_TICKS_OUTSIDE = true; // true=ticks outside bar, false=ticks inside bar

    // range labels
    private static final Font RANGE_LABEL_FONT = new PhetFont( 14 );
    private static final Color RANGE_LABEL_COLOR = Color.BLACK;

    // title
    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color TITLE_COLOR = Color.BLACK;

    // value display
    private static final PhetFont VALUE_FONT = new PhetFont( 16 );
    private static final Color VALUE_COLOR = Color.BLACK;

    // overload indicator
    private static final double OVERLOAD_INDICATOR_WIDTH = 0.75 * TRACK_SIZE.getWidth();
    private static final double OVERLOAD_INDICATOR_HEIGHT = 15;

    private final TrackNode trackNode;
    private final BarNode barNode;
    private final TitleNode titleNode;
    private final TimesTenValueNode valueNode;
    private final PowerOfTenRangeLabelNode maxLabelNode;
    private final RangeLabelNode minLabelNode;
    private final OverloadIndicatorNode overloadIndicatorNode;
    private final ZoomButtonNode zoomInButtonNode, zoomOutButtonNode;
    private final PImage closeButton;
    private final TickMarkNode maxTickMarkNode, minTickMarkNode;

    private double value;
    private final Property<Integer> exponentProperty; // exponent of the value display and max label
    private final Property<Boolean> hasBeenVisibleProperty;

    /**
     * Constructor.
     *
     * @param meter                model element for the meter
     * @param mvt                  model-view transform
     * @param barColor             color used to fill the bar
     * @param title                title displayed below the meter
     * @param valueMantissaPattern pattern used to format the mantissa of the value displayed below the meter
     * @param exponent             exponent of the value display and max label
     * @param units                units
     */
    public BarMeterNode( final BarMeter meter, final CLModelViewTransform3D mvt, Color barColor, String title, String valueMantissaPattern, int exponent, String units ) {

        this.value = meter.getValue();
        this.exponentProperty = new Property<Integer>( exponent );
        this.hasBeenVisibleProperty = new Property<Boolean>( meter.visibleProperty.get() );

        // track
        trackNode = new TrackNode();
        addChild( trackNode );

        // bar
        double maxValue = Math.pow( 10, exponent );
        barNode = new BarNode( barColor, maxValue, value );
        addChild( barNode );

        // minor ticks
        double deltaY = TRACK_SIZE.height / NUMBER_OF_TICKS;
        for ( int i = 0; i < NUMBER_OF_TICKS; i++ ) {
            TickMarkNode tickMarkNode = new TickMarkNode( MINOR_TICK_MARK_LENGTH );
            addChild( tickMarkNode );
            double xOffset = MINOR_TICKS_OUTSIDE ? -MINOR_TICK_MARK_LENGTH : 0;
            tickMarkNode.setOffset( xOffset, ( i + 1 ) * deltaY );
        }

        // majors ticks, for min and max
        maxTickMarkNode = new TickMarkNode( MAJOR_TICK_MARK_LENGTH );
        addChild( maxTickMarkNode );
        minTickMarkNode = new TickMarkNode( MAJOR_TICK_MARK_LENGTH );
        addChild( minTickMarkNode );

        // min range label
        minLabelNode = new RangeLabelNode( "0" );
        addChild( minLabelNode );

        // max range label
        maxLabelNode = new PowerOfTenRangeLabelNode( exponent );
        addChild( maxLabelNode );

        // title
        titleNode = new TitleNode( title );
        addChild( titleNode );

        // overload indicator
        overloadIndicatorNode = new OverloadIndicatorNode( barColor, maxValue, value );
        addChild( overloadIndicatorNode );

        // value
        valueNode = new TimesTenValueNode( new DecimalFormat( valueMantissaPattern ), exponent, units, value, VALUE_FONT, VALUE_COLOR );
        addChild( valueNode );

        // close button
        closeButton = new PImage( CLImages.CLOSE_BUTTON );
        addChild( closeButton );

        // zoom buttons
        {
            zoomInButtonNode = new ZoomInButtonNode();
            addChild( zoomInButtonNode );

            zoomOutButtonNode = new ZoomOutButtonNode();
            addChild( zoomOutButtonNode );

            updateZoomButtons();
        }

        // interactivity
        closeButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseReleased( PInputEvent event ) {
                meter.visibleProperty.set( false );
            }
        } );

        ActionListener zoomListener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                updateExponent();
            }
        };
        zoomInButtonNode.addActionListener( zoomListener );
        zoomOutButtonNode.addActionListener( zoomListener );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new WorldLocationDragHandler( meter.locationProperty, this, mvt ) );

        // layout
        updateLayout();

        // observers
        {
            // value
            meter.addValueObserver( new SimpleObserver() {
                public void update() {
                    setValue( meter.getValue() );
                }
            } );

            // visibility
            meter.visibleProperty.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( meter.visibleProperty.get() );
                }
            } );

            // location
            meter.locationProperty.addObserver( new SimpleObserver() {
                public void update() {
                    setOffset( mvt.modelToView( meter.locationProperty.get() ) );
                }
            } );

            // exponent
            exponentProperty.addObserver( new SimpleObserver() {
                public void update() {
                    handleExponentChanged();
                }
            } );
        }
    }

    public void reset() {
        exponentProperty.reset();
        hasBeenVisibleProperty.reset();
    }

    /**
     * When the meter first becomes visible, autoscale.
     *
     * @param visible
     */
    @Override public void setVisible( boolean visible ) {
        if ( visible != getVisible() ) {
            super.setVisible( visible );
            if ( visible && !hasBeenVisibleProperty.get() ) {
                hasBeenVisibleProperty.set( true );
                updateExponent();
            }
        }
    }

    private void updateLayout() {
        double x = 0;
        double y = 0;
        trackNode.setOffset( x, y );
        // bar inside track
        barNode.setOffset( trackNode.getOffset() );
        // max tick mark at top of track
        x = -maxTickMarkNode.getFullBoundsReference().getWidth();
        y = trackNode.getYOffset();
        maxTickMarkNode.setOffset( x, y );
        // min tick mark at bottom of track
        x = -minTickMarkNode.getFullBoundsReference().getWidth();
        y = trackNode.getFullBoundsReference().getMaxY();
        minTickMarkNode.setOffset( x, y );
        // max label centered on max tick
        x = maxTickMarkNode.getFullBoundsReference().getMinX() - maxLabelNode.getFullBoundsReference().width - 2;
        y = maxTickMarkNode.getFullBoundsReference().getCenterY() - ( maxLabelNode.getFullBoundsReference().getHeight() / 2 );
        maxLabelNode.setOffset( x, y );
        // min label centered on min tick
        x = minTickMarkNode.getFullBoundsReference().getMinX() - minLabelNode.getFullBoundsReference().width - 2;
        y = minTickMarkNode.getFullBoundsReference().getCenterY() - ( minLabelNode.getFullBoundsReference().getHeight() / 2 );
        minLabelNode.setOffset( x, y );
        // overload indicator centered above track
        x = trackNode.getFullBoundsReference().getCenterX();
        y = trackNode.getFullBoundsReference().getMinY() - overloadIndicatorNode.getFullBoundsReference().getHeight() - 1;
        overloadIndicatorNode.setOffset( x, y );
        // title centered below track
        x = trackNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = minLabelNode.getFullBoundsReference().getMaxY() + 2;
        titleNode.setOffset( x, y );
        // value centered below title
        x = titleNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 );
        y = titleNode.getFullBoundsReference().getMaxY() + 2;
        valueNode.setOffset( x, y );
        // close button at upper right of track
        x = trackNode.getFullBoundsReference().getMaxX() + 2;
        y = trackNode.getFullBoundsReference().getMinY();
        closeButton.setOffset( x, y );
        // zoom-in button below max label
        x = maxLabelNode.getFullBoundsReference().getMaxX() - zoomInButtonNode.getFullBoundsReference().getWidth();
        y = maxLabelNode.getFullBoundsReference().getMaxY() + 5;
        zoomInButtonNode.setOffset( x, y );
        // zoom-out button below zoom-in button
        x = zoomInButtonNode.getXOffset();
        y = zoomInButtonNode.getFullBoundsReference().getMaxY() + 1;
        zoomOutButtonNode.setOffset( x, y );
    }

    /*
     * At most one of the zoom buttons is enabled.
     * If the bar is empty, neither button is enabled.
     * If the bar is less than 10% full, the zoom in button is enabled.
     * If the bar is overflowing, the zoom out button is enabled.
     */
    private void updateZoomButtons() {
        double mantissa = value / Math.pow( 10, exponentProperty.get() );
        boolean plusEnabled = ( value != 0 ) && ( mantissa < 0.1 );
        boolean minusEnabled = ( value != 0 ) && ( mantissa > 1 );
        zoomInButtonNode.setEnabled( plusEnabled );
        zoomOutButtonNode.setEnabled( minusEnabled );
    }

    // Sets the exponent to a value that makes the mantissa >= 0.1
    private void updateExponent() {
        if ( value != 0 ) {
            int exponent = 0;
            while ( ( value / Math.pow( 10, exponent ) ) < 0.1 ) {
                exponent--;
            }
            exponentProperty.set( exponent );
        }
    }

    /**
     * Sets the value displayed by the meter.
     * Updates the bar and the value below the meter.
     *
     * @param value
     */
    protected void setValue( double value ) {
        if ( value < 0 ) {
            throw new IllegalArgumentException( "value must be >= 0 : " + value );
        }
        if ( value != this.value ) {

            this.value = value;

            // update components
            barNode.setValue( value );
            overloadIndicatorNode.setValue( value );
            valueNode.setValue( value );

            updateLayout();
            updateZoomButtons();
        }
    }

    // Sets the exponent used for the value and max label.
    private void handleExponentChanged() {

        int exponent = exponentProperty.get();

        // update components
        double maxValue = Math.pow( 10, exponent );
        barNode.setMaxValue( maxValue );
        overloadIndicatorNode.setMaxValue( maxValue );
        maxLabelNode.setExponent( exponent );
        valueNode.setExponent( exponent );

        updateLayout();
        updateZoomButtons();
    }

    /**
     * Sets the color used to fill the bar.
     *
     * @param color
     */
    protected void setBarColor( Color color ) {
        barNode.setPaint( color );
        overloadIndicatorNode.setArrowFillColor( color );
    }

    /*
     * The track that the bar moves in.
     * Origin is at upper-left corner.
     */
    private static class TrackNode extends PPath {

        public TrackNode() {
            setPathTo( new Rectangle2D.Double( 0, 0, TRACK_SIZE.width, TRACK_SIZE.height ) );
            setPaint( TRACK_FILL_COLOR );
            setStrokePaint( TRACK_STROKE_COLOR );
            setStroke( TRACK_STROKE );
        }
    }

    /*
     * The bar which indicates the magnitude of the value being read by the meter.
     * Origin is at upper left of track.
     */
    private static class BarNode extends PPath {

        private double value, maxValue;
        private final Rectangle2D rectangle;

        public BarNode( Color barColor, double maxValue, double value ) {

            this.value = value;
            this.maxValue = maxValue;

            rectangle = new Rectangle2D.Double( 0, 0, TRACK_SIZE.width, TRACK_SIZE.height );
            setPathTo( rectangle );
            setPaint( barColor );
            setStrokePaint( BAR_STROKE_COLOR );
            setStroke( BAR_STROKE );

            update();
        }

        public void setValue( double value ) {
            if ( value != this.value ) {
                this.value = value;
                update();
            }
        }

        public void setMaxValue( double maxValue ) {
            if ( maxValue != this.maxValue ) {
                this.maxValue = maxValue;
                update();
            }
        }

        private void update() {
            double percent = Math.min( 1, Math.abs( value ) / maxValue );
            double y = ( 1 - percent ) * TRACK_SIZE.height;
            double height = TRACK_SIZE.height - y;
            rectangle.setRect( 0, y, TRACK_SIZE.width, height );
            setPathTo( rectangle );
        }
    }

    /*
     * Horizontal tick mark line, with no label.
     * Origin is at the left end of the line.
     */
    private static class TickMarkNode extends PPath {

        public TickMarkNode( double tickMarkLength ) {
            super( new Line2D.Double( 0, 0, tickMarkLength, 0 ) );
            setStrokePaint( TICK_MARK_COLOR );
            setStroke( TICK_MARK_STROKE );
        }
    }

    /*
     * Label used to indicate the range.
     * Origin is at upper-left corner of bounding box.
     */
    private static class RangeLabelNode extends HTMLNode {

        public RangeLabelNode( String label ) {
            this();
            setHTML( label );
        }

        protected RangeLabelNode() {
            setHTMLColor( RANGE_LABEL_COLOR );
            setFont( RANGE_LABEL_FONT );
        }
    }

    /*
     * Label used to indicate a range that is a power of 10.
     * Origin is at upper-left corner of bounding box.
     */
    private static class PowerOfTenRangeLabelNode extends RangeLabelNode {

        private static final String PATTERN = "<html>10<sup>{0}</sup></html>";

        public PowerOfTenRangeLabelNode( int exponent ) {
            super( MessageFormat.format( PATTERN, exponent ) );
        }

        public void setExponent( int exponent ) {
            setHTML( MessageFormat.format( PATTERN, exponent ) );
        }
    }

    /*
     * Title used to indicate the purpose of this meter.
     * Origin is at upper-left corner of bounding box.
     */
    private static class TitleNode extends PText {

        public TitleNode( String label ) {
            super( label );
            setTextPaint( TITLE_COLOR );
            setFont( TITLE_FONT );
        }
    }

    /*
     * Overload indicator, visible when the value is greater than what the bar
     * is capable of displaying.  The indicator is an arrow that points upward.
     */
    private static class OverloadIndicatorNode extends PComposite {

        private final ArrowNode arrowNode;
        private double value, maxValue;

        public OverloadIndicatorNode( Color fillColor, double maxValue, double value ) {

            this.value = value;
            this.maxValue = maxValue;

            Point2D tailLocation = new Point2D.Double( 0, OVERLOAD_INDICATOR_HEIGHT );
            Point2D tipLocation = new Point2D.Double( 0, 0 );
            double headHeight = 0.6 * OVERLOAD_INDICATOR_HEIGHT;
            double headWidth = OVERLOAD_INDICATOR_WIDTH;
            double tailWidth = headWidth / 2;
            arrowNode = new ArrowNode( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
            arrowNode.setPaint( fillColor );
            addChild( arrowNode );

            update();
        }

        public void setValue( double value ) {
            if ( value != this.value ) {
                this.value = value;
                update();
            }
        }

        public void setMaxValue( double maxValue ) {
            if ( maxValue != this.maxValue ) {
                this.maxValue = maxValue;
                update();
            }
        }

        public void setArrowFillColor( Color color ) {
            arrowNode.setPaint( color );
        }

        private void update() {
            setVisible( value > maxValue );
        }
    }
}
