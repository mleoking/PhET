package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

/**
 * 
 * Notifies the user about a sim update when the user requested one.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimManualUpdateDialog extends SimAbstractUpdateDialog {
    
    public SimManualUpdateDialog( Frame owner, ISimInfo simInfo, PhetVersion newVersion ) {
        super( owner, simInfo, newVersion );
        initGUI();
    }
    
    protected JPanel createButtonPanel( ISimInfo simInfo, final PhetVersion newVersion ) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( new UpdateButton( this, simInfo, newVersion ) );
        buttonPanel.add( new CancelButton( this ) );
        return buttonPanel;
    }

}
