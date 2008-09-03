package edu.colorado.phet.eatingandexercise.control.valuenode;

import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.eatingandexercise.view.SliderNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by: Sam
 * Jun 24, 2008 at 4:01:47 PM
 * <p/>
 */
public class LinearValueControlNode extends PNode {
    private PText labelNode;
    private PSwing readoutNode;
    private SliderNode sliderNode;
    private PText unitsNode;
    private int SPACING = 5;
    private double value;
    private JFormattedTextField formattedTextField;
    private NumberFormat numberFormat;
    private double min;
    private double max;
    private LayoutStrategy layoutStrategy = new DefaultLayoutStrategy();
    private ArrayList listeners = new ArrayList();
    private Timer expirationTimer;

    public LinearValueControlNode( String label, String units, double min, double max, double value, NumberFormat numberFormat ) {
        this.min = min;
        this.max = max;
        this.numberFormat = numberFormat;
        this.value = value;
        labelNode = new PText( label );
        addChild( labelNode );

        formattedTextField = new JFormattedTextField( numberFormat );
        formattedTextField.setColumns( 4 );
        formattedTextField.setValue( new Double( value ) );
        formattedTextField.setHorizontalAlignment( JTextField.RIGHT );
        formattedTextField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    setValueAndNotifyModel( parseText(), true );
                }
                catch( ParseException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        expirationTimer = new Timer( 3000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                formattedTextField.setValue( new Double( getValue() ) );
            }
        } );
        expirationTimer.setRepeats( false );
        formattedTextField.addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                try {
                    double value = parseText();
                    if ( value >= LinearValueControlNode.this.min && value <= LinearValueControlNode.this.max ) {
                        setValueAndNotifyModel( value, false );
                    }
                    else {
                        //todo: set the readout to expire if the user does something else or waits too long
                        expirationTimer.start();
                    }
                }
                catch( ParseException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        formattedTextField.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        formattedTextField.setSelectionEnd( 0 );
                        formattedTextField.setSelectionEnd( formattedTextField.getText().length() );
                    }
                } );
            }
        } );
        readoutNode = new PSwing( formattedTextField );
        addChild( readoutNode );

        unitsNode = new PText( units );
        addChild( unitsNode );

        sliderNode = new SliderNode( min, max, value );
        sliderNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValueAndNotifyModel( sliderNode.getValue(), true );
            }
        } );
        addChild( sliderNode );
        setValueAndNotifyModel( value, true );//run the value through the numberformat
        relayout();
    }

    public void setTextFieldColumns( int c ) {
        formattedTextField.setColumns( c );
        readoutNode.computeBounds();
        relayout();
    }

    private double parseText() throws ParseException {
        return LinearValueControlNode.this.numberFormat.parse( formattedTextField.getText() ).doubleValue();
    }

    public void setSliderRange( double min, double max ) {
        sliderNode.setRange( min, max );
    }

    public JFormattedTextField getFormattedTextField() {
        return formattedTextField;
    }

    private void setValue( double v, boolean updateTextBox ) {
        //run the value through the numberformat, so that the displayed value matches the internal model value
        //todo: is this necessary?
        try {
            v = numberFormat.parse( String.valueOf( v ) ).doubleValue();
            if ( this.value != v ) {
                this.value = v;
                if ( updateTextBox ) {
                    formattedTextField.setValue( new Double( v ) );
                    expirationTimer.stop();//no need to expire the current-type in value, since something else happened to set the value
                }
                sliderNode.setValue( v );
            }
        }
        catch( ParseException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the value that this slider should display, does not send any notifications.
     * <p/>
     * <p/>
     * For situations that require a model with a value that cannot be exactly displayed by this component,
     * it is necessary to set the display value without firing change notifications.  This is similar to
     * treating this as an MVC view component when the notification comes from elsewhere, and a MVC control component
     * when this component originates the change.
     *
     * @param v the value to display
     */
    public void setValue( double v ) {
        setValue( v, true );
    }

    private void setValueAndNotifyModel( double v, boolean updateTextBox ) {
        double oldValue = getValue();
        if ( v >= min && v <= max ) {
            setValue( v, updateTextBox );
            if ( getValue() != oldValue ) {
                notifyListeners();
            }
        }
        else {
            setValue( oldValue, updateTextBox );
            if ( updateTextBox ) {
                formattedTextField.setValue( new Double( oldValue ) );
            }
            //todo: optionally show an out-of-range message
        }
    }

    private void relayout() {
        layoutStrategy.layout( this );
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

    public void setTextFieldFormat( NumberFormat numberFormat ) {
        this.numberFormat = numberFormat;
        formattedTextField.setFormatterFactory( new DefaultFormatterFactory( new NumberFormatter( numberFormat ) ) );
    }

    public void setLayoutStrategy( LayoutStrategy layoutStrategy ) {
        this.layoutStrategy = layoutStrategy;
        relayout();
    }

    public LayoutStrategy getGridLayout( LinearValueControlNode[] nodes ) {
        return new GridLayout( nodes );
    }

    //todo: perhaps this should be static
    public interface LayoutStrategy {
        void layout( LinearValueControlNode linearValueControlNode );
    }

    public class DefaultLayoutStrategy implements LayoutStrategy {
        public void layout( LinearValueControlNode linearValueControlNode ) {
            double maxHeight = getMaxChildHeight();
            labelNode.setOffset( 0, maxHeight / 2 - labelNode.getFullBounds().getHeight() / 2 );
            readoutNode.setOffset( labelNode.getFullBounds().getMaxX() + SPACING, maxHeight / 2 - readoutNode.getFullBounds().getHeight() / 2 );
            unitsNode.setOffset( readoutNode.getFullBounds().getMaxX() + SPACING, maxHeight / 2 - unitsNode.getFullBounds().getHeight() / 2 );
            sliderNode.setOffset( unitsNode.getFullBounds().getMaxX() + SPACING, maxHeight / 2 - sliderNode.getFullBounds().getHeight() / 2 );
        }
    }

    public static interface Getter {
        public double getValue( LinearValueControlNode node );
    }

    public class GridLayout implements LayoutStrategy {
        private LinearValueControlNode[] nodes;

        public GridLayout( LinearValueControlNode[] nodes ) {
            this.nodes = nodes;
        }

        public void layout( LinearValueControlNode linearValueControlNode ) {
            double maxHeight = getMaxChildHeight();

            double maxLabelNodeWidth = getMax( new Getter() {
                public double getValue( LinearValueControlNode n ) {
                    return n.labelNode.getFullBounds().getWidth();
                }
            } );
            double maxReadoutNodeWidth = getMax( new Getter() {
                public double getValue( LinearValueControlNode n ) {
                    return n.readoutNode.getFullBounds().getWidth();
                }
            } );
            double maxUnitsNodeWidth = getMax( new Getter() {
                public double getValue( LinearValueControlNode n ) {
                    return n.unitsNode.getFullBounds().getWidth();
                }
            } );
            double maxSliderNodeWidth = getMax( new Getter() {
                public double getValue( LinearValueControlNode n ) {
                    return n.sliderNode.getFullBounds().getWidth();
                }
            } );
//            System.out.println( "maxLabelNodeWidth = " + maxLabelNodeWidth );
            labelNode.setOffset( 0, maxHeight / 2 - labelNode.getFullBounds().getHeight() / 2 );
            readoutNode.setOffset( maxLabelNodeWidth + SPACING, maxHeight / 2 - readoutNode.getFullBounds().getHeight() / 2 );
            unitsNode.setOffset( maxReadoutNodeWidth + maxLabelNodeWidth + SPACING * 2, maxHeight / 2 - unitsNode.getFullBounds().getHeight() / 2 );
            sliderNode.setOffset( maxUnitsNodeWidth + maxLabelNodeWidth + maxReadoutNodeWidth + SPACING * 3, maxHeight / 2 - sliderNode.getFullBounds().getHeight() / 2 );
        }

        private double getMax( Getter getter ) {
            double[] v = new double[nodes.length];
            for ( int i = 0; i < nodes.length; i++ ) {
                v[i] = getter.getValue( nodes[i] );
            }
            return MathUtil.max( v );
        }
    }

    public static interface Listener {
        void valueChanged( double value );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).valueChanged( getValue() );
        }
    }

    public double getValue() {
        return value;
    }

    public static void main( String[] args ) {
        PiccoloTestFrame piccoloTestFrame = new PiccoloTestFrame( LinearValueControlNode.class.getName() );
        LinearValueControlNode control = new LinearValueControlNode( "label", "units", 0, 1.0 / 50.0, 0.02, new DecimalFormat( "0.00000" ) );
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
