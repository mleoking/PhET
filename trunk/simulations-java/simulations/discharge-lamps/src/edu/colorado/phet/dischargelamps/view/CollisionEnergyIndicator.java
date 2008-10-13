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

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetMultiLineTextGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.quantum.model.Atom;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.DischargeLampsResources;
import edu.colorado.phet.dischargelamps.SingleAtomModule;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.dischargelamps.quantum.model.Electron;
import edu.colorado.phet.dischargelamps.quantum.model.Plate;

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
    private boolean isEnabled = true;

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
        PhetImageGraphic electronGraphic = new PhetImageGraphic( elmp, DischargeLampsResources.getImage( DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME ) );
        electronGraphic.setLocation( (int) line.getBounds().getMaxX() + 5, -3 );
        addGraphic( electronGraphic );

        // Create the text
        PhetMultiLineTextGraphic textGraphic = new PhetMultiLineTextGraphic( elmp,
                                                                             DischargeLampsConfig.DEFAULT_CONTROL_FONT,
                                                                             new String[]{SimStrings.getInstance().getString( "Misc.energyAt" ),
                                                                                     SimStrings.getInstance().getString( "Misc.collision" )},
                                                                             Color.black );
        textGraphic.setLocation( (int) electronGraphic.getBounds().getMaxX() + 5, -8 );
        addGraphic( textGraphic );

        // Attach a listener to the model that moves the graphic when the model-view tranform changes
        model = (DischargeLampModel) module.getModel();
        model.addChangeListener( new ModelChangeListener() );
        atom = (DischargeLampAtom) module.getAtom();
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled( boolean enabled ) {
        isEnabled = enabled;
    }

    /**
     * Determines where the indicator should appear on the panel
     */
    private void update() {
        // Get the distance of the atom from the emitting plate, and the distance
        // between the plates
        double voltage = model.getVoltage();
        double plateToPlateDist = model.getLeftHandPlate().getPosition().distance( model.getRightHandPlate().getPosition() ) - Electron.ELECTRON_RADIUS;
        double plateToAtomDist = 0;
        Plate emittingPlate = null;
        if ( voltage > 0 ) {
            emittingPlate = model.getLeftHandPlate();
        }
        else {
            emittingPlate = model.getRightHandPlate();
        }

        // Get a line traced by the bottom of the electron as it moves from the left plate to the right plate
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
        double r = atom.getBaseRadius();
        Ellipse2D atomShape = new Ellipse2D.Double( atom.getCM().getX() - r,
                                                    atom.getCM().getY() - r,
                                                    r * 2,
                                                    r * 2 );
        // Find the points where the bottom of the electron touches the atom when and if it hits it. Note that
        // a line can intersect a circle at two points. We figure out which is the correct one later.
        Point2D[] paUpper = MathUtil.getLineCircleIntersection( atomShape, lUpper );

        // Get a line traced by the top of the electron as it moves from the left plate to the right plate
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

        // Find the points where the top of the electron touches the atom when and if it hits it. Note that
        // a line can intersect a circle at two points. We figure out which is the correct one later.
        Point2D[] paLower = MathUtil.getLineCircleIntersection( atomShape, lLower );

        this.setVisible( false );
        double dUpper = Double.POSITIVE_INFINITY;
        if ( paUpper[0] != null && paUpper[1] != null ) {
            this.setVisible( true );
            double d1 = Math.abs( emittingPlate.getPosition().getX() - paUpper[0].getX() );
            double d2 = Math.abs( emittingPlate.getPosition().getX() - paUpper[1].getX() );
            dUpper = Math.min( d1, d2 ) - Electron.ELECTRON_RADIUS;
        }
        double dLower = Double.POSITIVE_INFINITY;
        if ( paLower[0] != null && paLower[1] != null ) {
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

        if ( !isEnabled ) {
            setVisible( false );
        }
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
