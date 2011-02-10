// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PComboBox;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class MediumControlPanel extends PNode {

    public MediumControlPanel( final PhetPCanvas phetPCanvas, final Property<Medium> medium, final Property<Function1<Double, Color>> colorMappingFunction ) {
        final double inset = 12;
        final double initialIndexOfRefraction = medium.getValue().getIndexOfRefraction();
        final PNode content = new PNode() {{
            final PhetFont labelFont = new PhetFont( 16 );
            final PText materialLabel = new PText( "Material:" ) {{
                setFont( labelFont );
            }};
            addChild( materialLabel );
            final PComboBox[] x = new PComboBox[1];
            final Object[] mediumStates = new Object[] {
                    LRRModel.AIR,
                    LRRModel.WATER,
                    LRRModel.GLASS,
                    LRRModel.MYSTERY_A,
                    LRRModel.MYSTERY_B,
                    new LRRModel.MediumState( "Custom", LRRModel.MYSTERY_B.index + 1.2 ) {
                        public boolean isCustom() {
                            return true;
                        }
                    },
            };
            final PSwing comboBoxPSwing = new PSwing( x[0] = new PComboBox( mediumStates ) {
                {
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            LRRModel.MediumState selected = (LRRModel.MediumState) getSelectedItem();
                            if ( !selected.isCustom() ) {
                                setIndexOfRefraction( selected.index, medium, colorMappingFunction );
                            }
                        }
                    } );
                    updateComboBox();
                    medium.addObserver( new SimpleObserver() {
                        public void update() {
                            updateComboBox();
                        }
                    } );
                    setFont( labelFont );
                    setIndexOfRefraction( initialIndexOfRefraction, medium, colorMappingFunction );
                }

                private void updateComboBox() {
                    int selected = -1;
                    for ( int i = 0; i < mediumStates.length; i++ ) {
                        LRRModel.MediumState mediumState = (LRRModel.MediumState) mediumStates[i];
                        if ( mediumState.index == medium.getValue().getIndexOfRefraction() ) {
                            selected = i;
                        }
                    }
                    if ( selected != -1 ) {
                        setSelectedIndex( selected );
                    }
                    else {
                        setSelectedIndex( 5 );
                    }
                }
            } ) {{
                x[0].setEnvironment( this, phetPCanvas );
                setOffset( materialLabel.getFullBounds().getMaxX() + 10, materialLabel.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 + 1 );
            }};
            addChild( comboBoxPSwing );

            final PSwing slider = new PSwing( new IndexOfRefractionSlider( medium, colorMappingFunction, "" ) {{
                SwingUtils.setBackgroundDeep( this, new Color( 0, 0, 0, 0 ) );
                getTextField().setBackground( Color.white );
                getTextField().setFont( labelFont );
            }} ) {{
                setOffset( 0, materialLabel.getFullBounds().getMaxY() );
            }};
            addChild( slider );

            final PText indexOfRefractionLabel = new PText( "Index of Refraction (n)" ) {{
                setFont( labelFont );
                setOffset( 0, slider.getFullBounds().getMaxY() );
            }};
            addChild( indexOfRefractionLabel );
            setOffset( inset, inset );
        }};

        final PhetPPath background = new PhetPPath( Color.lightGray, new BasicStroke( 2 ), Color.darkGray ) {{
            final PropertyChangeListener updateSize = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    final PBounds b = content.getFullBounds();
                    setPathTo( new RoundRectangle2D.Double( 0, 0, b.getWidth() + inset * 2, b.getHeight() + inset * 2, 20, 20 ) );
                }
            };
            content.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updateSize );
            updateSize.propertyChange( null );
        }};
        addChild( background );
        addChild( content );
    }

    private void setIndexOfRefraction( double indexOfRefraction, Property<Medium> medium, Property<Function1<Double, Color>> colorMappingFunction ) {
        medium.setValue( new Medium( medium.getValue().getShape(), indexOfRefraction, colorMappingFunction.getValue().apply( indexOfRefraction ) ) );
    }

    public static void main( String[] args ) throws ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException, InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
                }
                catch ( ClassNotFoundException e ) {
                    e.printStackTrace();
                }
                catch ( InstantiationException e ) {
                    e.printStackTrace();
                }
                catch ( IllegalAccessException e ) {
                    e.printStackTrace();
                }
                catch ( UnsupportedLookAndFeelException e ) {
                    e.printStackTrace();
                }
                new JFrame() {{
                    setContentPane( new PhetPCanvas() {{
                        getLayer().addChild( new MediumControlPanel( this, null, null ) {{
                            System.out.println( "getFullBounds() = " + getFullBounds() );
//                            setOffset( 100, 100 );
//                            scale( 2 );
                        }} );
                    }} );
                    setSize( 800, 600 );
                    setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }
}