// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.view.graph;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.phscale.PHScaleStrings;

/**
 * ZoomControlPanel is the zoom control panel used in the linear bar graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ZoomControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _listeners;
    private final JButton _zoomInButton, _zoomOutButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ZoomControlPanel() {
        
        _listeners = new ArrayList();
        
        _zoomInButton = new JButton( PHScaleStrings.BUTTON_ZOOM_IN );
        _zoomInButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyZoomIn();
            }
        } );

        _zoomOutButton = new JButton( PHScaleStrings.BUTTON_ZOOM_OUT );
        _zoomOutButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyZoomOut();
            }
        } );

        setOpaque( false );
        EasyGridBagLayout zoomPanelLayout = new EasyGridBagLayout( this );
        setLayout( zoomPanelLayout );
        int row = 0;
        int column = 0;
        zoomPanelLayout.addFilledComponent( _zoomInButton, row++, column, GridBagConstraints.HORIZONTAL );
        zoomPanelLayout.addFilledComponent( _zoomOutButton, row++, column, GridBagConstraints.HORIZONTAL );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setZoomInEnabled( boolean enabled ) {
        _zoomInButton.setEnabled( enabled );
    }
    
    public void setZoomOutEnabled( boolean enabled ) {
        _zoomOutButton.setEnabled( enabled );
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface ZoomControlPanelListener {
        public void zoomIn();
        public void zoomOut();
    }
    
    public void addZoomControlPanelListener( ZoomControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeZoomControlPanelListener( ZoomControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyZoomIn() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ZoomControlPanelListener) i.next() ).zoomIn();
        }
    }
    
    private void notifyZoomOut() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ZoomControlPanelListener) i.next() ).zoomOut();
        }
    }

}
