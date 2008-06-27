package edu.colorado.phet.eatingandexercise.control;

import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.eatingandexercise.control.valuenode.LinearValueControlNode;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;

/**
 * Created by: Sam
* Jun 27, 2008 at 10:14:13 AM
*/
public class HumanSlider extends JPanel {
    private LinearValueControlNode linearValueControlNode;

    public HumanSlider( double min, double max, double value, String label, String textFieldPattern, String units ) {
        linearValueControlNode = new LinearValueControlNode( label, units, min, max, value, new DefaultDecimalFormat( textFieldPattern ) );
        add( new PNodeComponent( linearValueControlNode ) );
    }

    public double getValue() {
        return linearValueControlNode.getValue();
    }

    public void addChangeListener( final ChangeListener changeListener ) {
        linearValueControlNode.addListener( new LinearValueControlNode.Listener() {
            public void valueChanged( double value ) {
                changeListener.stateChanged( null );
            }
        } );
    }

    public void setValue( double v ) {
        linearValueControlNode.setValue( v );
    }

    public JTextField getTextField() {
        return new JTextField();
    }

    public JSlider getSlider() {
        return new JSlider();
    }

    public void setRange( double min, double max ) {
//            linearValueControlNode.setSliderRange( min, max );
    }

    public void setUnits( String distanceUnit ) {
        linearValueControlNode.setUnits( distanceUnit );
    }

    public void setPaintLabels( boolean b ) {
    }

    public void setPaintTicks( boolean b ) {
    }

    public void setTextFieldFormat( NumberFormat numberFormat ) {
    }

    public void setTickLabels( Hashtable table ) {
    }
}
