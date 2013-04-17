// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.ColoredSeparator.BlackSeparator;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Fill;

/**
 * Controls related to the properties of the dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricPropertiesControlPanel extends PhetTitledPanel {

    private final Capacitor capacitor;
    private final SimpleObserver dielectricConstantObserver;
    private final DielectricMaterialControl materialControl;
    private final DielectricConstantControl constantControl;

    private DielectricMaterial material;

    public DielectricPropertiesControlPanel( final Capacitor capacitor, DielectricMaterial[] dielectricMaterials, Property<DielectricChargeView> dielectricChargeView ) {
        super( CLStrings.DIELECTRIC );

        this.capacitor = capacitor;

        this.material = capacitor.getDielectricMaterial();
        dielectricConstantObserver = new SimpleObserver() {
            public void update() {
                handleDielectricConstantChanged();
            }
        };

        materialControl = new DielectricMaterialControl( dielectricMaterials, capacitor.getDielectricMaterial() );
        materialControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                capacitor.setDielectricMaterial( materialControl.getMaterial() );
            }
        } );

        constantControl = new DielectricConstantControl( CLConstants.DIELECTRIC_CONSTANT_RANGE.getDefault() );

        /*
         * The dielectric constant control serves as both a control (for custom dielectrics)
         * and a display (for all dielectrics, including "real world" dielectrics whose constant are immutable.)
         * If the dielectric constant control is changed when we have a custom dielectric, we must update the model.
         * Otherwise the control was changed to display the constant of an immutable dielectric, and we do nothing.
         */
        constantControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( capacitor.getDielectricMaterial() instanceof CustomDielectricMaterial ) {
                    ( (CustomDielectricMaterial) capacitor.getDielectricMaterial() ).setDielectricConstant( constantControl.getValue() );
                }
            }
        } );

        DielectricChargesControl chargesControl = new DielectricChargesControl( dielectricChargeView );

        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setInsets( new Insets( 0, 5, 5, 0 ) );
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setFill( Fill.HORIZONTAL );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( materialControl );
        innerPanel.add( new BlackSeparator() );
        innerPanel.add( constantControl );
        innerPanel.add( new BlackSeparator() );
        innerPanel.add( chargesControl );

        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );

        // observe capacitor
        capacitor.addDielectricMaterialObserver( new SimpleObserver() {
            public void update() {
                handleDielectricMaterialChanged();
            }
        } );

        // observe dielectric
        material.addDielectricConstantObserver( dielectricConstantObserver );
    }

    private void handleDielectricMaterialChanged() {

        DielectricMaterial material = capacitor.getDielectricMaterial();

        // update controls
        materialControl.setMaterial( material );
        constantControl.setEnabled( material instanceof CustomDielectricMaterial );
        constantControl.setValue( material.getDielectricConstant() );

        // rewire dielectric constant observer
        this.material.removeDielectricConstantObserver( dielectricConstantObserver );
        this.material = material;
        this.material.addDielectricConstantObserver( dielectricConstantObserver );
    }

    private void handleDielectricConstantChanged() {
        constantControl.setValue( capacitor.getDielectricMaterial().getDielectricConstant() );
    }
}
