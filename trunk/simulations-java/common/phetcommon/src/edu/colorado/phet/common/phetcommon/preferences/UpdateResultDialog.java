package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

public class UpdateResultDialog extends JDialog {
    public UpdateResultDialog( Frame parent, String title, String html ) {
        super( parent, title, true );
        init( html );
    }

    public UpdateResultDialog( Dialog parent, String title, String html ) {
        super( parent, title, true );
        init( html );
    }

    private void init( String html ) {
        JPanel contentPane = new JPanel();
        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        contentPane.add( createLogoPanel( html ), constraints );
        contentPane.add( Box.createRigidArea( new Dimension( 10, 10 ) ), constraints );
        contentPane.add( new OKButton(), constraints );
        setContentPane( contentPane );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JPanel createLogoPanel( String html ) {
        html = html.replaceAll( "@FONT_SIZE@", new PhetFont().getSize() + "pt" );
        html = html.replaceAll( "@FONT_FAMILY@", new PhetFont().getFamily() );
        PhetAboutDialog.HTMLPane copyrightLabel = new PhetAboutDialog.HTMLPane( html );

        HorizontalLayoutPanel logoPanel = new HorizontalLayoutPanel();
        logoPanel.setInsets( new Insets( 10, 10, 10, 10 ) ); // top,left,bottom,right
        logoPanel.add( copyrightLabel );

        return logoPanel;
    }

    private class OKButton extends JButton {
        private OKButton() {
            super( "OK" );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dispose();
                }
            } );
        }
    }
}
