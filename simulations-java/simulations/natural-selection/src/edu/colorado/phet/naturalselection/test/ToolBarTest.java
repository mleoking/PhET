package edu.colorado.phet.naturalselection.test;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

public class ToolBarTest extends JPanel {
    boolean docked = false;
    private Object lastAncestor;
    private JToolBar toolBar;

    private Component child;
    private Component placeholder;

    public ToolBarTest( String title, Component child, Component placeholder ) {
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
                            System.out.println( docked ? "Docked" : "Undocked" );
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

        JPanel panel = new JPanel( new GridLayout( 1, 1 ) );
        panel.add( child );

        toolBar.add( panel );
        add( toolBar );
    }

    private void onDock() {
        System.out.println( "onDock()" );
        remove( placeholder );
    }

    private void onUndock() {
        System.out.println( "onUndock()" );
        add( placeholder );
    }

    private static PhetPCanvas createExampleCanvas() {
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

        path = PPath.createRectangle( 50, 50, 50, 50 );
        path.setPaint( Color.BLACK );
        canvas.addWorldChild( path );
        canvas.setPreferredSize( new Dimension( 200, 200 ) );

        return canvas;
    }

    private static void init() {
        JFrame frame = new JFrame( "Detachable Demo" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.add( new ToolBarTest( "Pedigree Chart", createExampleCanvas(), new JLabel( "Placeholder" ) ) );

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
