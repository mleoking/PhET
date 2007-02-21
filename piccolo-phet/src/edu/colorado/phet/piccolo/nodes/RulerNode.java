/*
* The Physics Education Technology (PhET) project provides 
* a suite of interactive educational simulations. 
* Copyright (C) 2004-2006 University of Colorado.
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

public class RulerNode extends PhetPNode {
    
    private static final Color BACKGROUND_COLOR = new Color( 236, 225, 113 );
    private static final Color BACKGROUNG_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke();
    
    private static final Color TICK_COLOR = Color.BLACK;
    private static final Stroke TICK_STROKE = new BasicStroke();
    
    private static final Font LABEL_FONT = new Font( "Lucida Sans", Font.BOLD, 22 );
    
    private PNode parentNode;
    private PPath backgroundNode;
    private String[] readings;
    private String units;
    
    private int horizontalInset = 14;
    private int numMinorTicksBetweenMajors = 5;
    private double majorTickHeight = 10;
    private double minorTickHeight = 6;

    public RulerNode( String[] readings, String units, int width, int height ) {
        
        this.units = units;
        this.readings = readings;
        
        parentNode = new PNode();
        addChild( parentNode );
        
        backgroundNode = new PPath();
        backgroundNode.setPaint( BACKGROUND_COLOR );
        backgroundNode.setStrokePaint( BACKGROUNG_STROKE_COLOR );
        backgroundNode.setStroke( BACKGROUND_STROKE );
        parentNode.addChild( backgroundNode );

        setBounds( 0, 0, width, height );
        update();
    }

    public void setReadings( String[] readings ) {
        this.readings = readings;
        update();
    }

    protected void update() {
        doUpdate( getX(), getY(), getWidth(), getHeight() );
    }

    public void setUnitsText( String units ) {
        this.units = units;
        update();
    }

    public void setMeasurementPixelWidth( double width ) {
        setWidth( width + horizontalInset * 2 );
        update();
    }

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        super.internalUpdateBounds( x, y, width, height );
        doUpdate( x, y, width, height );
    }

    private void doUpdate( double x, double y, double width, double height ) {
        
        parentNode.removeAllChildren();
        
        backgroundNode.setPathToRectangle( (float)x, (float)y, (float)width, (float)height );
        parentNode.addChild( backgroundNode );

        double rulerDist = width - horizontalInset * 2;
        double distBetweenMajorReadings = rulerDist / ( readings.length - 1 );
        double distBetweenMinor = distBetweenMajorReadings / numMinorTicksBetweenMajors;
        for( int i = 0; i < readings.length; i++ ) {
            
            String reading = readings[i];
            PText pText = new PText( reading );
            double xVal = distBetweenMajorReadings * i + horizontalInset;
            double yVal = height / 2 - pText.getFullBounds().getHeight() / 2;
            pText.setOffset( xVal - pText.getFullBounds().getWidth() / 2, yVal );
            parentNode.addChild( pText );

            DoubleGeneralPath tickPath = createTickPair( xVal, height, majorTickHeight );
            PPath majorTick = new PPath( tickPath.getGeneralPath(), TICK_STROKE );
            majorTick.setStrokePaint( TICK_COLOR );
            parentNode.addChild( majorTick );

            if( i < readings.length - 1 ) {
                for( int k = 1; k < numMinorTicksBetweenMajors; k++ ) {
                    DoubleGeneralPath pair = createTickPair( xVal + k * distBetweenMinor, height, minorTickHeight );
                    PPath minorTick = new PPath( pair.getGeneralPath(), TICK_STROKE );
                    minorTick.setStrokePaint( TICK_COLOR );
                    parentNode.addChild( minorTick );
                }
            }

            if( i == 0 ) {
                PText unitsGraphic = new PText( units );
                unitsGraphic.setFont( LABEL_FONT );
                parentNode.addChild( unitsGraphic );
                unitsGraphic.setOffset( pText.getOffset().getX() + pText.getFullBounds().getWidth() + 5, pText.getOffset().getY() - pText.getFullBounds().getHeight() / 2 );
            }
        }
    }

    private DoubleGeneralPath createTickPair( double xVal, double height, double tickSize ) {
        DoubleGeneralPath tickPath = new DoubleGeneralPath( xVal, 0 );
        tickPath.lineTo( xVal, tickSize );
        tickPath.moveTo( xVal, height - tickSize );
        tickPath.lineTo( xVal, height );
        return tickPath;
    }

}
