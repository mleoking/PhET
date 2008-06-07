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

import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.EventListener;
import java.util.EventObject;

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
    EventChannel actionEventChannel = new EventChannel( ButtonActionListener.class );
    ButtonActionListener actionListenerProxy = (ButtonActionListener)actionEventChannel.getListenerProxy();

    public void addActionListener( ButtonActionListener listener ) {
        actionEventChannel.addListener( listener );
    }

    public void removeActionListener( ButtonActionListener listener ) {
        actionEventChannel.removeListener( listener );
    }

    public class ActionEvent extends EventObject {
        public ActionEvent( Object source ) {
            super( source );
        }
    }

    public interface ButtonActionListener extends EventListener {
        void actionPerformed( ActionEvent event );
    }
}
