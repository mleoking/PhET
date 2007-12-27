/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Thermometer;
import edu.colorado.phet.glaciers.model.Thermometer.ThermometerAdapter;
import edu.colorado.phet.glaciers.model.Thermometer.ThermometerListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * ThermometerNode is the visual representation of a thermometer.
 * It's origin is at the bottom center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThermometerNode extends AbstractToolNode {
    
    private static final Font TEXT_FONT = new PhetDefaultFont( 12 );
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Insets TEXT_INSETS = new Insets( 2, 2, 2, 2 ); //top,left,bottom,right
    private static final Color TEXT_BACKGROUND_COLOR = Color.WHITE;
    private static final Color TEXT_BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke TEXT_BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final String TEMPERATURE_PATTERN = "###0.0";
    private static final DecimalFormat TEMPERATURE_FORMAT = new DecimalFormat( TEMPERATURE_PATTERN );
    
    private ThermometerListener _thermometerListener;
    private Thermometer _thermometer;
    private PText _textNode;
    private PPath _textBackgroundNode;
    
    public ThermometerNode( Thermometer thermometer ) {
        super( thermometer );
        
        PImage imageNode = new PImage( GlaciersImages.THERMOMETER );
        
        String widestString = TEMPERATURE_PATTERN + " " + GlaciersStrings.UNITS_TEMPERATURE;
        _textNode = new PText( widestString );
        _textNode.setFont( TEXT_FONT );
        _textNode.setTextPaint( TEXT_COLOR );
        
        double width = _textNode.getWidth() + TEXT_INSETS.left + TEXT_INSETS.right;
        double height = _textNode.getHeight() + TEXT_INSETS.top + TEXT_INSETS.bottom;
        
        Rectangle2D r = new Rectangle2D.Double( 0, 0, width, height );
        _textBackgroundNode = new PPath( r );
        _textBackgroundNode.setPaint( TEXT_BACKGROUND_COLOR );
        _textBackgroundNode.setStrokePaint( TEXT_BACKGROUND_STROKE_COLOR );
        _textBackgroundNode.setStroke( TEXT_BACKGROUND_STROKE );
        
        addChild( imageNode );
        addChild( _textBackgroundNode );
        addChild( _textNode );
        
        double x = -imageNode.getWidth()/2;
        double y = -imageNode.getHeight();
        imageNode.setOffset( x, y );
        x = imageNode.getFullBoundsReference().getX() + ( imageNode.getFullBoundsReference().getWidth() / 2 );
        y = imageNode.getFullBoundsReference().getY() + ( imageNode.getFullBoundsReference().getHeight() / 2 );
        _textBackgroundNode.setOffset( x, y );
        
        _thermometerListener = new ThermometerAdapter() {
            public void temperatureChanged() {
                updateTemperature();
            }
        };
        
        _thermometer = thermometer;
        _thermometer.addListener( _thermometerListener );
        
        updatePosition();
        updateTemperature();
    }
    
    public void cleanup() {
        _thermometer.removeListener( _thermometerListener );
        super.cleanup();
    }
    
    private void updateTemperature() {
        double temperature = _thermometer.getTemperature();
        String s = TEMPERATURE_FORMAT.format( temperature ) + " " + GlaciersStrings.UNITS_TEMPERATURE;
        _textNode.setText( s );
        // right justify the text
        double x = _textBackgroundNode.getFullBoundsReference().getMaxX() - _textNode.getFullBoundsReference().getWidth() - TEXT_INSETS.right;
        double y = _textBackgroundNode.getFullBoundsReference().getY() + TEXT_INSETS.top;
        _textNode.setOffset( x, y );
    }
}
