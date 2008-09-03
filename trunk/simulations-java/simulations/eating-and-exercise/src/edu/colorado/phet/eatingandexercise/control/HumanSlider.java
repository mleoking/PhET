package edu.colorado.phet.eatingandexercise.control;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.eatingandexercise.control.valuenode.LinearValueControlNode;

/**
 * Created by: Sam
 * Jun 27, 2008 at 10:14:13 AM
 */
public class HumanSlider extends JPanel {
    private LinearValueControlNode linearValueControlNode;
    private PNodeComponent pNodeComponent;

    public HumanSlider( double min, double max, double value, String label, String textFieldPattern, String units ) {
        linearValueControlNode = new LinearValueControlNode( label, units, min, max, value, new DefaultDecimalFormat( textFieldPattern ) );
        linearValueControlNode.setTextFieldColumns( 7 );//current limiting factor is age
        pNodeComponent = new PNodeComponent( linearValueControlNode );
        add( pNodeComponent );
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
        linearValueControlNode.setSliderRange( min, max );
    }

    public void setUnits( String distanceUnit ) {
        linearValueControlNode.setUnits( distanceUnit );
    }

    public void setPaintLabels( boolean b ) {
    }

    public void setPaintTicks( boolean b ) {
    }

    public void setTextFieldFormat( NumberFormat numberFormat ) {
        linearValueControlNode.setTextFieldFormat( numberFormat );
    }

    public void setTickLabels( Hashtable table ) {
    }

    public static void layout( HumanSlider[] s ) {
        LinearValueControlNode[] nodes = new LinearValueControlNode[s.length];
        for ( int i = 0; i < nodes.length; i++ ) {
            nodes[i] = s[i].linearValueControlNode;
        }
        for ( int i = 0; i < nodes.length; i++ ) {
            nodes[i].setLayoutStrategy( nodes[i].getGridLayout( nodes ) );
        }
        for ( int i = 0; i < s.length; i++ ) {
            s[i].pNodeComponent.updatePreferredSize();
        }
    }

}
