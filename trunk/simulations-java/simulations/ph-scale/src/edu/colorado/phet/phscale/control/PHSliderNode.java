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


public class PHSliderNode extends PNode {
    
    private static final Font LABEL_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Font VALUE_FONT = new PhetFont( 18 );
    private static final Font SLIDER_FONT = new PhetFont( 16 );
    private static final int VALUE_COLUMNS = 4;
    private static final Color ACID_COLOR = PHScaleConstants.H3O_COLOR;
    private static final Color BASE_COLOR = PHScaleConstants.OH_COLOR;
    private static final Stroke SLIDER_TRACK_STROKE = new BasicStroke( 1f );
    private static final Color SLIDER_TRACK_STROKE_COLOR = Color.BLACK;
    
    private JFormattedTextField _valueTextField;
    
    public PHSliderNode( double width, double height ) {
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
        
        JSlider slider = new JSlider( JSlider.VERTICAL, -100, 1500, 500 );
        slider.setMajorTickSpacing( 100 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.setPaintTrack( false );
        slider.setPreferredSize( new Dimension( slider.getPreferredSize().width, 500 ) );
        Hashtable sliderLabelTable = new Hashtable();
        sliderLabelTable.put( new Integer( 0 ), new JLabel( "0" ) );
        sliderLabelTable.put( new Integer( 200 ), new JLabel( "2" ) );
        sliderLabelTable.put( new Integer( 400 ), new JLabel( "4" ) );
        sliderLabelTable.put( new Integer( 600 ), new JLabel( "6" ) );
        sliderLabelTable.put( new Integer( 800 ), new JLabel( "8" ) );
        sliderLabelTable.put( new Integer( 1000 ), new JLabel( "10" ) );
        sliderLabelTable.put( new Integer( 1200 ), new JLabel( "12" ) );
        sliderLabelTable.put( new Integer( 1400 ), new JLabel( "14" ) );
        slider.setLabelTable( sliderLabelTable );
        PSwing sliderWrapper = new PSwing( slider );
        
        double w = 10;
        double h = slider.getPreferredSize().getHeight();
        PPath sliderTrackNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        Paint trackPaint = new GradientPaint( 0f, 0f, BASE_COLOR, 0f, (float)h, ACID_COLOR );
        sliderTrackNode.setPaint( trackPaint );
        sliderTrackNode.setStroke( SLIDER_TRACK_STROKE );
        sliderTrackNode.setStrokePaint( SLIDER_TRACK_STROKE_COLOR );
        
        PText acidLabelNode = new PText( PHScaleStrings.LABEL_ACID );
        acidLabelNode.setFont( SLIDER_FONT );
        acidLabelNode.rotate( -Math.PI / 2 );
        sliderTrackNode.addChild( acidLabelNode );
        acidLabelNode.setOffset( -( acidLabelNode.getFullBoundsReference().getWidth() + 4 ), sliderTrackNode.getFullBoundsReference().getHeight() - acidLabelNode.getFullBoundsReference().getHeight() ); // bottom left of the track
        
        PText baseLabelNode = new PText( PHScaleStrings.LABEL_BASE );
        baseLabelNode.setFont( SLIDER_FONT );
        baseLabelNode.rotate( -Math.PI / 2 );
        sliderTrackNode.addChild( baseLabelNode );
        baseLabelNode.setOffset( -( baseLabelNode.getFullBoundsReference().getWidth() + 4 ), baseLabelNode.getFullBoundsReference().getHeight() ); // top left of the track
        
        addChild( valuePanelWrapper );
        addChild( sliderTrackNode );
        addChild( sliderWrapper );
        addChild( outlineNode );
        
        outlineNode.setOffset( 0, 0 );
        PBounds ob = outlineNode.getFullBoundsReference();
        PBounds vb = valuePanelWrapper.getFullBoundsReference();
        valuePanelWrapper.setOffset( ob.getX() + ( ob.getWidth() - vb.getWidth() ) / 2, ob.getY() + 10 );
        vb = valuePanelWrapper.getFullBoundsReference();
        PBounds sb = sliderWrapper.getFullBoundsReference();
        sliderWrapper.setOffset( ob.getX() + ( ob.getWidth() - sb.getWidth() ) / 2, vb.getMaxY() + 10 );
        sb = sliderWrapper.getFullBoundsReference();
        sliderTrackNode.setOffset( sb.getX(), sb.getY() );
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
