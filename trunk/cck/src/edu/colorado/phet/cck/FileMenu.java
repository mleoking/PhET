/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck;

import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.io.FileOpen;
import edu.colorado.phet.cck.io.FileSave;
import edu.colorado.phet.persistence.CckRemotePersistence;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 6, 2003
 * Time: 9:49:43 PM
 * Copyright (c) Dec 6, 2003 by Sam Reid
 */
public class FileMenu extends JMenu {
    public FileMenu( final CCK2Module module, final JFrame frame ) {
        super( "File" );
        setMnemonic( 'f' );
        JMenu fileMenu = this;


        JMenuItem newItem = new JMenuItem( "New" );
        newItem.setMnemonic( 'n' );
        newItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearCircuit();
            }
        } );

        JMenuItem save = new JMenuItem( "Save" );
        save.setMnemonic( 's' );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                FileSave fs = new FileSave();
                fs.save( module.getCircuit(), frame );
            }
        } );
        JMenuItem open = new JMenuItem( "Open" );
        open.setMnemonic( 'o' );
        open.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                FileOpen fo = new FileOpen();
                Circuit c = fo.open( frame );
                module.setCircuit( c );
            }
        } );
        final JMenuItem submit = new JMenuItem( "Submit" );
        submit.setMnemonic( 'u' );
        submit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String name = JOptionPane.showInputDialog( "Enter your first and last name." );
                if( name == null ) {
                    JOptionPane.showMessageDialog( submit, "Submission Cancelled" );
                    return;
                }
                else {
                    try {
                        String xml = new FileSave().toXMLString( module.getCircuit() );
                        CckRemotePersistence crp = new CckRemotePersistence( "http://cosmos.colorado.edu/phet/cckStore",
                                                                             name );
                        crp.store( xml );
                        JOptionPane.showMessageDialog( submit, "Submission Complete for: " + name );
                    }
                    catch( Exception ex ) {
                        JOptionPane.showMessageDialog( submit, "Could not submit because of: " + ex.toString() );
                    }
                }
            }
        } );

        JMenuItem show = new JMenuItem( "View Circuit Data" );
        show.setMnemonic( 'd' );
        show.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    String str = new FileSave().toXMLString( module.getCircuit() );
                    str.replaceAll( "//>", "//>/n" );
                    JFrame jf = new JFrame( "Circuit XML" );
                    JTextArea area = new JTextArea( str, 40, 40 );
                    // Enable line-wrapping
                    area.setLineWrap( true );
                    area.setWrapStyleWord( false );

                    area.setEditable( false );
                    JScrollPane jsp = new JScrollPane( area );
                    jf.setContentPane( jsp );
                    jf.setVisible( true );
                    jf.pack();
//                    area.setText(str);
                }
                catch( ValidationException e1 ) {
                    e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
                catch( MarshalException e1 ) {
                    e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
            }
        } );
        fileMenu.add( newItem );
        fileMenu.addSeparator();
        fileMenu.add( save );
        fileMenu.add( open );
        fileMenu.addSeparator();
        fileMenu.add( show );
        fileMenu.add( submit );
        fileMenu.addSeparator();
        JMenuItem exit = new JMenuItem( "Exit" );
        exit.setMnemonic( 'x' );
        exit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
        fileMenu.add( exit );
    }
}
