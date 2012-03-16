// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.view.beaker;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;

/**
 * ViewControlPanel selects the things that are visible in the beaker.
 * The choices are to show a "molecule count" and/or "H3O/OH ratio".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font CONTROL_FONT = PHScaleConstants.CONTROL_FONT;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _listeners;
    private final JCheckBox _countCheckBox;
    private final JCheckBox _ratioCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ViewControlPanel() {
        super();
        setOpaque( false );

        _listeners = new ArrayList();
        
        _countCheckBox = new JCheckBox( PHScaleStrings.CHECK_BOX_MOLECULE_COUNT );
        _countCheckBox.setOpaque( false );
        _countCheckBox.setFont( CONTROL_FONT );
        _countCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                notifyCountChanged();
            }
        });
        
        _ratioCheckBox = new JCheckBox( PHScaleStrings.getBeakerViewRatioString() );
        _ratioCheckBox.setOpaque( false );
        _ratioCheckBox.setFont( CONTROL_FONT );
        _ratioCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                notifyRatioChanged();
            }
        });
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( _countCheckBox, row++, col );
        layout.addComponent( _ratioCheckBox, row++, col );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isCountSelected() {
        return _countCheckBox.isSelected();
    }
    
    public void setCountSelected( boolean selected ) {
        if ( selected != isCountSelected() ) {
            _countCheckBox.setSelected( selected );
            notifyCountChanged();
        }
    }
    
    public boolean isRatioSelected() {
        return _ratioCheckBox.isSelected();
    }
    
    public void setRatioSelected( boolean selected ) {
        if ( selected != isRatioSelected() ) {
            _ratioCheckBox.setSelected( selected );
            notifyRatioChanged();
        }
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface ViewControlPanelListener {
        public void countChanged( boolean selected );
        public void ratioChanged( boolean selected );
    }
    
    public static class ViewControlPanelAdapter implements ViewControlPanelListener {
        public void countChanged( boolean selected ) {}
        public void ratioChanged( boolean selected ) {}
    }
    
    public void addViewControlPanelListener( ViewControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeViewControlPanelListener( ViewControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyCountChanged() {
        boolean selected = _countCheckBox.isSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewControlPanelListener) i.next() ).countChanged( selected );
        }
    }
    
    private void notifyRatioChanged() {
        boolean selected = _ratioCheckBox.isSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewControlPanelListener) i.next() ).ratioChanged( selected );
        }
    }

}
