package edu.colorado.phet.fluidpressureandflow;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.Units;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowControlPanel<T extends FluidPressureAndFlowModel> extends VerticalLayoutPanel {
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
            setFont( new PhetFont( 16, true ) );
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

    public FluidPressureAndFlowControlPanel( final FluidPressureAndFlowModule<T> module ) {
        super();

        final LogoPanel panel = new LogoPanel();
        panel.setBackground( BACKGROUND );
        addControl( panel );
        addControlFullWidth( new CheckBox( "Ruler", module.getRulerVisibleProperty() ) );
//        addControlFullWidth( new WorkEnergyCheckBox( "Energy Pie Chart", module.getShowPieChartProperty() ) );
//        addControlFullWidth( new WorkEnergyCheckBox( "Energy Bar Chart", module.getShowEnergyBarChartProperty() ) );
//        addControlFullWidth( new WorkEnergyCheckBox( "Ruler", module.getShowRulerProperty() ) );

        addControlFullWidth( new PhetTitledPanel( "Units" ) {{
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            setBackground( BACKGROUND );
            setForeground( FOREGROUND );

            add( new RadioButton( "atmospheres (atm)", new IsSelectedProperty<Units.PressureUnit>( Units.PressureUnit.ATMOSPHERE, module.getFluidPressureAndFlowModel().getPressureUnitProperty() ) ) );
            add( new RadioButton( "Pascals (Pa)", new IsSelectedProperty<Units.PressureUnit>( Units.PressureUnit.PASCAL, module.getFluidPressureAndFlowModel().getPressureUnitProperty() ) ) );
            add( new RadioButton( "<html>pounds per<br>square inch (psi)</html>", new IsSelectedProperty<Units.PressureUnit>( Units.PressureUnit.PSI, module.getFluidPressureAndFlowModel().getPressureUnitProperty() ) ) );
        }} );
        setBackground( BACKGROUND );
    }

    /**
     * This adapter class converts an enumeration property to a boolean property indicating true if the specified element is selected.
     * It is used to map the enumeration property types into radio button handlers.
     *
     * @param <T>
     */
    private static class IsSelectedProperty<T> extends Property<Boolean> {
        public IsSelectedProperty( final T a, final Property<T> p ) {
            super( p.getValue() == a );
            p.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( p.getValue().equals( a ) );
                }
            } );
            addObserver( new SimpleObserver() {
                public void update() {
                    if ( getValue() ) {
                        p.setValue( a );
                    }
                }
            } );
        }
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }

    private void addControl( JComponent panel ) {
        add( panel );
    }

}
