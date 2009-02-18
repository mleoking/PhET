package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Base class for sim update dialogs.
 */
public abstract class AbstractSimUpdateDialog extends PaintImmediateDialog {
    
    private static final String TRY_IT_LINK = PhetCommonResources.getString( "Common.updates.tryIt" );
    
    protected AbstractSimUpdateDialog( Frame owner, String title, final String project, final String sim, final String simName, final PhetVersion currentVersion, final PhetVersion newVersion, Locale locale) {
        super( owner, title );
        setResizable( false );
        setModal( true );
        
        // subpanels
        JPanel messagePanel = createMessagePanel( project, sim, simName, currentVersion, newVersion );
        JPanel buttonPanel = createButtonPanel( project, sim, simName, currentVersion, newVersion, locale );
        
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
    private JPanel createMessagePanel( final String project, final String sim, String simName, PhetVersion currentVersion, PhetVersion newVersion ) {
        
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
     * Those things will be added below the standard message.
     */
    protected JComponent createAdditionalMessageComponent() {
        return null;
    }
    
    /*
     * Subclasses provide their own actions via a button panel.
     */
    protected abstract JPanel createButtonPanel( final String project, final String sim, final String simName, final PhetVersion currentVersion, final PhetVersion newVersion, Locale locale );
    
    /*
     * Gets the message that compares the current version and new version.
     */
    private static String getVersionComparisonHTML( String simName, String currentVersion, String newVersion ) {
        String pattern = PhetCommonResources.getString( "Common.updates.versionComparison" );
        Object[] args = { simName, currentVersion, newVersion };
        return MessageFormat.format( pattern, args );
    }
}
