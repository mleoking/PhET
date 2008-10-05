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
    private JPanel contentPane;
    private GridBagConstraints constraints;
    private Window window;

    public UpdateResultDialog( Frame parent, String title, String html ) {
        super( parent, title, true );
        init( html );
        this.window = parent;
    }

    public UpdateResultDialog( Dialog parent, String title, String html ) {
        super( parent, title, true );
        init( html );
        this.window = parent;
    }

    private void init( String html ) {
        contentPane = new JPanel();
        contentPane.setLayout( new GridBagLayout() );
        constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        contentPane.add( createLogoPanel( html ), constraints );
        contentPane.add( Box.createRigidArea( new Dimension( 10, 10 ) ), constraints );
        setContentPane( contentPane );
        pack();

        center();
    }

    public void addOKButton() {
        contentPane.add( new OKButton(), constraints );
    }

    public JComponent createOKButton() {
        return new OKButton();
    }

    protected void center() {
        if ( window == null ) {
            SwingUtils.centerWindowOnScreen( this );
        }
        else {
            SwingUtils.centerDialogInParent( this );
        }
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

    public static UpdateResultDialog createDialog( Window window, String title, String text ) {
        if ( window instanceof Frame || window == null ) {
            return new UpdateResultDialog( (Frame) window, title, text );
        }
        else if ( window instanceof Dialog ) {
            return new UpdateResultDialog( (Dialog) window, title, text );
        }
        else {
            throw new RuntimeException( "Illegal window type: " + window.getClass() );
        }
    }

    public static void showDialog( Window window, String title, String text ) {
        createDialog( window, title, text ).setVisible( true );
    }

    public void addComponent( JComponent component ) {
        contentPane.add( component, constraints );
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
