/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Decorates with buttons and controls.
 */
public class IntensityReaderMenuDecorator extends PNode {
    private ArrayList listeners = new ArrayList();
    private IntensityReader intensityReader;
    private PSwing buttonPSwing;
    private Point lastMovePoint = null;

    public IntensityReaderMenuDecorator( String title, final PSwingCanvas pSwingCanvas, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, IClock clock ) {
        this.intensityReader = new IntensityReader( title, waveModel, latticeScreenCoordinates, clock );
        JButton options = new JButton( WIStrings.getString( "options" ) );
        options.setFont( new Font( "Lucida Sans", Font.PLAIN, 10 ) );
        final JPopupMenu jPopupMenu = new JPopupMenu( WIStrings.getString( "popup.menu" ) );
        final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem( WIStrings.getString( "display.readout" ), intensityReader.isReadoutVisible() );
        menuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                intensityReader.setReadoutVisible( menuItem.isSelected() );
            }
        } );
        jPopupMenu.add( menuItem );
        jPopupMenu.addSeparator();
        JMenuItem deleteItem = new JMenuItem( WIStrings.getString( "delete" ) );
        deleteItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doDelete();
            }

        } );
        jPopupMenu.add( deleteItem );
        pSwingCanvas.addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent e ) {
                lastMovePoint = e.getPoint();
            }

            public void mouseMoved( MouseEvent e ) {
                lastMovePoint = e.getPoint();
            }
        } );
        options.addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
            }

            public void mouseReleased( MouseEvent e ) {
                if( lastMovePoint != null ) {
                    jPopupMenu.show( pSwingCanvas, lastMovePoint.x, lastMovePoint.y );
                }
            }
        } );
        buttonPSwing = new PSwing( pSwingCanvas, options );
        addChild( intensityReader );
        addChild( buttonPSwing );
        intensityReader.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocation();
            }
        } );
    }

    private void doDelete() {
        for( int i = 0; i < listeners.size(); i++ ) {
            IntensityReaderMenuDecorator.Listener listener = (IntensityReaderMenuDecorator.Listener)listeners.get( i );
            listener.deleted();
        }
    }

    public static interface Listener {
        void deleted();
    }

    public void addListener( IntensityReaderMenuDecorator.Listener listener ) {
        listeners.add( listener );
    }

    private void updateLocation() {
        buttonPSwing.setOffset( intensityReader.getFullBounds().getX(), intensityReader.getFullBounds().getMaxY() );
    }

    public void update() {
        intensityReader.update();
    }
}
