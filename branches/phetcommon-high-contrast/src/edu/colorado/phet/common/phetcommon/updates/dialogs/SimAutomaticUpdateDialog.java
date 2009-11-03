package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
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

    public static void main(String[] args) {
        System.out.println( "I'm running!" );
        PhetApplicationConfig config = new PhetApplicationConfig( args, "moving-man" );
        final SimAutomaticUpdateDialog dialog = new SimAutomaticUpdateDialog( new JFrame(), config, new PhetVersion("1", "00", "05", "30000", "1234567890"), new IAskMeLaterStrategy() {
            public void setStartTime(long time) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public long getStartTime() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public void setDuration(long duration) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public long getDuration() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public boolean isDurationExceeded() {
                return true;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }, new IVersionSkipper() {
            public void setSkippedVersion(int skipVersion) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public int getSkippedVersion() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public boolean isSkipped(int version) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        dialog.setVisible( true );

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                dialog.setSize( 800, 800 );
            }
        });


    }
}
