/*
* The Physics Education Technology (PhET) project provides 
* a suite of interactive educational simulations. 
* Copyright (C) 2007 University of Colorado.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or 
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
* 
* For additional licensing options, please contact PhET at phethelp@colorado.edu
*/

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * RulerNode draws a ruler.
 *
 * @author Sam Reid, Chris Malley
 * @version $Revision$
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
    private static final String DEFAULT_FONT_NAME = "Lucida Sans";
    private static final int DEFAULT_FONT_STYLE = Font.PLAIN;
    private static final double DEFAULT_MAJOR_TICK_HEIGHT_TO_RULER_HEIGHT_RATIO = 0.40;
    private static final double DEFAULT_MINOR_TICK_HEIGHT_TO_RULER_HEIGHT_RATIO = 0.20;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double insetWidth;
    private String[] majorTickLabels;
    private Font majorTickFont;
    private String units;
    private Font unitsFont;
    private int numMinorTicksBetweenMajors;
    private double majorTickHeight;
    private double minorTickHeight;

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
                majorTickLabels, new Font( DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, fontSize), 
                units, new Font( DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, fontSize),
                numMinorTicksBetweenMajors,
                height * DEFAULT_MAJOR_TICK_HEIGHT_TO_RULER_HEIGHT_RATIO / 2, 
                height * DEFAULT_MINOR_TICK_HEIGHT_TO_RULER_HEIGHT_RATIO / 2 );
    }
    
    /**
     * Fully-specified constructor.
     * 
     * @param distanceBetweenFirstAndLastTick
     * @param insetWidth space to the left (and right) of the first (and last) tick marks
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
        
        setChildrenPickable( false );
       
        this.insetWidth = insetWidth;
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

        double totalWidth = distanceBetweenFirstAndLastTick + ( 2 * insetWidth );
        setBounds( 0, 0, totalWidth, height );
        update();
    }

    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    /**
     * Sets the distance between the first and last ticks.
     * 
     * @param distanceBetweenFirstAndLastTick
     */
    public void setDistanceBetweenFirstAndLastTick( double distanceBetweenFirstAndLastTick ) {
        setWidth( distanceBetweenFirstAndLastTick + ( 2 * insetWidth ) );
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
    
    //----------------------------------------------------------------------------
    // PNode overrides
    //----------------------------------------------------------------------------

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        super.internalUpdateBounds( x, y, width, height );
        doUpdate( x, y, width, height );
    }

    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    protected void update() {
        doUpdate( getX(), getY(), getWidth(), getHeight() );
    }

    /*
     * The ruler is constructed here.
     */
    private void doUpdate( double x, double y, double width, double height ) {
        
        parentNode.removeAllChildren();
        
        backgroundNode.setPathToRectangle( (float)x, (float)y, (float)width, (float)height );
        parentNode.addChild( backgroundNode );

        double distanceBetweenFirstAndLastTick = width - ( 2 * insetWidth );
        double distBetweenMajorReadings = distanceBetweenFirstAndLastTick / ( majorTickLabels.length - 1 );
        double distBetweenMinor = distBetweenMajorReadings / ( numMinorTicksBetweenMajors + 1 );
        
        for( int i = 0; i < majorTickLabels.length; i++ ) {
            
            // Major tick label
            String reading = majorTickLabels[i];
            PText majorTickLabelNode = new PText( reading );
            majorTickLabelNode.setFont( majorTickFont );
            double xVal = ( distBetweenMajorReadings * i ) + insetWidth;
            double yVal = height / 2 - majorTickLabelNode.getFullBounds().getHeight() / 2;
            majorTickLabelNode.setOffset( xVal - majorTickLabelNode.getFullBounds().getWidth() / 2, yVal );
            parentNode.addChild( majorTickLabelNode );

            // Major tick mark
            DoubleGeneralPath tickPath = createTickMark( xVal, height, majorTickHeight );
            PPath majorTickNode = new PPath( tickPath.getGeneralPath(), TICK_STROKE );
            majorTickNode.setStrokePaint( TICK_COLOR );
            parentNode.addChild( majorTickNode );

            // Minor tick marks
            if( i < majorTickLabels.length - 1 ) {
                for( int k = 1; k <= numMinorTicksBetweenMajors; k++ ) {
                    DoubleGeneralPath pair = createTickMark( xVal + k * distBetweenMinor, height, minorTickHeight );
                    PPath minorTickNode = new PPath( pair.getGeneralPath(), TICK_STROKE );
                    minorTickNode.setStrokePaint( TICK_COLOR );
                    parentNode.addChild( minorTickNode );
                }
            }

            // Units to the right of first major tick label, 
            if( i == 0 && units != null ) {
                PText unitsNode = new PText( units );
                unitsNode.setFont( unitsFont );
                parentNode.addChild( unitsNode );
                // To the right of first major tick label, baselines aligned
                unitsNode.setOffset( majorTickLabelNode.getOffset().getX() + majorTickLabelNode.getFullBounds().getWidth() + UNITS_SPACING, 
                        majorTickLabelNode.getOffset().getY() + ( majorTickLabelNode.getFullBounds().getHeight() - unitsNode.getFullBounds().getHeight() ) );
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
