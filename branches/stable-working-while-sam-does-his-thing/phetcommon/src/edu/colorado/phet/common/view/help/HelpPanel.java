/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.help;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * HelpPanel
 * <p>
 * A panel that holds buttons for showing/hiding help and mega-help
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HelpPanel extends JPanel {
    private Module module;

    public HelpPanel( final Module module ) {
        this.module = module;
        setNoHelpShowing();
    }

    private void setNoHelpShowing() {
        this.doLayout();
        this.removeAll();
        this.setLayout( new GridLayout( 1, 1 ));

        // Add a button to show onscreen help
        JButton button = new JButton( SimStrings.get( "Common.HelpPanel.ShowHelp" ) );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setHelpEnabled( true );
                setMiniHelpShowing();
            }
        } );
        this.add( button );
        revalidate();
    }

    private void setMiniHelpShowing() {
        this.removeAll();
        int numRows = module.hasMegaHelp() ? 2 : 1;
        this.setLayout( new GridLayout( numRows, 1 ));

        // Add the button for hiding onscreen help
        JButton helpBtn = new JButton( SimStrings.get( "Common.HelpPanel.HideHelp" ) );
        helpBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setHelpEnabled( false );
                setNoHelpShowing();
            }
        } );
        this.add( helpBtn );

        // If the module has megahelp, add a button to show it
        if( module.hasMegaHelp() ) {
            JButton megaHelpBtn = new JButton( "Common.HelpPanel.MegaHelp" );
            megaHelpBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.showMegaHelp();
                }
            } );
            this.add( megaHelpBtn );
        }
        revalidate();
    }
}
