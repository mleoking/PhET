// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.beerslawlab.common.BLLResources.Images;
import edu.colorado.phet.beerslawlab.common.model.Solvent.Water;
import edu.colorado.phet.beerslawlab.concentration.model.ConcentrationModel;
import edu.colorado.phet.beerslawlab.concentration.model.Solute;
import edu.colorado.phet.beerslawlab.concentration.model.SoluteColorScheme;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.controls.DoubleSpinner;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Editor for color schemes that map concentration to color.
 * This is a developer feature, and does not require i18n.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ColorSchemeEditorDialog extends JDialog {

    private static final String TITLE = "Color Scheme Editor (dev)";

    // Dialog that displays the Color Scheme Editor
    public ColorSchemeEditorDialog( Frame parentFrame, ConcentrationModel model ) {
        super( parentFrame, TITLE );
        setResizable( false );
        setContentPane( new ColorSchemeEditorCanvas( parentFrame, model ) );
        pack();
    }

    // Button for opening the Color Scheme Editor
    public static class ColorSchemeEditorButton extends JButton {

        private JDialog dialog;

        public ColorSchemeEditorButton( final Frame parentFrame, final ConcentrationModel model ) {
            super( "dev", new ImageIcon( Images.COLOR_SCHEME_EDITOR_ICON ));

            // Open the dialog when the button is pressed
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( dialog == null ) {
                        dialog = new ColorSchemeEditorDialog( parentFrame, model );
                        SwingUtils.centerInParent( dialog );
                    }
                    if ( !dialog.isVisible() ) {
                        dialog.setVisible( true );
                    }
                }
            } );
        }
    }

    // UI components for the Color Scheme Editor dialog
    private static class ColorSchemeEditorCanvas extends PhetPCanvas {

        private static final Dimension CANVAS_SIZE = new Dimension( 700, 500 );

        public ColorSchemeEditorCanvas( Frame parentFrame, final ConcentrationModel model ) {
            setBackground( new Color( 240, 240, 240 ) );
            setPreferredSize( CANVAS_SIZE );

            final double xMargin = 20;
            final double ySpacing = 30;

            // header
            PText headerNode = new PText( "    solute           min                mid                  max                     gradient" );
            headerNode.setFont( new PhetFont( Font.BOLD, 14 ) );
            headerNode.setOffset( 30, 10 );
            addChild( headerNode );

            // Solute color schemes
            PNode previousNode = headerNode;
            final ArrayList<Solute> solutes = model.getSolutes();
            for ( Solute solute : solutes ) {
                PNode colorSchemeNode = new SoluteColorSchemeNode( parentFrame, solute );
                addChild( colorSchemeNode );
                // right justified
                colorSchemeNode.setOffset( CANVAS_SIZE.getWidth() - colorSchemeNode.getFullBoundsReference().getWidth() - xMargin,
                                           previousNode.getFullBoundsReference().getMaxY() + ySpacing);
                previousNode = colorSchemeNode;
            }

            // Water color scheme
            final Property<Color> solventColorProperty = model.solution.solvent.color;
            final ColorControlNode waterColorNode = new ColorControlNode( parentFrame, "Water:", solventColorProperty.get() );
            addChild( waterColorNode );
            waterColorNode.setOffset( previousNode.getXOffset(), previousNode.getFullBoundsReference().getMaxY() + ySpacing );
            waterColorNode.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    solventColorProperty.set( waterColorNode.getColor() );
                }
            } );

            // Print button
            TextButtonNode printButtonNode = new TextButtonNode( "Print to Java Console", new PhetFont( 12 ), Color.ORANGE );
            printButtonNode.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    System.out.println( model.solution.solvent.name + "," + solventColorProperty.get() );
                    for ( Solute solute : solutes ) {
                        System.out.println( solute.name + ": " + ColorSchemeEditorDialog.toString( solute.colorScheme.get() ) );
                    }
                }
            } );
            addChild( printButtonNode );
            // lower-right corner
            printButtonNode.setOffset( CANVAS_SIZE.getWidth() - printButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                       CANVAS_SIZE.getHeight() - printButtonNode.getFullBoundsReference().getHeight() - 10 );
        }

        public void addChild( PNode node ) {
            getLayer().addChild( node );
        }
    }

    // Formatted so that the output can be pasted back into the SoluteColorScheme subclasses as constructor args.
    private static String toString( SoluteColorScheme colorScheme ) {
        return colorScheme.minConcentration + ", " + toString( colorScheme.minColor ) + ", " +
               colorScheme.midConcentration + ", " + toString( colorScheme.midColor ) + ", " +
               colorScheme.maxConcentration + ", " + toString( colorScheme.maxColor );
    }

    private static String toString( Color color ) {
        if ( color.equals( Water.COLOR ) ) {
            return "Water.COLOR";
        }
        else {
            return "new Color( " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + " )";
        }
    }

    // Piccolo wrapper for a Swing color control
    private static class ColorControlNode extends PNode {

        private final ColorControl colorControl;

        public ColorControlNode( Frame parentFrame, String label, Color color ) {
            colorControl = new ColorControl( parentFrame, label, color );
            colorControl.setOpaque( false );
            addChild( new PSwing( colorControl ) );
        }

        public Color getColor() {
            return colorControl.getColor();
        }

        public void addChangeListener( ChangeListener listener ) {
            colorControl.addChangeListener( listener );
        }
    }

    // Displays and edits the color scheme for one solute
    private static class SoluteColorSchemeNode extends PNode {

        public SoluteColorSchemeNode( Frame parentFrame, final Solute solute ) {

            // nodes
            HTMLNode soluteNameNode = new HTMLNode( solute.formula + ":", Color.BLACK, new PhetFont( 12 ) );
            final ColorControlNode minColorNode = new ColorControlNode( parentFrame, String.valueOf( solute.colorScheme.get().minConcentration ) + "=", solute.colorScheme.get().minColor );
            final ColorControlNode midColorNode = new ColorControlNode( parentFrame, "=", solute.colorScheme.get().midColor );
            final ColorControlNode maxColorNode = new ColorControlNode( parentFrame, String.valueOf( solute.colorScheme.get().maxConcentration ) + "=", solute.colorScheme.get().maxColor );
            final DoubleSpinner midConcentrationSpinner = new DoubleSpinner( solute.colorScheme.get().midConcentration,
                                                                       solute.colorScheme.get().minConcentration, solute.colorScheme.get().maxConcentration,
                                                                       0.01, "0.00", new Dimension( 60, 20 ));
            PSwing midConcentrationNode = new PSwing( midConcentrationSpinner );
            PText arrowTextNode = new PText( "-->" );
            PNode gradientNode = new GradientNode( solute.colorScheme );

            // rendering order
            {
                addChild( soluteNameNode );
                addChild( minColorNode );
                addChild( midConcentrationNode );
                addChild( midColorNode );
                addChild( maxColorNode );
                addChild( arrowTextNode );
                addChild( gradientNode );
            }

            // layout horizontally
            {
                final double xSpacing = 30;
                minColorNode.setOffset( soluteNameNode.getFullBoundsReference().getMaxX() + xSpacing, soluteNameNode.getYOffset() );
                midConcentrationNode.setOffset( minColorNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                minColorNode.getFullBoundsReference().getCenterY() - ( midConcentrationNode.getFullBoundsReference().getHeight() / 2 ) );
                midColorNode.setOffset( midConcentrationNode.getFullBoundsReference().getMaxX() + 2, soluteNameNode.getYOffset() );
                maxColorNode.setOffset( midColorNode.getFullBoundsReference().getMaxX() + xSpacing, soluteNameNode.getYOffset() );
                arrowTextNode.setOffset( maxColorNode.getFullBoundsReference().getMaxX() + xSpacing, soluteNameNode.getYOffset() );
                gradientNode.setOffset( arrowTextNode.getFullBoundsReference().getMaxX() + xSpacing,
                                        maxColorNode.getFullBoundsReference().getCenterY() - ( gradientNode.getFullBoundsReference().getHeight() / 2 ) );
            }

            // updates a solute's color scheme
            final VoidFunction0 updateColorScheme = new VoidFunction0() {
                public void apply() {
                    SoluteColorScheme oldScheme = solute.colorScheme.get();
                    SoluteColorScheme newScheme = new SoluteColorScheme( oldScheme.minConcentration, minColorNode.getColor(),
                                                                         midConcentrationSpinner.getDoubleValue(), midColorNode.getColor(),
                                                                         oldScheme.maxConcentration, maxColorNode.getColor() );
                    solute.colorScheme.set( newScheme );
                }
            };

            // Observe changes to concentration mid-point
            midConcentrationSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateColorScheme.apply();
                }
            } );

            // Observe color changes
            ChangeListener colorChangeListener = new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateColorScheme.apply();
                }
            };
            minColorNode.addChangeListener( colorChangeListener );
            midColorNode.addChangeListener( colorChangeListener );
            maxColorNode.addChangeListener( colorChangeListener );
        }
    }

    // Shows the gradient produced by a color scheme
    private static class GradientNode extends PComposite {

        private static final Dimension GRADIENT_SIZE = new Dimension( 200, 25 );

        public GradientNode( Property<SoluteColorScheme> colorScheme ) {

            final PPath leftNode = new PPath();
            leftNode.setStroke( null );
            final PPath rightNode = new PPath();
            rightNode.setStroke( null );
            PPath outlineNode = new PPath( new Rectangle2D.Double( 0, 0, GRADIENT_SIZE.width, GRADIENT_SIZE.height ) );

            addChild( leftNode );
            addChild( rightNode );
            addChild( outlineNode );

            // Update the gradient to match the color scheme
            colorScheme.addObserver( new VoidFunction1<SoluteColorScheme>() {
                public void apply( SoluteColorScheme colorScheme ) {
                    // shapes
                    double leftWidth = GRADIENT_SIZE.width * ( colorScheme.midConcentration - colorScheme.minConcentration ) / ( colorScheme.maxConcentration - colorScheme.minConcentration );
                    double rightWidth = GRADIENT_SIZE.width - leftWidth;
                    leftNode.setPathTo( new Rectangle2D.Double( 0, 0, leftWidth, GRADIENT_SIZE.height ) );
                    rightNode.setPathTo( new Rectangle2D.Double( leftWidth, 0, rightWidth, GRADIENT_SIZE.height ) );
                    // paints
                    leftNode.setPaint( new GradientPaint( 0f, 0f, colorScheme.minColor, (float)leftWidth, 0f, colorScheme.midColor ) );
                    rightNode.setPaint( new GradientPaint( (float)leftWidth, 0f, colorScheme.midColor, (float)( leftWidth + rightWidth ), 0f, colorScheme.maxColor ) );
                }
            } );
        }
    }
}
