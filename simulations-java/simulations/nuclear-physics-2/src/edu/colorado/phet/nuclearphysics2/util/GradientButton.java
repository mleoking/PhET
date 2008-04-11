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


public class GradientButton extends PhetPNode {

    private PNode _unpushedButtonImage;
    private PNode _pushedButtonImage;
    private PText _buttonLabel;
    private ArrayList _listeners = new ArrayList();

    public GradientButton(String upImageFileName, String downImageFileName, String labelText, 
            double labelOffsetFactorX, double labelOffsetFactorY) {

        // Add image for pushed button.
        _pushedButtonImage = NuclearPhysics2Resources.getImageNode(downImageFileName);
        _pushedButtonImage.setPickable( true );
        addChild( _pushedButtonImage );

        // Add image for unpushed button.
        _unpushedButtonImage = NuclearPhysics2Resources.getImageNode(upImageFileName);
        addChild( _unpushedButtonImage );
        
        // Add label for button.
        _buttonLabel = new PText(labelText);
        _buttonLabel.setFont( new Font("Comic Sans MS", Font.PLAIN, 16 ));
        _buttonLabel.setPickable( false );
        addChild( _buttonLabel );
        
        // Scale the buttons to match the size of the label, assuming some
        // amount of padding.
        double scale = _buttonLabel.getFullBounds().width * 1.3 / _unpushedButtonImage.getFullBounds().width;
        _unpushedButtonImage.setScale( scale );
        _pushedButtonImage.setScale( scale );
        
        // Position the label to be centered on the button.  This is offset a
        // bit in order to account for button shadow.
        double labelXPos = 
            ((_unpushedButtonImage.getFullBounds().width - _buttonLabel.getFullBounds().width) / 2) * labelOffsetFactorX; 
        double labelYPos = 
            ((_unpushedButtonImage.getFullBounds().height - _buttonLabel.getFullBounds().height) / 2) * labelOffsetFactorY;
        _buttonLabel.setOffset( labelXPos, labelYPos );

        // Register to catch the button press event.
        _unpushedButtonImage.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                _unpushedButtonImage.setVisible( false );
                ActionEvent actionEvent = new ActionEvent(this, 0, "button pressed");
                for (int i =0; i < _listeners.size(); i++){
                    ((ActionListener)_listeners.get(i)).actionPerformed( actionEvent );
                }
            }
            public void mouseReleased( PInputEvent event ) {
                _unpushedButtonImage.setVisible( true );
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
