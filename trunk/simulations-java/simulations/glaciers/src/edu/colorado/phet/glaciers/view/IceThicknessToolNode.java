/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.IceThicknessTool;
import edu.colorado.phet.glaciers.model.IceThicknessTool.IceThicknessToolListener;
import edu.colorado.phet.glaciers.view.AbstractToolOriginNode.DownToolOriginNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * IceThicknessToolNode is the visual representation of an ice thickness tool.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IceThicknessToolNode extends AbstractToolNode {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private static final Font FONT = new PhetDefaultFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final NumberFormat ICE_THICKNESS_FORMAT = new DefaultDecimalFormat( "0.0" );
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private IceThicknessTool _iceThicknessTool;
    private IceThicknessToolListener _iceThicknessToolListener;
    private JLabel _iceThicknessDisplay;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public IceThicknessToolNode( IceThicknessTool iceThicknessTool, ModelViewTransform mvt ) {
        super( iceThicknessTool, mvt );
        
        _iceThicknessTool = iceThicknessTool;
        _iceThicknessToolListener = new IceThicknessToolListener() {
            public void thicknessChanged() {
                updateThickness();
            }
        };
        _iceThicknessTool.addIceThicknessToolListener( _iceThicknessToolListener );
        
        PNode arrowNode = new DownToolOriginNode();
        addChild( arrowNode );
        arrowNode.setOffset( 0, 0 ); // this node identifies the origin
        
        PImage imageNode = new PImage( GlaciersImages.ICE_THICKNESS_TOOL );
        addChild( imageNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() + 3, arrowNode.getFullBoundsReference().getMinY() - imageNode.getFullBoundsReference().getHeight() );
        
        _iceThicknessDisplay = new JLabel();
        _iceThicknessDisplay.setFont( FONT );
        JPanel panel = new JPanel();
        panel.setBorder( BORDER );
        panel.add( _iceThicknessDisplay );
        PSwing panelNode = new PSwing( panel );
        addChild( panelNode );
        panelNode.setOffset( imageNode.getFullBoundsReference().getMaxX() + 2, imageNode.getFullBoundsReference().getMinY() );
        
        // initial state
        updateThickness();
    }
    
    public void cleanup() {
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the ice thickness display to match the model.
     */
    private void updateThickness() {
        double value = _iceThicknessTool.getThickness();
        String text = ICE_THICKNESS_FORMAT.format( value ) + " " + GlaciersStrings.UNITS_ICE_THICKNESS;
        _iceThicknessDisplay.setText( text );
        
        //TODO: open/close the calipers to match the ice thickness, align with ice
    }
}
