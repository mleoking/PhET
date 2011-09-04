// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.jme.JMEPropertyCheckBox;

/**
 * Includes common MS settings
 */
public class MoleculeShapesPropertyCheckBox extends JMEPropertyCheckBox {
    public MoleculeShapesPropertyCheckBox( String text, final SettableProperty<Boolean> property ) {
        super( text, property );

        setFont( MoleculeShapesConstants.CHECKBOX_FONT_SIZE );
        setForeground( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
        setOpaque( false );
    }
}
