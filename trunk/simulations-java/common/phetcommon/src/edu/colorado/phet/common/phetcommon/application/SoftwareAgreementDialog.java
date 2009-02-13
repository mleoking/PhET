
package edu.colorado.phet.common.phetcommon.application;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.HTMLEditorPane;

/**
 * Displays the full text of PhET's Software Use Agreement.
 */
public class SoftwareAgreementDialog extends GrayRectWorkaroundDialog {

    private static final String TITLE = PhetCommonResources.getString( "Common.softwareAgreement.title" );
    private static final String CLOSE_BUTTON = PhetCommonResources.getString( "Common.choice.close" );
    
    public SoftwareAgreementDialog( Frame owner ) {
        super( owner );
        init();
    }
    
    public SoftwareAgreementDialog( Dialog owner ) {
        super( owner );
        init();
    }
    
    private void init() {
        
        setTitle( TITLE );
        setModal( true );
        setResizable( false ); //TODO layout doesn't adjust properly when resized
        
        JComponent agreementPanel = createAgreementPanel();
        JComponent buttonPanel = createButtonPanel();

        JPanel panel = new JPanel();

        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 5, 5, 5, 5 ) ); // top, left, bottom, right
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( agreementPanel, row++, column );
        layout.addFilledComponent( buttonPanel, row++, column, GridBagConstraints.HORIZONTAL );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

  protected JComponent createAgreementPanel() {
        
        String html = HTMLUtils.createStyledHTMLFromFragment( SoftwareAgreement.getInstance().getContent() );
        HTMLEditorPane htmlEditorPane = new HTMLEditorPane( html );
        htmlEditorPane.setBackground( Color.WHITE );
        
        JScrollPane scrollPane = new JScrollPane( htmlEditorPane );
        scrollPane.setPreferredSize( new Dimension( scrollPane.getPreferredSize().width + 30, 300 ) );
        
        // this ensures that the first line of text is at the top of the scrollpane
        htmlEditorPane.setCaretPosition( 0 );
        
        return scrollPane;
    }
  
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
