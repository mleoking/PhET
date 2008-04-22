/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
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
    
    private final Glacier _glacier;
    private final GlacierListener _glacierListener;
    private final ModelViewTransform _mvt;
    private final Point2D _pModel, _pView; // reusable points
    private final PPath _pathNode;
    private final GeneralPath _path;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public EquilibriumLineNode( Glacier glacier, ModelViewTransform mvt ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _glacier = glacier;
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                update();
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        
        _mvt = mvt;
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        // horizontal line
        _path = new GeneralPath();
        _pathNode = new PPath();
        _pathNode.setStroke( STROKE );
        _pathNode.setStrokePaint( STROKE_COLOR );
        addChild( _pathNode );
        
        // intialize
        update();
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        
        _path.reset();
        
        // start drawing at the left edge of the birds-eye view bounds
        double ela = _glacier.getClimate().getELA();
        _pModel.setLocation( PlayArea.getBirdsEyeViewportOffset().getX(), ela );
        _mvt.modelToView( _pModel, _pView );
        _path.moveTo( (float)_pView.getX(), (float)_pView.getY() );
        
        final double x0 = _glacier.getHeadwallReference().getX();
        final double y0 = _glacier.getHeadwallReference().getY();
        if ( ela > y0 ) {
            // if the ELA is above the top of the headwall, then stop drawing at the headwall
            _pModel.setLocation( x0, ela );
            _mvt.modelToView( _pModel, _pView );
            _path.lineTo( (float) _pView.getX(), (float) _pView.getY() );
        }
        else {
            Point2D steadyStateELAContour = _glacier.getIntersectionWithSteadyStateELA();
            if ( steadyStateELAContour != null ) {
                // draw a line to the ice-air interface at the ELA
                _pModel.setLocation( steadyStateELAContour.getX(), ela );
                _mvt.modelToView( _pModel, _pView );
                _path.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                
                // draw a vertical line across the surface of the ice
                final double perspectiveHeight = 300;//XXX
                _pModel.setLocation( steadyStateELAContour.getX(), ela + perspectiveHeight );
                _mvt.modelToView( _pModel, _pView );
                _path.lineTo( (float) _pView.getX(), (float) _pView.getY() );
            }
            else {
                //TODO draw a line to where the ELA meets the valley floor
                double x = _glacier.getValley().getX( ela );
                _pModel.setLocation( x, ela );
                _mvt.modelToView( _pModel, _pView );
                _path.lineTo( (float) _pView.getX(), (float) _pView.getY() );
            }
            

        }
        
        _pathNode.setPathTo( _path );
    }
}
