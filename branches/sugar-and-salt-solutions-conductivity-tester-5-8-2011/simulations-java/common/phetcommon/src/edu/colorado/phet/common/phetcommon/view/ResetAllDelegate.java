// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view;

import java.awt.*;
import java.util.Vector;

import javax.swing.*;

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
    private boolean confirmationEnabled;

    /**
     * @param resettables things to reset
     * @param parent      parent component for the confirmation dialog
     */
    public ResetAllDelegate( final Resettable[] resettables, final Component parent ) {
        this.resettables = new Vector<Resettable>();
        for ( Resettable resettable : resettables ) {
            this.resettables.add( resettable );
        }
        this.parent = parent;
        this.confirmationEnabled = true;
    }

    public void setConfirmationEnabled( boolean confirmationEnabled ) {
        this.confirmationEnabled = confirmationEnabled;
    }

    public boolean isConfirmationEnabled() {
        return confirmationEnabled;
    }

    public void addResettable( Resettable resettable ) {
        resettables.add( resettable );
    }

    public void removeResettable( Resettable resettable ) {
        resettables.remove( resettable );
    }

    /**
     * Resets all Resettables, with optional confirmation.
     */
    public void resetAll() {
        if ( !confirmationEnabled || confirmReset() ) {
            for ( Resettable resettable : resettables ) {
                resettable.reset();
            }
        }
    }

    /*
    * Opens a confirmation dialog, returns true if the user selects "Yes".
    */
    private boolean confirmReset() {
        String message = PhetCommonResources.getInstance().getLocalizedString( "ControlPanel.message.confirmResetAll" );
        String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
        int option = PhetOptionPane.showYesNoDialog( parent, message, title );
        return ( option == JOptionPane.YES_OPTION );
    }
}