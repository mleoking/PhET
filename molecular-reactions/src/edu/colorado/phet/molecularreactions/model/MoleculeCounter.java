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
 * MoleculeCounter
 * <p>
 * Monitors an MRModel to keep track of the number of molecules of a specified type
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeCounter implements PublishingModel.ModelListener,
                                        AbstractMolecule.ClassListener {
    private Class moleculeClass;
    // Flag to mark that we are adding or removing molecules from the model,
    // so that we don't respond to add/remove messages from the model
    private boolean selfUpdating;
    private int cnt;
    private MRModel model;

    /**
     *
     * @param moleculeClass
     * @param model
     */
    public MoleculeCounter( Class moleculeClass, MRModel model ) {
        this.moleculeClass = moleculeClass;
        this.model = model;

        // Initialize the counter to number of existing molecules
        List modelElements = model.getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( moleculeClass.isInstance( o )) {
                cnt++;
            }
        }

        model.addListener( this );
        AbstractMolecule.addClassListener( this );
    }

    public int getCnt() {
        return cnt;
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of PublishingModel.Listener
    //--------------------------------------------------------------------------------------------------

    public void modelElementAdded( ModelElement element ) {
        if( !selfUpdating && moleculeClass.isInstance( element ) ) {
            setMoleculeCount();
        }
    }

    public void modelElementRemoved( ModelElement element ) {
        if( moleculeClass.isInstance( element ) ) {
            setMoleculeCount();
        }
    }


    private void setMoleculeCount() {
        List modelElements = model.getModelElements();
        int n = 0;
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( moleculeClass.isInstance( o ) && !( (AbstractMolecule)o ).isPartOfComposite() ) {
                n++;
            }
        }
        cnt = n;
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Molecule.ClassListener
    //--------------------------------------------------------------------------------------------------

    public void statusChanged( AbstractMolecule molecule ) {
        setMoleculeCount();
    }
}

