package edu.colorado.phet.neuron.view;

import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.neuron.model.Atom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Class that represents atoms in the view.
 */
public class AtomNode extends PNode {
	
	private Atom atom;
    private ModelViewTransform2D modelViewTransform;
    private PNode representation;
    private PText label;

    public AtomNode( Atom atom, ModelViewTransform2D modelViewTransform ) {
    	
		this.atom = atom;
        this.modelViewTransform = modelViewTransform;

        atom.addListener(new Atom.Listener() {
			public void positionChanged() {
				updateOffset();
			}
		});

        // Create the shape that represents this atom.
        representation = createRepresentation();
		addChild( representation );
        updateOffset();
        
        // Create the label.
        String labelText = MessageFormat.format("{0}{1}", atom.getChemicalSymbol(), atom.getChargeString());
        label = new PText(labelText);
        label.setFont(new PhetFont(12, true));
        label.setTextPaint(atom.getLabelColor());
        addChild(label);
        
        // Scale the label to fit within the sphere.
        double maxLabelDimension = Math.max(label.getFullBoundsReference().width, label.getFullBoundsReference().height);
        PNode tempRepresentation = createRepresentation();
        tempRepresentation.rotate(Math.PI / 4);
        double minShapeDimension = Math.min(representation.getFullBoundsReference().width,
        		tempRepresentation.getFullBoundsReference().height);
        double scale = minShapeDimension / maxLabelDimension;
        label.setScale(scale);
        
        // Center the label both vertically and horizontally.  This assumes
        // that the sphere is centered at 0,0.
        label.setOffset(-label.getFullBoundsReference().width / 2, -label.getFullBoundsReference().height / 2);
	}

    private void updateOffset() {
        setOffset( modelViewTransform.modelToView( atom.getPosition() ));
    }
    
    /**
     * Create the shape that will be used to represent this particular atom.
     * This was created when we realized that many textbooks use different
     * shapes for different atoms, rather than always a sphere.
     * 
     * @return
     */
    private PNode createRepresentation() {
    	PNode representation;

    	switch (atom.getType()){
    	case SODIUM:
    		representation = new SphericalNode( modelViewTransform.modelToViewDifferentialXDouble(atom.getDiameter()), 
    				atom.getRepresentationColor(), true);
    		break;
    	case POTASSIUM:
    		double size = modelViewTransform.modelToViewDifferentialXDouble(atom.getDiameter());
    		size = size * 0.85; // Scale down a bit so it is close to fitting within the diameter.
    		representation = new PPath( new Rectangle2D.Double(-size/2, -size/2, size, size));
    		representation.setPaint(atom.getRepresentationColor());
    		representation.rotate(Math.PI / 4);
    		break;
    	default:
    		System.err.println(getClass().getName() + " - Warning: No specific shape for this atom type, defaulting to sphere.");
    		representation = new SphericalNode( modelViewTransform.modelToViewDifferentialXDouble(atom.getDiameter()), 
    				atom.getRepresentationColor(), true);
    		break;
    	}
    	
    	return representation;
    }
}
