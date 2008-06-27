package edu.colorado.phet.eatingandexercise.control.valuenode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.eatingandexercise.view.SliderNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by: Sam
 * Jun 24, 2008 at 4:01:47 PM
 * <p/>
 * Todo: make sure readout value is identical to model value
 */
public class LinearValueControlNode extends PNode {
    private PText labelNode;
    private PSwing readoutNode;
    private SliderNode sliderNode;
    private PText unitsNode;
    private int SPACING = 5;
    private double value;
    private JFormattedTextField field;
    private NumberFormat numberFormat;

    public LinearValueControlNode( String label, String units, double min, double max, double value, NumberFormat numberFormat ) {
        this.numberFormat = numberFormat;
        this.value = value;
        labelNode = new PText( label );
        addChild( labelNode );

        field = new JFormattedTextField( numberFormat );
        field.setColumns( 4 );
        field.setText( value + "" );
        field.setHorizontalAlignment( JTextField.RIGHT );
        field.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setValue( Double.parseDouble( field.getText() ) );
            }
        } );
        readoutNode = new PSwing( field );
        addChild( readoutNode );

        unitsNode = new PText( units );
        addChild( unitsNode );

        sliderNode = new SliderNode( min, max, value );
        sliderNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( sliderNode.getValue() );
            }
        } );
        addChild( sliderNode );
        setValue( value );//run the value through the numberformat
        relayout();
    }

    public void setSliderRange( double min, double max ) {
        sliderNode.setRange( min, max );
    }

    public void setValue( double v ) {
        //run the value through the numberformat, so that the displayed value is always the model value
        v = Double.parseDouble( numberFormat.format( v ) );
        if ( this.value != v ) {
            this.value = v;
            field.setValue( new Double( v ) );
            sliderNode.setValue( v );
            notifyListener();
        }
    }

    private void relayout() {
        double maxHeight = getMaxChildHeight();
        labelNode.setOffset( 0, maxHeight / 2 - labelNode.getFullBounds().getHeight() / 2 );
        readoutNode.setOffset( labelNode.getFullBounds().getMaxX() + SPACING, maxHeight / 2 - readoutNode.getFullBounds().getHeight() / 2 );
        unitsNode.setOffset( readoutNode.getFullBounds().getMaxX() + SPACING, maxHeight / 2 - unitsNode.getFullBounds().getHeight() / 2 );
        sliderNode.setOffset( unitsNode.getFullBounds().getMaxX() + SPACING, maxHeight / 2 - sliderNode.getFullBounds().getHeight() / 2 );
    }

    private double getMaxChildHeight() {
        double maxHeight = 0;
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            maxHeight = Math.max( getChild( i ).getFullBounds().getHeight(), maxHeight );
        }
        return maxHeight;
    }

    public void setUnits( String unit ) {
        unitsNode.setText( unit );
    }

    public static interface Listener {
        void valueChanged( double value );
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListener() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).valueChanged( getValue() );
        }
    }

    public double getValue() {
        return value;
    }

    public static void main( String[] args ) {
        PiccoloTestFrame piccoloTestFrame = new PiccoloTestFrame( LinearValueControlNode.class.getName() );
        LinearValueControlNode control = new LinearValueControlNode( "label", "units", 0, 1.0/50.0, 0.02, new DecimalFormat( "0.00000" ) );
        control.setOffset( 200, 200 );
        control.addListener( new Listener() {
            public void valueChanged( double value ) {
                System.out.println( "LinearValueControlNode.valueChanged: " + value );
            }
        } );
        piccoloTestFrame.addNode( new BorderNode( control, 5, 3 ) );
        piccoloTestFrame.setVisible( true );
    }
}
