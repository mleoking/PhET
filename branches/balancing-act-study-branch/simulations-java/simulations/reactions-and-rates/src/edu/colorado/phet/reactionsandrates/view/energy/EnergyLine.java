// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view.energy;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.MRModel;
import edu.colorado.phet.reactionsandrates.modules.MRModule;
import edu.colorado.phet.reactionsandrates.util.Resetable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * A line that indicates the energy in the molecule being tracked, the
 * molecule closest to it, and any provisional bond that may be between them.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyLine extends PNode implements Resetable {
    public static final Paint linePaint = MRConfig.TOTAL_ENERGY_COLOR;
    public static final Stroke lineStroke = new BasicStroke( EnergyProfileGraphic.LINE_STROKE.getLineWidth() + 1 );

    private Line2D line;
    private PPath lineNode;
    private Dimension bounds;
    private MRModel model;
    private double scale;
    private PText totalEnergyLegend;
    private MRModule module;
    private double curEnergy = -1.0;

    /**
     * @param bounds The bounds within which this line is to be drawn
     * @param module The module.
     * @param clock  The clock.
     */
    public EnergyLine( Dimension bounds, MRModule module, IClock clock ) {
        this.module = module;
        this.bounds = bounds;
        this.model = module.getMRModel();

        line = new Line2D.Double();
        lineNode = new PPath( line );
        lineNode.setPaint( linePaint );
        lineNode.setStroke( lineStroke );
        lineNode.setStrokePaint( linePaint );
        scale = bounds.getHeight() / MRConfig.MAX_REACTION_THRESHOLD;

        addChild( lineNode );

        Font labelFont = new PhetFont( PhetFont.getDefaultFontSize()+1,true );
        totalEnergyLegend = new PText( MRConfig.RESOURCES.getLocalizedString( "EnergyView.Legend.totalEnergy" ) );
        totalEnergyLegend.setFont( labelFont );
        totalEnergyLegend.setTextPaint( MRConfig.TOTAL_ENERGY_COLOR );
        addChild( totalEnergyLegend );

        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                update();
            }
        } );

        model.addListener( new MRModel.ModelListenerAdapter() {
            public void notifyDefaultTemperatureChanged( double newInitialTemperature ) {
                update();
            }
        } );

        setChildrenPickable( false );
        setPickable( false );

        curEnergy = model.getTotalEnergy();
    }

    public double getEnergyLineY() {
        return line.getY1();
    }

    public void reset() {
        curEnergy = -1.0;
    }

    public void update() {
        if( module.isTemperatureBeingAdjusted() || module.isResetInProgress() || curEnergy < 0.0 ) {
            curEnergy = model.getTotalEnergy();
        }

        double y = Math.max( bounds.getHeight() - ( curEnergy * scale ), 0 );
        line.setLine( 0, y, bounds.getWidth(), y );
        lineNode.setPathTo( line );

        totalEnergyLegend.setOffset( 5, //line.getX2() - totalEnergyLegend.getFullBounds().getWidth(),
                                     y + 5 );
    }

    public void setLegendVisible( boolean visible ) {
        this.totalEnergyLegend.setVisible( visible );
    }

    public void setLabel( String propertyName ) {
        totalEnergyLegend.setText( MRConfig.RESOURCES.getLocalizedString( propertyName ) );
    }
}
