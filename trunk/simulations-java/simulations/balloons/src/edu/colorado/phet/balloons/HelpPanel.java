/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14417 $
 * Date modified : $Date:2007-04-12 22:25:24 -0600 (Thu, 12 Apr 2007) $
 */
package edu.colorado.phet.balloons;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * HelpPanel
 *
 * @author Ron LeMaster
 * @version $Revision:14417 $
 */
public class HelpPanel extends JPanel {
    private boolean miniHelpShowing = false;
    private String showHelpStr = BalloonsResources.getString( "Common.HelpPanel.ShowHelp" );
    private String hideHelpStr = BalloonsResources.getString( "Common.HelpPanel.HideHelp" );
    private String megaHelpStr = BalloonsResources.getString( "Common.HelpPanel.MegaHelp" );
    private JButton miniHelpBtn;
    private JButton megaHelpBtn;
    private int padY = 2;
    private IHelp module;

    public HelpPanel( final IHelp module ) {
        this.module = module;

        miniHelpBtn = new JButton( showHelpStr );
        megaHelpBtn = new JButton( megaHelpStr );

        // Hook up listeners to the buttons that will tell the
        // module what help to show, if any
        miniHelpBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                miniHelpShowing = !miniHelpShowing;

                // If there is no megahelp, don't show the megahelp button
                if ( miniHelpShowing ) {
                    setTwoButtonMode();
                }
                else {
                    setOneButtonMode();
                }
                module.setHelpEnabled( miniHelpShowing );
            }
        } );
        megaHelpBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.showMegaHelp();
            }
        } );

        add( miniHelpBtn );
        add( megaHelpBtn );
        layoutPanel();
        setOneButtonMode();
    }

    private void layoutPanel() {
        setPreferredSize( new Dimension( (int) Math.max( miniHelpBtn.getPreferredSize().getWidth(),
                                                         megaHelpBtn.getPreferredSize().getWidth() ),
                                         (int) ( miniHelpBtn.getPreferredSize().getHeight()
                                                 + megaHelpBtn.getPreferredSize().getHeight() + padY * 2 ) ) );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 0, 0,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        this.setLayout( new GridBagLayout() );
        add( miniHelpBtn, gbc );
        gbc.gridy = 1;
        add( megaHelpBtn, gbc );
    }

    private void setOneButtonMode() {
        megaHelpBtn.setVisible( false );
        miniHelpBtn.setText( showHelpStr );

        layoutPanel();
    }

    private void setTwoButtonMode() {
        // Don't show the megahelp button if the module doesn't provide megahelp
        megaHelpBtn.setVisible( module.hasMegaHelp() );
        miniHelpBtn.setText( hideHelpStr );

        // If you want the two buttons to pop up centered in the panel, comment out the next line
        // and uncomment the block following it
        layoutPanel();
    }
}
