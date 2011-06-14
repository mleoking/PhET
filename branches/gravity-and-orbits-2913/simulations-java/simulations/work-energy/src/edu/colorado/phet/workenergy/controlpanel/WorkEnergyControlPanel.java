// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.workenergy.controlpanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.workenergy.module.WorkEnergyModule;

/**
 * Control panel template.
 */
public class WorkEnergyControlPanel extends VerticalLayoutPanel {
    public static Color BACKGROUND = new Color( 232, 242, 152 );
    public static Color FOREGROUND = Color.black;
    public static final Font CONTROL_FONT = new PhetFont( 18, true );

    public static class WorkEnergyCheckBox extends JCheckBox {
        public WorkEnergyCheckBox( String label, final Property<Boolean> property ) {
            super( label, property.get() );
            setBackground( BACKGROUND );
            setForeground( FOREGROUND );
            setFont( CONTROL_FONT );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    property.set( isSelected() );
                }
            } );
            property.addObserver( new SimpleObserver() {
                public void update() {
                    setSelected( property.get() );
                }
            } );
        }
    }

    public WorkEnergyControlPanel( WorkEnergyModule module ) {
        super();

        final LogoPanel panel = new LogoPanel();
        panel.setBackground( BACKGROUND );
        addControl( panel );
        addControlFullWidth( new WorkEnergyCheckBox( "Energy Pie Chart", module.getShowPieChartProperty() ) );
        addControlFullWidth( new WorkEnergyCheckBox( "Energy Bar Chart", module.getShowEnergyBarChartProperty() ) );
        addControlFullWidth( new WorkEnergyCheckBox( "Ruler", module.getShowRulerProperty() ) );
        setBackground( BACKGROUND );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }

    private void addControl( JComponent panel ) {
        add( panel );
    }

}
