// Copyright 2002-2011, University of Colorado

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
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.umd.cs.piccolo.PCanvas;

/**
 * Controls for various colors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ColorControls extends JPanel {
    
    private final WeakAcid weakAcid;
    private final DotsNode dotsNode;
    private final PCanvas canvas;
    
    private final ColorControl solutionColorControl;
    private final ColorControl colorHAControl, colorAControl, colorH3OControl, colorOHControl, colorH2OControl;
    private final ColorControl canvasColorControl;
    
    public ColorControls( JFrame parentFrame, final WeakAcid weakAcid, final DotsNode dotsNode, final PCanvas canvas, boolean dev ) {
        setBorder( new TitledBorder( "Colors" ) );
        
        this.weakAcid = weakAcid;
        weakAcid.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        });
        
        this.dotsNode = dotsNode;
        dotsNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        });
        
        this.canvas = canvas;
        canvas.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( "background" ) ) {
                    updateControls();
                }
            }
        });
        
        solutionColorControl = new ColorControl( parentFrame, "solution color:", weakAcid.getColor() );
        solutionColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                weakAcid.setColor( solutionColorControl.getColor() );
            }
        } );
        
        colorHAControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.HA_FRAGMENT ), dotsNode.getColorHA() );
        colorHAControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setColorHA( colorHAControl.getColor() );
            }
        } );

        colorAControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.A_MINUS_FRAGMENT ), dotsNode.getColorA() );
        colorAControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setColorA( colorAControl.getColor() );
            }
        } );

        colorH3OControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.H3O_PLUS_FRAGMENT ), dotsNode.getColorH3O() );
        colorH3OControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setColorH3O( colorH3OControl.getColor() );
            }
        } );

        colorOHControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.OH_MINUS_FRAGMENT ), dotsNode.getColorOH() );
        colorOHControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setColorOH( colorOHControl.getColor() );
            }
        } );
        
        colorH2OControl = new ColorControl( parentFrame, HTMLUtils.toHTMLString( MGPConstants.H2O_FRAGMENT ), dotsNode.getColorH2O() );
        colorH2OControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dotsNode.setColorH2O( colorH2OControl.getColor() );
            }
        } );
        
        canvasColorControl = new ColorControl( parentFrame, "canvas color:", canvas.getBackground() );
        canvasColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                canvas.setBackground( canvasColorControl.getColor() );
            }
        } );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( solutionColorControl, row++, column );
        layout.addComponent( colorHAControl, row++, column );
        layout.addComponent( colorAControl, row++, column );
        layout.addComponent( colorH3OControl, row++, column );
        if ( dev ) {
            layout.addComponent( colorOHControl, row++, column );
        }
        layout.addComponent( colorH2OControl, row++, column );
        layout.addComponent( canvasColorControl, row++, column );
        
        updateControls();
    }
    
    private void updateControls() {
        solutionColorControl.setColor( weakAcid.getColor() );
        colorHAControl.setColor( dotsNode.getColorHA() );
        colorAControl.setColor( dotsNode.getColorA() );
        colorH3OControl.setColor( dotsNode.getColorH3O() );
        colorOHControl.setColor( dotsNode.getColorOH() );
        colorH2OControl.setColor( dotsNode.getColorH2O() );
        canvasColorControl.setColor( canvas.getBackground() );
    }
}
