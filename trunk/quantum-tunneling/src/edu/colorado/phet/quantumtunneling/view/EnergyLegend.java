/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * EnergyLegend is the legend for the Energy chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class EnergyLegend extends PNode {
    
    private static final double MARGIN = 5;
    private static final Stroke BORDER_STROKE = new BasicStroke( 0.5f );
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    
    public EnergyLegend() {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        // Total Energy
        LegendItem totalEnergyItem = 
            new LegendItem( SimStrings.get( "legend.totalEnergy" ), QTConstants.TOTAL_ENERGY_COLOR );
        totalEnergyItem.translate( 0, 0 );
        addChild( totalEnergyItem );
        
        // Potential Energy
        LegendItem potentialEnergyItem = 
            new LegendItem( SimStrings.get( "legend.potentialEnergy" ), QTConstants.POTENTIAL_ENERGY_COLOR );
        potentialEnergyItem.translate( totalEnergyItem.getFullBounds().getWidth() + 20, 0 );
        addChild( potentialEnergyItem );
        
        // Border
        Rectangle2D bounds = getFullBounds();
        double margin = MARGIN;
        double x = bounds.getX();
        double y = bounds.getY();
        double w = bounds.getWidth() + ( 2 * margin );
        double h = bounds.getHeight() + ( 2 * margin );
        Rectangle2D border = new Rectangle2D.Double( x, y, w, h );
        PPath borderNode = new PPath( border );
        borderNode.setStroke( BORDER_STROKE );
        borderNode.setStrokePaint( BORDER_COLOR );
        borderNode.setPaint( BACKGROUND_COLOR );
        totalEnergyItem.translate( MARGIN, MARGIN );
        potentialEnergyItem.translate( MARGIN, MARGIN );
        addChild( 0, borderNode );
    }
}
