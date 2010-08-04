/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import edu.colorado.phet.acidbasesolutions.constants.ABSResources;
import edu.colorado.phet.acidbasesolutions.model.ConductivityTester;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;


public class ConductivityTesterNode extends PhetPNode {

    private final ConductivityTester tester;
    
    public ConductivityTesterNode( final ConductivityTester tester ) {
        
        this.tester = tester;
        tester.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            public void visibilityChanged() {
                setVisible( tester.isVisible() );
            }
        });
        
        PImage imageNode = new PImage( ABSResources.getBufferedImage( "uncleMalley.png" ) ); //XXX
        addChild( imageNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth()/2, 0 );
        
        setVisible( tester.isVisible() );
        setOffset( tester.getLocationReference() );
    }
}
