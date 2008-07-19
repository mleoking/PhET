/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * HelpButton is a button for toggling module help.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HelpButton extends JButton {
    
    public static final String SHOW_HELP = PhetCommonResources.getInstance().getLocalizedString( "Common.HelpPanel.ShowHelp" );
    public static final String HIDE_HELP = PhetCommonResources.getInstance().getLocalizedString( "Common.HelpPanel.HideHelp" );

    private final Module _module;
    
    public HelpButton( Module module ) {
        super();
        
        _module = module;
        
        setEnabled( module.hasHelp() );
        
        // set button to maximum width
        setText( HIDE_HELP );
        double hideWidth = getPreferredSize().getWidth();
        setText( SHOW_HELP );
        double showWidth = getPreferredSize().getWidth();
        setPreferredSize( new Dimension( (int) Math.max( hideWidth, showWidth ), (int) getPreferredSize().getHeight() ) );
        
        // toggle help when the button is pressed
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setHelpEnabled( !isHelpEnabled() );
            }
        } );
    }
    
    public void setHelpEnabled( boolean b ) {
        if ( b != isHelpEnabled() ) {
            setText( b ? HIDE_HELP : SHOW_HELP );
            _module.setHelpEnabled( b );
        }
    }
    
    public boolean isHelpEnabled() {
        return getText().equals( HIDE_HELP );
    }
}
