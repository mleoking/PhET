package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class GORadioButton<T> extends JRadioButton {
    public GORadioButton( String title, final Property<T> property, final T value ) {
        super( title );
        final SimpleObserver updateSelected = new SimpleObserver() {
            public void update() {
                setSelected( property.getValue() == value );
            }
        };
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                property.setValue( value );
                updateSelected.update();//make sure radio buttons don't toggle off
            }
        } );
        property.addObserver( updateSelected );
        setOpaque( false );
        setFont( GravityAndOrbitsControlPanel.CONTROL_FONT );
        setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
        setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
    }

    @Override
    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        Object originalKey = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, originalKey );
    }
}
