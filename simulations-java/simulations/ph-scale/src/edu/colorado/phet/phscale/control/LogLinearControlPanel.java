/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleStrings;


public class LogLinearControlPanel extends JPanel {
    
    private static final Font CONTROL_FONT = new PhetFont( Font.PLAIN, 18 );;
    
    private final ArrayList _listeners;
    private final JRadioButton _logRadioButton;
    private final JRadioButton _linearRadioButton;
    
    public LogLinearControlPanel() {
        super();
        setOpaque( false );

        _listeners = new ArrayList();
        
        _logRadioButton = new JRadioButton( PHScaleStrings.RADIO_BUTTON_LOGARITHMIC_SCALE );
        _logRadioButton.setFont( CONTROL_FONT );
        
        _linearRadioButton = new JRadioButton( PHScaleStrings.RADIO_BUTTON_LINEAR_SCALE );
        _linearRadioButton.setFont( CONTROL_FONT );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _logRadioButton );
        buttonGroup.add( _linearRadioButton );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( _logRadioButton, row++, col );
        layout.addComponent( _linearRadioButton, row++, col );
        
        _logRadioButton.setSelected( true );
    }
    
    public boolean isLogSelected() {
        return _logRadioButton.isSelected();
    }
    
    public void setLogSelected( boolean selected ) {
        if ( selected != isLogSelected() ) {
            _logRadioButton.setSelected( selected );
            notifySelectionChanged();
        }
    }
    
    public boolean isLinearSelected() {
        return !isLogSelected();
    }
    
    public void setLinearSelected( boolean selected ) {
        setLogSelected( !selected );
    }
    
    public interface LogLinearControlPanelListener {
        public void selectionChanged();
    }
    
    public void addLogLinearControlPanelListener( LogLinearControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeLogLinearControlPanelListener( LogLinearControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifySelectionChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (LogLinearControlPanelListener) i.next() ).selectionChanged();
        }
    }
}
