/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.umd.cs.piccolo.PCanvas;

/**
 * Controls for the canvas (aka play area).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class CanvasControls extends JPanel {
    
    private final ColorControl backgroundColorControl;
    
    public CanvasControls( JFrame parentFrame, final PCanvas canvas ) {
        setBorder( new TitledBorder( "Play area" ) );
        
        canvas.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( "background" ) ) {
                    backgroundColorControl.setColor( canvas.getBackground() );
                }
            }
        });
        
        backgroundColorControl = new ColorControl( parentFrame, "background color:", canvas.getBackground() );
        backgroundColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                canvas.setBackground( backgroundColorControl.getColor() );
            }
        } );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( backgroundColorControl, row, column++ );
    }
}
