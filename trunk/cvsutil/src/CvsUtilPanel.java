/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * CvsUtilPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CvsUtilPanel extends JPanel {
    JFileChooser dirChooser;
    File dirToMigrate;
    String newRoot;
    private JTextField newRootTF;
    private JButton migrateBtn;

    public CvsUtilPanel() {
        super();
        dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        dirChooser.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dirToMigrate = dirChooser.getSelectedFile();
            }
        } );

        migrateBtn = new JButton( "<html><center>Migrate Selected<br>Directory<html>" );
        migrateBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean  migrate = true;
                if( dirToMigrate == null ) {
                    migrate = false;
                    JOptionPane.showMessageDialog( CvsUtilPanel.this,
                                                   "No source directory selected" );
                }
                if( newRoot == null ) {
                    migrate = false;
                    JOptionPane.showMessageDialog( CvsUtilPanel.this,
                                                   "No target root specified" );
                }

                if( migrate ) {
                    String[] args = new String[] { dirToMigrate.getAbsolutePath(), newRoot };
                    try {
                        new CvsMigrate( args );
                    }
                    catch( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }
            }
        } );


        JLabel rootLabel = new JLabel( "Target CVS root:" );
        newRootTF = new JTextField( 30 );
        newRootTF.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                newRoot = newRootTF.getText();
            }
        } );


        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 10, 4, 10, 4 ),
                                                         0, 0 );
        gbc.gridwidth = 2;
        setLayout( new GridBagLayout() );

        // Layout directory chooser
        JPanel dirChooserPanel = new JPanel();
        dirChooserPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                                                                     "Choose directory to migrate" ) );
        dirChooserPanel.add( dirChooser );
        add( dirChooserPanel, gbc );

        // Layout target root text field
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add( rootLabel, gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( newRootTF, gbc );

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        add( migrateBtn, gbc );
    }
}
