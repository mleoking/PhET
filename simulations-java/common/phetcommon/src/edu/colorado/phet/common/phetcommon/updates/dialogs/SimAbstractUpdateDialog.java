package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.updates.IAskMeLaterStrategy;
import edu.colorado.phet.common.phetcommon.updates.IVersionSkipper;
import edu.colorado.phet.common.phetcommon.updates.SimUpdater;
import edu.colorado.phet.common.phetcommon.util.DeploymentScenario;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Base class for sim update dialogs.
 */
public abstract class SimAbstractUpdateDialog extends PaintImmediateDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.updates.updateAvailable" );
    private static final String ASK_ME_LATER_BUTTON = PhetCommonResources.getString( "Common.updates.askMeLater" );
    private static final String SKIP_UPDATE_BUTTON = PhetCommonResources.getString( "Common.updates.skipThisUpdate" );
    private static final String CANCEL_BUTTON = PhetCommonResources.getString( "Common.choice.cancel" );
    private static final String TRY_IT_LINK = PhetCommonResources.getString( "Common.updates.tryIt" );

    private final ISimInfo simInfo;
    private final PhetVersion newVersion;
    
    protected SimAbstractUpdateDialog( Frame owner, ISimInfo simInfo, PhetVersion newVersion ) {
        super( owner, TITLE );
        setModal( true );
        setResizable( false );
        this.simInfo = simInfo;
        this.newVersion = newVersion;
    }
    
    /*
     * Subclass must call this at the end of their constructor,
     * so that the GUI is initialized *after* member data is initialized. 
     */
    protected void initGUI() {
        
        // subpanels
        JPanel messagePanel = createMessagePanel( simInfo, newVersion );
        JPanel buttonPanel = createButtonPanel( simInfo, newVersion );
        
        // main panel
        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( messagePanel, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addAnchoredComponent( buttonPanel, row++, column, GridBagConstraints.CENTER );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    /*
     * Message (top) panel, common to all update dialogs.
     */
    private JPanel createMessagePanel( ISimInfo simInfo, PhetVersion newVersion ) {
        
        final String project = simInfo.getProjectName();
        final String sim = simInfo.getFlavor();
        String simName = simInfo.getName();
        PhetVersion currentVersion = simInfo.getVersion();
        
        // information about the update that was found
        JLabel versionComparisonLabel = new JLabel( getVersionComparisonHTML( simName, currentVersion.formatForTitleBar(), newVersion.formatForTitleBar() ) );

        // link to sim's web page
        String tryItHtml = "<html><u>" + TRY_IT_LINK + "</u></html>";
        JLabel tryItLink = new JLabel( tryItHtml );
        tryItLink.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        tryItLink.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                PhetServiceManager.showSimPage( project, sim );
            }
        } );
        tryItLink.setForeground( Color.blue );

        // Subclass-specific messages
        JComponent additionlMessageComponent = createAdditionalMessageComponent();

        // layout
        JPanel messagePanel = new JPanel();
        messagePanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( messagePanel );
        messagePanel.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( versionComparisonLabel, row++, col );
        layout.addComponent( Box.createVerticalStrut( 5 ), row++, col );
        layout.addComponent( tryItLink, row++, col );
        if ( additionlMessageComponent != null ) {
            layout.addComponent( Box.createVerticalStrut( 5 ), row++, col );
            layout.addComponent( additionlMessageComponent, row++, col );
        }

        return messagePanel;
    }
    
    /*
     * Subclasses can override this if they have additional things to add to the message area.
     * Those things will be added below the standard message, and above the buttons.
     */
    protected JComponent createAdditionalMessageComponent() {
        return null;
    }
    
    /*
     * Subclasses provide their own actions via a button panel.
     */
    protected abstract JPanel createButtonPanel( ISimInfo simInfo, PhetVersion newVersion );
    
    /*
     * Gets the message that compares the current version and new version.
     */
    private static String getVersionComparisonHTML( String simName, String currentVersion, String newVersion ) {
        String pattern = PhetCommonResources.getString( "Common.updates.versionComparison" );
        Object[] args = { simName, currentVersion, newVersion };
        return MessageFormat.format( pattern, args );
    }
    
    protected static class UpdateButton extends JButton {
        public UpdateButton( final JDialog dialog, final ISimInfo simInfo, final PhetVersion newVersion ) {
            super( PhetCommonResources.getString( "Common.updates.updateNow" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dialog.dispose();
                    if ( DeploymentScenario.getInstance().isOnline() ) {
                        PhetServiceManager.showSimPage( simInfo.getProjectName(), simInfo.getFlavor() );
                    }
                    else {
                        new SimUpdater().updateSim( simInfo, newVersion );
                    }
                }
            } );
        }
    }
    
    protected static class AskMeLaterButton extends JButton {
        public AskMeLaterButton( final JDialog dialog, final IAskMeLaterStrategy askMeLaterStrategy ) {
            super( ASK_ME_LATER_BUTTON );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    askMeLaterStrategy.setStartTime( System.currentTimeMillis() );
                    dialog.dispose();
                }
            } );
        }
    }
    
    protected static class SkipVersionButton extends JButton {
        public SkipVersionButton( final JDialog dialog, final IVersionSkipper versionSkipper, final PhetVersion newVersion ) {
            super( SKIP_UPDATE_BUTTON );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    versionSkipper.setSkippedVersion( newVersion.getRevisionAsInt() );
                    dialog.dispose();
                }
            } );
        }
    }
    
    protected static class CancelButton extends JButton {
        public CancelButton( final JDialog dialog ) {
            super( CANCEL_BUTTON );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dialog.dispose();
                }
            } );
        }
    }
    
}
