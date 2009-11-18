package edu.colorado.phet.genenetwork.model;

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