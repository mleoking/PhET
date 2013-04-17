// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.simsharing.server.Server;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;

/**
 * @author Sam Reid
 */
public class StudentConfigFrame extends JFrame {

    private static GridBagConstraints createConstraints( int gridx, int gridy ) {
        return new GridBagConstraints( gridx, gridy, 1, 1, 0.5, 0.5, CENTER, NONE, new Insets( 2, 2, 2, 2 ), 0, 0 );
    }

    public static class PropertyTextField extends JTextField {
        public PropertyTextField( String title, final Property<String> text, int columns ) {
            super( text.get(), columns );
            addKeyListener( new KeyAdapter() {
                @Override public void keyReleased( KeyEvent e ) {
                    text.set( getText() );
                }
            } );
            setBorder( BorderFactory.createTitledBorder( title ) );
        }
    }

    public StudentConfigFrame() throws HeadlessException {
        super( "PhET Sim Sharing - Student Edition" );
        final Random random = new Random();
        final JFrame frame = this;
        final Property<Sim> selectedSim = new Property<Sim>( Sim.sims[0] );
        final Property<String> host = new Property<String>( Server.HOST_IP_ADDRESS );
        final Property<String> port = new Property<String>( Server.PORT + "" );
        final Property<String> studentID = new Property<String>( Server.names[random.nextInt( Server.names.length )] + "-" + random.nextInt( 1000 ) );
        setContentPane( new JPanel( new GridBagLayout() ) {{
            setBackground( Color.white );
            add( new JList( Sim.sims ) {{
                setBorder( new TitledBorder( "Simulation" ) );
                setSelectedValue( selectedSim.get(), true );
                addListSelectionListener( new ListSelectionListener() {
                    public void valueChanged( ListSelectionEvent e ) {
                        selectedSim.set( (Sim) getSelectedValue() );
                    }
                } );
            }}, createConstraints( 0, 0 ) );
            final int columns = Server.HOST_IP_ADDRESS.length() + 2;
            add( new VerticalLayoutPanel() {{
                add( new PropertyTextField( "Server", host, columns ) );
                add( new PropertyTextField( "Port", port, columns ) );
            }}, createConstraints( 1, 0 ) );
            add( new PropertyTextField( "Student ID", studentID, columns ), createConstraints( 2, 0 ) );
            add( new JButton( "Start" ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        try {
                            frame.dispose();
                            new Student( selectedSim.get(), host.get(), Integer.parseInt( port.get() ), studentID.get() ).start();
                        }
                        catch ( Exception ex ) {
                            ex.printStackTrace();
                        }
                    }
                } );
            }}, createConstraints( 3, 0 ) );
        }} );
        pack();
        SwingUtils.centerWindowOnScreen( this );
        setDefaultCloseOperation( EXIT_ON_CLOSE );
    }
}
