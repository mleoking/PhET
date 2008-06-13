/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleStrings;


public class ViewControlPanel extends JPanel {
    
    private static final Font CONTROL_FONT = new PhetFont( Font.PLAIN, 18 );;
    
    private final ArrayList _listeners;
    private final JCheckBox _countViewCheckBox;
    private final JCheckBox _ratioViewCheckBox;
    
    public ViewControlPanel() {
        super();
        setOpaque( false );

        _listeners = new ArrayList();
        
        _countViewCheckBox = new JCheckBox( PHScaleStrings.CHECK_BOX_MOLECULE_COUNT );
        _countViewCheckBox.setFont( CONTROL_FONT );
        
        _ratioViewCheckBox = new JCheckBox( PHScaleStrings.getBeakerViewRatioString() );
        _ratioViewCheckBox.setFont( CONTROL_FONT );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( _countViewCheckBox, row++, col );
        layout.addComponent( _ratioViewCheckBox, row++, col );
    }
    
    public boolean isCountViewSelected() {
        return _countViewCheckBox.isSelected();
    }
    
    public void setCountViewSelected( boolean selected ) {
        if ( selected != isCountViewSelected() ) {
            _countViewCheckBox.setSelected( selected );
            notifyViewChanged();
        }
    }
    
    public boolean isRatioViewSelected() {
        return _ratioViewCheckBox.isSelected();
    }
    
    public void setRatioViewSelected( boolean selected ) {
        if ( selected != isRatioViewSelected() ) {
            _ratioViewCheckBox.setSelected( selected );
            notifyViewChanged();
        }
    }
    
    public interface ViewControlPanelListener {
        public void viewChanged();
    }
    
    public void addBeakerViewControlPanelListener( ViewControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeBeakerViewControlPanelListener( ViewControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyViewChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewControlPanelListener) i.next() ).viewChanged();
        }
    }

}
