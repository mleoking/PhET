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
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionSpec;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;

import java.util.ArrayList;
import java.util.List;

/**
 * ProvisionalBondDetector
 * <p/>
 * Detects when provisional bonds should be created. It does not destroy provional bonds.
 * That is done by the bonds themselves
 * <p/>
 * This is a ModelElement. At each time step, it looks at all the molecules in the model
 * to see if any qualify for provisional bonds.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ProvisionalBondDetector implements ModelElement, PublishingModel.ModelListener {
    private MRModel model;
    private List bondedMolecules = new ArrayList();

    public ProvisionalBondDetector( MRModel model ) {
        this.model = model;
        model.addListener( this );

        // Scan the model for existing Bonds. SimpleMolecules participating in hard bonds are
        // not eligible to participate in provisional bonds
        List modelElements = model.getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( o instanceof Bond ) {
                Bond bond = (Bond)o;
                bondedMolecules.add( bond.getParticipants()[0] );
                bondedMolecules.add( bond.getParticipants()[1] );
            }
        }
    }

    /**
     * Scans the model for provisional bonds that should be created.
     *
     * @param dt
     */
    public void stepInTime( double dt ) {

        // Determine how far apart two molecules can be and have a provisional bond
        double provisionalBondMaxLength = model.getEnergyProfile().getThresholdWidth() / 2;

        // Look for SimpleMolecules that qualify for tentative bonds. If there is not a tentative bond
        // for a pair, then create one.
        List modelElements = model.getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( o instanceof SimpleMolecule ) {
                SimpleMolecule sm1 = (SimpleMolecule)o;

                for( int j = i + 1; j < modelElements.size(); j++ ) {
//                for( int j = 0; j < modelElements.size(); j++ ) {
                    Object o1 = modelElements.get( j );

                    // If the second model element is a composite molecule, see if the simple molecule
                    // is close enough to qualify for a provisional bond
                    if( o1 instanceof CompositeMolecule && sm1.getParentComposite() != o1 ) {
                        CompositeMolecule cm = (CompositeMolecule)o1;
                        Reaction reaction = model.getReaction();

                        // Check that the molecules are of types that can react
                        if( reaction.moleculesAreProperTypes( sm1, cm ) ) {
                            SimpleMolecule sm2 = reaction.getMoleculeToKeep( cm, sm1 );

                            double moleculeSeparation = sm1.getPosition().distance( sm2.getPosition() ) - sm1.getRadius() - sm2.getRadius();
                            if( moleculeSeparation <= provisionalBondMaxLength ) {

                                // If no provisional bond exists for either of these two simple molecules, create one
                                if( !bondedMolecules.contains( sm1 ) || !bondedMolecules.contains( sm2 ) ) {
                                    ProvisionalBond bond = new ProvisionalBond( sm1, sm2, provisionalBondMaxLength, model );
                                    model.addModelElement( bond );
                                    bondedMolecules.add( sm1 );
                                    bondedMolecules.add( sm2 );
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * If a ProvisionalBond is removed from the model, make its participants eligible for provisional bonding
     *
     * @param element
     */
    public void modelElementRemoved( ModelElement element ) {
        if( element instanceof ProvisionalBond ) {
            ProvisionalBond bond = (ProvisionalBond)element;
            bondedMolecules.remove( bond.getMolecules()[0] );
            bondedMolecules.remove( bond.getMolecules()[1] );
        }
    }

    /**
     * If a Bond is added to the model, its participants should become eligible for provisional bonding
     *
     * @param element
     */
    public void modelElementAdded( ModelElement element ) {
        if( element instanceof Bond ) {
            Bond bond = (Bond)element;
            bondedMolecules.remove( bond.getParticipants()[0] );
            bondedMolecules.remove( bond.getParticipants()[1] );
        }
    }
}
