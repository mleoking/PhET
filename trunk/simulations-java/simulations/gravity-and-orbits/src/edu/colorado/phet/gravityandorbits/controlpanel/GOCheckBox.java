package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * @author Sam Reid
 */
public class GOCheckBox extends JCheckBox {
    public GOCheckBox( String title, final Property<Boolean> property ) {
        super( title );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                property.setValue( isSelected() );
            }
        } );
        property.addObserver( new SimpleObserver() {
            public void update() {
                setSelected( property.getValue() );
            }
        } );
        setOpaque( false );
        setFont( new PhetFont( 18 ) );
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
