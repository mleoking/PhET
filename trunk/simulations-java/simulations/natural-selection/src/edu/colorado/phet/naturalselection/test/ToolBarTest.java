/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.colorado.phet.naturalselection.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

public class ToolBarTest extends JPanel {
    protected JTextArea textArea;

    boolean isDocked = false;
    static private Object lastAncestor;
    private JToolBar toolBar;

    static private Component placeholder = new JLabel( "Placeholder" );
    private static LayoutManager layout = new GridLayout( 1, 1);
    private PhetPCanvas canvas = new PhetPCanvas( new Dimension( 100, 100 ) );
    private static final Dimension CANVAS_SIZE = new Dimension( 200, 200 );
    //static private Component content;

    public ToolBarTest() {
        super( layout );

        //Create the toolbar.
        toolBar = new JToolBar( "Pedigree Chart" );
        //placeholder = Box.createRigidArea( new Dimension( 5, 5 ) );
        //toolBar.add( placeholder );

        toolBar.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( "ancestor" ) ) {
                    if ( evt.getNewValue() == null ) {
                        lastAncestor = evt.getOldValue();
                    }
                    if ( evt.getOldValue() == null ) {
                        if ( evt.getNewValue() != lastAncestor ) {
                            isDocked = !isDocked;
                            System.out.println( isDocked ? "Docked" : "Undocked" );
                            if ( !isDocked ) {
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

        canvas.setPreferredSize( CANVAS_SIZE );

        drawCanvas();

        JPanel panel = new JPanel( new GridLayout( 1, 1 ) );
        panel.add( canvas );

        toolBar.add( panel );
        add( toolBar );
    }

    private void drawCanvas() {
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
    }

    private void onDock() {
        System.out.println( "onDock()" );
        remove( placeholder );
        //setLayout( new GridLayout( 1, 1 ) );
    }

    private void onUndock() {
        System.out.println( "onUndock()" );
        //add( placeholder );
        //setLayout( new GridLayout( 2, 1 ) );
        add( placeholder );
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame( "ToolBarDemo" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        //Add content to the window.
        frame.add( new ToolBarTest() );

        //Display the window.
        frame.pack();
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put( "swing.boldMetal", Boolean.FALSE );
                createAndShowGUI();
            }
        } );
    }
}
