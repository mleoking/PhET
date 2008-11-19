/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Glacier.EvolutionState;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;

/**
 * EvolutionStatePanel displays the current and previous state variables that are 
 * internal to the glacier's evolution model.  This panel is for debugging purposes
 * and is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EvolutionStatePanel extends JPanel {
    
    private final Glacier _glacier;
    private final GlacierListener _glacierListener;
    private final EvolutionState _currentState, _previousState;
    
    // value displays
    private final NumberDisplay _elaCurrent, _elaPrevious;
    private final NumberDisplay _timescaleCurrent, _timescalePrevious;
    private final NumberDisplay _qAdvanceLimitCurrent, _qAdvanceLimitPrevious;
    private final NumberDisplay _deltaQelaCurrent, _deltaQelaPrevious;
    private final NumberDisplay _qelaCurrent, _qelaPrevious;
    private final NumberDisplay _qelaxCurrent, _qelaxPrevious;
    private final NumberDisplay _glacierLengthCurrent, _glacierLengthPrevious;
    private final Point2DDisplay _terminusCurrent, _terminusPrevious;
    
    public EvolutionStatePanel( Glacier glacier ) {
        super();

        // model
        _glacier = glacier;
        _glacierListener = new GlacierListener() {

            public void iceThicknessChanged() {
                update();
            }

            public void steadyStateChanged() {
                update();
            }
            
        };
        _glacier.addGlacierListener( _glacierListener );
        
        // storage of state
        _currentState = new EvolutionState();
        _previousState = new EvolutionState();
        
        // display
        _elaCurrent = new NumberDisplay( "0" );
        _elaPrevious = new NumberDisplay( "0" );
        _timescaleCurrent = new NumberDisplay( "0.00" );
        _timescalePrevious = new NumberDisplay( "0.00" );
        _qAdvanceLimitCurrent = new NumberDisplay( "0.00" );
        _qAdvanceLimitPrevious = new NumberDisplay( "0.00" );
        _deltaQelaCurrent = new NumberDisplay( "0.00" );
        _deltaQelaPrevious = new NumberDisplay( "0.00" );
        _qelaCurrent = new NumberDisplay( "0.00" );
        _qelaPrevious = new NumberDisplay( "0.00" );
        _qelaxCurrent = new NumberDisplay( "0.00" );
        _qelaxPrevious = new NumberDisplay( "0.00" );
        _glacierLengthCurrent = new NumberDisplay( "0.00" );
        _glacierLengthPrevious = new NumberDisplay( "0.00" );
        _terminusCurrent = new Point2DDisplay( "0" );
        _terminusPrevious = new Point2DDisplay( "0" );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setAnchor( GridBagConstraints.EAST );
        layout.setInsets( new Insets( 3, 10, 3, 10 ) ); // top, left, bottom, right
        this.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( new JLabel( "<html><u>variable</u></html>" ), row, col++ );
        layout.addComponent( new JLabel( "<html><u>current</u></html>" ), row, col++ );
        layout.addComponent( new JLabel( "<html><u>previous</u></html>" ), row, col++ );
        layout.addComponent( new JLabel( "<html><u>units</u></html>" ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "ela:" ), row, col++ );
        layout.addComponent( _elaCurrent, row, col++ );
        layout.addComponent( _elaPrevious, row, col++ );
        layout.addComponent( new JLabel( "m" ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "timescale:" ), row, col++ );
        layout.addComponent( _timescaleCurrent, row, col++ );
        layout.addComponent( _timescalePrevious, row, col++ );
        layout.addComponent( new JLabel( "yrs" ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "q_advance_limit:" ), row, col++ );
        layout.addComponent( _qAdvanceLimitCurrent, row, col++ );
        layout.addComponent( _qAdvanceLimitPrevious, row, col++ );
        layout.addComponent( new JLabel( "m/yr" ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "deltaQela:" ), row, col++ );
        layout.addComponent( _deltaQelaCurrent, row, col++ );
        layout.addComponent( _deltaQelaPrevious, row, col++ );
        layout.addComponent( new JLabel( "m" ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "qela:" ), row, col++ );
        layout.addComponent( _qelaCurrent, row, col++ );
        layout.addComponent( _qelaPrevious, row, col++ );
        layout.addComponent( new JLabel( "m" ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "qelax:" ), row, col++ );
        layout.addComponent( _qelaxCurrent, row, col++ );
        layout.addComponent( _qelaxPrevious, row, col++ );
        layout.addComponent( new JLabel( "m" ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "glacier length:" ), row, col++ );
        layout.addComponent( _glacierLengthCurrent, row, col++ );
        layout.addComponent( _glacierLengthPrevious, row, col++ );
        layout.addComponent( new JLabel( "m" ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "termius:" ), row, col++ );
        layout.addComponent( _terminusCurrent, row, col++ );
        layout.addComponent( _terminusPrevious, row, col++ );
        layout.addComponent( new JLabel( "m" ), row, col++ );
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
    }
    
    private void update() {
        // update data structures
        _previousState.setState( _currentState );
        _currentState.setState( _glacier.getEvolutionState() );
        // update display
        _elaCurrent.setValue( _currentState.ela );
        _elaPrevious.setValue( _previousState.ela );
        _timescaleCurrent.setValue( _currentState.timescale );
        _timescalePrevious.setValue( _previousState.timescale );
        _qAdvanceLimitCurrent.setValue( _currentState.qAdvanceLimit );
        _qAdvanceLimitPrevious.setValue( _previousState.qAdvanceLimit );
        _deltaQelaCurrent.setValue( _currentState.deltaQela );
        _deltaQelaPrevious.setValue( _previousState.deltaQela );
        _qelaCurrent.setValue( _currentState.qela );
        _qelaPrevious.setValue( _previousState.qela );
        _qelaxCurrent.setValue( _currentState.qelax );
        _qelaxPrevious.setValue( _previousState.qelax );
        _glacierLengthCurrent.setValue( _currentState.glacierLength );
        _glacierLengthPrevious.setValue( _previousState.glacierLength );
        _terminusCurrent.setValue( _currentState.terminus );
        _terminusPrevious.setValue( _previousState.terminus );
    }

    /*
     * Displays a numeric value.
     */
    private static class NumberDisplay extends JLabel {

        private final DecimalFormat format;
        
        public NumberDisplay( String formatPattern ) {
            super( "                              " );
            format = new DecimalFormat( formatPattern );
        }

        public void setValue( double value ) {
            setText( format.format( value ) );
        }
    }
    
    /*
     * Displays a Point2D.
     */
    private static class Point2DDisplay extends JLabel {
        
        private final DecimalFormat format;
        
        public Point2DDisplay( String formatPattern ) {
            super( "                                    " );
            format = new DecimalFormat( formatPattern );
        }
        
        public void setValue( Point2D p ) {
            setText( "(" + format.format( p.getX() ) + "," + format.format( p.getY() ) + ")" );
        }
    }
}
