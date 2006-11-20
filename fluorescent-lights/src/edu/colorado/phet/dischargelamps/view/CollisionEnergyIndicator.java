/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetMultiLineTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.SingleAtomModule;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.quantum.model.Atom;
import edu.colorado.phet.quantum.model.Electron;
import edu.colorado.phet.quantum.model.Plate;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * CollisionEnergyIndicator
 * <p/>
 * A collection of graphics that shows the energy an electron will have when it hits the
 * atom in the SingleAtomModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CollisionEnergyIndicator extends CompositePhetGraphic {

    private DischargeLampAtom atom;
    private DischargeLampModel model;
    private ModelViewTransform1D energyYTx;

    /**
     * @param elmp
     * @param module
     */
    public CollisionEnergyIndicator( DischargeLampEnergyLevelMonitorPanel elmp, SingleAtomModule module ) {
        super( elmp );

        // Create the dotted line
        float miterLimit = 10f;
        float[] dashPattern = {10f};
        float dashPhase = 5f;
        float strokeThickness = 1;
        Shape line = new Line2D.Double( 25, 0, 110, 0 );
        Paint paint = Color.red;
        Stroke stroke = new BasicStroke( strokeThickness );
        PhetShapeGraphic lineGraphic = new PhetShapeGraphic( elmp, line, stroke, paint );
        addGraphic( lineGraphic );

        // Create an arrowhead
        Arrow arrow = new Arrow( new Point2D.Double( 110, 0 ), new Point2D.Double( 25, 0 ), 8, 8, 1 );
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( elmp, arrow.getShape(), Color.red );
        addGraphic( arrowGraphic );

        // Create the electron graphic
        PhetImageGraphic electronGraphic = new PhetImageGraphic( elmp, DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME );
        electronGraphic.setLocation( (int)line.getBounds().getMaxX() + 5, -3 );
        addGraphic( electronGraphic );

        // Create the text
        PhetMultiLineTextGraphic textGraphic = new PhetMultiLineTextGraphic( elmp,
                                                                             DischargeLampsConfig.DEFAULT_CONTROL_FONT,
                                                                             new String[]{SimStrings.get("Misc.energyAt"),
                                                                                     SimStrings.get("Misc.collision") },
                                                                             Color.black );
        textGraphic.setLocation( (int)electronGraphic.getBounds().getMaxX() + 5, -8 );
        addGraphic( textGraphic );

        // Attach a listener to the model that moves the graphic when the model-view tranform changes
        model = (DischargeLampModel)module.getModel();
        model.addChangeListener( new ModelChangeListener() );
        atom = (DischargeLampAtom)module.getAtom();
        atom.addChangeListener( new AtomChangeListener() );
        this.energyYTx = elmp.getEnergyYTx();
        elmp.addChangeListener( new DischargeLampEnergyLevelMonitorPanel.ChangeListener() {
            public void energyTxChanged( ModelViewTransform1D energyTx ) {
                energyYTx = energyTx;
                update();
            }
        } );

        update();
    }

    private void update() {
        // Get the distance of the atom from the emitting plate, and the distance
        // between the plates
        double voltage = model.getVoltage();
        double plateToPlateDist = model.getLeftHandPlate().getPosition().distance( model.getRightHandPlate().getPosition() ) - Electron.ELECTRON_RADIUS;
        double plateToAtomDist = 0;
        Plate emittingPlate = null;
        if( voltage > 0 ) {
            emittingPlate = model.getLeftHandPlate();
        }
        else {
            emittingPlate = model.getRightHandPlate();
        }
        // Must correct the distance between the plate and the atom by the radii of the atom and an electron. Note
        // the sign of the electron radius correction. This seems to work(???)
        plateToAtomDist = emittingPlate.getPosition().distance( atom.getPosition() ) - atom.getBaseRadius() - Electron.ELECTRON_RADIUS;

        Point2D p1Upper = new Point2D.Double( model.getLeftHandPlate().getPosition().getX(),
                                         model.getLeftHandPlate().getPosition().getY() +
                                         Electron.ELECTRON_RADIUS +
                                         ( model.getLeftHandPlate().getEndpoints()[1].getY()
                                           - model.getLeftHandPlate().getEndpoints()[0].getY() ) / 2 );
        Point2D p2Upper = new Point2D.Double( model.getRightHandPlate().getPosition().getX(),
                                         model.getRightHandPlate().getPosition().getY() +
                                         Electron.ELECTRON_RADIUS +
                                         ( model.getRightHandPlate().getEndpoints()[1].getY()
                                           - model.getRightHandPlate().getEndpoints()[0].getY() ) / 2 );
        Line2D lUpper = new Line2D.Double( p1Upper, p2Upper );
        Ellipse2D e = new Ellipse2D.Double( atom.getCM().getX() - atom.getRadius(),
                                            atom.getCM().getY() - atom.getRadius(),
                                            atom.getRadius() * 2,
                                            atom.getRadius() * 2 );
        Point2D[] paUpper = MathUtil.getLineCircleIntersection( e, lUpper );

        Point2D p1Lower = new Point2D.Double( model.getLeftHandPlate().getPosition().getX(),
                                         model.getLeftHandPlate().getPosition().getY() -
                                         Electron.ELECTRON_RADIUS +
                                         ( model.getLeftHandPlate().getEndpoints()[1].getY()
                                           - model.getLeftHandPlate().getEndpoints()[0].getY() ) / 2 );
        Point2D p2Lower = new Point2D.Double( model.getRightHandPlate().getPosition().getX(),
                                         model.getRightHandPlate().getPosition().getY() -
                                         Electron.ELECTRON_RADIUS +
                                         ( model.getRightHandPlate().getEndpoints()[1].getY()
                                           - model.getRightHandPlate().getEndpoints()[0].getY() ) / 2 );
        Line2D lLower = new Line2D.Double( p1Lower, p2Lower );
        Point2D[] paLower = MathUtil.getLineCircleIntersection( e, lLower );

        this.setVisible( false );
        double dUpper = Double.POSITIVE_INFINITY;
        if( paUpper[0] != null && paUpper[1] != null ) {
            this.setVisible( true );
            double d1 = Math.abs( emittingPlate.getPosition().getX() - paUpper[0].getX() );
            double d2 = Math.abs( emittingPlate.getPosition().getX() - paUpper[1].getX() );
            dUpper = Math.min( d1, d2 ) - Electron.ELECTRON_RADIUS;
        }
        double dLower= Double.POSITIVE_INFINITY;
        if( paLower[0] != null && paLower[1] != null ) {
            this.setVisible( true );
            double d1 = Math.abs( emittingPlate.getPosition().getX() - paLower[0].getX() );
            double d2 = Math.abs( emittingPlate.getPosition().getX() - paLower[1].getX() );
            dLower = Math.min( d1, d2 ) - Electron.ELECTRON_RADIUS;
        }
        plateToAtomDist = Math.min( dUpper, dLower );


        // The energy an electron has when it hits the atom
        double electronEnergy = Math.abs( voltage ) * ( plateToAtomDist / plateToPlateDist );

        // Determine the y location of the line. Don't let it go off the top of the panel
        int y = energyYTx.modelToView( ( electronEnergy * DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR )
                                       + model.getAtomicStates()[0].getEnergyLevel() );

        y = Math.max( y, 10 );
        setLocation( 0, y );
        setBoundsDirty();
        repaint();
    }

    //----------------------------------------------------------------
    // Listener inner classes
    //----------------------------------------------------------------

    private class ModelChangeListener extends DischargeLampModel.ChangeListenerAdapter {
        public void voltageChanged( DischargeLampModel.ChangeEvent event ) {
            update();
        }
    }

    private class AtomChangeListener extends Atom.ChangeListenerAdapter {
        public void positionChanged( DischargeLampAtom.ChangeEvent event ) {
            update();
        }
    }
}
