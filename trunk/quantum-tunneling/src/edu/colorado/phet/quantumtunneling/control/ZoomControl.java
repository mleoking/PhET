/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jfree.chart.plot.XYPlot;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;


/**
 * ZoomControl
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ZoomControl extends JPanel {
    
    public static final int HORIZONTAL = SwingConstants.HORIZONTAL;
    public static final int VERTICAL = SwingConstants.VERTICAL;
    
    private static final Insets MARGIN = new Insets( 0, 0, 0, 0 );
    
    private int _orientation;
    private XYPlot _plot;
    private JButton _zoomInButton, _zoomOutButton;
    private double _zoomInFactor, _zoomOutFactor;
    
    public ZoomControl( int orientation, XYPlot plot ) {
        super();
        this.setOpaque( false );
        
        _orientation = orientation;
        _plot = plot;
        
        // Zoom In button
        _zoomInButton = new JButton( "+" );
        _zoomInButton.setOpaque( false );
        _zoomInButton.setMargin( MARGIN );
        _zoomInButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleZoomIn();
            }
        } );
        
        // Zoom Out button
        _zoomOutButton = new JButton( "-" );
        _zoomOutButton.setOpaque( false );
        _zoomOutButton.setMargin( MARGIN );
        _zoomOutButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleZoomOut();
            }
        } );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        if ( orientation == HORIZONTAL ) {
            layout.addComponent( _zoomInButton, 0, 0 );
            layout.addComponent( _zoomOutButton, 0, 1 );
        }
        else { 
            layout.addComponent( _zoomInButton, 0, 0 );
            layout.addComponent( _zoomOutButton, 1, 0 );
        }
        
        _zoomInFactor = 2.0;
        _zoomOutFactor = 0.5;
    }

    private void handleZoomIn() {
        System.out.println( "zoom in" );//XXX
        if ( _orientation == VERTICAL ) {
            //XXX
        }
        else {
            //XXX
        }
    }
    
    private void handleZoomOut() {
        System.out.println( "zoom out" );//XXX 
        if ( _orientation == VERTICAL ) {
            //XXX
        }
        else {
            //XXX
        }
    }
}
