// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.menu.TeacherMenu;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;

/**
 * "Teacher" menu that allows the user to select between normal colors and a "White Background" mode
 */
public class MoleculeShapesTeacherMenu extends TeacherMenu {
    public MoleculeShapesTeacherMenu( Property<Boolean> whiteBackground ) {
        addWhiteBackgroundMenuItem( whiteBackground );
        whiteBackground.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean useWhiteBackground ) {
                if ( useWhiteBackground ) {
                    MoleculeShapesColor.PROJECTOR.apply( MoleculeShapesColor.handler );
                }
                else {
                    MoleculeShapesColor.DEFAULT.apply( MoleculeShapesColor.handler );
                }
            }
        } );
    }
}
