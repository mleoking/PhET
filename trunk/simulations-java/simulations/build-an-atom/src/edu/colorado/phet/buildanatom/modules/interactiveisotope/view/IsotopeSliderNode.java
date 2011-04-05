/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MovableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.view.IsotopeMixturesCanvas;
import edu.colorado.phet.buildanatom.modules.isotopemixture.view.LabeledIsotopeNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.DefaultLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
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

    private static final Dimension2D SIZE = new PDimension( 120, 75 );

    public IsotopeSliderNode( final IsotopeMixturesModel.NumericalIsotopeQuantityControl modelControl, ModelViewTransform mvt ){
        PNode rootNode = new PNode();
        rootNode.setOffset( mvt.modelToView( modelControl.getCenterPositionRef() ) );
        addChild( rootNode );

        // Add the slider that controls the quantity of this isotope in the
        // test chamber.
        String label = modelControl.getIsotopeConfig().getName() + "-" + modelControl.getIsotopeConfig().getMassNumber();
        final LinearValueControl isotopeQuantityControl = new LinearValueControl( 0, modelControl.getCapacity(), label, "###", null, new DefaultLayoutStrategy(SwingConstants.CENTER)){{
                    setUpDownArrowDelta( 1 );
                    setMajorTicksVisible( false );
                    setTextFieldEditable( true );
                    setFont( new PhetFont( Font.PLAIN, 14 ) );
                    setSliderWidth( (int)SIZE.getWidth() );
                    setValue( 0 );
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            modelControl.setIsotopeQuantity( (int)Math.round( getValue() ) );
                        }
                    });
                    SwingUtils.setBackgroundDeep( this, IsotopeMixturesCanvas.BACKGROUND_COLOR );
                    getTextField().setBackground( Color.WHITE );
                    PNode isotopeNode = new LabeledIsotopeNode(ModelViewTransform.createIdentity(), new MovableAtom(
                            modelControl.getIsotopeConfig().getNumProtons(), modelControl.getIsotopeConfig().getNumNeutrons(),
                            10, new Point2D.Double(0, 0) ), modelControl.getBaseColor() );
                    setValueLabelIcon( new ImageIcon( isotopeNode.toImage() ) );
        }};


        // Wrap the control in a PSwing and add it to our root node.
        PNode linearSliderNode = new PSwing( isotopeQuantityControl ){{
            centerFullBoundsOnPoint( 0, 0 );
        }};
        rootNode.addChild( linearSliderNode );

        // Observe the model control for changes and update the slider accordingly.
        modelControl.addQuantityPropertyObserver( new SimpleObserver() {
            public void update() {
                if ( modelControl.getQuantity() != (int)isotopeQuantityControl.getValue() ){
                    isotopeQuantityControl.setValue( modelControl.getQuantity() );
                }
            }
        });
    }
}
