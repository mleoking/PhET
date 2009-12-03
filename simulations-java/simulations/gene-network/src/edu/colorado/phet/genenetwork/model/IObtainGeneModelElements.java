package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Interface that allows the user to obtain the model elements that are
 * currently in existence within the model.
 * 
 * @author John Blanco
 */
public interface IObtainGeneModelElements {

	ArrayList<LacI> getLacIList();

	ArrayList<LacZ> getLacZList();

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
	
	DnaStrand getDnaStrand();
	
	/**
	 * Create a lacZ gene and add it to the model.
	 * 
	 * @return A reference to the newly created model element, null if some
	 * problem prevented the creation.
	 */
	LacZGene createAndAddLacZGene(Point2D initialPosition);

	/**
	 * Create a lacI gene and add it to the model.
	 * 
	 * @return A reference to the newly created model element, null if some
	 * problem prevented the creation.
	 */
	LacIGene createAndAddLacIGene(Point2D initialPosition);

	/**
	 * Create a lac operator (a.k.a. lacI binding region) and add it to the
	 * model.
	 * 
	 * @return A reference to the newly created model element, null if some
	 * problem prevented the creation.
	 */
	LacOperator createAndAddLacOperator(Point2D initialPosition);

	/**
	 * Create a lac promoter (a.k.a. polymerase binding region) and add it to
	 * the model.
	 * 
	 * @return A reference to the newly created model element, null if some
	 * problem prevented the creation.
	 */
	LacPromoter createAndAddLacPromoter(Point2D initialPosition);

	/**
	 * Create an RNA Polymerase and add it to the model.
	 * 
	 * @return A reference to the newly created model element, null if some
	 * problem prevented the creation.
	 */
	RnaPolymerase createAndAddRnaPolymerase(Point2D initialPosition);

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
	 * @param mRna
	 */
    void addMessengerRna(MessengerRna mRna);
    
    /**
     * Add a new transformation arrow to the model.
     * @param transformationArrow
     */
    void addTransformationArrow(TransformationArrow transformationArrow);
    
    /**
     * Add a new LacZ molecule (or protein or whatever it is) to the model.
     * @param lacZ
     */
    void addLacZ(LacZ lacZ);

	void addLacI(LacI lacIToAddToModel);
}