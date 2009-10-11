package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

public class DetachOptionPanel extends JPanel {
    boolean docked = false;
    private Object lastAncestor;
    private JToolBar toolBar;

    private Component child;
    private Component placeholder;

    private List<Listener> listeners = new LinkedList<Listener>();

    public DetachOptionPanel( String title, Component child, Component placeholder ) {
        super( new GridLayout( 1, 1 ) );

        this.child = child;
        this.placeholder = placeholder;

        toolBar = new JToolBar( title );

        toolBar.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( "ancestor" ) ) {
                    if ( evt.getNewValue() == null ) {
                        lastAncestor = evt.getOldValue();
                    }
                    if ( evt.getOldValue() == null ) {
                        if ( evt.getNewValue() != lastAncestor ) {
                            docked = !docked;
                            //System.out.println( docked ? "Docked" : "Undocked" );
                            if ( !docked ) {
                                onUndock();
                            }
                            else {
                                onDock();
                            }
                        }
                    }
                }
            }
        } );

        //toolBar.setLayout( new BoxLayout( toolBar, BoxLayout.LINE_AXIS ) );
        toolBar.setLayout( new GridBagLayout() );

        final JPanel panel = new JPanel( new GridLayout( 1, 1 ) );
        panel.add( child );

        GridBagConstraints statsConstraints = new GridBagConstraints();
        statsConstraints.fill = GridBagConstraints.BOTH;
        statsConstraints.weightx = 1.0;
        statsConstraints.weighty = 1.0;
        toolBar.add( panel, statsConstraints );

        //add( toolBar );
        add( placeholder );

        toolBar.addComponentListener( new ComponentListener() {
            public void componentResized( ComponentEvent componentEvent ) {
                System.out.println( "toolBar: " + toolBar.getSize() );
            }

            public void componentMoved( ComponentEvent componentEvent ) {

            }

            public void componentShown( ComponentEvent componentEvent ) {

            }

            public void componentHidden( ComponentEvent componentEvent ) {

            }
        } );
        panel.addComponentListener( new ComponentListener() {
            public void componentResized( ComponentEvent componentEvent ) {
                System.out.println( "panel: " + panel.getSize() );
            }

            public void componentMoved( ComponentEvent componentEvent ) {

            }

            public void componentShown( ComponentEvent componentEvent ) {

            }

            public void componentHidden( ComponentEvent componentEvent ) {

            }
        } );

    }

    public void setChildVisible() {
        removeAll();
        add( toolBar );
        repaint();
        toolBar.invalidate();
        validate();
    }

    public void setPlaceholderVisible() {
        removeAll();
        add( placeholder );
        repaint();
        placeholder.invalidate();
        validate();
    }

    private void onDock() {
        //System.out.println( "onDock()" );
        remove( placeholder );
        for ( Listener listener : listeners ) {
            listener.onDock();
        }
    }

    private void onUndock() {
        //System.out.println( "onUndock()" );
        add( placeholder );
        for ( Listener listener : listeners ) {
            listener.onUndock();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static interface Listener {
        public void onDock();

        public void onUndock();
    }

    public static PhetPCanvas createExampleCanvas() {
        PhetPCanvas canvas = new PhetPCanvas( new Dimension( 100, 100 ) );
        PPath path;

        path = PPath.createRectangle( 0, 0, 50, 50 );
        path.setPaint( Color.RED );
        canvas.addWorldChild( path );

        path = PPath.createRectangle( 50, 0, 50, 50 );
        path.setPaint( Color.MAGENTA );
        canvas.addWorldChild( path );

        path = PPath.createRectangle( 0, 50, 50, 50 );
        path.setPaint( Color.YELLOW );
        canvas.addWorldChild( path );

        path = PPath.createRectangle( 50, 50, 100, 50 );
        path.setPaint( Color.BLACK );
        canvas.addWorldChild( path );
        canvas.setPreferredSize( new Dimension( 200, 200 ) );

        canvas.setBorder( new LineBorder( Color.BLUE ) );

        return canvas;
    }

    private static void init() {
        JFrame frame = new JFrame( "Detachable Demo" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        JPanel container = new JPanel( new GridLayout( 1, 3 ) );
        frame.setContentPane( container );

        final DetachOptionPanel bar = new DetachOptionPanel( "Pedigree Chart", createExampleCanvas(), new JLabel( "Placeholder" ) );
        final JRadioButton radioPlaceholder = new JRadioButton( "Placeholder" );
        final JRadioButton radioPedigree = new JRadioButton( "Pedigree" );

        ButtonGroup group = new ButtonGroup();
        group.add( radioPlaceholder );
        group.add( radioPedigree );

        radioPlaceholder.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                System.out.println( "Placeholder!" );
                bar.setPlaceholderVisible();
            }
        } );

        radioPedigree.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                System.out.println( "Pedigree!" );
                bar.setChildVisible();
            }
        } );

        bar.addListener( new Listener() {
            public void onDock() {
                radioPlaceholder.setEnabled( true );
                radioPedigree.setEnabled( true );
                radioPedigree.setSelected( true );
            }

            public void onUndock() {
                radioPlaceholder.setEnabled( false );
                radioPedigree.setEnabled( false );
            }
        } );

        /*
        container.add( bar );
        container.add( radioPlaceholder );
        container.add( radioPedigree );
        */
        container.setLayout( new GridBagLayout() );

        GridBagConstraints statsConstraints = new GridBagConstraints();
        statsConstraints.gridx = 1;
        statsConstraints.gridy = 0;
        statsConstraints.gridwidth = 2;
        statsConstraints.fill = GridBagConstraints.BOTH;
        statsConstraints.anchor = GridBagConstraints.NORTH;
        statsConstraints.weightx = 1.0;
        statsConstraints.weighty = 1.0;
        container.add( bar, statsConstraints );
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        container.add( radioPlaceholder, c );
        c.gridx = 3;
        container.add( radioPedigree, c );

        //radioPedigree.setSelected( true );
        radioPlaceholder.setSelected( true );

        frame.pack();

        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                init();
            }
        } );
    }
}
