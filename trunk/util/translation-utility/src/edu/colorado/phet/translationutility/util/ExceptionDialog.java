/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.util;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.translationutility.TUResources;

/**
 * ExceptionDialog notifies the user of an exception, and shows important information
 * that should be reported to PhET for troubleshooting.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExceptionDialog extends JDialog {

    public ExceptionDialog( Frame owner, String title, Exception e ) {
        super( owner );
        setModal( true );
        setTitle( title );
        setResizable( false );
        
        JLabel errorMessage = new JLabel( "ERROR: " + e.getMessage() );
        
        JLabel reportLabel = new JLabel( "<html>To report this problem, send an email to phethelp@colorado.edu.<br>Copy-&-paste the information shown below, and describe how to reproduce the problem." );
        
        String infoString = "Translation Utility version: " + TUResources.getVersion() + "\n";
        infoString += "OS version: " + System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" ) + "\n";
        infoString += "Java version: " + System.getProperty( "java.version" ) + "\n";
        infoString += "\nStack trace:\n";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        e.printStackTrace( pw );
        infoString += sw.toString();
        
        JTextArea stackTraceTextArea = new JTextArea( infoString );
        stackTraceTextArea.setEditable( false );
        JScrollPane scrollPane = new JScrollPane( stackTraceTextArea );
        int width = (int) 550;
        int height = (int) Math.min( 200, scrollPane.getPreferredSize().getHeight() );
        scrollPane.setPreferredSize( new Dimension( width, height ) );
        
        JPanel infoPanel = new JPanel();
        EasyGridBagLayout infoLayout = new EasyGridBagLayout( infoPanel );
        infoPanel.setLayout( infoLayout );
        int row = 0;
        int column = 0;
        infoLayout.addComponent( errorMessage, row++, column, 2, 1 );
        infoLayout.addComponent( Box.createVerticalStrut( 15 ), row++, column );
        infoLayout.addComponent( reportLabel, row++, column, 2, 1 );
        infoLayout.addComponent( Box.createVerticalStrut( 15 ), row++, column );
        infoLayout.addComponent( scrollPane, row++, column, 2, 1 );
        
        JButton closeButton = new JButton( "Close" );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( closeButton );
        
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        row = 0;
        column = 0;
        layout.addComponent( infoPanel, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addAnchoredComponent( buttonPanel, row++, column, GridBagConstraints.CENTER );
        
        getContentPane().add( panel );
        pack();
        if ( owner == null ) {
            SwingUtils.centerWindowOnScreen( this );
        }
    }
    
    // test
    public static void main( String[] args ) {
        Exception e = new IOException( "my message" );
        JDialog dialog = new ExceptionDialog( null, "my title", e );
        dialog.setVisible( true );
    }
}
