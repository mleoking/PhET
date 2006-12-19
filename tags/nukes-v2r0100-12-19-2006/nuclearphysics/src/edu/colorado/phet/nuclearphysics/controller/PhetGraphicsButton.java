/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.util.EventChannel;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.EventObject;
import java.util.EventListener;
import java.io.IOException;

/**
 * PhetGraphicsButton
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhetGraphicsButton extends CompositePhetGraphic {
    protected PhetImageGraphic buttonUpIG;
    protected PhetImageGraphic buttonDownIG;

    public PhetGraphicsButton( Component component, BufferedImage upImage, BufferedImage downImage ) {
        super( component );

        if( downImage != null ) {
            buttonDownIG = new PhetImageGraphic( component, downImage );
            addGraphic( buttonDownIG );
        }
        buttonUpIG = new PhetImageGraphic( component, upImage );
        addGraphic( buttonUpIG );
        setCursorHand();

        addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                actionListenerProxy.actionPerformed( new ActionEvent( this ) );
                if( buttonDownIG != null ) {
                    buttonUpIG.setVisible( false );
                }
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                buttonUpIG.setVisible( true );
            }
        } );

    }

    //--------------------------------------------------------------------------------------------------
    // ActionListener definition
    //--------------------------------------------------------------------------------------------------
    EventChannel actionEventChannel = new EventChannel( ActionListener.class );
    ActionListener actionListenerProxy = (ActionListener)actionEventChannel.getListenerProxy();

    public void addActionListener( ActionListener listener ) {
        actionEventChannel.addListener( listener );
    }

    public void removeActionListener( ActionListener listener ) {
        actionEventChannel.removeListener( listener );
    }

    public class ActionEvent extends EventObject {
        public ActionEvent( Object source ) {
            super( source );
        }
    }

    public interface ActionListener extends EventListener {
        void actionPerformed( ActionEvent event );
    }
}
