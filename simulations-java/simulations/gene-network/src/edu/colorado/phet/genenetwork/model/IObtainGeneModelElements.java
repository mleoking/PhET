package edu.colorado.phet.genenetwork.model;

import java.util.ArrayList;

/**
 * Interface that allows the user to obtain the model elements that are
 * currently in existence within the model.
 * 
 * @author John Blanco
 */
public interface IObtainGeneModelElements {

	public abstract ArrayList<LacI> getLacIList();

	public abstract ArrayList<LacZ> getLacZList();

	public abstract ArrayList<Glucose> getGlucoseList();

	public abstract ArrayList<Galactose> getGalactoseList();

	public abstract ArrayList<RnaPolymerase> getRnaPolymeraseList();

	public abstract Cap getCap();

	public abstract CapBindingRegion getCapBindingRegion();

	public abstract LacOperator getLacOperator();

	public abstract LacIGene getLacIGene();

	public abstract LacZGene getLacZGene();

	public abstract LacYGene getLacYGene();

	public abstract LacIPromoter getLacIPromoter();

	/**
	 * Get a list of all simple model elements in the model.
	 * 
	 * @return
	 */
	public abstract ArrayList<SimpleModelElement> getAllSimpleModelElements();
	
    /**
     * Returns true if LacI (a.k.a. Lac Inhibitor) is currently attached to
     * the DNA strand.  This is important because if it is, RNA polymerase is
     * essentially blocked from traversing the DNA strand.
     * 
     * @return
     */
	public abstract boolean isLacIAttachedToDna();

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
}