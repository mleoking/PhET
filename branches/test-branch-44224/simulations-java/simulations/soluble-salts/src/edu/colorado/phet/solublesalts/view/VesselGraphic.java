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

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.SolubleSaltResources;
import edu.colorado.phet.solublesalts.model.Vessel;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.util.ScientificNotation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * VesselGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class VesselGraphic extends PNode implements SolubleSaltsModule.ResetListener {

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

    public VesselGraphic( final Vessel vessel, SolubleSaltsModule module ) {
        module.addResetListener( this );
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

        setMinorTickSpacing( module.getCalibration() );
        setMajorTickSpacing( module.getCalibration() );

        update( vessel );
    }

    private void update( Vessel vessel ) {
        this.vessel = vessel;
        float thickness = (float) ( vessel.getWallThickness() * SolubleSaltsConfig.SCALE );
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
     * @param calibration
     */
    public void setMajorTickSpacing( SolubleSaltsConfig.Calibration calibration ) {

        // Clear any existing ticks
        for ( int i = 0; i < majorTicks.size(); i++ ) {
            PNode tick = (PNode) majorTicks.get( i );
            removeChild( tick );
        }
        majorTicks.clear();

        // Create the ticks
        int numTicks = (int) ( vessel.getDepth() / ( calibration.majorTickSpacing / calibration.volumeCalibrationFactor ) );
        for ( int i = 1; i <= numTicks; i++ ) {
            double y = ( vessel.getDepth() - i * ( calibration.majorTickSpacing / calibration.volumeCalibrationFactor ) );
            PPath tick = new PPath( new Line2D.Double( vessel.getWidth() + 2,
                                                       y,
                                                       vessel.getWidth() + vessel.getWallThickness() / 2,
                                                       y ) );
            tick.setStroke( new BasicStroke( 2 ) );
            tick.setStrokePaint( tickColor );
            tick.setPaint( tickColor );
            addChild( tick );

            double volume = ( vessel.getDepth() - y ) * calibration.volumeCalibrationFactor;
            String volumeStr = ScientificNotation.toHtml( volume, 1, "", SolubleSaltResources.getString( "ControlLabels.liters.abbreviation" ) );
            HTMLNode text = new HTMLNode( volumeStr );
            Font orgFont = text.getFont();
            Font newFont = new Font( orgFont.getName(), Font.PLAIN, orgFont.getSize() + 12 );
            text.setFont( newFont );
            text.setOffset( vessel.getWidth() + vessel.getWallThickness() + 5, y - 25 );
            addChild( text );

            minorTicks.add( tick );
            minorTicks.add( text );
        }
    }

    /**
     * Sets the spacing of the minor tick marks on the wall of the vessel
     *
     * @param calibration
     */
    public void setMinorTickSpacing( SolubleSaltsConfig.Calibration calibration ) {
        // Clear any existing ticks
        for ( int i = 0; i < minorTicks.size(); i++ ) {
            PNode tick = (PNode) minorTicks.get( i );
            removeChild( tick );
        }
        minorTicks.clear();

        // Create the ticks
        int numTicks = (int) ( vessel.getDepth() / ( calibration.minorTickSpacing / calibration.volumeCalibrationFactor ) );
        for ( int i = 1; i <= numTicks; i++ ) {
            double y = vessel.getDepth() - i * ( calibration.minorTickSpacing / calibration.volumeCalibrationFactor );
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

    public void reset( SolubleSaltsConfig.Calibration calibration ) {
        setMinorTickSpacing( calibration );
        setMajorTickSpacing( calibration );
        update( vessel );
    }
}
