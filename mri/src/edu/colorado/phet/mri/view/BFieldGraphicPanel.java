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

import edu.colorado.phet.mri.model.GradientElectromagnet;
import edu.colorado.phet.mri.model.MriModel;

import javax.swing.*;

/**
 * BFieldGraphicPanel
 * <p>
 * Displays a PFieldArrowGraphic in a JPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BFieldGraphicPanel extends JPanel {

    public BFieldGraphicPanel( MriModel model) {
        final BFieldArrowGraphic fieldGraphic = new BFieldArrowGraphic( (GradientElectromagnet)model.getLowerMagnet(),
                                                                        (GradientElectromagnet)model.getUpperMagnet(),
                                                                        0 );
        setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( ),"Magnetic Field") );
        add( fieldGraphic );
    }
}
