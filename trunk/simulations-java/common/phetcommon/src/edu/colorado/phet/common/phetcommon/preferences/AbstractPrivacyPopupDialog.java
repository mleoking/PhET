package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.GrayRectWorkaroundDialog;
import edu.colorado.phet.common.phetcommon.tracking.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jan 15, 2009
 * Time: 3:35:01 PM
 */
public abstract class AbstractPrivacyPopupDialog extends GrayRectWorkaroundDialog {
    private static final String CLOSE_BUTTON = PhetCommonResources.getString( "Common.choice.close" );
    private String title;
    public AbstractPrivacyPopupDialog( String title,Frame frame ) {
        super( frame );
        this.title=title;
    }

    public AbstractPrivacyPopupDialog( String title,Dialog owner ) {
        super( owner );
        this.title=title;
    }

    protected void init( ITrackingInfo trackingInfo ) {

        setTitle( title);
        setModal( true );
        setResizable( false ); //TODO layout doesn't adjust properly when resized

        JComponent description = createDescription();
        JComponent report = createReport( trackingInfo );
        JComponent buttonPanel = createButtonPanel();

        JPanel panel = new JPanel();

        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 5, 5, 5, 5 ) ); // top, left, bottom, right
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( description, row++, column );
        layout.addComponent( report, row++, column );
        layout.addFilledComponent( buttonPanel, row++, column, GridBagConstraints.HORIZONTAL );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    protected abstract JComponent createDescription();

    protected abstract JComponent createReport( ITrackingInfo trackingInfo );

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        JButton closeButton = new JButton( CLOSE_BUTTON );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        });
        panel.add( closeButton );
        return panel;
    }
}
