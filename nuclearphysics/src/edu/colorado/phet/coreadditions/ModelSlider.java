/**
 * Class: ModelSlider
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.coreadditions;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ModelSlider extends JPanel {
    private JSlider slider;
    private ModelViewTx1D tx;

    public ModelSlider( String title, double minModelValue,
                        double maxModelValue, double defaultModelValue ) {
        this( title, minModelValue, maxModelValue, defaultModelValue, 100 );
    }

    public ModelSlider( String title, double minModelValue,
                        double maxModelValue, double defaultModelValue,
                        int steps ) {
        this.setLayout( new GridBagLayout() );
        tx = new ModelViewTx1D( minModelValue, maxModelValue,
                                0, steps );
        slider = new JSlider( SwingConstants.HORIZONTAL, 0,
                              steps,
                              (int)tx.modelToView( defaultModelValue ) );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double rate = tx.viewToModel( slider.getValue() );
            }
        } );

        int rowIdx = 0;
        try {
            GridBagUtil.addGridBagComponent( this, new JLabel( title ), 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.CENTER );
            GridBagUtil.addGridBagComponent( this, slider, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }

    public double getModelValue() {
        return tx.viewToModel( slider.getValue() );
    }

    public void addChangeListener( ChangeListener changeListener ) {
        slider.addChangeListener( changeListener );
    }

    public void setModelValue( double value ) {
        slider.setValue( (int)tx.modelToView( value ) );
    }

    public void setMajorTickSpacing( double spacing ) {
        slider.setMajorTickSpacing( (int)tx.modelToView( spacing ));
    }

    public void setMinorTickSpacing( double spacing ) {
        slider.setMinorTickSpacing( (int)tx.modelToView( spacing ));
    }

    public void setPaintTicks( boolean b) {
        slider.setPaintTicks( b );
    }

    public void setPaintLabels( boolean b ) {
        slider.setPaintLabels( b );
    }
}

