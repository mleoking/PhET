// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.ion.Chlorine;
import edu.colorado.phet.solublesalts.model.ion.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.ISugarAndSaltModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.MicroModule.SugarMoleculeMinus;
import edu.colorado.phet.sugarandsaltsolutions.micro.MicroModule.SugarMoleculePlus;

/**
 * @author Sam Reid
 */
public class MicroModel implements ISugarAndSaltModel {
    //Model for the concentration in SI (moles/m^3)
    public final DoubleProperty sugarConcentration = new DoubleProperty( 0.0 );
    public final DoubleProperty saltConcentration = new DoubleProperty( 0.0 );
    public final Property<Boolean> showConcentrationValues = new Property<Boolean>( false );

    // Use NaCl by default
    public final Property<DispenserType> dispenserType = new Property<DispenserType>( DispenserType.SALT );
    public final Property<Boolean> showConcentrationBarChart = new Property<Boolean>( true );
    private final SolubleSaltsModel solubleSaltsModel;

    public MicroModel( SolubleSaltsModel solubleSaltsModel ) {
        this.solubleSaltsModel = solubleSaltsModel;
    }

    public void reset() {
        sugarConcentration.reset();
        saltConcentration.reset();
        showConcentrationValues.reset();
        dispenserType.reset();
        showConcentrationBarChart.reset();
    }

    public DoubleProperty getSaltMoles() {
        return saltConcentration;
    }

    public DoubleProperty getSugarMoles() {
        return sugarConcentration;
    }

    public void removeSalt() {
        for ( Object modelElement : new ArrayList() {{
            addAll( solubleSaltsModel.getIonsOfType( Sodium.class ) );
            addAll( solubleSaltsModel.getIonsOfType( Chlorine.class ) );
        }} ) {
            solubleSaltsModel.removeModelElement( (ModelElement) modelElement );
        }
    }

    public void removeSugar() {
        for ( Object modelElement : new ArrayList() {{
            addAll( solubleSaltsModel.getIonsOfType( SugarMoleculeMinus.class ) );
            addAll( solubleSaltsModel.getIonsOfType( SugarMoleculePlus.class ) );
        }} ) {
            solubleSaltsModel.removeModelElement( (ModelElement) modelElement );
        }
    }
}
