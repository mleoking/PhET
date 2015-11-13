// Copyright 2015, University of Colorado Boulder

package edu.colorado.phet.common.phetcommon.ask;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;

/**
 * Panel that asks for a donation.
 *
 * @author Aaron Davis
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class AskPanel extends GridPanel {

    private static final String DONATE_URL = "https://donatenow.networkforgood.org/1437859";

    public AskPanel( PhetApplicationConfig config, final Dialog dialog ) {

        // layout components, some of which are optional
        int xMargin = 40;
        int yMargin = 20;
        setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( yMargin, xMargin, yMargin, xMargin ) ) );
        setGridX( 0 ); // vertical
        setAnchor( Anchor.CENTER );

        add( Box.createVerticalStrut( 15 ) );

        // image
        add( new JLabel( new ImageIcon( PhetCommonResources.getImage( "ask/ask.png" ) ) ) );

        add( Box.createVerticalStrut( 15 ) );

        // Opens a web browser and closes the dialog
        JButton donateButton = new JButton( "Make a Donation!" );
        donateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetServiceManager.showWebPage( DONATE_URL );
                dialog.dispose();
            }
        } );
        add( donateButton );

        // Closes the dialog
        JButton maybeLaterButton = new JButton( "Maybe later" );
        maybeLaterButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dialog.dispose();
            }
        } );
        add( maybeLaterButton );

        // Closes the dialog
        JButton playButton = new JButton( "Play" );
        playButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dialog.dispose();
            }
        } );
        add( playButton );

        add( Box.createVerticalStrut( 15 ) );
    }
}
