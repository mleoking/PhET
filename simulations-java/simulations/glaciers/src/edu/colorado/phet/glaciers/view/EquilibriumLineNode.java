/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * EquilibriumLineNode is the visual representation of the equilibrium line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquilibriumLineNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color STROKE_COLOR = Color.RED;
    private static final Stroke STROKE = 
        new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Climate _climate;
    private ClimateListener _climateListener;
    private ModelViewTransform _mvt;
    private Point2D _pModel, _pView; // reusable points
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public EquilibriumLineNode( Climate climate, ModelViewTransform mvt ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _climate = climate;
        _climateListener = new ClimateListener() {

            public void snowfallChanged() {
                update();
            }

            public void snowfallReferenceElevationChanged() {
                update();
            }

            public void temperatureChanged() {
                update();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        _mvt = mvt;
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        // horizontal line
        _pModel.setLocation( 80E3, 0 );
        mvt.modelToView( _pModel, _pView );
        Line2D path = new Line2D.Double( 0, 0, _pView.getX(), 0 );
        PPath pathNode = new PPath( path );
        pathNode.setStroke( STROKE );
        pathNode.setStrokePaint( STROKE_COLOR );
        addChild( pathNode );
        
        // intialize
        update();
    }
    
    public void cleanup() {
        _climate.removeClimateListener( _climateListener );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        
        double ela = _climate.getELA();
        _pModel.setLocation( 0, ela );
        _mvt.modelToView( _pModel, _pView );
        
        // update position of this node
        setOffset( _pView );
    }
}
