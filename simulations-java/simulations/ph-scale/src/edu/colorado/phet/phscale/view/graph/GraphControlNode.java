// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.view.graph;

import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.view.graph.ScaleControlPanel.ScaleControlPanelListener;
import edu.colorado.phet.phscale.view.graph.UnitsControlPanel.UnitsControlPanelListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * GraphControlNode contains the bar graph and all associated controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphControlNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, PHScaleConstants.CONTROL_FONT_SIZE + 2 );
    private static final double GRAPH_OUTLINE_WIDTH = 225;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GraphNode _graphNode;
    private final UnitsControlPanel _unitsControlPanel;
    private final ScaleControlPanel _scaleControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GraphControlNode( Liquid liquid, double logTicksYSpacing ) {
        super();
        
        PText titleNode = new PText( PHScaleStrings.TITLE_WATER_COMPONENTS );
        titleNode.setFont( TITLE_FONT );
        addChild( titleNode );
        
        // units controls
        _unitsControlPanel = new UnitsControlPanel();
        _unitsControlPanel.addUnitsControlPanelListener( new UnitsControlPanelListener() {
            public void selectionChanged() {
                _graphNode.setConcentrationUnits( _unitsControlPanel.isConcentrationSelected() );
            }
        });
        PSwing unitsControlPanelWrapper = new PSwing( _unitsControlPanel );
        addChild( unitsControlPanelWrapper );
        
        // graph
        _graphNode = new GraphNode( liquid, GRAPH_OUTLINE_WIDTH, logTicksYSpacing );
        addChild( _graphNode );
        
        // legend
        LegendNode legendNode = new LegendNode();
        addChild( legendNode );
        
        // scale controls
        _scaleControlPanel = new ScaleControlPanel();
        _scaleControlPanel.addScaleControlPanelListener( new ScaleControlPanelListener() {
            public void selectionChanged() {
                _graphNode.setLogScale( _scaleControlPanel.isLogSelected() );
            }
        });
        PSwing scaleControlPanelWrapper = new PSwing( _scaleControlPanel );
        addChild( scaleControlPanelWrapper );
        
        // layout
        titleNode.setOffset( 0, 0 );
        PBounds tb = titleNode.getFullBoundsReference();
        unitsControlPanelWrapper.setOffset( tb.getX(), tb.getMaxY() + 10 );
        PBounds ub = unitsControlPanelWrapper.getFullBoundsReference();
        _graphNode.setOffset( ub.getX(), ub.getMaxY() + 10 );
        PBounds gb = _graphNode.getFullBoundsReference();
        PBounds lb = legendNode.getFullBoundsReference();
        legendNode.setOffset( ( GRAPH_OUTLINE_WIDTH - lb.getWidth() ) / 2, gb.getMaxY() + 2 );
        lb = legendNode.getFullBoundsReference();
        scaleControlPanelWrapper.setOffset( unitsControlPanelWrapper.getX(), lb.getMaxY() + 10 );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setConcentrationSelected( boolean selected ) {
        _unitsControlPanel.setConcentrationSelected( selected );
    }
    
    public void setLogSelected( boolean selected ) {
        _scaleControlPanel.setLogSelected( selected );
    }
    
    /**
     * Gets the offset used to vertically align the graph ticks with the pH slider ticks.
     * Offset is in global coordinates.
     * Only the y offset is meaningful.
     * 
     * @return
     */
    public Point2D getTickAlignmentGlobalOffset() {
        return _graphNode.getTickAlignmentGlobalOffset();
    }
}
