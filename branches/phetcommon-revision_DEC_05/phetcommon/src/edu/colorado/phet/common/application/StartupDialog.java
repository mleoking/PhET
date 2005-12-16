/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * A dialog that lets the user know "something is happening" while the
 * application gets itself started.
 */
public class StartupDialog extends JDialog {
    private JLabel label;

    public StartupDialog( Frame owner, String title ) throws HeadlessException {
        super( owner, "Startup", false );
        setUndecorated( true );
        getRootPane().setWindowDecorationStyle( JRootPane.INFORMATION_DIALOG );
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        String labelFormat = SimStrings.get( "PhetApplication.StartupDialog.message" );
        Object[] args = {title};
        String labelString = MessageFormat.format( labelFormat, args );
        label = new JLabel( labelString );

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate( true );
        BufferedImage image = null;
        try {
            image = ImageLoader.loadBufferedImage( "images/Phet-Flatirons-logo-3-small.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        ImageIcon logo = new ImageIcon( image );

        getContentPane().setLayout( new GridBagLayout() );
        final GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                               GridBagConstraints.CENTER,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 0, 20, 0, 10 ), 0, 0 );
        gbc.gridheight = 2;
        getContentPane().add( new JLabel( logo ), gbc );
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets( 20, 10, 10, 20 );
        getContentPane().add( label, gbc );
        gbc.gridy++;
        gbc.insets = new Insets( 10, 10, 20, 20 );
        getContentPane().add( progressBar, gbc );
        pack();
        setLocation( (int)( screenSize.getWidth() / 2 - getWidth() / 2 ),
                     (int)( screenSize.getHeight() / 2 - getHeight() / 2 ) );
    }
}
