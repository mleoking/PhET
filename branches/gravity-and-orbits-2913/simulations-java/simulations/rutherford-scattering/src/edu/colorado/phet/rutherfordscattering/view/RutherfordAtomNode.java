// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.rutherfordscattering.model.RutherfordAtom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * RutherfordAtomNode is the visual representation of the Rutherford Atom model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RutherfordAtomNode extends PhetPNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MIN_NUCLEUS_RADIUS = 20; // view coordinates
    
    private static final Color NUCLEUS_OUTLINE_COLOR = Color.GRAY;
    private static final Stroke NUCLEUS_OUTLINE_STROKE = 
        new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {2,3}, 0 );

    private static final Color ORBIT_COLOR = Color.GRAY;
    public static final Stroke ORBIT_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,6}, 0 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private RutherfordAtom _atom;
    
    private PImage _nucleusNode;
    private PPath _nucleusOutlineNode;
    private ElectronNode _electronNode;
    
    private Random _randomDistance;
    private Random _randomAngle;
    
    private boolean _outlineModeEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param atom
     */
    public RutherfordAtomNode( RutherfordAtom atom ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _atom = atom;
        _atom.addObserver( this );
        
        _nucleusNode = new PImage();
        addChild( _nucleusNode );
        
        _nucleusOutlineNode = new PPath();
        _nucleusOutlineNode.setStroke( NUCLEUS_OUTLINE_STROKE );
        _nucleusOutlineNode.setStrokePaint( NUCLEUS_OUTLINE_COLOR );
        addChild( _nucleusOutlineNode );
        
        Point2D o = _atom.getElectronOffsetRef();
        double orbitRadius = Math.sqrt( ( o.getX() * o.getX() ) + ( o.getY() * o.getY() ) );
        PNode electronOrbitNode = createOrbitNode( orbitRadius );
        addChild( electronOrbitNode );
        
        _electronNode = new ElectronNode();
        addChild( _electronNode );
        
        _randomDistance = new Random();
        _randomAngle = new Random( 55 );
        
        _outlineModeEnabled = false;
        
        Point2D atomPosition = atom.getPositionRef();
        Point2D nodePosition = RSModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
        
        updateNucleus();
        update( _atom, RutherfordAtom.PROPERTY_ELECTRON_OFFSET );
    }
    
    /*
     * Creates a node for an orbit with a specified radius.
     * The origin is at the center of the node.
     * 
     * @param radius
     */
    private static PPath createOrbitNode( double radius ) {
        Ellipse2D shape = new Ellipse2D.Double( -radius, -radius, 2 * radius, 2 * radius );
        PPath orbitNode = new PPath();
        orbitNode.setPathTo( shape );
        orbitNode.setStroke( ORBIT_STROKE );
        orbitNode.setStrokePaint( ORBIT_COLOR );
        return orbitNode;
    }
    
    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    /**
     * When the node is in outline mode, the nucleus is drawn as an outline.
     * This is useful when dragging sliders, so that the user can see how 
     * the size changes, and the responsiveness remains acceptable.
     * 
     * @param outlineModeEnabled true or false
     */
    public void setOutlineModeEnabled( boolean outlineModeEnabled ) {
        if ( outlineModeEnabled != _outlineModeEnabled ) {
            _outlineModeEnabled = outlineModeEnabled;
            updateNucleus();
        }
    }
    
    public boolean isOutlineModeEnabled() {
        return _outlineModeEnabled;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _atom ) {
            if ( arg == RutherfordAtom.PROPERTY_ELECTRON_OFFSET ) {
                updateElectronOffset();
            }
            else if ( arg == RutherfordAtom.PROPERTY_NUMBER_OF_PROTONS || arg == RutherfordAtom.PROPERTY_NUMBER_OF_NEUTRONS ) {
                updateNucleus();
            }
        }
    }
    
    /*
     * Moves the electron to match the model.
     */
    private void updateElectronOffset() {
        Point2D electronOffset = _atom.getElectronOffsetRef();
        // treat coordinates as distances, since _electronNode is a child node
        double nodeX = RSModelViewTransform.transform( electronOffset.getX() );
        double nodeY = RSModelViewTransform.transform( electronOffset.getY() );
        _electronNode.setOffset( nodeX, nodeY );
    }
    
    /*
     * Builds a new nucleus that matches the model.
     */
    private void updateNucleus() {
        
        // Calculate the radius of the new nucleus
        int numberOfProtons = _atom.getNumberOfProtons();
        int numberOfNeutrons = _atom.getNumberOfNeutrons();
        int currentParticles = numberOfProtons + numberOfNeutrons;
        int minParticles = _atom.getMinNumberOfProtons() + _atom.getMinNumberOfNeutrons();
        double C = MIN_NUCLEUS_RADIUS / Math.pow( minParticles, 1/3d );
        double radius = C * Math.pow( currentParticles, 1/3d );
        assert( radius > 0 );
       
        _nucleusNode.setVisible( !_outlineModeEnabled );
        _nucleusOutlineNode.setVisible( _outlineModeEnabled );
        
        if ( _outlineModeEnabled ) {
            _nucleusOutlineNode.setPathTo( new Ellipse2D.Double( -radius, -radius, 2 * radius, 2 * radius ) );
        }
        else {
            // Randomly place protons and neutrons inside a circle
            double maxProtonRadius = radius - ( new ProtonNode().getDiameter() / 2 );
            double maxNeutronRadius = radius - ( new NeutronNode().getDiameter() / 2 );
            PNode parentNode = new PNode();
            for ( int i = 0; i < Math.max( numberOfProtons, numberOfNeutrons ); i++ ) {

                if ( i < numberOfProtons ) {
                    double d = maxProtonRadius * Math.sqrt( _randomDistance.nextDouble() );
                    double theta = 2 * Math.PI * _randomAngle.nextDouble();
                    final double x = d * Math.cos( theta );
                    final double y = d * Math.sin( theta );
                    ProtonNode protonNode = new ProtonNode();
                    protonNode.setOffset( x, y );
                    parentNode.addChild( protonNode );
                }

                if ( i < numberOfNeutrons ) {
                    double d = maxNeutronRadius * Math.sqrt( _randomDistance.nextDouble() );
                    double theta = 2 * Math.PI * _randomAngle.nextDouble();
                    final double x = d * Math.cos( theta );
                    final double y = d * Math.sin( theta );
                    NeutronNode neutronNode = new NeutronNode();
                    neutronNode.setOffset( x, y );
                    parentNode.addChild( neutronNode );
                }
            }
            // Flatten to an image
            _nucleusNode.setImage( parentNode.toImage() );
            _nucleusNode.setOffset( -_nucleusNode.getWidth() / 2, -_nucleusNode.getHeight() / 2 );
        }
    }
}
