package edu.colorado.phet.naturalselection.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.Allele;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.colorado.phet.naturalselection.model.TailGene;
import edu.colorado.phet.naturalselection.model.TeethGene;
import edu.colorado.phet.naturalselection.util.HighContrastImageFilter;
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
    private PImage image;
    private PImage mutatedImage;

    // add ons
    private PPath deadX;
    private PPath selectedBorder;

    // alleles
    private Allele color;
    private Allele teeth;
    private Allele tail;

    // whether dead or alive
    private boolean dead = false;

    // whether it is selected in the pedigree chart
    private boolean selected = false;

    // whether we are flipped visually left-to-right
    private boolean flipped = false;


    /**
     * Constructor
     *
     * @param color Color allele
     * @param teeth Teeth allele
     * @param tail  Tail allele
     */
    public DisplayBunnyNode( Allele color, Allele teeth, Allele tail ) {
        this.color = color;
        this.teeth = teeth;
        this.tail = tail;

        // we form the image name programmatically, just 'cause. 

        String imageName = "bunny";

        if ( color == ColorGene.WHITE_ALLELE ) {
            imageName += "_white";
        }
        else {
            imageName += "_brown";
        }

        if ( tail == TailGene.TAIL_LONG_ALLELE ) {
            imageName += "_big_tail";
        }

        if ( teeth == TeethGene.TEETH_LONG_ALLELE ) {
            imageName += "_long_teeth";
        }

        imageName += ".png";

        image = NaturalSelectionResources.getImageNode( imageName );

        if ( NaturalSelectionApplication.isHighContrast() ) {
            if ( color == ColorGene.WHITE_ALLELE ) {
                image = HighContrastImageFilter.getWhiteBunny().getPImage( imageName );
            }
            else {
                image = HighContrastImageFilter.getBrownBunny().getPImage( imageName );
            }
        }

        addChild( image );

        initDeadX();
        addChild( deadX );

        initSelectedBorder();
        addChild( selectedBorder );

        setDead( false );
        setSelected( false );
    }

    /**
     * Draw the red X that is displayed when a bunny is dead
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
        deadX.scale( 0.5 );
    }

    private void initSelectedBorder() {
        float padding = 7f;
        selectedBorder = PPath.createRectangle( -padding, -padding, (float) getBunnyWidth() + 2 * padding, (float) getBunnyHeight() + 2 * padding );
        selectedBorder.setStroke( new BasicStroke( 7 ) );
        selectedBorder.setStrokePaint( Color.BLUE );

        // invisible middle
        selectedBorder.setPaint( new Color( 0, 0, 0, 0 ) );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public void setMutated() {
        // TODO: (low) allow a bunny to be ... unmutated if necessary?
        mutatedImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_MUTATION_BUNNY );
        mutatedImage.setOffset( getBunnyWidth() / 2, -getBunnyHeight() / 5 );
        mutatedImage.setScale( 1.3 );
        addChild( mutatedImage );
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped( boolean flipped ) {
        if ( flipped != this.flipped ) {
            if ( flipped ) {
                setTransform( new PAffineTransform( -1, 0, 0, 1, getBunnyWidth(), 0 ) );
            }
            else {
                setTransform( new PAffineTransform( 1, 0, 0, 1, 0, 0 ) );
            }
        }
        this.flipped = flipped;
    }


    public void setDead( boolean dead ) {
        this.dead = dead;

        deadX.setVisible( dead );
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;

        selectedBorder.setVisible( selected );
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isDead() {
        return dead;
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
        return image.getWidth();
    }

    public double getBunnyHeight() {
        return image.getHeight();
    }

}
