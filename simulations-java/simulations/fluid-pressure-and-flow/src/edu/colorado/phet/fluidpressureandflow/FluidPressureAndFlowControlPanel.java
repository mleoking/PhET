package edu.colorado.phet.fluidpressureandflow;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.FluidPressureModule;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowControlPanel extends VerticalLayoutPanel {
    public static Color BACKGROUND = new Color( 232, 242, 152 );
    public static Color FOREGROUND = Color.black;
    public static final Font CONTROL_FONT = new PhetFont( 18, true );

    public static class CheckBox extends JCheckBox {
        public CheckBox( String label, final Property<Boolean> property ) {
            super( label, property.getValue() );
            setBackground( BACKGROUND );
            setForeground( FOREGROUND );
            setFont( CONTROL_FONT );
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
        }
    }

    public static class RadioButton extends JRadioButton {
        public RadioButton( String label, final Property<Boolean> property ) {
            super( label, property.getValue() );
            setBackground( BACKGROUND );
            setForeground( FOREGROUND );
            setFont( CONTROL_FONT );
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
        }
    }

    public FluidPressureAndFlowControlPanel( FluidPressureAndFlowModule module ) {
        super();

        final LogoPanel panel = new LogoPanel();
        panel.setBackground( BACKGROUND );
        addControl( panel );
//        addControlFullWidth( new WorkEnergyCheckBox( "Energy Pie Chart", module.getShowPieChartProperty() ) );
//        addControlFullWidth( new WorkEnergyCheckBox( "Energy Bar Chart", module.getShowEnergyBarChartProperty() ) );
//        addControlFullWidth( new WorkEnergyCheckBox( "Ruler", module.getShowRulerProperty() ) );

//        addControlFullWidth( new PhetTitledPanel( "Units" ) {{
//            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
//            setBackground( BACKGROUND );
//            setForeground( FOREGROUND );
//            add( new RadioButton( "Pascals (Pa)", new Property<Boolean>( true ) ) );
//            add( new RadioButton( "atmospheres (atm)", new Property<Boolean>( false ) ) );
//            add( new RadioButton( "pounds per square inch (psi)", new Property<Boolean>( false ) ) );
//        }} );
        setBackground( BACKGROUND );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }

    private void addControl( JComponent panel ) {
        add( panel );
    }

}
