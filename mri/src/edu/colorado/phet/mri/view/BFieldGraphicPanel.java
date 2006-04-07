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

/**
 * BFieldGraphicPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BFieldGraphicPanel extends PhetPCanvas {
    private RegisterablePNode indicatorGraphic;

    public BFieldGraphicPanel( Electromagnet magnet ) {
        setPreferredSize( new Dimension( 200, 300 ));
        BFieldIndicator indicator = new BFieldIndicator( magnet );
        indicatorGraphic = new RegisterablePNode( indicator );

        magnet.addChangeListener( new Electromagnet.ChangeListener() {
            public void stateChanged( Electromagnet.ChangeEvent event ) {
                updateRegistrationPoint();
            }
        } );
        addWorldChild( indicatorGraphic );
        updateRegistrationPoint();
    }

    private void updateRegistrationPoint() {
        indicatorGraphic.setOffset( getWidth() / 2, getHeight() / 2);
        indicatorGraphic.setRegistrationPoint( indicatorGraphic.getWidth() / 2, indicatorGraphic.getHeight() / 2 );
    }
}
