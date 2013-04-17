// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.isotopemixture.model;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This class is the model representation of a numerical controller that
 * allows the user to add or remove isotopes from the test chamber.  It is
 * admittedly a little odd to have a class like this that is really more
 * of a view sort of thing, but it was needed in order to be consistent
 * with the buckets, which are the other UI device that the user has for
 * moving isotopes into and out of the test chamber.  The buckets must
 * have a presence in the model so that the isotopes that are outside of
 * the chamber have somewhere to go, so this class allows buckets and
 * other controls to be handled consistently between the model and view.
 */
public class NumericalIsotopeQuantityControl {
    private static final int CAPACITY = 100;
    private final Point2D centerPosition = new Point2D.Double();
    private final ImmutableAtom isotopeConfig;
    private final MixIsotopesModel model;
    private final Property<Integer> quantityProperty = new Property<Integer>( 0 );

    // This property tracks whether this model element is still a part
    // of the active model, such that it should be displayed in the view.
    private final BooleanProperty partOfModelProperty = new BooleanProperty( true );

    /**
     * Constructor.
     */
    public NumericalIsotopeQuantityControl( MixIsotopesModel model, ImmutableAtom atomConfig, Point2D position ) {
        this.model = model;
        this.isotopeConfig = atomConfig;
        this.centerPosition.setLocation( position );
    }

    public Point2D getCenterPositionRef() {
        return centerPosition;
    }

    public ImmutableAtom getIsotopeConfig() {
        return isotopeConfig;
    }

    public int getCapacity() {
        return CAPACITY;
    }

    public BooleanProperty getPartOfModelProperty() {
        return partOfModelProperty;
    }

    /**
     * Notify this model element that it has been removed from the model.
     * This will result in notifications being sent that should cause view
     * elements to be removed from the view.
     */
    protected void removedFromModel() {
        partOfModelProperty.set( false );
    }

    /**
     * Set the quantity of the isotope associated with this control to the
     * specified value.
     *
     * @param targetQuantity
     * @return
     */
    public void setIsotopeQuantity( int targetQuantity ) {
        assert targetQuantity <= CAPACITY;
        int changeAmount = targetQuantity - model.getIsotopeTestChamber().getIsotopeCount( isotopeConfig );
        if ( changeAmount > 0 ) {
            for ( int i = 0; i < changeAmount; i++ ) {
                MovableAtom newIsotope = new MovableAtom( isotopeConfig.getNumProtons(),
                                                          isotopeConfig.getNumNeutrons(), MixIsotopesModel.SMALL_ISOTOPE_RADIUS,
                                                          model.getIsotopeTestChamber().generateRandomLocation(), model.getClock() );
                model.getIsotopeTestChamber().addIsotopeToChamber( newIsotope );
                model.notifyIsotopeInstanceAdded( newIsotope );
            }
        }
        else if ( changeAmount < 0 ) {
            for ( int i = 0; i < -changeAmount; i++ ) {
                MovableAtom isotope = model.getIsotopeTestChamber().removeIsotopeMatchingConfig( isotopeConfig );
                if ( isotope != null ) {
                    isotope.removedFromModel();
                }
            }
        }
        quantityProperty.set( targetQuantity );
    }

    /**
     * Force the quantity property to sync up with the test chamber.
     */
    protected void syncToTestChamber() {
        quantityProperty.set( model.getIsotopeTestChamber().getIsotopeCount( isotopeConfig ) );
    }

    /**
     * @return
     */
    public Color getBaseColor() {
        return model.getColorForIsotope( isotopeConfig );
    }

    public void addQuantityPropertyObserver( SimpleObserver so ) {
        quantityProperty.addObserver( so );
    }

    public void removeQuantityPropertyObserver( SimpleObserver so ) {
        quantityProperty.removeObserver( so );
    }

    public int getQuantity() {
        // Verify that the internal quantity property matches that of the
        // test chamber.
        assert quantityProperty.get() == model.getIsotopeTestChamber().getIsotopeCount( getIsotopeConfig() );
        // Return the value.
        return quantityProperty.get();
    }
}
