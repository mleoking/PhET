/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * DeBroglieNode is the visual representation of the deBroglie model of the hydrogen atom.
 * The deBroglie model has 4 different visual representations, implemented as 
 * subclasses of AbstractDeBroglieViewStrategy.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieNode extends AbstractHydrogenAtomNode implements Observer {
        
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DeBroglieModel _atom; // model element
    private AbstractDeBroglieViewStrategy _viewStrategy;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param atom
     */
    public DeBroglieNode( DeBroglieModel atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        _viewStrategy = createViewStrategy( _atom );
        addChild( _viewStrategy );
        
        Point2D atomPosition = atom.getPosition();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _atom ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                _viewStrategy.update();
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_OFFSET ) {
                _viewStrategy.update();
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ATOM_IONIZED ) {
                //XXX
            }
            else if ( arg == DeBroglieModel.PROPERTY_VIEW ) {
                removeChild( _viewStrategy );
                _viewStrategy = createViewStrategy( _atom );
                addChild( _viewStrategy );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // View strategies
    //----------------------------------------------------------------------------
    
    /**
     * AbstractDeBroglieViewStrategy is the base class for all view strategies.
     * A view strategy is a PNode that renders the deBroglie model using a 
     * specific visual representation.
     */
    public static abstract class AbstractDeBroglieViewStrategy extends PNode {

        private DeBroglieModel _atom;

        public AbstractDeBroglieViewStrategy( DeBroglieModel atom ) {
            super();
            setPickable( false );
            setChildrenPickable( false );
            _atom = atom;
        }
        
        protected DeBroglieModel getAtom() {
            return _atom;
        }

        /**
         * Updates the visual representation of the atom.
         */
        public abstract void update();
    }
    
    /**
     * AbstractDeBroglie2DViewStrategy is the base class for all 2D 
     * view strategies.  These representations all have a static proton at the 
     * center of the atom, surrounded by static lines that show the orbits.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    public static abstract class AbstractDeBroglie2DViewStrategy extends AbstractDeBroglieViewStrategy {

        public AbstractDeBroglie2DViewStrategy( DeBroglieModel atom ) {
            super( atom );
            initStaticNodes();
        }
        
        /*
         * Creates nodes for the proton and orbits.
         */
        private void initStaticNodes() {

            // Orbits
            int groundState = getAtom().getGroundState();
            int numberOfStates = getAtom().getNumberOfStates();
            for ( int state = groundState; state < ( groundState + numberOfStates ); state++ ) {
                double radius = getAtom().getOrbitRadius( state );
                PNode orbitNode = OrbitNodeFactory.createOrbitNode( radius );
                addChild( orbitNode );
            }
            
            // Proton
            ProtonNode protonNode = new ProtonNode();
            protonNode.setOffset( 0, 0 );
            addChild( protonNode );
        }
    }
    
    /**
     * Factory method for creating a view strategy for the atom.
     * @param atom
     * @return IDeBroglieViewStrategy
     */
    private static AbstractDeBroglieViewStrategy createViewStrategy( DeBroglieModel atom ) {

        AbstractDeBroglieViewStrategy strategy = null;
        
        DeBroglieView view = atom.getView();
        if ( view == DeBroglieView.BRIGHTNESS_MAGNITUDE ) {
            strategy = new DeBroglieBrightnessMagnitudeNode( atom );
        }
        else if ( view == DeBroglieView.BRIGHTNESS ) {
            strategy = new DeBroglieBrightnessNode( atom );
        }
        else if ( view == DeBroglieView.RADIAL_DISTANCE ) {
            strategy = new DeBroglieRadialDistanceNode( atom );
        }
        else if ( view == DeBroglieView.HEIGHT_3D ) {
            strategy = new DeBroglieHeight3DNode( atom );
        }
        else {
            throw new UnsupportedOperationException( "unsupported DeBroglieView: " + view );
        }
        
        return strategy;
    }
}
