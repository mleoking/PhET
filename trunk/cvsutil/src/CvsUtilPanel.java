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
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
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
    boolean makeBackup;
    private JTextField newRootTF;
    private JButton migrateBtn;
    private JCheckBox backupCB;
    private JLabel rootLabel;

    /**
     *
     */
    public CvsUtilPanel() {
        createControls();
        layoutControls();
    }

    /**
     *
     */
    private void createControls() {
        // The file choose
        dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );

        // The text field that gets the new root, and a label for it
        rootLabel = new JLabel( "Target CVS root:" );
        newRootTF = new JTextField( 30 );
        newRootTF.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                newRoot = newRootTF.getText();
            }
        } );
        newRootTF.addFocusListener( new FocusListener() {
            public void focusGained( FocusEvent e ) {
            }

            public void focusLost( FocusEvent e ) {
                newRoot = newRootTF.getText();
            }
        } );

        // The check box for requesting a backup
        backupCB = new JCheckBox( "Make backup before migrating" );
        backupCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                makeBackup = backupCB.isSelected();
            }
        } );

        // The button that performs the migration
        migrateBtn = new JButton( "<html><center>Migrate Selected<br>Directory<html>" );
        migrateBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean migrate = true;
                dirToMigrate = dirChooser.getSelectedFile();
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
                    String backupFlag = makeBackup ? "-b" : "";
                    String[] args = new String[]{dirToMigrate.getAbsolutePath(), newRoot, backupFlag};
                    try {
                        new CvsMigrate( args );
                    }
                    catch( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }
            }
        } );
    }

    /**
     *
     */
    private void layoutControls() {
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 10, 4, 10, 4 ),
                                                         0, 0 );
        gbc.gridwidth = 2;
        setLayout( new GridBagLayout() );

        // The directory chooser
        JPanel dirChooserPanel = new JPanel();
        dirChooserPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                                                                     "Choose directory to migrate" ) );
        dirChooserPanel.add( dirChooser );
        add( dirChooserPanel, gbc );

        // The target root text field
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add( rootLabel, gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( newRootTF, gbc );

        // The backup checkbox
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        add( backupCB, gbc );

        // The migrate button
        gbc.gridy++;
        add( migrateBtn, gbc );
    }
}
