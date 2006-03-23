/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.SolubleSaltsApplication;
import edu.colorado.phet.solublesalts.model.Vessel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * VesselGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class VesselGraphic extends PNode implements SolubleSaltsApplication.ResetListener {

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private PPath shape;
    private PPath water;
    private Color waterColor = SolubleSaltsConfig.WATER_COLOR;
    private Vessel vessel;
    private Color tickColor = new Color( 255, 180, 180 );
    private ArrayList minorTicks = new ArrayList();
    private ArrayList majorTicks = new ArrayList();

    public VesselGraphic( final Vessel vessel ) {
        ((SolubleSaltsApplication)SolubleSaltsApplication.instance()).addResetListener( this );
        this.vessel = vessel;

        // Listen for state changes in the vessel
        vessel.addChangeListener( new Vessel.ChangeListener() {
            public void stateChanged( Vessel.ChangeEvent event ) {
                update( event.getVessel() );
            }
        } );

        shape = new PPath();
        addChild( shape );
        water = new PPath();
        water.setPaint( waterColor );
        water.setStrokePaint( null );
        this.addChild( water );

        setMinorTickSpacing( SolubleSaltsConfig.VESSEL_MINOR_TICK_SPACING );
        setMajorTickSpacing( SolubleSaltsConfig.VESSEL_MAJOR_TICK_SPACING );

        update( vessel );
    }

    private void update( Vessel vessel ) {
        this.vessel = vessel;
        float thickness = (float)( vessel.getWallThickness() * SolubleSaltsConfig.SCALE );
        Rectangle2D rect = vessel.getShape();
        DoubleGeneralPath walls = new DoubleGeneralPath();
        walls.moveTo( -thickness / 2, 0 );
        walls.lineToRelative( 0, rect.getHeight() + thickness / 2 );
        walls.lineToRelative( rect.getWidth() + thickness, 0 );
        walls.lineToRelative( 0, -( rect.getHeight() + thickness / 2 ) );
        shape.setPathTo( walls.getGeneralPath() );
        shape.setStroke( new BasicStroke( thickness ) );
        water.setPathTo( new Rectangle2D.Double( 0,
                                                 shape.getHeight() - thickness * 3 / 2 - vessel.getWaterLevel(),
                                                 vessel.getShape().getWidth(),
                                                 vessel.getWaterLevel() ) );
        setOffset( vessel.getLocation() );
    }

    /**
     * Sets the spacing of the major tick marks on the wall of the vessel
     *
     * @param spacing
     */
    public void setMajorTickSpacing( double spacing ) {

        // Clear any existing ticks
        for( int i = 0; i < majorTicks.size(); i++ ) {
            PNode tick = (PNode)majorTicks.get( i );
            removeChild( tick );
        }
        majorTicks.clear();

        // Create the ticks
        int numTicks = (int)( vessel.getDepth() / ( spacing / SolubleSaltsConfig.VOLUME_CALIBRATION_FACTOR ) );
        for( int i = 1; i <= numTicks; i++ ) {
            double y = ( vessel.getDepth() - i * ( spacing / SolubleSaltsConfig.VOLUME_CALIBRATION_FACTOR ) );
            PPath tick = new PPath( new Line2D.Double( vessel.getWidth() + 2,
                                                       y,
                                                       vessel.getWidth() + vessel.getWallThickness() / 2,
                                                       y ) );
            tick.setStroke( new BasicStroke( 2 ) );
            tick.setStrokePaint( tickColor );
            tick.setPaint( tickColor );
            addChild( tick );

            DecimalFormat format = new DecimalFormat( "0.0E0" );

            String str = format.format( ( vessel.getDepth() - y ) * SolubleSaltsConfig.VOLUME_CALIBRATION_FACTOR );
            str = str.concat( " L" );
            PText text = new PText( str );
            Font orgFont = text.getFont();
            Font newFont = new Font( orgFont.getName(), orgFont.getStyle(), orgFont.getSize() + 12 );
            text.setFont( newFont );
            text.setOffset( vessel.getWidth() + vessel.getWallThickness() + 5, y - 14 );
            addChild( text );

            minorTicks.add( tick );
            minorTicks.add( text );
        }
    }

    /**
     * Sets the spacing of the minor tick marks on the wall of the vessel
     *
     * @param spacing
     */
    public void setMinorTickSpacing( double spacing ) {
        // Clear any existing ticks
        for( int i = 0; i < minorTicks.size(); i++ ) {
            PNode tick = (PNode)minorTicks.get( i );
            removeChild( tick );
        }
        minorTicks.clear();

        // Create the ticks
        int numTicks = (int)( vessel.getDepth() / ( spacing / SolubleSaltsConfig.VOLUME_CALIBRATION_FACTOR ) );
        for( int i = 1; i <= numTicks; i++ ) {
            double y = vessel.getDepth() - i * ( spacing / SolubleSaltsConfig.VOLUME_CALIBRATION_FACTOR );
            PPath tick = new PPath( new Line2D.Double( vessel.getWidth() + 2,
                                                       y,
                                                       vessel.getWidth() + vessel.getWallThickness() / 4,
                                                       y ) );
            tick.setStroke( new BasicStroke( 2 ) );
            tick.setStrokePaint( tickColor );
            tick.setPaint( tickColor );
            addChild( tick );

            minorTicks.add( tick );
        }
    }

    public void reset() {
        setMinorTickSpacing( SolubleSaltsConfig.VESSEL_MINOR_TICK_SPACING );
        setMajorTickSpacing( SolubleSaltsConfig.VESSEL_MAJOR_TICK_SPACING );
        update( vessel );
    }
}
