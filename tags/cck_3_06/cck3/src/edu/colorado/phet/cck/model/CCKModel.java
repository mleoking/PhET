/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.cck.model;

import edu.colorado.phet.cck.model.analysis.CircuitAnalysisCCKAdapter;
import edu.colorado.phet.cck.model.analysis.CircuitSolver;
import edu.colorado.phet.cck.model.analysis.MNASolver;
import edu.colorado.phet.cck.model.components.Battery;
import edu.colorado.phet.cck.model.components.Branch;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * CCKModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CCKModel {
    private Circuit circuit;
    private CircuitSolver circuitSolver;
    private ResistivityManager resistivityManager;
    private boolean internalResistanceOn = false;

    private ParticleSet particleSet;
    private ConstantDensityLayout layout;

    private boolean modelChanged = false;
    private CircuitChangeListener circuitChangeListener;

    /**
     * Scale and size information.
     */
    private double aspectRatio = 1.2;
    private static final double SCALE = .5;
    private double modelWidth = 10;
    private double modelHeight = modelWidth / aspectRatio;
    //    private static final Rectangle2D.Double INIT_MODEL_BOUNDS = new Rectangle2D.Double( 0, 0, modelWidth, modelHeight );
    private Rectangle2D.Double modelBounds = new Rectangle2D.Double( 0, 0, modelWidth, modelHeight );
    public static double ELECTRON_DX = .56 * SCALE;
    private static final double switchscale = 1.45;
    public static final ComponentDimension RESISTOR_DIMENSION = new ComponentDimension( 1.3 * SCALE, .6 * SCALE );
    public static final ComponentDimension CAP_DIM = new ComponentDimension( 1.8 * SCALE, .6 * SCALE );
    public static final ComponentDimension AC_DIM = new ComponentDimension( 1.3 * SCALE, .6 * SCALE );
    public static final ComponentDimension SWITCH_DIMENSION = new ComponentDimension( 1.5 * SCALE * switchscale, 0.8 * SCALE * switchscale );
    public static final ComponentDimension LEVER_DIMENSION = new ComponentDimension( 1.0 * SCALE * switchscale, 0.5 * SCALE * switchscale );
    public static final ComponentDimension BATTERY_DIMENSION = new ComponentDimension( 1.9 * SCALE, 0.7 * SCALE );
    public static final ComponentDimension SERIES_AMMETER_DIMENSION = new ComponentDimension( 2.33 * SCALE, .92 * SCALE );
    public static final ComponentDimension INDUCTOR_DIM = new ComponentDimension( 2.5 * SCALE, 0.6 * SCALE );
    private static double bulbLength = 1;
    private static double bulbHeight = 1.5;
    private static double bulbDistJ = .39333;
    private static double bulbScale = 1.9;
    public static final BulbDimension BULB_DIMENSION = new BulbDimension( bulbLength * SCALE * bulbScale, bulbHeight * SCALE * bulbScale, bulbDistJ * SCALE * bulbScale );
    public static final double WIRE_LENGTH = BATTERY_DIMENSION.getLength() * 1.2;
    public static final double JUNCTION_GRAPHIC_STROKE_WIDTH = .015;
    public static final double JUNCTION_RADIUS = .162;

    public static final double MIN_RESISTANCE = 1E-8;
    public static final double SCH_BULB_DIST = 1;

    public CCKModel() {
        // Create the circuitSolver and the listener that will invoke it

        circuitChangeListener = new CircuitChangeListener() {
            public void circuitChanged() {
                modelChanged = true;//defers computation to stepInTime()
            }
        };
        this.circuit = createCircuit( circuitChangeListener );
        circuitSolver = new CircuitAnalysisCCKAdapter( new MNASolver() );
        particleSet = new ParticleSet( getCircuit() );
        layout = new ConstantDensityLayout( getCircuit(), particleSet );
        getCircuit().addCircuitListener( layout );

//        CurrentDirectionManager directionManager = new CurrentDirectionManager( circuit );
//        getCircuit().addCircuitListener( directionManager );

        this.resistivityManager = new ResistivityManager( getCircuit() );
        getCircuit().addCircuitListener( resistivityManager );
    }

    protected Circuit createCircuit( CircuitChangeListener circuitChangeListener ) {
        return new Circuit( circuitChangeListener );
    }


    public void stepInTime( double dt ) {
//        dt = 1.0;
        //todo we can no longer have DT dynamic because it destroys smoothness of the plots
        if( getCircuit().isDynamic() || modelChanged ) {
            getCircuit().stepInTime( dt );
            circuitSolver.apply( getCircuit() );
            modelChanged = false;
        }
        particleSet.stepInTime( dt );
    }

    public Circuit getCircuit() {
        return circuit;
    }
//
//    private boolean isRunning() {
//        return true;
//    }

    public double getScale() {
        return SCALE;
    }

    public void layoutElectrons( Branch[] branches ) {
        layout.layoutElectrons( branches );
    }

    public CircuitSolver getCircuitSolver() {
        return circuitSolver;
    }

    public ParticleSet getParticleSet() {
        return particleSet;
    }

    public ConstantDensityLayout getElectronLayout() {
        return layout;
    }

    public Rectangle2D.Double getModelBounds() {
        return modelBounds;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public double getModelWidth() {
        return modelWidth;
    }

    public double getModelHeight() {
        return modelHeight;
    }

    public CircuitChangeListener getCircuitChangeListener() {
        return circuitChangeListener;
    }

    public void setResistivityEnabled( boolean selected ) {

//        System.out.println( "Set resistivity enabled= " + selected );
        if( selected == resistivityManager.isEnabled() ) {
            return;
        }
        else {
            resistivityManager.setEnabled( selected );
            if( !selected ) {
                setWireResistance( MIN_RESISTANCE );
            }
        }
    }

    private void setWireResistance( double defaultResistance ) {
        for( int i = 0; i < getCircuit().numBranches(); i++ ) {
            Branch br = getCircuit().branchAt( i );
            if( br.getClass().equals( Branch.class ) ) {
                br.setResistance( defaultResistance );
            }
        }
    }

    public ResistivityManager getResistivityManager() {
        return resistivityManager;
    }

    public boolean isInternalResistanceOn() {
        return internalResistanceOn;
    }

    public void setInternalResistanceOn( boolean selected ) {
        if( this.internalResistanceOn != selected ) {
            this.internalResistanceOn = selected;
        }

        Branch[] b = getCircuit().getBranches();
        for( int i = 0; i < b.length; i++ ) {
            Branch branch = b[i];
            if( branch instanceof Battery ) {
                Battery batt = (Battery)branch;
                batt.setInternalResistanceOn( selected );
            }
        }
    }

    public void resetDynamics() {
        getCircuit().resetDynamics();
    }

    public void clear() {
        getCircuit().clear();
        getCircuit().clearSelection();
        getParticleSet().clear();
    }

    public Junction[] split( Junction junction ) {
        Junction[] newJunctions = circuit.split( junction );
        ArrayList branches = new ArrayList();
        for( int i = 0; i < newJunctions.length; i++ ) {
            Junction j = newJunctions[i];
            branches.addAll( Arrays.asList( circuit.getAdjacentBranches( j ) ) );
        }
//        Branch[] bo = (Branch[])branches.toArray( new Branch[0] );
//        layoutElectrons( bo );
        layoutElectrons( getCircuit().getBranches() );//todo should we relayout all electrons, or just adjacent
        return newJunctions;
    }

    public void setCircuit( Circuit circuit ) {
        this.circuit.setState( circuit );
        layoutElectrons();
    }

    private void layoutElectrons() {
        layoutElectrons( circuit.getBranches() );
    }

    public void deleteSelectedBranches() {
        circuit.deleteSelectedBranches();
    }

    public void selectAll() {
        circuit.selectAll();
    }

    public void desolderSelectedJunctions() {
        circuit.desolderSelectedJunctions();
    }

    public void clearSelection() {
        circuit.clearSelection();
    }
}
