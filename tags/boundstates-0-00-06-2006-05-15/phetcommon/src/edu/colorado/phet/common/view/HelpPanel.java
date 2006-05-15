/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;

/**
 * HelpPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HelpPanel extends JPanel {
    private boolean miniHelpShowing = false;
    private String showHelpStr = SimStrings.get( "Common.HelpPanel.ShowHelp" );
    private String hideHelpStr = SimStrings.get( "Common.HelpPanel.HideHelp" );
    private String megaHelpStr = SimStrings.get( "Common.HelpPanel.MegaHelp" );
    private JButton miniHelpBtn;
    private JButton megaHelpBtn;
    private int padY = 2;
    private Module module;

    public HelpPanel( final Module module ) {
        this.module = module;

        miniHelpBtn = new JButton( showHelpStr );
        megaHelpBtn = new JButton( megaHelpStr );
        
        SwingUtils.fixButtonOpacity( miniHelpBtn );
        SwingUtils.fixButtonOpacity( megaHelpBtn );

        // Hook up listeners to the buttons that will tell the
        // module what help to show, if any
        miniHelpBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                miniHelpShowing = !miniHelpShowing;

                // If there is no megahelp, don't show the megahelp button
                if( miniHelpShowing ) {
                    setTwoButtonMode();
                }
                else {
                    setOneButtonMode();
                }
                module.setHelpEnabled( miniHelpShowing );
                // Synchronize the Help menu item.
                PhetApplication.instance().getPhetFrame().getHelpMenu().setHelpSelected( miniHelpShowing );
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
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 0, 0,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        this.setLayout( new GridBagLayout() );
        add( miniHelpBtn, gbc );
        gbc.gridy = 1;
        add( megaHelpBtn, gbc );
        if( true ) {
            return;
        }
        this.invalidate();
        this.repaint();

//        SpringLayout layout = new SpringLayout();
//        this.setLayout( layout );
//        Spring halfWidthS = FractionSpring.half( layout.getConstraint( SpringLayout.EAST, this ) );
//        Spring halfHeightS = FractionSpring.half( layout.getConstraint( SpringLayout.SOUTH, this ) );
//        Spring topOfMiniBtnS = Spring.sum( halfHeightS, Spring.minus( Spring.constant( (int)miniHelpBtn.getPreferredSize().getLength() + padY ) ) );
//        Spring leftOfMiniBtnS = Spring.sum( halfWidthS, Spring.minus( Spring.constant( (int)miniHelpBtn.getPreferredSize().getWidth() / 2 ) ) );
//        layout.putConstraint( SpringLayout.NORTH, miniHelpBtn, topOfMiniBtnS, SpringLayout.NORTH, this );
//        layout.putConstraint( SpringLayout.WEST, miniHelpBtn, leftOfMiniBtnS, SpringLayout.WEST, this );
//
//        Spring topOfMegaBtnS = Spring.sum( halfHeightS, Spring.constant( padY ) );
//        Spring leftOfMegaBtnS = Spring.sum( halfWidthS, Spring.minus( Spring.constant( (int)megaHelpBtn.getPreferredSize().getWidth() / 2 ) ) );
//        layout.putConstraint( SpringLayout.NORTH, megaHelpBtn, topOfMegaBtnS, SpringLayout.NORTH, this );
//        layout.putConstraint( SpringLayout.WEST, megaHelpBtn, leftOfMegaBtnS, SpringLayout.WEST, this );
//
//        this.invalidate();
//        this.repaint();
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
        Spring topOfMiniBtnS = Spring.sum( halfHeightS, Spring.minus( Spring.constant( (int)miniHelpBtn.getPreferredSize().getLength() / 2 )));
        Spring leftOfMiniBtnS = Spring.sum( halfWidthS, Spring.minus( Spring.constant( (int)miniHelpBtn.getPreferredSize().getWidth() / 2 )));
        layout.putConstraint( SpringLayout.NORTH, miniHelpBtn, topOfMiniBtnS, SpringLayout.NORTH, this );
        layout.putConstraint( SpringLayout.WEST, miniHelpBtn, leftOfMiniBtnS, SpringLayout.WEST, this );

        this.invalidate();
        this.repaint();
*/
    }

    private void setTwoButtonMode() {
        // Don't show the megahelp button if the module doesn't provide megahelp
        megaHelpBtn.setVisible( module.hasMegaHelp() );
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
        Spring topOfMiniBtnS = Spring.sum( halfHeightS, Spring.minus( Spring.constant( (int)miniHelpBtn.getPreferredSize().getLength() + padY )));
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

    public void setHelpEnabled( boolean enabled ) {
        miniHelpShowing = enabled;
        if( enabled ) {
            miniHelpBtn.setText( hideHelpStr );
        }
        else {
            miniHelpBtn.setText( showHelpStr );
        }
    }
    
    public boolean isHelpEnabled() {
        return miniHelpShowing;
    }
}
