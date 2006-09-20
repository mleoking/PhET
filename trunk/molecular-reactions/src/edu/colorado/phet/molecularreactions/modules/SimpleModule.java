/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_AB_BC_C_Reaction;

import java.awt.*;

/**
 * MRModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleModule extends MRModule {

    public SimpleModule() {
        super( "Simple");
        testL( (MRModel)getModel() );
    }

    /**
     * Multiple molecules
     *
     * @param model
     */
    void testL( MRModel model ) {
        {
            model.setReaction( new A_AB_BC_C_Reaction( model ) );
            {
                SimpleMolecule m1 = new MoleculeB();
                double yLoc = model.getBox().getMinY() + model.getBox().getHeight() / 2;
                m1.setPosition( 180, yLoc );
                m1.setVelocity( 0, 0 );
                model.addModelElement( m1 );
                SimpleMolecule m1a = new MoleculeA();
                m1a.setPosition( m1.getPosition().getX() + m1.getRadius() + m1a.getRadius(), yLoc );
                m1a.setVelocity( 0, 0 );
                model.addModelElement( m1a );

                CompositeMolecule cm = new MoleculeAB( new SimpleMolecule[]{m1, m1a},
                                                       new Bond[]{new Bond( m1, m1a )} );
                cm.setOmega( 0 );
                cm.setVelocity( 0, 0 );
                model.addModelElement( cm );

                SimpleMolecule m2 = new MoleculeC();
                m2.setPosition( m1.getPosition().getX() - 130, m1.getPosition().getY() );
                m2.setVelocity( 4, 0 );
                model.addModelElement( m2 );

                m2.setSelectionStatus( Selectable.SELECTED );
            }
        }
    }
}
