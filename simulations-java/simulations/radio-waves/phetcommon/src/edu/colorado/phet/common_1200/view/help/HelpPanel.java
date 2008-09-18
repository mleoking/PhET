/**
 * Class: HelpPanel
 * Package: edu.colorado.phet.common.view.help
 * Author: Another Guy
 * Date: May 25, 2004
 */
package edu.colorado.phet.common_1200.view.help;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_1200.application.Module;
import edu.colorado.phet.radiowaves.coreadditions.FractionSpring;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpPanel extends JPanel {
    private boolean miniHelpShowing = false;
    private String showHelpStr = SimStrings.get( "Common.HelpPanel.ShowHelp" );
    private String hideHelpStr = SimStrings.get( "Common.HelpPanel.HideHelp" );
    private String megaHelpStr = SimStrings.get( "Common.HelpPanel.MegaHelp" );
    private JButton miniHelpBtn;
    private JButton megaHelpBtn;
    private int padY = 2;

    public HelpPanel( final Module module ) {
        miniHelpBtn = new JButton( showHelpStr );
        megaHelpBtn = new JButton( megaHelpStr );

        // Hook up listeners to the buttons that will tell the
        // module what help to show, if any
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

        add( miniHelpBtn );
        add( megaHelpBtn );
        layoutPanel();
        setOneButtonMode();
    }

    private void layoutPanel() {
        setPreferredSize( new Dimension( (int)Math.max( miniHelpBtn.getPreferredSize().getWidth(),
                                                        megaHelpBtn.getPreferredSize().getWidth() ),
                                         (int)( miniHelpBtn.getPreferredSize().getHeight()
                                                + megaHelpBtn.getPreferredSize().getHeight() + padY * 2 ) ) );
        SpringLayout layout = new SpringLayout();
        this.setLayout( layout );
        Spring halfWidthS = FractionSpring.half( layout.getConstraint( SpringLayout.EAST, this ) );
        Spring halfHeightS = FractionSpring.half( layout.getConstraint( SpringLayout.SOUTH, this ) );
        Spring topOfMiniBtnS = Spring.sum( halfHeightS, Spring.minus( Spring.constant( (int)miniHelpBtn.getPreferredSize().getHeight() + padY ) ) );
        Spring leftOfMiniBtnS = Spring.sum( halfWidthS, Spring.minus( Spring.constant( (int)miniHelpBtn.getPreferredSize().getWidth() / 2 ) ) );
        layout.putConstraint( SpringLayout.NORTH, miniHelpBtn, topOfMiniBtnS, SpringLayout.NORTH, this );
        layout.putConstraint( SpringLayout.WEST, miniHelpBtn, leftOfMiniBtnS, SpringLayout.WEST, this );

        Spring topOfMegaBtnS = Spring.sum( halfHeightS, Spring.constant( padY ) );
        Spring leftOfMegaBtnS = Spring.sum( halfWidthS, Spring.minus( Spring.constant( (int)megaHelpBtn.getPreferredSize().getWidth() / 2 ) ) );
        layout.putConstraint( SpringLayout.NORTH, megaHelpBtn, topOfMegaBtnS, SpringLayout.NORTH, this );
        layout.putConstraint( SpringLayout.WEST, megaHelpBtn, leftOfMegaBtnS, SpringLayout.WEST, this );

        this.invalidate();
        this.repaint();
    }

    private void setOneButtonMode() {
        megaHelpBtn.setVisible( false );
        miniHelpBtn.setText( showHelpStr );

        // If you want the two buttons to pop up centered in the panel, comment out the next line
        // and uncomment the block following it
        layoutPanel();
/*
        setPreferredSize( new Dimension( 100, 70 ));
        SpringLayout layout = new SpringLayout();
        this.setLayout( layout );
        Spring halfWidthS =  FractionSpring.half( layout.getConstraint( SpringLayout.EAST, this ));
        Spring halfHeightS =  FractionSpring.half( layout.getConstraint( SpringLayout.SOUTH, this ));
        Spring topOfMiniBtnS = Spring.sum( halfHeightS, Spring.minus( Spring.constant( (int)miniHelpBtn.getPreferredSize().getHeight() / 2 )));
        Spring leftOfMiniBtnS = Spring.sum( halfWidthS, Spring.minus( Spring.constant( (int)miniHelpBtn.getPreferredSize().getWidth() / 2 )));
        layout.putConstraint( SpringLayout.NORTH, miniHelpBtn, topOfMiniBtnS, SpringLayout.NORTH, this );
        layout.putConstraint( SpringLayout.WEST, miniHelpBtn, leftOfMiniBtnS, SpringLayout.WEST, this );

        this.invalidate();
        this.repaint();
*/
    }

    private void setTwoButtonMode() {
        megaHelpBtn.setVisible( true );
        miniHelpBtn.setText( hideHelpStr );

        // If you want the two buttons to pop up centered in the panel, comment out the next line
        // and uncomment the block following it
        layoutPanel();
/*
        setPreferredSize( new Dimension( 100, 70 ));
        SpringLayout layout = new SpringLayout();
        this.setLayout( layout );
        int padY = 2;
        Spring halfWidthS =  FractionSpring.half( layout.getConstraint( SpringLayout.EAST, this ));
        Spring halfHeightS =  FractionSpring.half( layout.getConstraint( SpringLayout.SOUTH, this ));
        Spring topOfMiniBtnS = Spring.sum( halfHeightS, Spring.minus( Spring.constant( (int)miniHelpBtn.getPreferredSize().getHeight() + padY )));
        Spring leftOfMiniBtnS = Spring.sum( halfWidthS, Spring.minus( Spring.constant( (int)miniHelpBtn.getPreferredSize().getWidth() / 2 )));
        layout.putConstraint( SpringLayout.NORTH, miniHelpBtn, topOfMiniBtnS, SpringLayout.NORTH, this );
        layout.putConstraint( SpringLayout.WEST, miniHelpBtn, leftOfMiniBtnS, SpringLayout.WEST, this );

        Spring topOfMegaBtnS = Spring.sum( halfHeightS, Spring.constant( padY ));
        Spring leftOfMegaBtnS = Spring.sum( halfWidthS, Spring.minus( Spring.constant( (int)megaHelpBtn.getPreferredSize().getWidth() / 2 )));
        layout.putConstraint( SpringLayout.NORTH, megaHelpBtn, topOfMegaBtnS, SpringLayout.NORTH, this );
        layout.putConstraint( SpringLayout.WEST, megaHelpBtn, leftOfMegaBtnS, SpringLayout.WEST, this );

        this.invalidate();
        this.repaint();
*/
    }

}
