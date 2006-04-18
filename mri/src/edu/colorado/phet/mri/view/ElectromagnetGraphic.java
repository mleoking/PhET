/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * ElectromagnetGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectromagnetGraphic extends RegisterablePNode implements Electromagnet.ChangeListener {
    private PNode coilGraphic;
    private PNode upperLead, lowerLead;
    private Electromagnet electromagnet;
    private Arrow upperArrow;
    private RegisterablePNode upperArrowGraphic;
    private Arrow lowerArrow;
    private RegisterablePNode lowerArrowGraphic;
    private Point2D upperArrowCenterPoint;
    private Point2D lowerArrowCenterPoint;

    public ElectromagnetGraphic( Electromagnet electromagnet ) {

        this.electromagnet = electromagnet;
        electromagnet.addChangeListener( this );

        Rectangle2D bounds = electromagnet.getBounds();
        setRegistrationPoint( bounds.getWidth() / 2, bounds.getHeight() / 2 );

        // Graphics for the lead wires
        Line2D upperLeadLine = new Line2D.Double( -300, bounds.getHeight() / 3, bounds.getWidth() / 2, bounds.getHeight() / 3 );
        Line2D lowerLeadLine = new Line2D.Double( -300, bounds.getHeight() * 2 / 3, bounds.getWidth() / 2, bounds.getHeight() * 2 / 3 );
        upperLead = new PPath( upperLeadLine );
        lowerLead = new PPath( lowerLeadLine );
        addChild( upperLead );
        addChild( lowerLead );

        // Graphics for the arrows indicating current
        upperArrowCenterPoint = new Point2D.Double( -100, 0 );
        lowerArrowCenterPoint = new Point2D.Double( -100, bounds.getHeight() );
        upperArrow = new Arrow( new Point2D.Double( -100, - 20 ),
                                new Point2D.Double( 0, -20 ), 15, 15, 3 );
        upperArrowGraphic = new RegisterablePNode( new PPath( upperArrow.getShape() ) );
        addChild( upperArrowGraphic );

        lowerArrow = new Arrow( new Point2D.Double( 0, 20 ),
                                new Point2D.Double( -100, 20 ), 15, 15, 3 );
        lowerArrowGraphic = new RegisterablePNode( new PPath( lowerArrow.getShape() ) );
        addChild( lowerArrowGraphic );

        // Graphics for the "I" characters indicating current
        PText lowerI = new PText( "I" );
        lowerI.setFont( new Font( "Serif", Font.BOLD, 24 ) );
        lowerI.setOffset( lowerArrowCenterPoint.getX() - 10, lowerArrowCenterPoint.getY() + 3 );
        addChild( lowerI );

        // Graphic for the coil
        Ellipse2D shape = new Ellipse2D.Double( 0, 0, bounds.getWidth(), bounds.getHeight() );
        coilGraphic = new PPath( shape );
        addChild( coilGraphic );


        update();
    }

    public void update() {
        setOffset( electromagnet.getPosition() );

        double fractionMaxCurrent = electromagnet.getCurrent() / MriConfig.MAX_FADING_COIL_CURRENT;
        int blueComponent = (int)( 200 - 200 * fractionMaxCurrent );
        coilGraphic.setPaint( new Color( 200, 200, blueComponent ) );

        double maxArrowLength = 200;
        double minArrowLength = 20;
        double arrowLength = ( maxArrowLength - minArrowLength ) * fractionMaxCurrent + minArrowLength;
        upperArrow.setTailLocation( new Point2D.Double( upperArrowCenterPoint.getX() + arrowLength / 2,
                                                        upperArrowCenterPoint.getY() ) );
        upperArrow.setTipLocation( new Point2D.Double( upperArrowCenterPoint.getX() - arrowLength / 2,
                                                       upperArrowCenterPoint.getY() ) );
        removeChild( upperArrowGraphic );
        upperArrowGraphic = new RegisterablePNode( new PPath( upperArrow.getShape() ) );
        addChild( upperArrowGraphic );

        lowerArrow.setTipLocation( new Point2D.Double( lowerArrowCenterPoint.getX() + arrowLength / 2,
                                                       lowerArrowCenterPoint.getY() ) );
        lowerArrow.setTailLocation( new Point2D.Double( lowerArrowCenterPoint.getX() - arrowLength / 2,
                                                        lowerArrowCenterPoint.getY() ) );
        removeChild( lowerArrowGraphic );
        lowerArrowGraphic = new RegisterablePNode( new PPath( lowerArrow.getShape() ) );
        addChild( lowerArrowGraphic );
    }

    public void stateChanged( Electromagnet.ChangeEvent event ) {
        update();
    }
}
