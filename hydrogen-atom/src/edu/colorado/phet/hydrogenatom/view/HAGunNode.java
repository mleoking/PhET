/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * HAGunNode
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAGunNode extends PNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Point2D BUTTON_OFFSET = new Point2D.Double( 33, 68 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PImage _onButton, _offButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HAGunNode() {
        super();
        
        BufferedImage gunImage = null;
        BufferedImage onButtonImage = null;
        BufferedImage offButtonImage = null;
        BufferedImage cableImage = null;
        BufferedImage panelImage = null;
        try {
            gunImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_GUN );
            onButtonImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_GUN_ON_BUTTON );
            offButtonImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_GUN_OFF_BUTTON );
            cableImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_GUN_CONTROL_CABLE );
            panelImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_GUN_CONTROL_PANEL );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        
        PImage cableNode = new PImage( cableImage );
        addChild( cableNode );
        cableNode.setOffset( 13, 175 );
        
        PImage panelNode = new PImage( panelImage );
        addChild( panelNode );
        panelNode.setOffset( 0, 210 );
        
        PImage gunNode = new PImage( gunImage );
        addChild( gunNode );
        
        _onButton = new PImage( onButtonImage );
        addChild( _onButton );
        _onButton.setOffset( BUTTON_OFFSET );
        
        _offButton = new PImage( offButtonImage );
        addChild( _offButton );
        _offButton.setOffset( BUTTON_OFFSET );
       
        PBasicInputEventHandler buttonHandler = new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                toggleButton();
            }
        };
        _onButton.addInputEventListener( buttonHandler );
        _offButton.addInputEventListener( buttonHandler );
        _onButton.addInputEventListener( new CursorHandler() );
        _offButton.addInputEventListener( new CursorHandler() );
        
        _onButton.setVisible( false );
        _offButton.setVisible( true );
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    private void toggleButton() {
        _onButton.setVisible( !_onButton.getVisible() );
        _onButton.setPickable( _onButton.getVisible() );
        _offButton.setVisible( !_offButton.getVisible() );
        _offButton.setPickable( _offButton.getVisible() );
    }
}
