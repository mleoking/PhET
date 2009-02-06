package edu.colorado.phet.ohm1d.gui;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CoreCountSlider extends JPanel implements ChangeListener {
    // Transform transform;
    JSlider js;
    JTextField jtf;
    String name;
    String units;
    Vector listeners = new Vector();

    //The transform goes from component space to physics space.  That is, the output of the slider is transform ed to get the actual value.
    public CoreCountSlider( int min, int max, int value, String name, Image im, String units ) {
        JLabel label = new JLabel( name, new ImageIcon( im ), JLabel.TRAILING );
        this.name = name;
        this.units = units;
        jtf = new JTextField();
        jtf.setEditable( false );
        //double defaultDomain=transform.invert().evaluate(defaultValue);
        js = new JSlider( min, max, value );
        js.setPaintTicks( true );
        int range = max - min;
        js.setMajorTickSpacing( range / 10 );
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        add( label );
        add( js );
        add( jtf );
        setBorder( BorderFactory.createLineBorder( Color.blue ) );//TitledBorder(name));
        js.addChangeListener( this );
        fireChange();
    }

    public void addIntListener( CoreCountListener val ) {
        listeners.add( val );
    }

    //sync the controller with the model.
    public void fireChange() {
        //double value=transform.evaluate(js.getValue());
        int value = js.getValue();
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (CoreCountListener) listeners.get( i ) ).coreCountChanged( value );
        }
        //jtf.setText("Hi Ingrid!: "+value);
        double displayValue = value * .2 / 3.0;

        jtf.setText( name + " = " + nf.format( displayValue ) + " " + units );
    }

    static final NumberFormat nf = new DecimalFormat( "##.##" );

    public void stateChanged( ChangeEvent ce ) {
        fireChange();
    }
}
