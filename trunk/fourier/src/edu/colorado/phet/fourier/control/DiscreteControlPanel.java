/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;
import edu.colorado.phet.fourier.view.ComponentsGraphic;
import edu.colorado.phet.fourier.view.SineWaveGraphic;
import edu.colorado.phet.fourier.view.SumGraphic;


/**
 * DiscreteControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteControlPanel extends FourierControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Things to be controlled.
    private FourierSeries _fourierSeriesModel;
    private ComponentsGraphic _componentsGraphic;
    private SumGraphic _sumGraphic;

    // UI components
    private ControlPanelSlider _numberOfComponentsSlider;
    private ControlPanelSlider _fundamentalFrequencySlider;
    private JComboBox _waveTypeComboBox;

    // Choices
    private Hashtable _waveTypeChoices;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param fourierSeriesModel
     */
    public DiscreteControlPanel( 
            FourierModule module, 
            FourierSeries fourierSeriesModel, 
            ComponentsGraphic componentsGraphic, 
            SumGraphic sumGraphic ) {
        
        super( module );
        
        assert ( fourierSeriesModel != null );
        assert( componentsGraphic != null );
        assert( sumGraphic != null );
        
        // Things we'll be controlling.
        _fourierSeriesModel = fourierSeriesModel;
        _componentsGraphic = componentsGraphic;
        _sumGraphic = sumGraphic;

        // Number of harmonics
        {
            String format = SimStrings.get( "DiscreteControlPanel.numberOfComponents" );
            _numberOfComponentsSlider = new ControlPanelSlider( format );
            _numberOfComponentsSlider.setMaximum( 15 );
            _numberOfComponentsSlider.setMinimum( 5 );
            _numberOfComponentsSlider.setValue( 7 );
            _numberOfComponentsSlider.setMajorTickSpacing( 2 );
            _numberOfComponentsSlider.setMinorTickSpacing( 1 );
            _numberOfComponentsSlider.setSnapToTicks( true );
        }

        // Fundamental frequency
        {
            String format = SimStrings.get( "DiscreteControlPanel.fundamentalFrequency" );
            _fundamentalFrequencySlider = new ControlPanelSlider( format );
            _fundamentalFrequencySlider.setMaximum( 1200 );
            _fundamentalFrequencySlider.setMinimum( 200 );
            _fundamentalFrequencySlider.setValue( 440 );
            _fundamentalFrequencySlider.setMajorTickSpacing( 250 );
            _fundamentalFrequencySlider.setMinorTickSpacing( 50 );
            _fundamentalFrequencySlider.setSnapToTicks( false );
        }
        
        // Wave Type
        JPanel waveTypePanel = new JPanel();
        {
            // Label
            JLabel label = new JLabel( SimStrings.get( "DiscreteControlPanel.show" ) );
            
            // Choices
            _waveTypeChoices = new Hashtable();
            _waveTypeChoices.put( SimStrings.get( "DiscreteControlPanel.sines" ), new Integer( SineWaveGraphic.WAVE_TYPE_SINE ) );
            _waveTypeChoices.put( SimStrings.get( "DiscreteControlPanel.cosines" ), new Integer( SineWaveGraphic.WAVE_TYPE_COSINE ) );
            
            // Wave Type combo box
            _waveTypeComboBox = new JComboBox( );
            Enumeration enum = _waveTypeChoices.keys();
            while ( enum.hasMoreElements() ) {
                _waveTypeComboBox.addItem( enum.nextElement() );
            }
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( waveTypePanel );
            waveTypePanel.setLayout( layout );
            layout.addAnchoredComponent( label, 0, 0, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _waveTypeComboBox, 0, 1, GridBagConstraints.WEST );
        }

//        // Layout
//        EasyGridBagLayout layout = new EasyGridBagLayout( this );
//        setLayout( layout );
//        int row = 0;
//        layout.addFilledComponent( _numberOfComponentsSlider, row++, 0, GridBagConstraints.HORIZONTAL );
//        layout.addFilledComponent( _fundamentalFrequencySlider, row++, 0, GridBagConstraints.HORIZONTAL );
//        layout.addComponent( waveTypePanel, row++, 0 );
        addFullWidth( _numberOfComponentsSlider );
        addFullWidth( _fundamentalFrequencySlider );
        addFullWidth( waveTypePanel );

        // Wire up event handling.
        EventListener listener = new EventListener();
        _numberOfComponentsSlider.addChangeListener( listener );
        _fundamentalFrequencySlider.addChangeListener( listener );
        _waveTypeComboBox.addActionListener( listener );

        // Set the state of the controls.
        update();
    }

    /**
     * Updates the control panel to match the state of the things that it's controlling.
     */
    public void update() {
        // Number of components
        _numberOfComponentsSlider.setValue( _fourierSeriesModel.getNumberOfComponents() );
        // Fundamental frequency
        _fundamentalFrequencySlider.setValue( (int) _fourierSeriesModel.getFundamentalFrequency() );
        // Wave Type
        {
            Object item = null;
            switch ( _componentsGraphic.getWaveType() ) {
            case SineWaveGraphic.WAVE_TYPE_SINE:
                item = SimStrings.get( "DiscreteControlPanel.sines" );
                break;
            case SineWaveGraphic.WAVE_TYPE_COSINE:
                item = SimStrings.get( "DiscreteControlPanel.cosines" );
                break;
            default:
            }
            assert ( item != null );
            _waveTypeComboBox.setSelectedItem( item );
        }
    }

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     */
    private class EventListener implements ActionListener, ChangeListener {

        public EventListener() {}

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _waveTypeComboBox ) {
                // Use the selection to lookup the associated symbolic constant.
                Object key = _waveTypeComboBox.getSelectedItem();
                Object value = _waveTypeChoices.get( key );
                assert( value != null && value instanceof Integer ); // programming error
                int waveType = ((Integer)value).intValue();
                _componentsGraphic.setWaveType( waveType );
                _sumGraphic.setWaveType( waveType );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _numberOfComponentsSlider ) {
                if ( !_numberOfComponentsSlider.getSlider().getValueIsAdjusting() ) {
                    int numberOfComponents = _numberOfComponentsSlider.getValue();
                    _fourierSeriesModel.setNumberOfComponents( numberOfComponents );
                }
            }
            else if ( event.getSource() == _fundamentalFrequencySlider ) {
                int fundamentalFrequency = _fundamentalFrequencySlider.getValue();
                _fourierSeriesModel.setFundamentalFrequency( fundamentalFrequency );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }

}
