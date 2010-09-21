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

import javax.swing.JPanel;

import edu.colorado.phet.mri.MriResources;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.util.ControlBorderFactory;

/**
 * BFieldGraphicPanel
 * <p/>
 * Displays a PFieldArrowGraphic in a JPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BFieldGraphicPanel extends JPanel {

    public BFieldGraphicPanel( MriModel model ) {
        final BFieldArrowGraphic fieldGraphic = new BFieldArrowGraphic( model, 0 );
        setBorder( ControlBorderFactory.createPrimaryBorder( MriResources.getString( "ControlPanel.FieldArrowTitle" ) ) );
        add( fieldGraphic );
    }
}
