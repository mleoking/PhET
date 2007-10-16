/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.control;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.mvcexample.MVCApplication;
import edu.colorado.phet.mvcexample.model.CModelElement;
import edu.colorado.phet.mvcexample.model.CModelElement.CModelElementListener;

/**
 * CControlPanel is the control panel for an CModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CControlPanel extends JPanel implements CModelElementListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = MVCApplication.RESOURCE_LOADER.getLocalizedString( "CControlPanel.title" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private CModelElement _modelElement;
    
    private PositionControl _positionDisplay;
    private OrientationControl _orientationControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public CControlPanel( CModelElement modelElement ) {
        super();
        
        _modelElement = modelElement;
        _modelElement.addListener( this );
        
        // Title
        JLabel titleLabel = new JLabel( TITLE );
        titleLabel.setFont( new PhetDefaultFont( 14, true /* bold */ ) );
        
        // Position control (display only)
        _positionDisplay = new PositionControl();
        _positionDisplay.setValue( _modelElement.getPositionReference() );
        
        // Orientation control
        _orientationControl = new OrientationControl();
        _orientationControl.setValue( _modelElement.getOrientation() );
        _orientationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Object o = e.getSource();
                if ( o == _orientationControl ) {
                    final double radians = Math.toRadians( _orientationControl.getValue() );
                    _modelElement.setOrientation( radians );
                }
            }
        } );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row++, column );
        layout.addComponent( _positionDisplay, row++, column );
        layout.addComponent( _orientationControl, row++, column );
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _modelElement.removeListener( this );
    }
    
    //----------------------------------------------------------------------------
    // CModelElementListener implementation
    //----------------------------------------------------------------------------
    
    public void positionChanged() {
        _positionDisplay.setValue( _modelElement.getPositionReference() );
    }
    
    public void orientationChanged() {
        final double degrees = Math.toDegrees( _modelElement.getOrientation() );
        _orientationControl.setValue( degrees );
    }
}
