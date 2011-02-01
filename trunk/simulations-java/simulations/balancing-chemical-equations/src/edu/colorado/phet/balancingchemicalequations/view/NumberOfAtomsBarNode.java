// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.balancingchemicalequations.model.Atom;
import edu.colorado.phet.balancingchemicalequations.model.Atom.N;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * A bar that displays some number of atoms for a specified atom.
 * The bar is capable of displaying some maximum number of atoms.
 * If the number of atoms exceeds that maximum, then an upward-pointing
 * arrow appears at the top of the bar.
 * <p>
 * Origin is at the bottom center of the bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NumberOfAtomsBarNode extends PComposite {

    private static final int MAX_NUMBER_OF_ATOMS = 16;
    private static final PDimension MAX_BAR_SIZE = new PDimension( 20, 70 );
    private static final PDimension ARROW_SIZE = new PDimension( 1.5 * MAX_BAR_SIZE.getWidth(), 10 );
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;

    private final Atom atom;
    private int numberOfAtoms;

    public NumberOfAtomsBarNode( Atom atom ) {
        this.atom = atom;
        this.numberOfAtoms = 0;
        update();
    }

    public Atom getAtom() {
        return atom;
    }

    public void setNumberOfAtoms( int numberOfAtoms ) {
        if ( numberOfAtoms < 0 ) {
            throw new IllegalArgumentException( "numberOfAtoms must be >= 0: " + numberOfAtoms );
        }
        if ( numberOfAtoms != this.numberOfAtoms ) {
            this.numberOfAtoms = numberOfAtoms;
            update();
        }
    }

    // update geometry to match numberOfAtoms
    private void update() {

        // the symbol doesn't change, but it's easier to just start over
        removeAllChildren();

        // bar
        PPath barNode = new PPath();
        addChild( barNode );
        barNode.setStroke( STROKE );
        barNode.setStrokePaint( STROKE_COLOR );
        barNode.setPaint( atom.getColor() );
        if ( numberOfAtoms <= MAX_NUMBER_OF_ATOMS ) {
            // standard bar
            double height = MAX_BAR_SIZE.getHeight() * ( (double) numberOfAtoms / MAX_NUMBER_OF_ATOMS );
            if ( height == 0 ) {
                height = 0.001 * MAX_BAR_SIZE.getHeight();
            }
            barNode.setPathTo( new Rectangle2D.Double( -MAX_BAR_SIZE.getWidth() / 2, -height, MAX_BAR_SIZE.getWidth(), height ) );
        }
        else {
            // bar with upward-pointing arrow, path is specified clockwise from arrow tip.
            GeneralPath arrowPath = new GeneralPath();
            arrowPath.moveTo( 0f, (float) -( MAX_BAR_SIZE.getHeight() + ARROW_SIZE.getHeight() ) );
            arrowPath.lineTo( (float) ARROW_SIZE.getWidth() / 2, (float) -MAX_BAR_SIZE.getHeight() );
            arrowPath.lineTo( (float) MAX_BAR_SIZE.getWidth() / 2, (float) -MAX_BAR_SIZE.getHeight() );
            arrowPath.lineTo( (float) MAX_BAR_SIZE.getWidth() / 2, 0f );
            arrowPath.lineTo( (float) -MAX_BAR_SIZE.getWidth() / 2, 0f );
            arrowPath.lineTo( (float) -MAX_BAR_SIZE.getWidth() / 2, (float) -MAX_BAR_SIZE.getHeight() );
            arrowPath.lineTo( (float) -ARROW_SIZE.getWidth() / 2, (float) -MAX_BAR_SIZE.getHeight() );
            arrowPath.closePath();
            barNode.setPathTo( arrowPath );
        }

        // symbol
        HTMLNode symbolNode = new HTMLNode( atom.getSymbol() );
        symbolNode.setFont( new PhetFont() );
        addChild( symbolNode );

        // number
        PText numberNode = new PText( String.valueOf( numberOfAtoms ) );
        numberNode.setFont( new PhetFont() );
        addChild( numberNode );

        // layout
        {
            // bar at origin
            double x = 0;
            double y = 0;
            barNode.setOffset( x, y );

            // symbol centered below bar
            x = barNode.getFullBoundsReference().getCenterX() - ( symbolNode.getFullBoundsReference().getWidth() / 2 );
            y = barNode.getFullBoundsReference().getMaxY() + 2;
            symbolNode.setOffset( x, y );

            // number about bar
            x = barNode.getFullBoundsReference().getCenterX() - ( numberNode.getFullBoundsReference().getWidth() / 2 );
            y = barNode.getFullBoundsReference().getMinY() - 2 - ( numberNode.getFullBoundsReference().getHeight() );
            numberNode.setOffset( x, y );
        }
    }

    // Test cases
    public static void main( String[] args ) {

        final int numberOfAtoms = 0;

        // bar
        final NumberOfAtomsBarNode barNode = new NumberOfAtomsBarNode( new N() );
        barNode.setNumberOfAtoms( numberOfAtoms );
        barNode.setOffset( 100, 200 );

        // control
        final JSlider slider = new JSlider( 0, 30, numberOfAtoms );
        slider.setOrientation( JSlider.VERTICAL );
        slider.setMajorTickSpacing( 10 );
        slider.setMinorTickSpacing( 1 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                barNode.setNumberOfAtoms( slider.getValue() );
            }
        } );
        PSwing sliderNode = new PSwing( slider );
        sliderNode.setOffset( 200, 50 );

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 350, 400 ) );
        canvas.getLayer().addChild( barNode );
        canvas.getLayer().addChild( sliderNode );

        // frame
        JFrame frame = new JFrame( NumberOfAtomsBarNode.class.getName() );
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
