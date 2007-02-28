package edu.colorado.phet.forces1d.common.plotdevice;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.forces1d.common.phetcomponents.PhetButton;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 27, 2004
 * Time: 12:03:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoPauseClear extends GraphicLayerSet {
    private ArrayList listeners = new ArrayList();

    public GoPauseClear( Component component ) {
        super( component );
        PhetButton go = new PhetButton( component, SimStrings.get( "GoPauseClear.go" ) );
        PhetButton pause = new PhetButton( component, SimStrings.get( "GoPauseClear.pause" ) );
        PhetButton clear = new PhetButton( component, SimStrings.get( "GoPauseClear.clear" ) );
        go.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for( int i = 0; i < listeners.size(); i++ ) {
                    Listener listener = (Listener)listeners.get( i );
                    listener.go();
                }

            }
        } );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for( int i = 0; i < listeners.size(); i++ ) {
                    Listener listener = (Listener)listeners.get( i );
                    listener.pause();
                }
            }
        } );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for( int i = 0; i < listeners.size(); i++ ) {
                    Listener listener = (Listener)listeners.get( i );
                    listener.clear();
                }
            }
        } );
        addGraphic( go );
        addGraphic( pause );
        addGraphic( clear );
        int inset = 5;
        go.setLocation( 0, 0 );
        pause.setLocation( 0, go.getHeight() + inset );
        clear.setLocation( 0, pause.getHeight() + go.getHeight() + inset * 2 );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static interface Listener {
        void go();

        void pause();

        void clear();
    }
}
