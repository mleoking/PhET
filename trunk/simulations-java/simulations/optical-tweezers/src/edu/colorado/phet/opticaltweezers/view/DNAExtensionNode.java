/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;

import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * DNAExtensionNode displays the DNA extension length, 
 * a straight line between the ends of the DNA strand.
 * The extension value is labeled at the midpoint of the line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAExtensionNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color LINE_STROKE_COLOR = Color.BLACK;
    private static final Stroke LINE_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,6}, 0 ); // dashed
    
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final Font VALUE_FONT = new PhetDefaultFont();
    
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( "0.0" );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GeneralPath _extensionPath;
    private PPath _extensionNode;
    private PText _valueNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DNAExtensionNode() {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _extensionPath = new GeneralPath();
        _extensionNode = new PPath();
        _extensionNode.setStroke( LINE_STROKE );
        _extensionNode.setStrokePaint( LINE_STROKE_COLOR );
        addChild( _extensionNode );
        
        _valueNode = new PText();
        _valueNode.setFont( VALUE_FONT );
        _valueNode.setTextPaint( VALUE_COLOR );
        addChild( _valueNode );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /**
     * Updates the line and displayed value.
     * 
     * @param value extension value, in model coordinates
     * @param tailX x coordiate of tail end, in view coordinates
     * @param tailY y coordiate of tail end, in view coordinates
     * @param headX x coordiate of head end, in view coordinates
     * @param headY y coordiate of head end, in view coordinates
     */
    public void update( double value, double tailX, double tailY, double headX, double headY ) {

        _extensionPath.reset();
        _extensionPath.moveTo( (float) tailX, (float) tailY );
        _extensionPath.lineTo( (float) headX, (float) headY );
        _extensionNode.setPathTo( _extensionPath );
        
        String valueString = VALUE_FORMAT.format( value ) + " nm";
        _valueNode.setText( valueString );
        _valueNode.setOffset( tailX + ( ( headX - tailX ) / 2 ), tailY + ( ( headY - tailY ) / 2 ) ); // at midpoint of the extension line
    }
}
