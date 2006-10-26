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
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;

/**
 * SolarSystemNode is the visual representation of the "solar system" model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SolarSystemNode extends AbstractHydrogenAtomNode implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SolarSystemModel _atom;
    private ProtonNode _protonNode;
    private ElectronNode _electronNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param atom
     */
    public SolarSystemNode( SolarSystemModel atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        _protonNode = new ProtonNode();
        _electronNode = new ElectronNode();
       
        addChild( _protonNode );
        addChild( _electronNode );
          
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }

        update( _atom, SolarSystemModel.PROPERTY_POSITION );
        update( _atom, SolarSystemModel.PROPERTY_ELECTRON_POSITION );
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
            if ( arg == SolarSystemModel.PROPERTY_POSITION ) {
                // the entire atom has moved
                Point2D atomPosition = _atom.getPosition();
                Point2D nodePosition = ModelViewTransform.transform( atomPosition );
                System.out.println( "atomPosition=" + atomPosition + " nodePosition=" + nodePosition );//XXX
                setOffset( nodePosition );
            }
            else if ( arg == SolarSystemModel.PROPERTY_ELECTRON_POSITION ) {
                // the electron has moved
                Point2D relativeElectronPosition = _atom.getRelativeElectronPosition();
                // treat coordinates as distances, since _electronNode is a child node
                double nodeX = ModelViewTransform.transform( relativeElectronPosition.getX() );
                double nodeY = ModelViewTransform.transform( relativeElectronPosition.getY() );
                _electronNode.setOffset( nodeX, nodeY );
            }
        }
    }
}
