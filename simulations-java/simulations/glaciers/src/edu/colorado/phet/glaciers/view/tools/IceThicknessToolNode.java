/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

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
import edu.colorado.phet.glaciers.model.AbstractTool.ToolListener;
import edu.colorado.phet.glaciers.model.IceThicknessTool.IceThicknessToolListener;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolOriginNode.DownToolOriginNode;
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
    private MovableListener _movableListener;
    private ToolListener _toolListener;
    private JLabel _iceThicknessDisplay;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public IceThicknessToolNode( IceThicknessTool iceThicknessTool, ModelViewTransform mvt ) {
        super( iceThicknessTool, mvt );
        
        _iceThicknessTool = iceThicknessTool;
        _iceThicknessToolListener = new IceThicknessToolListener() {
            public void thicknessChanged() {
                update();
            }
        };
        _iceThicknessTool.addIceThicknessToolListener( _iceThicknessToolListener );
        
        _movableListener = new MovableAdapter() {
            public void positionChanged() {
                update();
            }
        };
        _iceThicknessTool.addMovableListener( _movableListener );
        
        _toolListener = new ToolListener() {
            public void draggingChanged() {
                update();
            }
        };
        _iceThicknessTool.addToolListener( _toolListener );
        
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
        update();
    }
    
    public void cleanup() {
        _iceThicknessTool.removeIceThicknessToolListener( _iceThicknessToolListener );
        _iceThicknessTool.removeMovableListener( _movableListener );
        _iceThicknessTool.removeToolListener( _toolListener );
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the tool to match the model.
     */
    private void update() {
        
        if ( _iceThicknessTool.isDragging() ) {
            String text = "? " + GlaciersStrings.UNITS_ICE_THICKNESS;
            _iceThicknessDisplay.setText( text );
            
            //TODO: calipers should be in neutral state while dragging
        }
        else {
            double value = _iceThicknessTool.getThickness();
            String text = ICE_THICKNESS_FORMAT.format( value ) + " " + GlaciersStrings.UNITS_ICE_THICKNESS;
            _iceThicknessDisplay.setText( text );
            
            //TODO: open/close the calipers to match the ice thickness, align with ice
        }

    }
}
