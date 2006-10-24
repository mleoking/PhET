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
import edu.colorado.phet.common.util.EventChannel;

import java.util.EventListener;
import java.util.List;

/**
 * SelectedMoleculeTracker
 * <p/>
 * The simulation has a *selected molecule*. This is a molecule chosen by the user.
 * Instances of the SelectedMoleculeTracker find the coleset molecule to the
 * *selected molecule* that could react with it. These two molecules are then
 * represented on the EnergyView.
 * <p>
 * Instances of the class attach themselves as listeners to all instances of SimpleMolecule,
 * and listener for changes in their selection status.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SelectedMoleculeTracker implements ModelElement,
                                                PublishingModel.ModelListener,
                                                SimpleMolecule.ChangeListener {
    private MRModel model;
    private SimpleMolecule moleculeTracked;
    private SimpleMolecule closestMolecule;

    public SelectedMoleculeTracker( MRModel model ) {
        this.model = model;
        model.addListener( this );
    }

    private void setMoleculeTracked( SimpleMolecule moleculeTracked ) {
        SimpleMolecule prevMolecule = this.moleculeTracked;
        if( prevMolecule != null ) {
            prevMolecule.setSelectionStatus( Selectable.NOT_SELECTED );
        }
        this.moleculeTracked = moleculeTracked;
        listenerProxy.moleculeBeingTrackedChanged( moleculeTracked, prevMolecule );
    }

    public SimpleMolecule getMoleculeTracked() {
        return moleculeTracked;
    }

    public SimpleMolecule getClosestMolecule() {
        return closestMolecule;
    }

    public void stepInTime( double dt ) {
        List modelElements = model.getModelElements();

        // Look for the closest molecule to the one being tracked that isn't of the
        // same type
        if( moleculeTracked != null ) {

            Class nearestMoleculeType = null;
            // Determine which type of molecules are eligible to be "closest".
            if( moleculeTracked instanceof MoleculeA ) {
                if( moleculeTracked.isPartOfComposite() ) {
                    nearestMoleculeType = MoleculeC.class;
                }
                else {
                    nearestMoleculeType = MoleculeB.class;
                }
            }
            if( moleculeTracked instanceof MoleculeC ) {
                if( moleculeTracked.isPartOfComposite() ) {
                    nearestMoleculeType = MoleculeA.class;
                }
                else {
                    nearestMoleculeType = MoleculeB.class;
                }
            }
            if( moleculeTracked instanceof MoleculeB ) {
                CompositeMolecule cm = (CompositeMolecule)moleculeTracked.getParentComposite();
                AbstractMolecule[] components = cm.getComponentMolecules();
                if( components[0] == moleculeTracked ) {
                    nearestMoleculeType = components[1].getClass();
                }
                else {
                    nearestMoleculeType = components[0].getClass();
                }
            }

            SimpleMolecule prevClosetMolecule = closestMolecule;
            double closestDistSq = Double.POSITIVE_INFINITY;
            for( int i = 0; i < modelElements.size(); i++ ) {
                Object o = modelElements.get( i );
                if( nearestMoleculeType.isInstance( o ) ) {
                    SimpleMolecule testMolecule = (SimpleMolecule)o;
                    if( moleculeTracked.isPartOfComposite() && !testMolecule.isPartOfComposite()
                        || !moleculeTracked.isPartOfComposite() && testMolecule.isPartOfComposite() ) {
//                    if( moleculeTracked instanceof MoleculeA && testMolecule instanceof MoleculeB
//                        || moleculeTracked instanceof MoleculeC && testMolecule instanceof MoleculeB ) {


                        // Make sure that the non-B molecule in the composite is not the same type as the
                        // non-composite
                        SimpleMolecule sm1 = moleculeTracked.isPartOfComposite() ? (SimpleMolecule)testMolecule : moleculeTracked;
                        CompositeMolecule cm = moleculeTracked.isPartOfComposite() ? (CompositeMolecule)moleculeTracked.getFullMolecule() : (CompositeMolecule)testMolecule.getFullMolecule();
                        SimpleMolecule sm2 = cm.getComponentMolecules()[0] instanceof MoleculeB ? cm.getComponentMolecules()[1] : cm.getComponentMolecules()[0];
                        if( sm1.getClass() == sm2.getClass() ) {
                            break;
                        }

                        double distSq = moleculeTracked.getPosition().distanceSq( testMolecule.getPosition() );
                        if( distSq < closestDistSq ) {
                            closestDistSq = distSq;
                            closestMolecule = testMolecule;
                        }
                    }
                }
            }

            if( closestMolecule != null && closestMolecule != prevClosetMolecule ) {
                if( prevClosetMolecule != null ) {
                    prevClosetMolecule.setSelectionStatus( Selectable.NOT_SELECTED );
                }
                closestMolecule.setSelectionStatus( Selectable.NEAREST_TO_SELECTED );
                listenerProxy.closestMoleculeChanged( closestMolecule, prevClosetMolecule );
            }

            if( !(closestMolecule instanceof MoleculeB || moleculeTracked instanceof MoleculeB )) {
//                System.out.println( "SelectedMoleculeTracker.stepInTime" );
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of MRModel.Listener
    //--------------------------------------------------------------------------------------------------

    public void modelElementAdded( ModelElement element ) {
        if( element instanceof SimpleMolecule ) {
            ( (SimpleMolecule)element ).addListener( this );
        }
    }

    public void modelElementRemoved( ModelElement element ) {
        if( element instanceof SimpleMolecule ) {
            ( (SimpleMolecule)element ).removeListener( this );
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

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public interface Listener extends EventListener {
        void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule, SimpleMolecule prevTrackedMolecule );

        void closestMoleculeChanged( SimpleMolecule newClosestMolecule, SimpleMolecule prevClosestMolecule );
    }

    private EventChannel listenerEventChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)listenerEventChannel.getListenerProxy();

    public void addListener( Listener listener ) {
        listenerEventChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        listenerEventChannel.removeListener( listener );
    }
}
