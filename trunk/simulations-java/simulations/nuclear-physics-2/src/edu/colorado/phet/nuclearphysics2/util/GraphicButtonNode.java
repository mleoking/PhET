/* Copyright 2008, University of Colorado */


package edu.colorado.phet.nuclearphysics2.util;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class represents a button that is a PNode and that is composed of the
 * supplied graphics images and text string.
 *
 * @author John Blanco
 */
public class GraphicButtonNode extends PhetPNode {

    private PNode _unarmedImage;
    private PNode _armedImage;
    private PText _buttonLabel;
    private ArrayList _listeners = new ArrayList();

    public GraphicButtonNode(String unarmedImageFileName, String armedImageFileName, String labelText, 
            double labelOffsetFactorX, double labelOffsetFactorY) {

        // Add image for "armed" button.
        _armedImage = NuclearPhysics2Resources.getImageNode(armedImageFileName);
        _armedImage.setPickable( true );
        addChild( _armedImage );

        // Add image for "unarmed" button.
        _unarmedImage = NuclearPhysics2Resources.getImageNode(unarmedImageFileName);
        addChild( _unarmedImage );
        
        // Add label for button.
        _buttonLabel = new PText(labelText);
        _buttonLabel.setFont( new Font("Comic Sans MS", Font.PLAIN, 16 ));
        _buttonLabel.setPickable( false );
        addChild( _buttonLabel );
        
        // Scale the buttons to match the size of the label, assuming some
        // amount of padding.
        double scale = _buttonLabel.getFullBounds().width * 1.3 / _unarmedImage.getFullBounds().width;
        _unarmedImage.setScale( scale );
        _armedImage.setScale( scale );
        
        // Position the label to be centered on the button.  This is offset a
        // bit in order to account for button shadow.
        double labelXPos = 
            ((_unarmedImage.getFullBounds().width - _buttonLabel.getFullBounds().width) / 2) * labelOffsetFactorX; 
        double labelYPos = 
            ((_unarmedImage.getFullBounds().height - _buttonLabel.getFullBounds().height) / 2) * labelOffsetFactorY;
        _buttonLabel.setOffset( labelXPos, labelYPos );

        // Register to catch the button press event.
        _unarmedImage.addInputEventListener( new PBasicInputEventHandler() {
            boolean _mousePressed = false;
            boolean _mouseInside = false;
            
            public void mouseEntered( PInputEvent event ) {
                _mouseInside = true;
                if ( _mousePressed ) {
                    _unarmedImage.setVisible( false );
                }
            }
            
            public void mouseExited( PInputEvent event ) {
                _mouseInside = false;
                if ( _mousePressed ) {
                    _unarmedImage.setVisible( true );
                }
            }
            
            public void mousePressed( PInputEvent event ) {
                _mousePressed = true;
                _unarmedImage.setVisible( false );
            }
            
            public void mouseReleased( PInputEvent event ) {
                _mousePressed = false;
                _unarmedImage.setVisible( true );
                if ( _mouseInside ) {
                    fireEvent( new ActionEvent(this, 0, "button released") );
                }
            }
            
            private void fireEvent( ActionEvent event ) {
                for (int i =0; i < _listeners.size(); i++){
                    ((ActionListener)_listeners.get(i)).actionPerformed( event );
                }
            }
        } );

    }

    public void addActionListener( ActionListener listener ) {
        if (!_listeners.contains( listener )){
            _listeners.add( listener );
        }
    }

    public void removeActionListener( ActionListener listener ) {
        _listeners.remove( listener );
    }
}
