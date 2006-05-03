/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.dialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.control.SliderControl;
import edu.colorado.phet.boundstates.model.BSCoulomb3DWell;
import edu.colorado.phet.boundstates.model.BSDoubleRange;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSCoulomb3DDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb3DDialog extends BSCoulomb1DDialog {

    public BSCoulomb3DDialog( Frame parent, BSCoulomb3DWell potential, BSDoubleRange offsetRange ) {
        super( parent, potential, offsetRange, new BSDoubleRange(0,0,0,0) /* spacingRange */ );
        setTitle( SimStrings.get( "BSCoulomb3DDialog.title" ) );
    }
}
