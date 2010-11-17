package edu.colorado.phet.fluidpressureandflow;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.IsSelectedProperty;
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

        addControlFullWidth( new PhetTitledPanel( "Units" ) {{
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            setBackground( BACKGROUND );
            setForeground( FOREGROUND );

            final T model = module.getFluidPressureAndFlowModel();
            add( new RadioButton( "atmospheres (atm)", new IsSelectedProperty<Units.Unit>( Units.ATMOSPHERE, model.getPressureUnitProperty() ) ) );
            add( new RadioButton( "Pascals (Pa)", new IsSelectedProperty<Units.Unit>( Units.PASCAL, model.getPressureUnitProperty() ) ) );
            add( new RadioButton( "<html>pounds per<br>square inch (psi)</html>", new IsSelectedProperty<Units.Unit>( Units.PSI, model.getPressureUnitProperty() ) ) );
            add( new JSeparator() );
            add( new RadioButton( "feet (ft)", new IsSelectedProperty<Units.Unit>( Units.FEET, model.getDistanceUnitProperty() ) ) );
            add( new RadioButton( "meters (m)", new IsSelectedProperty<Units.Unit>( Units.METERS, model.getDistanceUnitProperty() ) ) );
        }} );
        setBackground( BACKGROUND );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }

    private void addControl( JComponent panel ) {
        add( panel );
    }

}
