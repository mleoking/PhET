/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.mvcexample.model.AModelElement;
import edu.colorado.phet.mvcexample.model.BModelElement;
import edu.colorado.phet.mvcexample.model.MVCModel;
import edu.colorado.phet.mvcexample.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * ExampleCanvas is the canvas for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MVCCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private MVCModel _model;
    
    // View 
    private PNode _rootNode;
    private ANode _aNode;
    private BNode _bNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MVCCanvas( Dimension renderingSize, MVCModel model ) {
        super( renderingSize );
        
        _model = model;
        
        setBackground( Color.WHITE );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
        
        ModelViewTransform modelViewTransform = _model.getModelViewTransform();
        
        // A
        AModelElement aModelElement = model.getAModelElement();
        _aNode = new ANode( aModelElement, modelViewTransform );
        _rootNode.addChild( _aNode );
        
        // B
        BModelElement bModelElement = model.getBModelElement();
        _bNode = new BNode( bModelElement.getWidth(), bModelElement.getHeight(), modelViewTransform );
        _rootNode.addChild( _bNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public BNode getBNode() {
        return _bNode;
    }
}
