/**
 * Class: HelpPanel
 * Package: edu.colorado.phet.common.view.help
 * Author: Another Guy
 * Date: May 25, 2004
 */
package edu.colorado.phet.common.view.help;

import edu.colorado.phet.common.application.Module;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpPanel extends JPanel {
    private boolean miniHelpShowing = false;
    private String showHelpStr = "Help!";
    private String hideHelpStr = "Hide Help";
    private String megaHelpStr = "Megahelp";
    private JButton miniHelpBtn;
    private JButton megaHelpBtn;
    private Module module;

    public HelpPanel( final Module module ) {
        this.module = module;
        miniHelpBtn = new JButton( showHelpStr );
        megaHelpBtn = new JButton( megaHelpStr );
        miniHelpBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                miniHelpShowing = !miniHelpShowing;
                if( miniHelpShowing ) {
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
//        setLayout( new GridBagLayout() );
//        setLayout( new SpringLayout() );

//        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        init();
        setOneButtonMode();
    }

    private void init() {
        this.setLayout( new GridBagLayout());
        Insets insets = new Insets( 2, 2, 2, 2 );
//        GridBagConstraints gbc = new GridBagConstraints();
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 0, 0,
                                                         GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0 );
        add( miniHelpBtn, gbc );
//        add( miniHelpBtn );//, gbc );
//        gbc.gridy = 2;      /
        gbc.gridy++;
        add( megaHelpBtn, gbc );
//        add( megaHelpBtn );//, gbc );
    }

    private void setOneButtonMode() {
        megaHelpBtn.setVisible( false );
        miniHelpBtn.setText( showHelpStr );
    }

    private void setTwoButtonMode() {
        megaHelpBtn.setVisible( true );
        miniHelpBtn.setText( hideHelpStr );
    }

}
