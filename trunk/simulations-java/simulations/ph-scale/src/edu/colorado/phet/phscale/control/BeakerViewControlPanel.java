/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleStrings;


public class BeakerViewControlPanel extends JPanel {
    
    private static final Font CONTROL_FONT = new PhetFont( Font.PLAIN, 18 );;
    
    private final ArrayList _listeners;
    private final JCheckBox _countViewCheckBox;
    private final JCheckBox _ratioViewCheckBox;
    
    public BeakerViewControlPanel() {
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
        layout.addComponent( _countViewCheckBox, row, col++ );
        layout.addComponent( Box.createHorizontalStrut( 15 ), row, col++ );
        layout.addComponent( _ratioViewCheckBox, row, col++ );
    }
    
    public boolean isCountViewSelected() {
        return _countViewCheckBox.isSelected();
    }
    
    public void setCountViewSelected( boolean selected ) {
        if ( selected != isCountViewSelected() ) {
            _countViewCheckBox.setSelected( selected );
            notifyCountViewChanged();
        }
    }
    
    public boolean isRatioViewSelected() {
        return _ratioViewCheckBox.isSelected();
    }
    
    public void setRatioViewSelected( boolean selected ) {
        if ( selected != isRatioViewSelected() ) {
            _ratioViewCheckBox.setSelected( selected );
            notifyRatioViewChanged();
        }
    }
    
    public interface BeakerViewControlPanelListener {
        public void countViewChanged( boolean selected );
        public void ratioViewChanged( boolean selected );
    }
    
    public static class BeakerViewControlPanelAdapter implements BeakerViewControlPanelListener {
        public void countViewChanged( boolean selected ) {}
        public void ratioViewChanged( boolean selected ) {}
    }
    
    public void addBeakerViewControlPanelListener( BeakerViewControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeBeakerViewControlPanelListener( BeakerViewControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyCountViewChanged() {
        boolean b = isCountViewSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (BeakerViewControlPanelListener) i.next() ).countViewChanged( b );
        }
    }
    
    private void notifyRatioViewChanged() {
        boolean b = isRatioViewSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (BeakerViewControlPanelListener) i.next() ).ratioViewChanged( b );
        }
    }

}
