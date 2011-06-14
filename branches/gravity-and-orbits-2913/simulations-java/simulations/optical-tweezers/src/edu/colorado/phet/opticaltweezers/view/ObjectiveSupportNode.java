// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ObjectiveSupportNode is a support apparatus for the objective.
 * It looks a bit like an adjustable wrench, and two of these are
 * used to connect the objective to the control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ObjectiveSupportNode extends PPath {

    private static final double HEAD_WIDTH = 20;
    private static final double HEAD_HEIGHT = 20;
    private static final double BEAM_WIDTH = 10;
    
    public ObjectiveSupportNode( double height ) {
        super();
        
        GeneralPath headPath = new GeneralPath();
        headPath.moveTo( 0, 0 );
        headPath.lineTo( (float) ( 0.75 * HEAD_WIDTH ), 0 );
        headPath.lineTo( (float) HEAD_WIDTH, (float) ( 0.25 * HEAD_HEIGHT ) );
        headPath.lineTo( (float) ( HEAD_WIDTH / 2 ), (float) ( 0.25 * HEAD_HEIGHT ) );
        headPath.lineTo( (float) ( HEAD_WIDTH / 2 ), (float) ( 0.75 * HEAD_HEIGHT ) );
        headPath.lineTo( (float) HEAD_WIDTH, (float) ( 0.75 * HEAD_HEIGHT ) );
        headPath.lineTo( (float) ( 0.75 * HEAD_WIDTH ), (float) HEAD_HEIGHT );
        headPath.lineTo( (float) 0, (float) HEAD_HEIGHT );
        headPath.closePath();
        
        Shape beamShape = new Rectangle2D.Double( 0, 0, BEAM_WIDTH, height );
        
        Area area = new Area( headPath );
        area.add( new Area( beamShape ) );
        
        setPathTo( area );
        setPaint( Color.DARK_GRAY );
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
    }
    
    public double getHeadHeight() {
        return HEAD_HEIGHT;
    }
}
