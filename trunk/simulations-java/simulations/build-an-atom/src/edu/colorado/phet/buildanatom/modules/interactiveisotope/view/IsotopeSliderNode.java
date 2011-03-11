/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.buildanatom.modules.isotopemixture.view.IsotopeMixturesCanvas;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * PNode that displays a slider, a numerical value, and a "key" isotope, and
 * that connects with a model element to allow users to add or remove isotopes
 * from the model.
 *
 * @author John Blanco
 */
public class IsotopeSliderNode extends PNode {

    private static final Dimension2D SIZE = new PDimension( 170, 75 );

    public IsotopeSliderNode( final IsotopeMixturesModel.LinearAddRemoveIsotopesControl modelControl, ModelViewTransform mvt ){
        PNode enclosure = new PhetPPath(
                new Rectangle2D.Double( -SIZE.getWidth() / 2, -SIZE.getHeight() / 2, SIZE.getWidth(), SIZE.getHeight() ),
                Color.WHITE,
                new BasicStroke( 2 ),
                Color.BLACK);
        enclosure.setOffset( mvt.modelToView( modelControl.getCenterPositionRef() ) );
        addChild( enclosure );

        // Add the slider that controls the quantity of this isotope.
        LinearValueControl isotopeQuantityControl = null;
        isotopeQuantityControl = new LinearValueControl(
                0, 100, modelControl.getAtomConfig().getName() + "-" + modelControl.getAtomConfig().getMassNumber(),
                "###", null){{
                    setUpDownArrowDelta( 1 );
                    setTextFieldEditable( true );
                    setFont( new PhetFont( Font.PLAIN, 14 ) );
                    setTickPattern( "0" );
                    setMajorTickSpacing( 25 );
                    setMinorTickSpacing( 12.5 );
                    setValue( 0 );
                    setBackground( IsotopeMixturesCanvas.BACKGROUND_COLOR );
                    getSlider().setBackground( IsotopeMixturesCanvas.BACKGROUND_COLOR );
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            modelControl.setIsotopeQuantity( (int)Math.round( getValue() ) );
                        }
                    });
                }};

        PNode linearSliderNode = new PSwing( isotopeQuantityControl ){{
            centerFullBoundsOnPoint( 0, 0 );
        }};
        enclosure.addChild( linearSliderNode );

        /*
        PNode labeledU235Nucleus = new LabeledNucleusImageNode("uranium-nucleus-small.png",
                NuclearPhysicsStrings.URANIUM_235_ISOTOPE_NUMBER,
                NuclearPhysicsStrings.URANIUM_235_CHEMICAL_SYMBOL,
                NuclearPhysicsConstants.URANIUM_235_LABEL_COLOR );
        Icon icon = new ImageIcon( labeledU235Nucleus.toImage(25, 25, null) );
        _u235AmountControl.setValueLabelIcon( icon );
        */


    }
}
