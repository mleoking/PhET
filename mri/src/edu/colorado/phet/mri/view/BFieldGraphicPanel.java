/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.mri.model.Electromagnet;

import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * BFieldGraphicPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BFieldGraphicPanel extends PhetPCanvas {
    private RegisterablePNode indicatorGraphic;
    private double maxArrowFractionOfHeight;

    public BFieldGraphicPanel( Electromagnet magnet ) {
        setPreferredSize( new Dimension( 200, 200 ));
        maxArrowFractionOfHeight = 0.9;
        final BFieldIndicator indicator = new BFieldIndicator( magnet, getPreferredSize().getHeight() * maxArrowFractionOfHeight );
        indicatorGraphic = new RegisterablePNode( indicator );

        magnet.addChangeListener( new Electromagnet.ChangeListener() {
            public void stateChanged( Electromagnet.ChangeEvent event ) {
                updateRegistrationPoint();
            }
        } );
        addWorldChild( indicatorGraphic );

        // When the panel is resized (or first displayed) update the placement of the arror
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateRegistrationPoint();
                indicator.setMaxLength( getHeight() * maxArrowFractionOfHeight );
            }
        } );
    }

    private void updateRegistrationPoint() {
        indicatorGraphic.setOffset( getWidth() / 2, getHeight() / 2);
        indicatorGraphic.setRegistrationPoint( indicatorGraphic.getWidth() / 2, indicatorGraphic.getHeight() / 2 );
    }
}
