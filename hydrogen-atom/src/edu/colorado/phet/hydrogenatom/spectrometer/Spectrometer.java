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

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Spectrometer
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Spectrometer extends PhetPNode {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JButton _closeButton;
    private JButton _startStopButton;
    private JButton _resetButton;
    private JButton _snapshotButton;
    
    private boolean _isRunning;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Spectrometer( PSwingCanvas canvas, boolean isaSnapshot ) {
        
        _isRunning = false;
        
        // Images
        Icon cameraIcon = null;
        try {
            BufferedImage cameraImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_CAMERA );
            cameraIcon = new ImageIcon( cameraImage );
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
        
        _closeButton = new JButton( "close" );//XXX use an icon
        _startStopButton = new JButton( SimStrings.get( "button.spectrometer.start" ) );
        _resetButton = new JButton( SimStrings.get( "button.spectrometer.reset" ) );
        _snapshotButton = new JButton( cameraIcon );
        
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
        if ( isaSnapshot ) {
            addChild( closeButtonNode );
        }
        addChild( buttonPanelNode );
        
        PBounds b = spectrometerImage.getFullBounds();
        closeButtonNode.setOffset( b.getX() + 5, b.getY() + b.getHeight() - closeButtonNode.getFullBounds().getHeight() - 5 );
        buttonPanelNode.setOffset( b.getX() + b.getWidth() - buttonPanelNode.getFullBounds().getWidth() - 5, b.getY() + b.getHeight() - buttonPanelNode.getFullBounds().getHeight() - 5 );
    
        _startStopButton.addActionListener( new ActionListener() {
           public void actionPerformed( ActionEvent event ) {
               _isRunning = !_isRunning;
               if ( _isRunning ) {
                   _startStopButton.setText( SimStrings.get( "button.spectrometer.stop" ) );
               }
               else {
                   _startStopButton.setText( SimStrings.get( "button.spectrometer.start" ) );
               }
           }
        });
    }
}
