// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jmolphet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;

/**
 * Console for Jmol, provides an editable area for entering scripts and a read-only area for viewing status.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JmolConsole extends JPanel {

    public JmolConsole( final JmolViewer viewer ) {

        // script input is done in a scrolling editable text area
        JLabel inputLabel = new JLabel( "Jmol script:" );
        final JTextArea inputArea = new JTextArea() {{
            setLineWrap( true );
        }};
        JScrollPane inputPane = new JScrollPane( inputArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        inputPane.setPreferredSize( new Dimension( 500, 200 ) );

        // output is in a scrolling read-only text area
        JLabel outputLabel = new JLabel( "Jmol status:" );
        final JTextArea outputArea = new JTextArea() {{
            setLineWrap( true );
            setEditable( false );
        }};
        JScrollPane outputPane = new JScrollPane( outputArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        outputPane.setPreferredSize( new Dimension( 500, 150 ) );

        // pressing the "Run" button runs the script
        final JButton runButton = new JButton( "Run" );
        runButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String script = inputArea.getText();
                Object status = viewer.scriptWaitStatus( script, null );
                if ( status != null ) {
                    outputArea.append( "\n" );
                    outputArea.append( status.toString() );
                }
            }
        } );

        // Pressing the "Clear" button clear the output area.
        final JButton clearButton = new JButton( "Clear" );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                outputArea.setText( "" );
            }
        } );

        JPanel topPanel = new JPanel( new BorderLayout() );
        topPanel.add( inputLabel, BorderLayout.NORTH );
        topPanel.add( inputPane, BorderLayout.CENTER );
        topPanel.add( new JPanel() {{ add( runButton ); }}, BorderLayout.SOUTH );

        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( outputLabel, BorderLayout.NORTH );
        bottomPanel.add( outputPane, BorderLayout.CENTER );
        bottomPanel.add( new JPanel() {{ add( clearButton ); }}, BorderLayout.SOUTH );

        setLayout( new BorderLayout() );
        add( topPanel, BorderLayout.CENTER );
        add( bottomPanel, BorderLayout.SOUTH );
        setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    }

    // test
    public static void main( String[] args ) {

        class ViewerPanel extends JPanel {

            private final JmolViewer viewer;

            public ViewerPanel() {
                viewer = JmolViewer.allocateViewer( this, new SmarterJmolAdapter(), null, null, null, "-applet", null );
            }

            public JmolViewer getViewer() {
                return viewer;
            }

            // Jmol's canonical example of embedding in other Java is to override the paint method, so we do that here.
            @Override public void paint( Graphics g ) {
                // copied from Jmol's Integration.java
                Dimension currentSize = new Dimension();
                getSize( currentSize ); // stores size in currentSize
                Rectangle clipBounds = new Rectangle();
                g.getClipBounds( clipBounds );
                viewer.renderScreenImage( g, currentSize, clipBounds );
            }

        }

        ViewerPanel viewerPanel = new ViewerPanel();
        viewerPanel.setPreferredSize( new Dimension( 400, 400 ) );

        JmolConsole console = new JmolConsole( viewerPanel.getViewer() );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( viewerPanel, BorderLayout.WEST );
        mainPanel.add( console, BorderLayout.CENTER );

        JFrame frame = new JFrame();
        frame.setContentPane( mainPanel );
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
    }
}
