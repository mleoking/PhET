/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;


public class PHControlNode extends PNode {
    
    private static final int MARGIN = 15;
    private static final Font LABEL_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Font VALUE_FONT = new PhetFont( 18 );
    private static final int VALUE_COLUMNS = 4;
    private static final PDimension SLIDER_TRACK_SIZE = new PDimension( 10, 500 );
    private static final PDimension KNOB_SIZE = new PDimension( 30, 40 );
    
    private JFormattedTextField _valueTextField;
    private PHSliderNode _sliderNode;
    
    public PHControlNode() {
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
        
        _sliderNode = new PHSliderNode( SLIDER_TRACK_SIZE, KNOB_SIZE );
        
        PNode parentNode = new PNode();
        parentNode.addChild( valuePanelWrapper );
        parentNode.addChild( _sliderNode );
        valuePanelWrapper.setOffset( 0, 0 );
        PBounds vb = valuePanelWrapper.getFullBoundsReference();
        _sliderNode.setOffset( ( vb.getWidth() / 2 ) - ( SLIDER_TRACK_SIZE.getWidth() / 2 ), vb.getHeight() + KNOB_SIZE.getWidth()/2 + 10 );
        
        double w = parentNode.getFullBoundsReference().getWidth() + ( 2 * MARGIN );
        double h = parentNode.getFullBoundsReference().getHeight() + ( 2 * MARGIN ) + KNOB_SIZE.getWidth()/2;
        PPath outlineNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        outlineNode.setStroke( new BasicStroke( 2f ) );
        outlineNode.setStrokePaint( Color.BLACK );
        
        addChild( parentNode );
        addChild( outlineNode );
        
        outlineNode.setOffset( 0, 0 );
        PBounds ob = outlineNode.getFullBoundsReference();
        PBounds pb = parentNode.getFullBoundsReference();
        parentNode.setOffset( ( ob.getWidth() - pb.getWidth() ) / 2, MARGIN );
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
