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

/**
 * SelectedMoleculeTracker
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SelectedMoleculeTracker implements ModelElement,
                                                PublishingModel.ModelListener,
                                                SimpleMolecule.Listener{
    private MRModel model;
    private SimpleMolecule moleculeTracked;
    private SimpleMolecule closestMolecule;

    public SelectedMoleculeTracker( MRModel model ) {
        this.model = model;
        model.addListener( this );
    }

    public void setMoleculeTracked( SimpleMolecule moleculeTracked ) {
        this.moleculeTracked = moleculeTracked;
    }

    public void stepInTime( double dt ) {
        List modelElements = model.getModelElements();

        // If the molecule being tracked is in the models list of elements, that means
        // it's not in a compound molecule. If that's the case, look for the closest
        // molecule to it of the other type
        if( moleculeTracked != null && modelElements.contains( moleculeTracked )) {

            SimpleMolecule prevClosetMolecule = closestMolecule;

            double closestDistSq = Double.POSITIVE_INFINITY;
            for( int i = 0; i < modelElements.size(); i++ ) {
                Object o = modelElements.get( i );
                if( o instanceof SimpleMolecule && o != moleculeTracked ) {
                    SimpleMolecule testMolecule = (SimpleMolecule)o;
                    double distSq = moleculeTracked.getPosition().distanceSq( testMolecule.getPosition() );
                    if( distSq < closestDistSq ) {
                        closestDistSq = distSq;
                        closestMolecule = testMolecule;
                    }
                }
            }

            if( closestMolecule != null && closestMolecule != prevClosetMolecule ) {
                if( prevClosetMolecule != null ) {
                    prevClosetMolecule.setSelectionStatus( Selectable.NOT_SELECTED);
                }
                closestMolecule.setSelectionStatus( Selectable.NEAREST_TO_SELECTED );
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of MRModel.Listener
    //--------------------------------------------------------------------------------------------------

    public void modelElementAdded( ModelElement element ) {
        if( element instanceof SimpleMolecule ) {
            ((SimpleMolecule)element).addListener( this );
        }
    }

    public void modelElementRemoved( ModelElement element ) {
        if( element instanceof SimpleMolecule ) {
            ((SimpleMolecule)element).removeListener( this );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimpleMolecule.Listener
    //--------------------------------------------------------------------------------------------------

    public void selectionStatusChanged( SimpleMolecule molecule ) {
        if( molecule.getSelectionStatus() == Selectable.SELECTED ) {
            setMoleculeTracked( molecule );
        }
    }
}
