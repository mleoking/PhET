package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.IAskMeLaterStrategy;
import edu.colorado.phet.common.phetcommon.updates.IVersionSkipper;

/**
 * Notifies the user about a sim update that was automatically discovered.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimAutomaticUpdateDialog extends SimAbstractUpdateDialog {

    private static final String PREFEENCES_MESSAGE = PhetCommonResources.getString( "Common.updates.seePreferences" );

    private final IAskMeLaterStrategy askMeLaterStrategy;
    private final IVersionSkipper versionSkipper;
    
    public SimAutomaticUpdateDialog( Frame owner, ISimInfo simInfo, PhetVersion newVersion, IAskMeLaterStrategy askMeLaterStrategy, IVersionSkipper versionSkipper ) {
        super( owner, simInfo, newVersion );
        this.askMeLaterStrategy = askMeLaterStrategy;
        this.versionSkipper = versionSkipper;
        initGUI();
    }
    
    protected JPanel createButtonPanel( ISimInfo simInfo, final PhetVersion newVersion ) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( new UpdateButton( this, simInfo, newVersion ) );
        buttonPanel.add( Box.createHorizontalStrut( 30 ) );
        buttonPanel.add( new AskMeLaterButton( this, askMeLaterStrategy ) );
        buttonPanel.add( new SkipVersionButton( this, versionSkipper, newVersion ) );
        return buttonPanel;
    }
    
    /*
     * Message that will be added below the standard message, and above the button panel.
     */
    protected JComponent createAdditionalMessageComponent() {
        // message about how to access the Preferences dialog
        String preferencesHTML = "<html><font size=\"2\">" + PREFEENCES_MESSAGE + "</font></html>";
        return new JLabel( preferencesHTML );
    }

}
