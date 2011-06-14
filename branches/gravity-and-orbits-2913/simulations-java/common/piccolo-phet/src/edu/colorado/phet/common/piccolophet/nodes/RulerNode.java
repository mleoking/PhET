// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * RulerNode draws a ruler.
 *
 * @author Sam Reid, Chris Malley
 * @version $Revision:14676 $
 */
public class RulerNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Background properties
    private static final Color BACKGROUND_COLOR = new Color( 236, 225, 113 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke();

    // Tick properties
    private static final Color TICK_COLOR = Color.BLACK;
    private static final Stroke TICK_STROKE = new BasicStroke();

    // Horizontal space between left-most major tick label and units
    private static final int UNITS_SPACING = 3;

    // Defaults
    private static final int DEFAULT_INSET_WIDTH = 14;
    private static final String DEFAULT_FONT_NAME = PhetFont.getDefaultFontName();
    private static final int DEFAULT_FONT_STYLE = Font.PLAIN;
    private static final double DEFAULT_MAJOR_TICK_HEIGHT_TO_RULER_HEIGHT_RATIO = 0.40;
    private static final double DEFAULT_MINOR_TICK_HEIGHT_TO_RULER_HEIGHT_RATIO = 0.20;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double distanceBetweenFirstAndLastTick;
    private double insetWidth;
    private double height;

    private String[] majorTickLabels;
    private Font majorTickFont;
    private String units;
    private Font unitsFont;
    private String unitsMajorTickLabel; // units will be placed to the right of this tick label
    private int numMinorTicksBetweenMajors;
    private double majorTickHeight;
    private double minorTickHeight;
    private Stroke tickStroke = TICK_STROKE;
    private double unitsSpacing = UNITS_SPACING;
    private double fontScale = 1.0;

    private PNode parentNode;
    private PPath backgroundNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Simplified constructor.
     * Creates a good-looking ruler for most situations.
     * Use the fully-specified constructor if you are a ruler tweaker.
     *
     * @param distanceBetweenFirstAndLastTick
     *
     * @param height
     * @param majorTickLabels
     * @param units
     * @param numMinorTicksBetweenMajors
     * @param fontSize
     */
    public RulerNode(
            double distanceBetweenFirstAndLastTick,
            double height,
            String[] majorTickLabels,
            String units,
            int numMinorTicksBetweenMajors,
            int fontSize ) {

        this( distanceBetweenFirstAndLastTick, DEFAULT_INSET_WIDTH, height,
              majorTickLabels, createDefaultFont( fontSize ),
              units, createDefaultFont( fontSize ),
              numMinorTicksBetweenMajors,
              height * DEFAULT_MAJOR_TICK_HEIGHT_TO_RULER_HEIGHT_RATIO / 2,
              height * DEFAULT_MINOR_TICK_HEIGHT_TO_RULER_HEIGHT_RATIO / 2 );
    }

    public static Font createDefaultFont( int fontSize ) {
        return new Font( DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, fontSize );
    }

    /**
     * Fully-specified constructor.
     *
     * @param distanceBetweenFirstAndLastTick
     *
     * @param insetWidth                 space to the left (and right) of the first (and last) tick marks
     * @param height
     * @param majorTickLabels
     * @param majorTickFont
     * @param units
     * @param unitsFont
     * @param numMinorTicksBetweenMajors
     * @param majorTickHeight
     * @param minorTickHeight
     */
    public RulerNode(
            double distanceBetweenFirstAndLastTick,
            double insetWidth,
            double height,
            String[] majorTickLabels,
            Font majorTickFont,
            String units,
            Font unitsFont,
            int numMinorTicksBetweenMajors,
            double majorTickHeight,
            double minorTickHeight ) {

        this.distanceBetweenFirstAndLastTick = distanceBetweenFirstAndLastTick;
        this.insetWidth = insetWidth;
        this.height = height;

        this.majorTickLabels = majorTickLabels;
        this.majorTickFont = majorTickFont;
        this.units = units;
        this.unitsFont = unitsFont;
        this.numMinorTicksBetweenMajors = numMinorTicksBetweenMajors;
        this.majorTickHeight = majorTickHeight;
        this.minorTickHeight = minorTickHeight;

        parentNode = new PNode();
        addChild( parentNode );

        backgroundNode = new PPath();
        backgroundNode.setPaint( BACKGROUND_COLOR );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        backgroundNode.setStroke( BACKGROUND_STROKE );
        parentNode.addChild( backgroundNode );

        update();
    }

    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------

    /**
     * Sets the distance between the first and last ticks.
     *
     * @param distanceBetweenFirstAndLastTick
     *
     */
    public void setDistanceBetweenFirstAndLastTick( double distanceBetweenFirstAndLastTick ) {
        this.distanceBetweenFirstAndLastTick = distanceBetweenFirstAndLastTick;
        update();
    }

    /**
     * Gets the amount of space that appears to the left (and right)
     * of the first (and last) tick marks.
     */
    public double getInsetWidth() {
        return insetWidth;
    }

    /**
     * Sets the amount of space that appears to the left (and right)
     * of the first (and last) tick marks.
     *
     * @param insetWidth the amount of space that appears to the left (and right) of the first (and last) tick marks.
     */
    public void setInsetWidth( double insetWidth ) {
        this.insetWidth = insetWidth;
        update();
    }

    /**
     * Sets the major tick labels.
     *
     * @param majorTickLabels
     */
    public void setMajorTickLabels( String[] majorTickLabels ) {
        this.majorTickLabels = majorTickLabels;
        update();
    }

    /**
     * Sets the units.
     *
     * @param units
     */
    public void setUnits( String units ) {
        this.units = units;
        update();
    }

    /**
     * Sets the major tick label that the units will be placed next to.
     * By default, the units are placed next to the left-most tick label.
     *
     * @param majorTickLabel
     */
    public void setUnitsAssociatedMajorTickLabel( String majorTickLabel ) {
        this.unitsMajorTickLabel = majorTickLabel;
        update();
    }

    /**
     * Sets the background paint.
     *
     * @param paint
     */
    public void setBackgroundPaint( Paint paint ) {
        backgroundNode.setPaint( paint );
    }

    public void setBackgroundStroke( Stroke stroke ) {
        backgroundNode.setStroke( stroke );
    }

    public void setTickStroke( Stroke tickStroke ) {
        this.tickStroke = tickStroke;
        update();
    }

    public void setUnitsSpacing( double unitsSpacing ) {
        this.unitsSpacing = unitsSpacing;
        update();
    }

    /**
     * Sets an optional scale for the fonts used in this ruler (default scale is 1.0).  This is useful when embedding the ruler in model coordinates.
     *
     * @param fontScale
     */
    public void setFontScale( double fontScale ) {
        this.fontScale = fontScale;
        update();
    }

    /**
     * Gets the background paint.
     *
     * @return
     */
    public Paint getBackGroundPaint() {
        return backgroundNode.getPaint();
    }

    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------

    protected void update() {

        parentNode.removeAllChildren();

        double width = distanceBetweenFirstAndLastTick + ( 2 * insetWidth );
        backgroundNode.setPathToRectangle( 0, 0, (float) width, (float) height );
        parentNode.addChild( backgroundNode );

        if ( majorTickLabels != null && majorTickLabels.length > 0 ) {

            double distanceBetweenFirstAndLastTick = width - ( 2 * insetWidth );
            double distBetweenMajorReadings = distanceBetweenFirstAndLastTick / ( majorTickLabels.length - 1 );
            double distBetweenMinor = distBetweenMajorReadings / ( numMinorTicksBetweenMajors + 1 );

            // Lay out tick marks from left to right
            for ( int i = 0; i < majorTickLabels.length; i++ ) {

                // Major tick label
                String majorTickLabel = majorTickLabels[i];
                PText majorTickLabelNode = new PText( majorTickLabel );
                majorTickLabelNode.setFont( majorTickFont );
                majorTickLabelNode.setScale( fontScale );
                double xVal = ( distBetweenMajorReadings * i ) + insetWidth;
                double yVal = height / 2 - majorTickLabelNode.getFullBounds().getHeight() / 2;
                //Clamp and make sure the labels stay within the ruler, especially if the insetWidth has been set low (or to zero)
                majorTickLabelNode.setOffset( xVal - majorTickLabelNode.getFullBounds().getWidth() / 2, yVal );

                //only add the major tick label if the insetWidth is nonzero or if it is not an end label
                if ( insetWidth != 0 || ( i != 0 && i != majorTickLabels.length - 1 ) ) {
                    parentNode.addChild( majorTickLabelNode );
                }

                // Major tick mark
                DoubleGeneralPath tickPath = createTickMark( xVal, height, majorTickHeight );
                PPath majorTickNode = new PPath( tickPath.getGeneralPath(), tickStroke );
                majorTickNode.setStrokePaint( TICK_COLOR );
                parentNode.addChild( majorTickNode );

                // Minor tick marks
                if ( i < majorTickLabels.length - 1 ) {
                    for ( int k = 1; k <= numMinorTicksBetweenMajors; k++ ) {
                        DoubleGeneralPath pair = createTickMark( xVal + k * distBetweenMinor, height, minorTickHeight );
                        PPath minorTickNode = new PPath( pair.getGeneralPath(), tickStroke );
                        minorTickNode.setStrokePaint( TICK_COLOR );
                        parentNode.addChild( minorTickNode );
                    }
                }

                /*
                 * Units label.
                 * If an associated major tick mark has been specified for the units label,
                 * put the units to the right of that tick mark.
                 * By default, place units to the right of the first (left-most) major tick label.
                 */
                if ( units != null ) {

                    PText unitsNode = null;

                    if ( unitsMajorTickLabel != null ) {
                        if ( unitsMajorTickLabel.equals( majorTickLabel ) ) {
                            unitsNode = new PText( units );
                        }
                    }
                    else {
                        //Only show the units after the first visible major tick label
                        if ( ( i == 0 && insetWidth != 0 ) || ( i == 1 && insetWidth == 0 ) ) {
                            unitsNode = new PText( units );
                        }
                    }

                    if ( unitsNode != null ) {
                        unitsNode.setFont( unitsFont );
                        unitsNode.setScale( fontScale );
                        parentNode.addChild( unitsNode );
                        // To the right of the major tick label, baselines aligned
                        double xOffset = majorTickLabelNode.getOffset().getX() + majorTickLabelNode.getFullBounds().getWidth() + unitsSpacing;
                        double yOffset = majorTickLabelNode.getOffset().getY() + ( majorTickLabelNode.getFullBounds().getHeight() - unitsNode.getFullBounds().getHeight() );
                        unitsNode.setOffset( xOffset, yOffset );
                    }
                }
            }
        }
    }

    //----------------------------------------------------------------------------
    // Static utility methods
    //----------------------------------------------------------------------------

    /*
     * Creates a tick mark at a specific x location.
     * Each tick is marked at the top and bottom of the ruler.
     */

    private static DoubleGeneralPath createTickMark( double xPosition, double rulerHeight, double tickHeight ) {
        DoubleGeneralPath tickPath = new DoubleGeneralPath( xPosition, 0 );
        tickPath.lineTo( xPosition, tickHeight );
        tickPath.moveTo( xPosition, rulerHeight - tickHeight );
        tickPath.lineTo( xPosition, rulerHeight );
        return tickPath;
    }

}