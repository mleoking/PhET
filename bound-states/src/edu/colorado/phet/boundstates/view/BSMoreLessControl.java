/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.view;

import javax.swing.JButton;

import org.jfree.data.Range;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class BSMoreLessControl extends PSwing {

    private static final double ZOOM_FACTOR = 15;
    
    private JButton _button;
    private String _moreString, _lessString;
    private boolean _moreFlag;
    private BSCombinedChart _combinedChart;
    private BSEnergyPlot _energyPlot;
    
    public BSMoreLessControl( PSwingCanvas canvas, BSCombinedChart combinedChart, BSEnergyPlot energyPlot ) {
        super( canvas, new JButton() );
        _button = (JButton)getComponent();
        _button.setOpaque( false );
        _moreString = "More";
        _lessString = "Less";
        _button.setText( _moreString );
        _moreFlag = true;
        _combinedChart = combinedChart;
        _energyPlot = energyPlot;
        addInputEventListener( new EventDispatcher() );
    }
    
    private class EventDispatcher extends PBasicInputEventHandler {
        
        public void mouseReleased( PInputEvent event ) {
            if ( _moreFlag ) {
                handleZoomIn();
            }
            else {
                handleZoomOut();
            }
        }
    }
    
    private void handleZoomIn() {
        assert( _moreFlag == true );
        Range range = _energyPlot.getRangeAxis().getRange();
        double min = range.getLowerBound() * ZOOM_FACTOR;
        double max = range.getUpperBound();
        setRange( min, max );
        _moreFlag = false;
        _button.setText( _lessString );
    } 
    
    private void handleZoomOut() {
        assert( _moreFlag == false );
        Range range = _energyPlot.getRangeAxis().getRange();
        double min = range.getLowerBound() / ZOOM_FACTOR;
        double max = range.getUpperBound();
        setRange( min, max );
        _moreFlag = true;
        _button.setText( _moreString );
    }
    
    private void setRange( double min, double max ) {
        _energyPlot.getRangeAxis().setRange( min, max );
        _combinedChart.getEnergyPlot().getRangeAxis().setRange( min, max );
    }
}
