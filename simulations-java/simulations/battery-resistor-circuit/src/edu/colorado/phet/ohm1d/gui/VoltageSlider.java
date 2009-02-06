package edu.colorado.phet.ohm1d.gui;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.ohm1d.common.math.functions.Transform;

public class VoltageSlider extends JPanel implements ChangeListener {
    Transform transform;
    JSlider js;
    JTextField jtf;
    String name;
    Vector listeners = new Vector();
    NumberFormat nf;

    String units;

    //The transform goes from component space to physics space.  That is, the output of the slider is transform ed to get the actual value.
    public VoltageSlider( Transform transform, String name, Image image, double defaultValue, NumberFormat nf, String units ) {
        this.nf = nf;
        this.units = units;
        this.name = name;
        this.transform = transform;
        jtf = new JTextField();
        jtf.setEditable( false );
        double defaultDomain = transform.invert().evaluate( defaultValue );
        //System.err.println("Transform="+transform);
        int min = (int) transform.getDomainMin();
        int max = (int) transform.getDomainMax();
        int defDom = (int) defaultDomain;
        int range = max - min;
        //System.err.println("min="+min+", max="+max+", defDom="+defDom);
        js = new JSlider( min, max, defDom );
        js.setPaintTicks( true );
        js.setMajorTickSpacing( range / 10 );
        // js.setMinorTickSpacing(range/4);
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        JLabel label = new JLabel( name, new ImageIcon( image ), JLabel.TRAILING );
        add( label );//new JLabel(name,image,JLabel.TRAILING));
        add( js );
        add( jtf );
        setBorder( BorderFactory.createLineBorder( Color.blue ) );//BevelBorder(BevelBorder.RAISED));
        js.addChangeListener( this );
        fireChange();
    }

    public void addVoltageListener( VoltageListener val ) {
        listeners.add( val );
    }

    //sync the controller with the model.
    public void fireChange() {
        double value = transform.evaluate( js.getValue() );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (VoltageListener) listeners.get( i ) ).valueChanged( value );
        }
        jtf.setText( name + " = " + nf.format( value ) + " " + units );
    }

    public void stateChanged( ChangeEvent ce ) {
        fireChange();
    }
}
