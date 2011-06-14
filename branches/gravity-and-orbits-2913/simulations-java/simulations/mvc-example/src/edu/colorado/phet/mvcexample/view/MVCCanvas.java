// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.view;

import java.awt.Color;
import java.awt.Dimension;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.mvcexample.model.AModelElement;
import edu.colorado.phet.mvcexample.model.BModelElement;
import edu.colorado.phet.mvcexample.model.CModelElement;
import edu.colorado.phet.mvcexample.model.MVCModel;
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
    private CNode _cNode;
    
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
        
        // A
        AModelElement aModelElement = model.getAModelElement();
        _aNode = new ANode( aModelElement );
        _rootNode.addChild( _aNode );
        
        // B
        BModelElement bModelElement = model.getBModelElement();
        _bNode = new BNode( bModelElement.getSize(), bModelElement.getColor() );
        _rootNode.addChild( _bNode );
        
        // C
        CModelElement cModelElement = model.getCModelElement();
        _cNode = new CNode( cModelElement );
        _rootNode.addChild( _cNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public BNode getBNode() {
        return _bNode;
    }
}
