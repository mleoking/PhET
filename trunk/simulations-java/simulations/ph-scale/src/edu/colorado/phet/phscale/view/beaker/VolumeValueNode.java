/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.beaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * VolumeValueNode displays a volume value, with an left-pointing arrow to the left of the value.
 * The origin is at the tip of the arrowhead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VolumeValueNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final DecimalFormat TEXT_FORMAT = new DefaultDecimalFormat( "0.00" );
    private static final Font TEXT_FONT = new PhetFont( Font.BOLD, PHScaleConstants.CONTROL_FONT_SIZE );
    private static final Color TEXT_COLOR = Color.BLACK;
    
    private static final PDimension ARROW_SIZE = new PDimension( 10, 15 );
    private static final Color ARROW_COLOR = Color.BLACK;
    
    private static final double X_SPACING = 3;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final PText _textNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public VolumeValueNode() {
        
        setPickable( false );
        setChildrenPickable( false );
        
        _textNode = new PText( "?" );
        _textNode.setFont( TEXT_FONT );
        _textNode.setTextPaint( TEXT_COLOR );
        
        // arrowhead, point to the right, origin at tip
        float w = (float) ARROW_SIZE.getWidth();
        float h = (float) ARROW_SIZE.getHeight();
        GeneralPath arrowPath = new GeneralPath();
        arrowPath.moveTo( 0, 0 );
        arrowPath.lineTo( w, -h / 2 );
        arrowPath.lineTo( w, h / 2 );
        arrowPath.closePath();
        PPath arrowNode = new PPath( arrowPath );
        arrowNode.setPaint( ARROW_COLOR );
        
        addChild( arrowNode );
        addChild( _textNode );
        
        // origin at arrowhead tip, text to right of arrow, vertically align center of arrowhead and text
        arrowNode.setOffset( 0, 0 );
        _textNode.setOffset( arrowNode.getFullBoundsReference().getMaxX() + X_SPACING, -_textNode.getFullBoundsReference().getHeight() / 2 );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setValue( double volume ) {
        String s = TEXT_FORMAT.format( volume ) + PHScaleStrings.UNITS_LITERS;
        _textNode.setText( s );
    }
}
