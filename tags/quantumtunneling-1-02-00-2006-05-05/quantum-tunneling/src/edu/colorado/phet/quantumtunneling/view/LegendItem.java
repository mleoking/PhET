/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.*;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * LegendItem is an item in a graph legend.
 * The item consists of a colored line and a text label.
 * The line is positioned to the left of the label.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LegendItem extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final Font DEFAULT_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 14 );
    private static final Stroke DEFAULT_LINE_STROKE = new BasicStroke( 3f );
    private static final float DEFAUT_LINE_LENGTH = 15f;
    
    private static final double INTERNAL_SPACING = 4;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PPath _line;
    private PText _text;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param title
     * @param linePaint
     */
    public LegendItem( String title, Paint linePaint ) {
        super();
        
        _line = new PPath();
        float[] xp = { 0f, DEFAUT_LINE_LENGTH };
        float[] yp = { 0f, 0f };
        _line.setPathToPolyline( xp, yp );
        _line.setStroke( DEFAULT_LINE_STROKE );
        _line.setStrokePaint( linePaint );
        
        _text = new PText( title );
        _text.setFont( DEFAULT_FONT );
        _text.setTextPaint( DEFAULT_TEXT_COLOR );
        
        addChild( _line );
        addChild( _text );
        
        updateLayout();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the font.
     * 
     * @param font
     */
    public void setFont( Font font ) {
        _text.setFont( font );
    }
    
    /**
     * Sets the color used for the text.
     * 
     * @param color
     */
    public void setTitleColor( Color color ) {
        _text.setTextPaint( color );
    }
    
    /**
     * Sets the line paint.
     * 
     * @param paint
     */
    public void setLinePaint( Paint paint ) {
        _line.setStrokePaint( paint );
    }
    
    /**
     * Sets the length of the colored line.
     * 
     * @param lineLength
     */
    public void setLineLength( float lineLength ) {
        float[] xp = { 0f, lineLength };
        float[] yp = { 0f, 0f };
        _line.setPathToPolyline( xp, yp );
        updateLayout();
    }
    
    //----------------------------------------------------------------------------
    // Layout
    //----------------------------------------------------------------------------
    
    /*
     * Lays out the nodes -- line on the right, text on the left.
     */
    private void updateLayout() {
        AffineTransform at1 = new AffineTransform();
        at1.translate( 0, _text.getHeight() / 2 );
        _line.setTransform( at1 );
        
        AffineTransform at2 = new AffineTransform();
        at2.translate( _line.getWidth() + INTERNAL_SPACING, 0 );
        _text.setTransform( at2 );
    }
}
