/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.Allele;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.colorado.phet.naturalselection.model.TeethGene;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PAffineTransform;

/**
 * Piccolo node that displays a bunny with various traits (or a red X if dead)
 *
 * @author Jonathan Olson
 */
public class DisplayBunnyNode extends PNode {

    // images
    private PImage whiteImage;
    private PImage brownImage;

    // red X
    private PPath deadX;

    // temporary teeth node
    private PPath tempTeeth;

    // alleles
    private Allele color;
    private Allele teeth;
    private Allele tail;

    // whether dead or alive
    private boolean dead;

    // whether we are flipped visually left-to-right
    private boolean flipped;

    /**
     * Constructor
     *
     * @param color Color allele
     * @param teeth Teeth allele
     * @param tail  Tail allele
     */
    public DisplayBunnyNode( Allele color, Allele teeth, Allele tail ) {
        whiteImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_DISPLAY_BUNNY_WHITE );
        brownImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_DISPLAY_BUNNY_BROWN );
        initDeadX();
        initTempTeeth();

        // add children
        addChild( tempTeeth );
        addChild( whiteImage );
        addChild( brownImage );
        addChild( deadX );

        // set up the bunny
        setColor( color );
        setTeeth( teeth );
        setTail( tail );
        setDead( false );
    }

    private void initTempTeeth() {
        //tempTeeth = new PPath();
        tempTeeth = PPath.createEllipse( 140, 100, 70, 70 );

        //tempTeeth.setStroke( new BasicStroke( 20 ) );
        //tempTeeth.setStrokePaint( Color.RED );
        tempTeeth.setPaint( new Color( 0xFF, 0x00, 0x00, 0x66 ) );
    }

    /**
     * Draw the red X
     */
    private void initDeadX() {
        deadX = new PPath();

        deadX.setStroke( new BasicStroke( 20 ) );
        deadX.setStrokePaint( Color.RED );

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( getBunnyWidth(), getBunnyHeight() );
        path.moveTo( getBunnyWidth(), 0 );
        path.lineTo( 0, getBunnyHeight() );
        deadX.setPathTo( path.getGeneralPath() );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped( boolean flipped ) {
        if ( flipped != this.flipped ) {
            if ( flipped ) {
                //setScale( -1.0 );
                //PAffineTransform trans = getTransform();
                setTransform( new PAffineTransform( -1, 0, 0, 1, getBunnyWidth(), 0 ) );
                //setOffset( new Point2D.Double( getBunnyWidth(), 0 ) );
            }
            else {
                setTransform( new PAffineTransform( 1, 0, 0, 1, 0, 0 ) );
                //setScale( 1.0 );
                //setOffset( new Point2D.Double( 0, 0 ) );
            }
        }
        this.flipped = flipped;
    }

    public void setDead( boolean dead ) {
        this.dead = dead;

        deadX.setVisible( dead );
    }

    public boolean isDead() {
        return dead;
    }

    public void setColor( Allele color ) {
        this.color = color;

        if ( color == ColorGene.WHITE_ALLELE ) {
            whiteImage.setVisible( true );
            brownImage.setVisible( false );
        }
        else if ( color == ColorGene.BROWN_ALLELE ) {
            whiteImage.setVisible( false );
            brownImage.setVisible( true );
        }
    }

    public void setTeeth( Allele teeth ) {
        this.teeth = teeth;

        if ( teeth == TeethGene.TEETH_REGULAR_ALLELE ) {
            tempTeeth.setVisible( false );
        }
        else if ( teeth == TeethGene.TEETH_HUGE_ALLELE ) {
            tempTeeth.setVisible( true );
        }
    }

    public void setTail( Allele tail ) {
        this.tail = tail;
    }


    public Allele getColor() {
        return color;
    }

    public Allele getTeeth() {
        return teeth;
    }

    public Allele getTail() {
        return tail;
    }

    public double getBunnyWidth() {
        return whiteImage.getWidth();
    }

    public double getBunnyHeight() {
        return whiteImage.getHeight();
    }

}
