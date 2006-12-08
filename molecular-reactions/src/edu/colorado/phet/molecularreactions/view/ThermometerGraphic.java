/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * ThermometerGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
/* Copyright 2003-2004, University of Colorado */


public class ThermometerGraphic extends PNode {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static Color s_color = Color.red;
    private static Color s_outlineColor = Color.black;

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private Ellipse2D.Double bulb;
    private NumberFormat formatter = new DecimalFormat( "#0" );
    private Font font = new Font( "Lucida Sans", Font.BOLD, 10 );
    private float columnStrokeWidth = 1.5f;
    private BasicStroke columnStroke = new BasicStroke( columnStrokeWidth );
    private Color rectColor = Color.yellow;
    private int readoutWidth;
    private float readoutRectStrokeWidth = 0.5f;
    private BasicStroke readoutRectStroke = new BasicStroke( readoutRectStrokeWidth );
    private BasicStroke oneStroke;

    private PPath fillNode;
    private Rectangle2D columnFill;


    private double bulbRadius = 8;
    private double columnWidth = 8;
    private double overallHeight = 150;
    private MRModel model;
    private double maxModelValue;
    private Rectangle2D column;
    private double fillHeightScale;

    /**
     * @param minModelValue
     * @param maxModelValue
     */
    public ThermometerGraphic( MRModel model, IClock clock, double minModelValue, double maxModelValue ) {
        this.model = model;
        this.maxModelValue = maxModelValue;

        column = new Rectangle2D.Double(0,0,columnWidth, overallHeight - bulbRadius );
        PPath columnBackground = new PPath( column );
        columnBackground.setPaint( Color.white );
        fillHeightScale = (overallHeight - bulbRadius) / maxModelValue;
        PPath columnNode = new PPath( column );
        columnNode.setPaint( new Color( 0,0,0,0 ) );

        columnFill = new Rectangle2D.Double();
        fillNode = new PPath( columnFill );
        fillNode.setPaint( Color.red );
        bulb = new Ellipse2D.Double( 0,0, bulbRadius * 2, bulbRadius * 2 );
        PPath bulbNode = new PPath( bulb );
        bulbNode.setPaint( Color.red );
        setPickable( false );

        columnBackground.setOffset(  -columnWidth / 2, 0 );
        fillNode.setOffset( -columnWidth / 2, 0 );
        bulbNode.setOffset( -bulbRadius, overallHeight - bulbRadius * 2);
        columnNode.setOffset( -columnWidth / 2, 0 );

        addChild( columnBackground );
        addChild( fillNode );
        addChild( columnNode );
        addChild( bulbNode );

        clock.addClockListener( new Updater() );
    }

    public void update() {
        double modelValue = Math.min( model.getTemperature(), maxModelValue );
        double ke =  modelValue * fillHeightScale;
        columnFill.setFrame( 0, column.getHeight() - ke, column.getWidth(), ke );
        fillNode.setPathTo( columnFill);
    }

    private class Updater extends ClockAdapter {
        public void clockTicked( ClockEvent clockEvent ) {
            update();
        }
    }
}
