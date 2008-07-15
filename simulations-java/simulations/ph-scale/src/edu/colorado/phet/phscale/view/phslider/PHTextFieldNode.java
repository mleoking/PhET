/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.phslider;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * PHTextFieldNode is a labeled, editable text field for setting pH.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHTextFieldNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font LABEL_FONT = new PhetFont( Font.BOLD, PHScaleConstants.CONTROL_FONT_SIZE );
    private static final Font VALUE_FONT = new PhetFont( PHScaleConstants.CONTROL_FONT_SIZE );
    private static final int VALUE_COLUMNS = 4;
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( "#0.00" );
    private static final double ARROW_KEY_DELTA = 0.01;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _pH;
    private final IntegerRange _range;
    private final JFormattedTextField _textField;
    private final ArrayList _listeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PHTextFieldNode( IntegerRange range ) {
        super();
        
        _range = new IntegerRange( range );
        
        _listeners = new ArrayList();
        
        EventHandler textFieldEventHandler = new EventHandler();
        
        JLabel phLabel = new JLabel( PHScaleStrings.LABEL_PH );
        phLabel.setFont( LABEL_FONT );
        
        _textField = new JFormattedTextField( "XX.XX" );
        _textField.setFont( VALUE_FONT );
        _textField.setColumns( VALUE_COLUMNS );
        _textField.setHorizontalAlignment( JTextField.RIGHT );
        _textField.addActionListener( textFieldEventHandler );
        _textField.addFocusListener( textFieldEventHandler );
        _textField.addKeyListener( textFieldEventHandler );
        
        JPanel valuePanel = new JPanel();
        EasyGridBagLayout valuePanelLayout = new EasyGridBagLayout( valuePanel );
        valuePanel.setLayout( valuePanelLayout );
        valuePanelLayout.addComponent( phLabel, 0, 0 );
        valuePanelLayout.addComponent( _textField, 0, 1 );
        PSwing valuePanelWrapper = new PSwing( valuePanel );
        
        addChild( valuePanelWrapper );
        
        // initialize
        setPH( range.getDefault() );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the pH value.
     * 
     * @return double
     */
    public double getPH() {
        return _pH;
    }
    
    /**
     * Sets the pH value and notifies all ChangeListeners.
     * 
     * @param pH
     */
    public void setPH( double pH ) {
        if ( !_range.contains( pH ) ) {
            throw new IllegalArgumentException( "pH is out of range: " + pH );
        }
        if ( pH != _pH ) {
            _pH = pH;
            setTextField( pH );
            notifyChanged();
        }
    }
    
    /**
     *  Enables or disables the text field.
     *  When disabled, the text field will not respond to user input, and will be blank.
     *  Enabling the text field causes the pH value to be displayed.
     *  This is useful when the beaker is empty, in which case pH is meaningless.
     *  
     *  @param enabled
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled != _textField.isEnabled() ) {
            _textField.setEnabled( enabled );
            if ( enabled ) {
                setTextField( _pH );
            }
            else {
                _textField.setText( "" );
            }
        }
    }
    
    /*
     * Sets the pH value displayed in the text field.
     */
    private void setTextField( double pH ) {
        assert( _range.contains( pH ) );
        if ( _textField.isEnabled() ) {
            _textField.setText( VALUE_FORMAT.format( pH ) );
        }
    }
    
    //----------------------------------------------------------------------------
    // Handlers
    //----------------------------------------------------------------------------
    
    private void handleTextFieldChanged() {
        String s = _textField.getText();
        try {
            double pH = Double.parseDouble( s );
            if ( _range.contains( pH ) ) {
                setPH( pH );
            }
            else {
                revert();
            }
        }
        catch ( NumberFormatException e ) {
            revert();
        }
    }
    
    private void revert() {
        Toolkit.getDefaultToolkit().beep();
        setTextField( _pH );
    }
    
    //----------------------------------------------------------------------------
    // SEvent handling
    //----------------------------------------------------------------------------
    
    private class EventHandler extends KeyAdapter implements ActionListener, FocusListener {

        /* Use the up/down arrow keys to change the value. */
        public void keyPressed( KeyEvent e ) {
            if ( e.getSource() == _textField ) {
                if ( e.getKeyCode() == KeyEvent.VK_UP ) {
                    double pH = _pH + ARROW_KEY_DELTA;
                    if ( _range.contains( pH ) ) {
                        setPH( pH );
                    }
                }
                else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                    double pH = _pH - ARROW_KEY_DELTA;
                    if ( _range.contains( pH ) ) {
                        setPH( pH );
                    }
                }
            }
        }

        /* User pressed enter in the text field. */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _textField ) {
                handleTextFieldChanged();
            }
        }

        /* Selects the entire text field when it gains focus. */
        public void focusGained( FocusEvent e ) {
            if ( e.getSource() == _textField ) {
//                _textField.selectAll();  // disabled, see Unfuddle #660
            }
        }

        /* Processes the contents of the text field when it loses focus. */
        public void focusLost( FocusEvent e ) {
            if ( e.getSource() == _textField ) {
                try {
                    _textField.commitEdit();
                    handleTextFieldChanged();
                }
                catch ( ParseException pe ) {
                    revert();
                }
            }
        }
    }

    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    public void addChangeListener( ChangeListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        _listeners.add( listener );
    }
    
    private void notifyChanged() {
        ChangeEvent event = new ChangeEvent( this );
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ChangeListener) i.next() ).stateChanged( event );
        }
    }
}
