/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.spectrometer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.spectrometer.SpectrometerListener.SpectrometerEvent;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * SpectrometerNode
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SpectrometerNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color TITLE_COLOR = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JButton _closeButton;
    private JButton _startStopButton;
    private JButton _resetButton;
    private JButton _snapshotButton;
    
    private boolean _isRunning;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SpectrometerNode( PSwingCanvas canvas, String title, Font font, boolean isaSnapshot ) {
        
        _isRunning = false;
        _listenerList = new EventListenerList();
        
        // Images
        Icon cameraIcon = null;
        Icon closeIcon = null;
        try {
            BufferedImage cameraImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_CAMERA );
            cameraIcon = new ImageIcon( cameraImage );
            BufferedImage closeImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_CLOSE_BUTTON );
            closeIcon = new ImageIcon( closeImage );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        
        PImage spectrometerImage = null;
        if ( isaSnapshot ) {
            spectrometerImage = PImageFactory.create( HAConstants.IMAGE_SPECTROMETER_SNAPSHOT );
        }
        else {
            spectrometerImage = PImageFactory.create( HAConstants.IMAGE_SPECTROMETER );
        }
        
        PText titleNode = new PText( title );
        titleNode.setFont( font );
        titleNode.setTextPaint( TITLE_COLOR );
        
        _closeButton = new JButton( closeIcon );
        _closeButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        _startStopButton = new JButton( SimStrings.get( "button.spectrometer.start" ) );
        _startStopButton.setFont( font );
        _resetButton = new JButton( SimStrings.get( "button.spectrometer.reset" ) );
        _resetButton.setFont( font );
        _snapshotButton = new JButton( cameraIcon );
        _snapshotButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        
        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _startStopButton, row, col++ );
        layout.addComponent( _resetButton, row, col++ );
        layout.addComponent( _snapshotButton, row, col );
        
        // Piccolo wrappers
        PSwing closeButtonNode = new PSwing( canvas, _closeButton );
        PSwing buttonPanelNode = new PSwing( canvas, panel );
        
        // Opacity
        panel.setOpaque( false );
        _closeButton.setOpaque( false );
        _startStopButton.setOpaque( false );
        _resetButton.setOpaque( false );
        _snapshotButton.setOpaque( false );
        
        addChild( spectrometerImage );
        addChild( titleNode );
        addChild( closeButtonNode );
        if ( !isaSnapshot ) {
            addChild( buttonPanelNode );
        }
        
        PBounds b = spectrometerImage.getFullBounds();
        PBounds cb = closeButtonNode.getFullBounds();
        closeButtonNode.setOffset( b.getX() + b.getWidth() - cb.getWidth() - 15, b.getY() + 8 );
        titleNode.setOffset( b.getX() + 15, b.getY() + 12 );
        if ( !isaSnapshot ) {
            buttonPanelNode.setOffset( b.getX() + b.getWidth() - buttonPanelNode.getFullBounds().getWidth() - 15, b.getY() + b.getHeight() - buttonPanelNode.getFullBounds().getHeight() - 5 );
        }        
        
        _startStopButton.addActionListener( new ActionListener() {
           public void actionPerformed( ActionEvent event ) {
               _isRunning = !_isRunning;
               if ( _isRunning ) {
                   fireStart();
                   _startStopButton.setText( SimStrings.get( "button.spectrometer.stop" ) );
               }
               else {
                   fireStop();
                   _startStopButton.setText( SimStrings.get( "button.spectrometer.start" ) );
               }
           }
        });
        _resetButton.addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent event ) {
                fireReset();
            }
        } );
        _closeButton.addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent event ) {
                fireClose();
            }
        } );
        _snapshotButton.addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent event ) {
                fireSnapshot();
            }
        } );
        
        if ( isaSnapshot ) {
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    final double dx = event.getCanvasDelta().width;
                    final double dy = event.getCanvasDelta().height;
                    translate( dx, dy );
                }
                public void mousePressed( PInputEvent event ) {
                    moveToFront();
                }
            } );
        }
        else {
            buttonPanelNode.addInputEventListener( new CursorHandler() );
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * Adds a SpectrometerListener.
     *
     * @param listener the listener
     */
    public void addSpectrometerListener( SpectrometerListener listener ) {
        _listenerList.add( SpectrometerListener.class, listener );
    }

    /**
     * Removes a SpectrometerListener.
     *
     * @param listener the listener
     */
    public void removeSpectrometerListener( SpectrometerListener listener ) {
        _listenerList.remove( SpectrometerListener.class, listener );
    }

    private void fireStart() {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == SpectrometerListener.class ) {
                ( (SpectrometerListener)listeners[i + 1] ).start( new SpectrometerEvent( this ) );
            }
        }
    }
    
    private void fireStop() {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == SpectrometerListener.class ) {
                ( (SpectrometerListener)listeners[i + 1] ).stop( new SpectrometerEvent( this ) );
            }
        }
    }

    private void fireReset() {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == SpectrometerListener.class ) {
                ( (SpectrometerListener)listeners[i + 1] ).reset( new SpectrometerEvent( this ) );
            }
        }
    }

    private void fireClose() {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == SpectrometerListener.class ) {
                ( (SpectrometerListener)listeners[i + 1] ).close( new SpectrometerEvent( this ) );
            }
        }
    }
    
    private void fireSnapshot() {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == SpectrometerListener.class ) {
                ( (SpectrometerListener)listeners[i + 1] ).snapshot( new SpectrometerEvent( this ) );
            }
        }
    }
}
