/* Copyright 2010, University of Colorado */ 

package edu.colorado.phet.common.phetcommon.view;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;

/**
 * Delegate for "Reset All" behavior.
 * Calling resetAll opens a confirmation dialog. 
 * If the user confirms, then all Resettables are reset.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ResetAllDelegate {
    
    private final Vector<Resettable> resettables;
    private final Component parent;
    
    /** 
     * @param resettables things to reset
     * @param parent parent component for the confirmation dialog
     */
    public ResetAllDelegate( final Resettable[] resettables, final Component parent ) {
        this.resettables = new Vector<Resettable>();
        for ( Resettable resettable : resettables ) {
            this.resettables.add( resettable );
        }
        this.parent = parent;
    }
    
    /**
     * Requests confirmation. If affirmative, resets all Resettables.
     */
    public void resetAll() {
        String message = PhetCommonResources.getInstance().getLocalizedString( "ControlPanel.message.confirmResetAll" );
        String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
        int option = PhetOptionPane.showYesNoDialog( parent, message, title );
        if ( option == JOptionPane.YES_OPTION ) {
            for ( Resettable resettable : resettables ) {
                resettable.reset();
            }
        }
    }
    
    public void addResettable( Resettable resettable ) {
        resettables.add( resettable );
    }
    
    public void removeResettable( Resettable resettable ) {
        resettables.remove( resettable );
    }
}