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
        PSwing autoRadioButton = new ModeRadioButton( "Automatic Mode", true, isAutoMode );
        PSwing manualRadioButton = new ModeRadioButton( "Manual Mode", false, isAutoMode );

        addChild( autoRadioButton );
        addChild( manualRadioButton );

        manualRadioButton.setOffset( autoRadioButton.getFullBounds().getMaxX() + 10, 0 );
    }

    private static class ModeRadioButton extends PSwing {
        public ModeRadioButton( String title, final boolean isAuto, final Property<Boolean> isAutoMode ) {
            super( new JRadioButton( title ) {{
                isAutoMode.addObserver( new SimpleObserver() {
                    public void update() {
                        final boolean is = isAuto ? isAutoMode.get() : !isAutoMode.get();
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                setSelected( is );
                            }
                        } );
                    }
                } );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent actionEvent ) {
                        final boolean is = isAuto ? isSelected() : !isSelected();
                        LWJGLUtils.invoke( new Runnable() {
                            public void run() {
                                isAutoMode.set( is );
                            }
                        } );
                    }
                } );
            }} );
        }
    }
}
