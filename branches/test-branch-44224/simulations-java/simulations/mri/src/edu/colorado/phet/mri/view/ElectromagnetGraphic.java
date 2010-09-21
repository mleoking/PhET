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

import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.RegisterablePNode;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.MriResources;
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.mri.model.GradientElectromagnet;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ElectromagnetGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectromagnetGraphic extends RegisterablePNode implements Electromagnet.ChangeListener {
    private PNode coilGraphic;
    private PNode upperLead, lowerLead;
    private GradientElectromagnet magnet;
//    private Arrow upperArrow;
//    private RegisterablePNode upperArrowGraphic;
    //    private Arrow lowerArrow;
    //    private RegisterablePNode lowerArrowGraphic;
    private Point2D upperArrowCenterPoint;
    private Point2D lowerArrowCenterPoint;
    private double scale;

    /**
     * @param electromagnet
     */
    public ElectromagnetGraphic( GradientElectromagnet electromagnet ) {
        this( electromagnet, 1 );
    }

    /**
     * @param electromagnet
     * @param scale
     */
    public ElectromagnetGraphic( GradientElectromagnet electromagnet, double scale ) {
        this.scale = scale;

        this.magnet = electromagnet;
        electromagnet.addChangeListener( this );

        Rectangle2D bounds = electromagnet.getBounds();
        setRegistrationPoint( bounds.getWidth() / 2, bounds.getHeight() / 2 );

        // Graphics for the arrows indicating current
//        createCurrentArrows( bounds );

        // Graphics for the lead wires
        createLeadWires( bounds );

        update();
    }

//    private void createCurrentArrows( Rectangle2D bounds ) {
//        upperArrowCenterPoint = new Point2D.Double( -100, 0 );
//        lowerArrowCenterPoint = new Point2D.Double( -100, bounds.getHeight() );
//        upperArrow = new Arrow( new Point2D.Double( -100, - 20 ),
//                                new Point2D.Double( 0, -20 ), 15, 15, 3, 0.5, true );
//        upperArrowGraphic = new RegisterablePNode( new PPath( upperArrow.getShape() ) );
//        addChild( upperArrowGraphic );
//
//        lowerArrow = new Arrow( new Point2D.Double( 0, 20 ),
//                                new Point2D.Double( -100, 20 ), 15, 15, 3, 0.5, true );
//        lowerArrowGraphic = new RegisterablePNode( new PPath( lowerArrow.getShape() ) );
//        addChild( lowerArrowGraphic );
//
    // Graphics for the "I" characters indicating current
//        PText lowerI = new PText( "I" );
//        lowerI.setFont( new Font( "Serif", Font.BOLD, 24 ) );
//        lowerI.setOffset( lowerArrowCenterPoint.getX() - 10, lowerArrowCenterPoint.getY() + 3 );
//        addChild( lowerI );
//    }

    private void createLeadWires( Rectangle2D bounds ) {
        Line2D upperLeadLine = new Line2D.Double( -300, 10, 0, 10 );
        Line2D lowerLeadLine = new Line2D.Double( -300, bounds.getHeight() - 10, 0, bounds.getHeight() - 10 );
        upperLead = new PPath( upperLeadLine );
        lowerLead = new PPath( lowerLeadLine );
        addChild( upperLead );
        addChild( lowerLead );

        if( magnet.getOrientation() == GradientElectromagnet.HORIZONTAL ) {
            coilGraphic = PImageFactory.create( MriResources.getImage( MriConfig.COIL_IMAGE ), new Dimension( (int)bounds.getWidth(), (int)bounds.getHeight() ) );
        }
        else {
            coilGraphic = PImageFactory.create( MriResources.getImage( MriConfig.COIL_IMAGE ), new Dimension( (int)bounds.getHeight(), (int)bounds.getWidth() ) );
            coilGraphic.rotate( Math.PI / 2 );
            coilGraphic.translate( 0, -bounds.getWidth() );
        }
        addChild( coilGraphic );
    }

    /**
     *
     */
    public void update() {
        setOffset( magnet.getPosition() );

//        double fractionMaxCurrent = magnet.getCurrent() / MriConfig.MAX_FADING_COIL_CURRENT;
//        int blueComponent = (int)( 200 - 200 * fractionMaxCurrent );
//        coilGraphic.setPaint( new Color( 200, 200, blueComponent ) );
//
//        double maxArrowLength = 120;
//        double minArrowLength = 20;
//        double arrowLength = ( maxArrowLength - minArrowLength ) * fractionMaxCurrent + minArrowLength * scale;
//        upperArrow.setTailLocation( new Point2D.Double( upperArrowCenterPoint.getX() + arrowLength / 2,
//                                                        upperArrowCenterPoint.getY() ) );
//        upperArrow.setTipLocation( new Point2D.Double( upperArrowCenterPoint.getX() - arrowLength / 2,
//                                                       upperArrowCenterPoint.getY() ) );
//        removeChild( upperArrowGraphic );
//        upperArrowGraphic = new RegisterablePNode( new PPath( upperArrow.getShape() ) );
//        addChild( upperArrowGraphic );
//
//        lowerArrow.setTipLocation( new Point2D.Double( lowerArrowCenterPoint.getX() + arrowLength / 2,
//                                                       lowerArrowCenterPoint.getY() ) );
//        lowerArrow.setTailLocation( new Point2D.Double( lowerArrowCenterPoint.getX() - arrowLength / 2,
//                                                        lowerArrowCenterPoint.getY() ) );
//        removeChild( lowerArrowGraphic );
//        lowerArrowGraphic = new RegisterablePNode( new PPath( lowerArrow.getShape() ) );
//        addChild( lowerArrowGraphic );
    }

    public void stateChanged( Electromagnet.ChangeEvent event ) {
        update();
    }
}
