/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.model.ModelElement;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * ProvisionalBondDetector
 * <p>
 * Detects when provisional bonds should be created.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ProvisionalBondDetector extends PublishingModel.ModelListenerAdapter implements ModelElement {
    private MRModel model;
    private Map moleculeToBond = new HashMap();

    public ProvisionalBondDetector( MRModel model ) {
        this.model = model;
        model.addListener( this );
    }

    public void stepInTime( double dt ) {

        // Look for SimpleMolecules that qualify for tentative bonds. If there is not a tentative bond
        // for a pair, then create one.
        List modelElements = model.getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( o instanceof SimpleMolecule ) {
                SimpleMolecule sm1 = (SimpleMolecule)o;

                for( int j = 0; j < modelElements.size(); j++ ) {
                    Object o1 = modelElements.get( j );

                    // If we have two SimpleMolecules, see if they're close enough to qualify for a
                    // provisional bond
                    if( o1 instanceof SimpleMolecule && o != o1 ) {
                        SimpleMolecule sm2 = (SimpleMolecule)o1;
                        double provisionalBondMaxLength = model.getEnergyProfile().getThresholdWidth() / 2;
                        double moleculeSeparation = sm1.getPosition().distance( sm2.getPosition() ) - sm1.getRadius() - sm2.getRadius();
                        if( moleculeSeparation <= provisionalBondMaxLength ) {

                            // If no provisional bond exists for either of these two simple molecules, create one
                            if( moleculeToBond.get( sm1 ) == null && moleculeToBond.get( sm2 ) == null ) {
                                ProvisionalBond bond = new ProvisionalBond( sm1, sm2, provisionalBondMaxLength, model );
                                model.addModelElement( bond );
                                moleculeToBond.put( sm1, bond );
                                moleculeToBond.put( sm2, bond );
                            }
                        }
                    }
                }
            }
        }
    }

    public void modelElementRemoved( ModelElement element ) {
        if( element instanceof ProvisionalBond ) {
            ProvisionalBond bond = (ProvisionalBond)element;
            moleculeToBond.remove( bond.getMolecules()[0] );
            moleculeToBond.remove( bond.getMolecules()[1] );
        }
    }
}
