package edu.colorado.phet.rotation.torque;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;

/**
 * Author: Sam Reid
 * Aug 21, 2007, 9:35:11 AM
 */
public class AlignedSliderSetLayoutStrategy implements ILayoutStrategy {
    private AbstractValueControl[] sliders;

    public AlignedSliderSetLayoutStrategy( AbstractValueControl[] sliders ) {
        this.sliders = sliders;
    }

    public void doLayout() {
        for ( int i = 0; i < sliders.length; i++ ) {
            doLayout( sliders[i] );
        }
    }

    public static interface ValueGetter {
        JComponent getPreviousComponent( AbstractValueControl v );
    }

    public void doLayout( AbstractValueControl valueControl ) {
        valueControl.setLayout( null );
        JComponent slider = valueControl.getSlider();
        JComponent textField = valueControl.getTextField();
        JComponent valueLabel = valueControl.getValueLabel();
        JComponent unitsLabel = valueControl.getUnitsLabel();
        int sliderHeight = slider.getPreferredSize().height;
        valueLabel.setBounds( 0, ( sliderHeight - valueLabel.getPreferredSize().height ) / 2, valueLabel.getPreferredSize().width, valueLabel.getPreferredSize().height );
        textField.setBounds( getMaxX( new ValueGetter() {
            public JComponent getPreviousComponent( AbstractValueControl v ) {
                return v.getValueLabel();
            }
        } ), ( sliderHeight - textField.getPreferredSize().height ) / 2, textField.getPreferredSize().width, textField.getPreferredSize().height );
        unitsLabel.setBounds( getMaxX( new ValueGetter() {
            public JComponent getPreviousComponent( AbstractValueControl v ) {
                return v.getTextField();
            }
        } ), ( sliderHeight - unitsLabel.getPreferredSize().height ) / 2, unitsLabel.getPreferredSize().width, unitsLabel.getPreferredSize().height );
        slider.setBounds( getMaxX( new ValueGetter() {
            public JComponent getPreviousComponent( AbstractValueControl v ) {
                return v.getUnitsLabel();
            }
        } ), 0, slider.getPreferredSize().width, slider.getPreferredSize().height );

        valueControl.add( valueLabel );
        valueControl.add( textField );
        valueControl.add( unitsLabel );
        valueControl.add( slider );
        valueControl.setPreferredSize( new Dimension( slider.getX() + slider.getWidth(), slider.getHeight() ) );
    }

    private int getMaxX( ValueGetter valueGetter ) {
        int max = 0;
        for ( int i = 0; i < sliders.length; i++ ) {
            AbstractValueControl slider = sliders[i];
            int value = valueGetter.getPreviousComponent( slider ).getX() + valueGetter.getPreviousComponent( slider ).getWidth();
            if ( value > max ) {
                max = value;
            }
        }
        return max;
    }
}
