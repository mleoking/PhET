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


public class GraphUnitsControlPanel extends JPanel {
    
    private static final Font CONTROL_FONT = new PhetFont( Font.PLAIN, 18 );;
    
    private final ArrayList _listeners;
    private final JRadioButton _concentrationRadioButton;
    private final JRadioButton _numberOfMolesRadioButton;
    
    public GraphUnitsControlPanel() {
        super();
        setOpaque( false );

        _listeners = new ArrayList();
        
        _concentrationRadioButton = new JRadioButton( PHScaleStrings.getConcentrationString() );
        _concentrationRadioButton.setFont( CONTROL_FONT );
        
        _numberOfMolesRadioButton = new JRadioButton( PHScaleStrings.getNumberOfMolesString() );
        _numberOfMolesRadioButton.setFont( CONTROL_FONT );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _concentrationRadioButton );
        buttonGroup.add( _numberOfMolesRadioButton );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( _concentrationRadioButton, row++, col );
        layout.addComponent( _numberOfMolesRadioButton, row++, col );
        
        _concentrationRadioButton.setSelected( true );
    }
    
    public boolean isConcentrationSelected() {
        return _concentrationRadioButton.isSelected();
    }
    
    public void setConcentrationSelected( boolean selected ) {
        if ( selected != isConcentrationSelected() ) {
            _concentrationRadioButton.setSelected( selected );
            notifySelectionChanged();
        }
    }
    
    public boolean isMolesSelected() {
        return !isConcentrationSelected();
    }
    
    public void setMolesSelected( boolean selected ) {
        setConcentrationSelected( !selected );
    }
    
    public interface GraphUnitsControlPanelListener {
        public void selectionChanged();
    }
    
    public void addGraphUnitsControlPanelListener( GraphUnitsControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeGraphUnitsControlPanelListener( GraphUnitsControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifySelectionChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (GraphUnitsControlPanelListener) i.next() ).selectionChanged();
        }
    }
}
