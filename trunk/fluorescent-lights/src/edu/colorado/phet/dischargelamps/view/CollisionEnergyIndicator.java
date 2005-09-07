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

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.SingleAtomModule;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.dischargelamps.model.Plate;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.GroundState;

import java.awt.*;
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

    private Atom atom;
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
        Arrow arrow = new Arrow( new Point2D.Double( 110, 0), new Point2D.Double( 25, 0), 8, 8, 1 );
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( elmp, arrow.getShape(), Color.red );
        addGraphic( arrowGraphic );

        // Create the electron graphic
        PhetImageGraphic electronGraphic = new PhetImageGraphic( elmp, DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME );
        electronGraphic.setLocation( (int)line.getBounds().getMaxX() + 5, -3 );
        addGraphic( electronGraphic );

        // Create the text
        PhetMultiLineTextGraphic textGraphic = new PhetMultiLineTextGraphic( elmp,
                                                                             DischargeLampsConfig.DEFAULT_CONTROL_FONT,
                                                                             new String[]{"Energy at", "collision"},
                                                                             Color.black );
        textGraphic.setLocation( (int)electronGraphic.getBounds().getMaxX() + 5, -8 );
        addGraphic( textGraphic );

        // Attach a listener to the model that moves the graphic when the model-view tranform changes
        model = (DischargeLampModel)module.getModel();
        model.addChangeListener( new ModelChangeListener() );
        atom = module.getAtom();
        atom.addChangeListener( new AtomChangeListener() );
        this.energyYTx = elmp.getEnergyYTx();
        energyYTx.addListener( new ModelViewTransform1D.Observer() {
            public void transformChanged( ModelViewTransform1D transform ) {
                update();
            }
        } );

        update();
    }

    private void update() {
        // Get the distance of the atom from the emitting plate, and the distance
        // between the plates
        double voltage = model.getVoltage();
        double plateToPlateDist = model.getLeftHandPlate().getPosition().distance( model.getRightHandPlate().getPosition() );
        double plateToAtomDist = 0;
        Plate emittingPlate = null;
        if( voltage > 0 ) {
            emittingPlate = model.getLeftHandPlate();
        }
        else {
            emittingPlate = model.getRightHandPlate();
        }
        plateToAtomDist = emittingPlate.getPosition().distance( atom.getPosition() ) - atom.getRadius();

        // The energy an electron has when it hits the atom
        double electronEnergy = Math.abs( voltage ) * ( plateToAtomDist / plateToPlateDist );

        // This factor converts
        double fudge = 5.67;
        new GroundState().getEnergyLevel();
        int y = energyYTx.modelToView( ( electronEnergy * fudge ) + model.getAtomicStates()[0].getEnergyLevel() );
        setLocation( 0, y );
//        setLocation( 0, Math.max( y, 5 ) );
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
