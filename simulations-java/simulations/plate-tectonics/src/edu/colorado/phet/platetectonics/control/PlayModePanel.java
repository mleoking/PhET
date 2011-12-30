// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Allows the user to select between automatic and manual mode
 */
public class PlayModePanel extends PNode {
    public PlayModePanel( final Property<Boolean> isAutoMode ) {
        // TODO: refactor for better radio button handling (this is really fairly ugly)
        PSwing autoRadioButton = new PSwing( new JRadioButton( "Automatic Mode" ) {{
            isAutoMode.addObserver( new SimpleObserver() {
                @Override public void update() {
                    final boolean is = isAutoMode.get();
                    SwingUtilities.invokeLater( new Runnable() {
                        @Override public void run() {
                            setSelected( is );
                        }
                    } );
                }
            } );
            addActionListener( new ActionListener() {
                @Override public void actionPerformed( ActionEvent actionEvent ) {
                    final boolean is = isSelected();
                    LWJGLUtils.invoke( new Runnable() {
                        @Override public void run() {
                            isAutoMode.set( is );
                        }
                    } );
                }
            } );
        }} );
        PSwing manualRadioButton = new PSwing( new JRadioButton( "Manual Mode" ) {{
            isAutoMode.addObserver( new SimpleObserver() {
                @Override public void update() {
                    final boolean is = !isAutoMode.get();
                    SwingUtilities.invokeLater( new Runnable() {
                        @Override public void run() {
                            setSelected( is );
                        }
                    } );
                }
            } );
            addActionListener( new ActionListener() {
                @Override public void actionPerformed( ActionEvent actionEvent ) {
                    final boolean is = !isSelected();
                    LWJGLUtils.invoke( new Runnable() {
                        @Override public void run() {
                            isAutoMode.set( is );
                        }
                    } );
                }
            } );
        }} );
        addChild( autoRadioButton );
        addChild( manualRadioButton );
        manualRadioButton.setOffset( autoRadioButton.getFullBounds().getMaxX() + 10, 0 );
    }
}
