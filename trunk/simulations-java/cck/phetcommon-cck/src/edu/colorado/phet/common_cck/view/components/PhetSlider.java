/**
 * Class: PhetSlider
 * Class: edu.colorado.phet.coreadditions
 * User: Ron LeMaster
 * Date: Nov 5, 2003
 * Time: 9:41:53 AM
 */
package edu.colorado.phet.common_cck.view.components;

import edu.colorado.phet.common_cck.math.ModelViewTx1D;
import edu.colorado.phet.common_cck.view.util.SimStrings;
import edu.colorado.phet.common_cck.view.util.SwingUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;

public class PhetSlider extends JPanel {
    private JTextField textField;
    private JSlider slider;
    private ModelViewTx1D transform;
    private String units;
    private NumberFormat formatter;
    private static final int SLIDER_MAX = 100000000;
    private static final int SLIDER_MIN = 0;
    private double min;
    private double max;
    private double initialValue;
    private ArrayList listeners = new ArrayList();
    private double value = Double.NaN;//to force an update at the end of the constructor.
    private int numMajorTicks;
    private int numMinorTicks;
    private JLabel titleLabel;
    private JTextField unitsReadout;

    public PhetSlider( String title, String units, final double min, final double max, double initial ) {
        this( title, units, min, max, initial, new DecimalFormat( "0.0#" ) );
    }

    public PhetSlider( String title, String units, final double min, final double max,
                       double initialValue, NumberFormat formatter ) {
        this.min = min;
        this.max = max;
        this.formatter = formatter;
        this.units = units;
        this.transform = new ModelViewTx1D( min, max, SLIDER_MIN, SLIDER_MAX );
        this.initialValue = initialValue;
        numMajorTicks = 10;
        int numMinorsPerMajor = 4;
        numMinorTicks = ( numMajorTicks - 1 ) * numMinorsPerMajor + 1;

        setLayout( new GridBagLayout() );
        setBorder( BorderFactory.createEtchedBorder() );
        this.textField = createTextField();
        textField.setHorizontalAlignment( JTextField.RIGHT );

        createSlider();

        titleLabel = new JLabel( title ) {
            protected void paintComponent( Graphics g ) {
                SwingUtils.setAntiAliasingOn( (Graphics2D)g );
                super.paintComponent( g );
            }
        };
        Font titleFont = new Font( "Lucida Sans", Font.BOLD, 20 );
        titleLabel.setFont( titleFont );

        unitsReadout = new JTextField( " " + this.units ) {
            protected void paintComponent( Graphics g ) {
                SwingUtils.setAntiAliasingOn( (Graphics2D)g );
                super.paintComponent( g );
            }
        };
        unitsReadout.setFocusable( false );
        unitsReadout.setEditable( false );
        unitsReadout.setBorder( null );

        JPanel textPanel = new JPanel();
        textPanel.setLayout( new BorderLayout() );
        textPanel.add( textField, BorderLayout.WEST );
        textPanel.add( unitsReadout, BorderLayout.EAST );
        try {
            SwingUtils.addGridBagComponent( this, titleLabel, 0, 0, 1, 1,
                                            GridBagConstraints.NONE, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( this, slider, 0, 1, 1, 1,
                                            GridBagConstraints.NONE, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( this, textPanel, 0, 2, 2, 1,
                                            GridBagConstraints.NONE, GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            throw new RuntimeException( e );
        }
        setValue( initialValue );
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public JTextField getTextField() {
        return textField;
    }

    public JTextField getUnitsReadout() {
        return unitsReadout;
    }

    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        slider.setEnabled( enabled );
        textField.setEnabled( enabled );
        unitsReadout.setEnabled( enabled );
        titleLabel.setEnabled( enabled );
    }

    public void requestSliderFocus() {
        slider.requestFocus();
    }

    public void setNumMajorTicks( int numMajorTicks ) {
        this.numMajorTicks = numMajorTicks;
        relabelSlider();
    }

    public void setNumMinorTicks( int numMinorTicks ) {
        this.numMinorTicks = numMinorTicks;
        relabelSlider();
    }

    private void relabelSlider() {
        int dMajor = SLIDER_MAX / ( numMajorTicks - 1 );
        int dMinor = SLIDER_MAX / ( numMinorTicks - 1 );
        Font labelFont = new Font( "Lucida Sans", 0, 10 );
        Hashtable table = new Hashtable();
        for( int value = 0; value <= SLIDER_MAX; value += dMajor ) {
            double modelValue = transform.viewToModel( value );
            JLabel label = new JLabel( formatter.format( modelValue ) ) {
                protected void paintComponent( Graphics g ) {
                    SwingUtils.setAntiAliasingOn( (Graphics2D)g );
                    super.paintComponent( g );
                }
            };
            label.setFont( labelFont );
            table.put( new Integer( value ), label );
        }
        slider.setLabelTable( table );

        slider.setMajorTickSpacing( dMajor );
        slider.setMinorTickSpacing( dMinor );
    }

    private void createSlider() {
        final JSlider slider = new JSlider( SwingConstants.HORIZONTAL,
                                            SLIDER_MIN, SLIDER_MAX,
                                            transform.modelToView( initialValue ) );

        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double modelValue = transform.viewToModel( slider.getValue() );
                setValue( modelValue );
            }
        } );
        int dMinor = SLIDER_MAX / ( numMinorTicks - 1 );
        double modelDX = transform.viewToModelDifferential( dMinor / 4 );
//        System.out.println( "modelDX = " + modelDX );
        SliderKeyHandler skh = new SliderKeyHandler( modelDX );
        slider.addKeyListener( skh );
        this.slider = slider;
        relabelSlider();
    }

    public void setPaintTicks( boolean paintTicks ) {
        slider.setPaintTicks( paintTicks );
    }

    public void setPaintLabels( boolean paintLabels ) {
        slider.setPaintLabels( paintLabels );
    }

    private void fireStateChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener)listeners.get( i );
            ChangeEvent ce = new ChangeEvent( this );
            changeListener.stateChanged( ce );
        }
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField( 8 );
        textField.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
            }

            public void focusLost( FocusEvent e ) {
                setValue( getValue() );
            }
        } );
        textField.addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    testCommit();
                }
                else if( e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
                    setValue( initialValue );
                }
            }
        } );

        return textField;
    }

    public boolean testCommit() {
        try {
            commitEdit();
            return true;
        }
        catch( IllegalValueException ive ) {
            String outofrange = SimStrings.get( "Common.PhetSlider.OutOfRange" );
            String minimum = SimStrings.get( "Common.PhetSlider.Minimum" );
            String maximum = SimStrings.get( "Common.PhetSlider.Maximum" );
            String youentered = SimStrings.get( "Common.PhetSlider.YouEntered" );
            String description = SimStrings.get( "Common.PhetSlider.Description" );
            JOptionPane.showMessageDialog( this, outofrange + ".\n" + minimum + "= " + ive.getMin()
                                                 + ", " + maximum + "=" + ive.getMax() + "\n" + youentered + ": "
                                                 + ive.getValue(), description, JOptionPane.ERROR_MESSAGE );
            double value = getValue();
            textField.setText( formatter.format( value ) );
            return false;
        }
    }

    public void setRange( double min, double max ) {
        double val = getValue();
        this.min = min;
        this.max = max;
        if( val < min ) {
            val = min;
        }
        if( val > max ) {
            val = max;
        }
        transform = new ModelViewTx1D( min, max, SLIDER_MIN, SLIDER_MAX );
        setValue( val, true );

    }

    public static class IllegalValueException extends Exception {
        private double min;
        private double max;
        private double value;

        public IllegalValueException( double min, double max, double value ) {
            super( IllegalValueException.class.getName() + " min=" + min + ", max=" + max + " value=" + value );
            this.min = min;
            this.max = max;
            this.value = value;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getValue() {
            return value;
        }

    }

    public void commitEdit() throws IllegalValueException {
        String text = PhetSlider.this.textField.getText();
        text = text.replace( ',', '.' );
        try {
            double value = Double.parseDouble( text );
            if( value >= min && value <= max ) {
                //still legal.
                setValue( value );
            }
            else {
                //we could display a message that reminds the user to stay between the min and max values.
                throw new IllegalValueException( min, max, value );
            }
        }
        catch( NumberFormatException nfe ) {
            return;
        }
    }

    private class SliderKeyHandler implements KeyListener {
        int keyCode = -1;
        private Timer timer;
        private double delta;

        public SliderKeyHandler( double delta ) {
            this.delta = delta;
            timer = new Timer( 30, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    fire();
                }
            } );
            timer.setInitialDelay( 300 );
        }

        private void fire() {
            if( keyCode == -1 ) {
                return;
            }
            else if( keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_DOWN ) {
                double request = getValue() - delta;
                if( request < min ) {
                    request = min;
                }
                setValue( request );
            }
            else if( keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_UP ) {
                double request = getValue() + delta;
                if( request > max ) {
                    request = max;
                }
                setValue( request );
            }
            else if( keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_SPACE ) {
                setValue( initialValue );
            }
        }

        public void keyPressed( KeyEvent e ) {
            keyCode = e.getKeyCode();
            fire();
            timer.setInitialDelay( 200 );
            timer.start();
        }

        public void keyReleased( KeyEvent e ) {
            keyCode = -1;
            timer.stop();
        }

        public void keyTyped( KeyEvent e ) {
            keyCode = e.getKeyCode();
            fire();
        }
    }

    public void setValue( double value ) {
        setValue( value, false );
//        if( value == this.value ) {
//            return;
//        }
//        else{
//            setValue(value,true);
//        }
    }

    private void setValue( double value, boolean forceChange ) {
        if( value == this.value && !forceChange ) {
            return;
        }
        if( value >= min && value <= max ) {
            String string = formatter.format( value );
            string = string.replace( ',', '.' );
            double newValue = Double.parseDouble( string );
            if( this.value == newValue && !forceChange ) {
                return;
            }

            this.value = newValue;
            textField.setText( string );
            int sliderValue = transform.modelToView( value );
            if( sliderValue != slider.getValue() ) {
                slider.setValue( sliderValue ); //this recursively changes values
            }
            fireStateChanged();
        }
    }

    public double getValue() {
        return this.value;
    }

    public void addChangeListener( ChangeListener changeListener ) {
        listeners.add( changeListener );
    }

    public void setNumMinorTicksPerMajorTick( int numMinorsPerMajor ) {
        int testValue = ( numMajorTicks - 1 ) * numMinorsPerMajor + 1;
        setNumMinorTicks( testValue );
    }

    public void setExtremumLabels( JComponent low, JComponent high ) {
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( slider.getMinimum() ), low );
        labelTable.put( new Integer( slider.getMaximum() ), high );
        slider.setLabelTable( labelTable );
    }

}
