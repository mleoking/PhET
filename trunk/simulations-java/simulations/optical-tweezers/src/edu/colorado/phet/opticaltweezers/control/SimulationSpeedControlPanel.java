/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.OTClock;


public class SimulationSpeedControlPanel extends JPanel {

    private OTClock _clock;
    private SimulationSpeedSlider _slider;
    
    public SimulationSpeedControlPanel( Font titleFont, Font controlFont, OTClock clock ) {
        super();
        
        JLabel titleLabel = new JLabel( OTResources.getString( "label.simulationSpeed" ) );
        titleLabel.setFont( titleFont );
        
        _clock = clock;
        _clock.addClockListener( new ClockAdapter() {
            //XXX _slider.setValue when the clock's timing strategy (dt) is changed
        });
        
        _slider = new SimulationSpeedSlider( clock.getSlowRange(), clock.getFastRange(), clock.getDt() );
        _slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleSliderChange();
            }
        });
        _slider.setOffset( 15, 25 );//HACK
        
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setBackground( getBackground() );
        canvas.setBorder( null );
        canvas.getLayer().addChild( _slider );
        int w = (int) _slider.getFullBounds().getWidth() + 5; //HACK
        int h = (int) _slider.getFullBounds().getHeight() + 8; //HACK
        canvas.setPreferredSize( new Dimension( w, h ) );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row++, column );
        layout.addComponent( canvas, row++, column );
        
    }
    
    public void setSimulationSpeed( double dt ) {
        _slider.setValue( dt );
        handleSliderChange();
    }
    
    public double getSimulationSpeed() {
        return _slider.getValue();
    }
    
    private void handleSliderChange() {
        _clock.setDt( _slider.getValue() );
    }
}
