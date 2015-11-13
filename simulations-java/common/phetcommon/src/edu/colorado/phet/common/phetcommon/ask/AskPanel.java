// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.ask;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Panel that asks for a donation.
 *
 * @author Aaron Davis
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AskPanel extends GridPanel {

    public AskPanel( PhetApplicationConfig config, final Dialog dialog ) {

        final String donateURL = "https://donatenow.networkforgood.org/1437859";
        String visibleURL = "Make a Donation!";

        // layout components, some of which are optional
        int xMargin = 40;
        int yMargin = 20;
        setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( yMargin, xMargin, yMargin, xMargin ) ) );
        setGridX( 0 ); // vertical
        setAnchor( Anchor.CENTER );
        add( new JLabel( "Donate to PhET!" ) {{
            setFont( new PhetFont( 18 ) );
        }} );
        add( Box.createVerticalStrut( 15 ) );

        // image
        add( new JLabel( new ImageIcon( config.getResourceLoader().getImage( "phet-girl-donate_experienced.png" ) ) ) {{
            // #3364, clicking on the image opens a web browser to the URL
            addMouseListener( new MouseAdapter() {
                @Override public void mousePressed( MouseEvent e ) {
                    PhetServiceManager.showWebPage( donateURL );
                }
            } );
        }} );

        add( Box.createVerticalStrut( 15 ) );
        JButton donateButton = new JButton( "Make a Donation!" );
        donateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetServiceManager.showWebPage( donateURL );
            }
        } );
        add( donateButton );

        JButton noThanksButton = new JButton( "No Thanks" );
        noThanksButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dialog.dispose();
            }
        } );
        add( noThanksButton );
        add( Box.createVerticalStrut( 15 ) );
    }
}
