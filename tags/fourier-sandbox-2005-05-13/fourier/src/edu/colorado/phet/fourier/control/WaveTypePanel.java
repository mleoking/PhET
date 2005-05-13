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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;
import edu.colorado.phet.fourier.view.ComponentsGraphic;
import edu.colorado.phet.fourier.view.SineWaveGraphic;
import edu.colorado.phet.fourier.view.SumGraphic;


/**
 * @author cmalley
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WaveTypePanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Things to be controlled.
    private ComponentsGraphic _componentsGraphic;
    private SumGraphic _sumGraphic;
    
    // UI components
    private JComboBox _waveTypeComboBox;
    
    // Choices
    private Hashtable _waveTypeChoices;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param componentsGraphic
     */
    public WaveTypePanel( ComponentsGraphic componentsGraphic, SumGraphic sumGraphic )
    {
        assert( componentsGraphic != null );
        assert( sumGraphic != null );
        
        // Things we'll be controlling.
        _componentsGraphic = componentsGraphic;
        _sumGraphic = sumGraphic;
        
        // Title
        Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, 2 );
        String title = SimStrings.get( "WaveTypePanel.title" );
        TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
        setBorder( titleBorder );
        
        // Wave Type
        JPanel waveTypePanel = new JPanel();
        {
            // Label
            JLabel label = new JLabel( SimStrings.get( "WaveTypePanel.show" ) );
            
            // Choices
            _waveTypeChoices = new Hashtable();
            _waveTypeChoices.put( SimStrings.get( "WaveTypePanel.sines" ), new Integer( SineWaveGraphic.WAVE_TYPE_SINE ) );
            _waveTypeChoices.put( SimStrings.get( "WaveTypePanel.cosines" ), new Integer( SineWaveGraphic.WAVE_TYPE_COSINE ) );
            
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
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        layout.addComponent( waveTypePanel, row++, 0 );
        
        // Wire up event handling.
        EventListener listener = new EventListener();
        _waveTypeComboBox.addActionListener( listener );
        
        // Set the state of the controls.
        update();
    }
    
    /**
     * Updates the control panel to match the state of the things that it's controlling.
     */
    public void update() {
        // Wave Type
        {
            Object item = null;
            switch ( _componentsGraphic.getWaveType() ) {
            case SineWaveGraphic.WAVE_TYPE_SINE:
                item = SimStrings.get( "WaveTypePanel.sines" );
                break;
            case SineWaveGraphic.WAVE_TYPE_COSINE:
                item = SimStrings.get( "WaveTypePanel.cosines" );
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
    private class EventListener implements ActionListener {
        
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
    }
}