/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.text.ParseException;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;


public class PHControlNode extends PNode {
    
    private static final Font LABEL_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Font VALUE_FONT = new PhetFont( 18 );
    private static final int VALUE_COLUMNS = 4;
    
    private JFormattedTextField _valueTextField;
    private PHSliderNode _sliderNode;
    
    public PHControlNode( double width, double height ) {
        super();
        
        EventHandler listener = new EventHandler();
        
        JLabel phLabel = new JLabel( PHScaleStrings.LABEL_PH );
        phLabel.setFont( LABEL_FONT );
        
        _valueTextField = new JFormattedTextField( "XX.XX" );
        _valueTextField.setFont( VALUE_FONT );
        _valueTextField.setColumns( VALUE_COLUMNS );
        _valueTextField.setHorizontalAlignment( JTextField.RIGHT );
        _valueTextField.addActionListener( listener );
        _valueTextField.addFocusListener( listener );
        _valueTextField.addKeyListener( listener );
        
        JPanel valuePanel = new JPanel();
        EasyGridBagLayout valuePanelLayout = new EasyGridBagLayout( valuePanel );
        valuePanel.setLayout( valuePanelLayout );
        valuePanelLayout.addComponent( phLabel, 0, 0 );
        valuePanelLayout.addComponent( _valueTextField, 0, 1 );
        PSwing valuePanelWrapper = new PSwing( valuePanel );
        
        PPath outlineNode = new PPath( new Rectangle2D.Double( 0, 0, width, height ) );
        outlineNode.setStroke( new BasicStroke( 2f ) );
        outlineNode.setStrokePaint( Color.BLACK );
        
        _sliderNode = new PHSliderNode( 10, 500 );
        
        addChild( valuePanelWrapper );
        addChild( _sliderNode );
        addChild( outlineNode );
        
        outlineNode.setOffset( 0, 0 );
        PBounds ob = outlineNode.getFullBoundsReference();
        PBounds vb = valuePanelWrapper.getFullBoundsReference();
        valuePanelWrapper.setOffset( ob.getX() + ( ob.getWidth() - vb.getWidth() ) / 2, ob.getY() + 10 );
        vb = valuePanelWrapper.getFullBoundsReference();
        PBounds sb = _sliderNode.getFullBoundsReference();
        _sliderNode.setOffset( ob.getX() + ( ob.getWidth() - sb.getWidth() ) / 2, vb.getMaxY() + 10 );
    }
    
    private void handleSliderChanged() {
        //XXX
    }
    
    private void handleTextFieldChanged() {
        //XXX
    }
    
    private void revertTextField() {
        //XXX
    }


    private class EventHandler extends KeyAdapter implements ActionListener, ChangeListener, FocusListener {

        /* Use the up/down arrow keys to change the value. */
        public void keyPressed( KeyEvent e ) {
            if ( e.getSource() == _valueTextField ) {
                if ( e.getKeyCode() == KeyEvent.VK_UP ) {
                    //XXX increment
                }
                else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                    //XXX decrement
                }
            }
        }

        /* User pressed enter in text field. */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _valueTextField ) {
                handleTextFieldChanged();
            }
        }

        /* Slider was moved. */
        public void stateChanged( ChangeEvent event ) {
//            if ( event.getSource() == _intensitySlider ) {
//                handleSliderChanged();
//            }
        }

        /**
         * Selects the entire value text field when it gains focus.
         */
        public void focusGained( FocusEvent e ) {
            if ( e.getSource() == _valueTextField ) {
                _valueTextField.selectAll();
            }
        }

        /**
         * Processes the contents of the value text field when it loses focus.
         */
        public void focusLost( FocusEvent e ) {
            if ( e.getSource() == _valueTextField ) {
                try {
                    _valueTextField.commitEdit();
                    handleTextFieldChanged();
                }
                catch ( ParseException pe ) {
                    Toolkit.getDefaultToolkit().beep();
                    revertTextField();
                }
            }
        }
    }

}
