// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

/**
 * Interface that allows the user to control and to obtain information about a
 * gene network model.  This interface is designed to accomodate the specific
 * needs of this simulation, and not a generic gene network model.
 *
 * @author John Blanco
 */
public interface IGeneNetworkModelControl {

    ArrayList<LacI> getLacIList();

    ArrayList<LacZ> getLacZList();

    ArrayList<LacY> getLacYList();

    ArrayList<Glucose> getGlucoseList();

    ArrayList<Galactose> getGalactoseList();

    ArrayList<RnaPolymerase> getRnaPolymeraseList();

    Cap getCap();

    CapBindingRegion getCapBindingRegion();

    LacOperator getLacOperator();

    LacIGene getLacIGene();

    LacZGene getLacZGene();

    LacYGene getLacYGene();

    LacIPromoter getLacIPromoter();

    LacPromoter getLacPromoter();

    DnaStrand getDnaStrand();

    Rectangle2D getCellMembraneRect();

    /**
     * Get a count of the number of lactose molecules that are inside the
     * cell.  Note that if there is no cell membrane present, all lactose
     * molecules are considered to be inside the cell.
     *
     * @return
     */
    int getLactoseLevel();

    /**
     * Create a lacZ gene and add it to the model.
     *
     * @return A reference to the newly created model element, null if some
     *         problem prevented the creation.
     */
    LacZGene createAndAddLacZGene( Point2D initialPosition );

    /**
     * Create a lacY gene and add it to the model.
     *
     * @return A reference to the newly created model element, null if some
     *         problem prevented the creation.
     */
    LacYGene createAndAddLacYGene( Point2D initialPosition );

    /**
     * Create a lacI gene and add it to the model.
     *
     * @return A reference to the newly created model element, null if some
     *         problem prevented the creation.
     */
    LacIGene createAndAddLacIGene( Point2D initialPosition );

    /**
     * Create a lac operator (a.k.a. lacI binding region) and add it to the
     * model.
     *
     * @return A reference to the newly created model element, null if some
     *         problem prevented the creation.
     */
    LacOperator createAndAddLacOperator( Point2D initialPosition );

    /**
     * Create a lac promoter (a.k.a. polymerase binding region) and add it to
     * the model.
     *
     * @return A reference to the newly created model element, null if some
     *         problem prevented the creation.
     */
    LacPromoter createAndAddLacPromoter( Point2D initialPosition );

    /**
     * Create a lac I promoter (a.k.a. polymerase binding region) and add it to
     * the model.
     *
     * @return A reference to the newly created model element, null if some
     *         problem prevented the creation.
     */
    LacIPromoter createAndAddLacIPromoter( Point2D position );

    /**
     * Create an RNA Polymerase and add it to the model.
     *
     * @return A reference to the newly created model element, null if some
     *         problem prevented the creation.
     */
    RnaPolymerase createAndAddRnaPolymerase( Point2D initialPosition );

    /**
     * Create a molecule of lactose and inject it into the model.
     */
    void createAndAddLactose( Point2D initialPosition, MutableVector2D initialVelocity );

    /**
     * Get a list of all simple model elements in the model.
     *
     * @return
     */
    ArrayList<SimpleModelElement> getAllSimpleModelElements();

    /**
     * Returns true if LacI (a.k.a. Lac Inhibitor) is currently attached to
     * the DNA strand.  This is important because if it is, RNA polymerase is
     * essentially blocked from traversing the DNA strand.
     *
     * @return
     */
    boolean isLacIAttachedToDna();

    /**
     * Returns true if the lac operator (a.k.a. lacI binding region) is
     * present in the DNA strand.
     */
    boolean isLacOperatorPresent();

    /**
     * Returns true if the lac Z gene is present in the DNA strand.
     */
    boolean isLacZGenePresent();

    /**
     * Add a new messenger RNA to the model.
     *
     * @param mRna
     */
    void addMessengerRna( MessengerRna mRna );

    /**
     * Add a new transformation arrow to the model.
     *
     * @param transformationArrow
     */
    void addTransformationArrow( TransformationArrow transformationArrow );

    /**
     * Add a new LacZ molecule (or protein or whatever it is) to the model.
     *
     * @param lacZ
     */
    void addLacZ( LacZ lacZ );

    void addLacI( LacI lacIToAddToModel );

    void addLacY( LacY lacY );

    /**
     * Add a listener.
     */
    void addListener( IGeneNetworkModelListener listener );

    /**
     * Remove a listener.
     */
    void removeListener( IGeneNetworkModelListener listener );

    /**
     * Set the location in model space of the tool box that may be present in
     * the view.  This is used to allow model components to determine whether
     * or not they are being moved over the tool box.
     *
     * @param rect
     */
    void setToolBoxRect( Rectangle2D rect );

    /**
     * Get a boolean value indicating whether a given point in model space is
     * in the tool box.
     */
    boolean isPointInToolBox( Point2D pt );

    /**
     * Search through all of the lactose molecules (which are a combination of
     * glucose and galactose) and return the nearest one.  If specified, only
     * free ones are returned (meaning ones that are not already attached to or
     * moving towards some other model element).
     *
     * @param pt              - Location of the requester.
     * @param positionWrtCell - Position with respect to the cell, meaning
     *                        whether it should be outside or inside.
     * @param freeOnly        - Only return free lactose molecules, i.e. ones that
     *                        are not attached to something else or heading towards such an
     *                        attachment.
     * @return A reference to a glucose molecule that is bound to a galactose
     *         and therefore part of lactose.  Returns null if no available lactose
     *         can be found.
     */
    Glucose getNearestLactose( Point2D pt, PositionWrtCell positionWrtCell, boolean freeOnly );

    /**
     * Search through all of the Lac I molecules and return the closest one
     * that is not bound to LacZ.
     */
    LacI getNearestFreeLacI( Point2D pt );

    /**
     * Get an open location on the cell membrane for LacY.  This is done so
     * that all the LacYs don't pile up in the same spot.
     *
     * @return
     */
    Point2D getOpenSpotForLacY();

    /**
     * Search through all of the RNA Polymerase molecules and return the
     * closest one that is not bound to LacZ.
     */
    RnaPolymerase getNearestFreeRnaPolymerase( Point2D pt );

    /**
     * Returns true if it is okay to inject lactose, false if not.
     */
    public boolean isLactoseInjectionAllowed();

    /**
     * Set the state variable that controls whether or not the legend should
     * be visible
     */
    public void setLegendVisible( boolean legendVisible );

    /**
     * Returns true if the legend should be visible, false if not.
     */
    public boolean isLegendVisible();

    /**
     * Set the state variable that controls whether or not the lactose meter
     * should be visible
     */
    public void setLactoseMeterVisible( boolean lactoseMeterVisible );

    /**
     * Returns true if the lactose meter should be visible, false if not.
     */
    public boolean isLactoseMeterVisible();

    /**
     * Enable or disable the automatic injection of lactose into the model.
     *
     * @param automaticLactoseInjectionEnabled
     *
     */
    public void setAutomaticLactoseInjectionEnabled( boolean automaticLactoseInjectionEnabled );

    /**
     * Get the state of automatic lactose injection.
     *
     * @return
     */
    public boolean isAutomaticLactoseInjectionEnabled();

    /**
     * Set location and initial velocity for automatic periodic injection of lactose.
     *
     * @param automaticLactoseInjectionEnabled
     *
     */
    public void setAutomaticLactoseInjectionParams( Point2D location, MutableVector2D velocity );

    /**
     * Get the bounds for motion inside the cell.
     *
     * @return
     */
    Rectangle2D getInteriorMotionBounds();

    /**
     * Get the area of the model where it is inside the cell and it is okay to
     * move around and not end up overlapping the DNA.  This actually excludes
     * an area that is quite a bit larger than the DNA so that model elements
     * within it stay far enough above that they don't end up overlapping it
     * at all.
     *
     * @return
     */
    Rectangle2D getInteriorMotionBoundsAboveDna();

    /**
     * Get the area of the model that is outside of the cell where things can
     * be moving around (and not float off into space and thus become
     * invisible to the users).
     *
     * @return bounds rect, or null if there is no cell exterior for the model.
     */
    Rectangle2D getExteriorMotionBounds();

    /**
     * Classify the provided position with respect to the cell membrane, i.e.
     * whether it is in, out, or on the membrane.
     *
     * @param pt
     * @return
     */
    PositionWrtCell classifyPosWrtCell( Point2D pt );
}