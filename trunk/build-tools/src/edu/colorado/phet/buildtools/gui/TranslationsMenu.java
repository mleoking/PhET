package edu.colorado.phet.buildtools.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.translate.CommonTranslationDeployClient;
import edu.colorado.phet.buildtools.translate.TranslationDeployClient;

/**
 * Translations menu that holds options for deploying simualtion and common translations
 */
public class TranslationsMenu extends JMenu {

    private File trunk;

    public TranslationsMenu( final File trunk ) {
        super( "Translations" );

        this.trunk = trunk;

        JMenuItem deployItem = new JMenuItem( "Deploy Simulation Translations..." );
        deployItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    TranslationDeployClient translationDeployClient = new TranslationDeployClient( trunk );
                    translationDeployClient.startClient();
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        add( deployItem );

        JMenuItem deployCommonItem = new JMenuItem( "Deploy Common Translations..." );
        deployCommonItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle( "Choose a common translation to deploy" );
                int ret = fileChooser.showOpenDialog( null );
                if ( ret != JFileChooser.APPROVE_OPTION ) {
                    System.out.println( "File was not selected, aborting" );
                    return;
                }

                File resourceFile = fileChooser.getSelectedFile();

                new CommonTranslationDeployClient( resourceFile, trunk ).deployCommonTranslation();

                JOptionPane.showMessageDialog( null, "The instructions to complete the common translation deployment have been printed to the console", "Instructions", JOptionPane.INFORMATION_MESSAGE );
            }
        } );
        add( deployCommonItem );
    }
}
