package edu.colorado.phet.naturalselection.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Displays a pedigree of a selected bunny (or text to click on a bunny if none are selected)
 *
 * @author Jonathan Olson
 */
public class PedigreeNode extends PNode {

    /**
     * Holds the pedigree when it is displayed
     */
    private PBoundedNode child;

    /**
     * Holds the "click on a bunny" message when the pedigree is not displayed
     */
    private PBoundedNode textHolder;

    /**
     * Maximum ancestor level shown in the generation chart. The bunny selected is at level 0, its parents are level
     * 1, so for example, MAX_LEVEL of 2 would show a bunny, its parents, and its grandparents
     */
    private static final int MAX_LEVEL = 4;

    // padding spaces between bunnies in the pedigree and the lines that connect them
    private static final double LINE_PAD_HORIZ = 2.0;
    private static final double LINE_PAD_VERT = 2.0;

    // whether a bunny is currently showing
    private boolean showingBunny = false;

    public PedigreeNode() {
        textHolder = new PBoundedNode();
        PText bunnyText = new PText( NaturalSelectionStrings.PEDIGREE_START_MESSAGE );
        bunnyText.setFont( new PhetFont( 24 ) );
        textHolder.addChild( bunnyText );
        textHolder.setOffset( -bunnyText.getWidth() / 2, 10 );

        addChild( textHolder );
    }

    public void reset() {
        removeChildren();
        addChild( textHolder );
    }

    private void removeChildren() {
        if ( showingBunny ) {
            showingBunny = false;
            removeChild( child );
        }
        else {
            removeChild( textHolder );
        }
    }

    public void displayBunny( Bunny bunny ) {
        removeChildren();

        showingBunny = true;

        child = getBlock( bunny, 0, getBunnyMaxLevel( bunny ) );
        addChild( child );
    }

    /**
     * What width the bunny should be scaled to when it is at a particular level
     *
     * @param level The level
     * @return Bunny width in pixels
     */
    private static double desiredBunnyWidth( int level ) {
        if ( level == 0 ) {
            return 50.0;
        }
        else if ( level == 1 ) {
            return 40.0;
        }
        else if ( level == 2 ) {
            return 30.0;
        }
        else if ( level == 3 ) {
            return 25.0;
        }
        else if ( level == 4 ) {
            return 15.0;
        }

        return 15.0;
    }

    /**
     * Get the vertical padding between bunnies of generation level, and those of generation level + 1
     *
     * @param level The level
     * @return Vertical padding in pixels
     */
    private static double getVPad( int level ) {
        return 10;
    }

    /**
     * Get the horizontal padding between bunnies inside of a generation level. This is mostly important just for the
     * highest (oldest) level of bunnies
     *
     * @param level The level
     * @return Horizontal padding in pixels
     */
    private static double getHPad( int level ) {
        if ( level == 0 ) {
            return 40.0;
        }
        else if ( level == 1 ) {
            return 30.0;
        }
        else if ( level == 2 ) {
            return 20.0;
        }
        else if ( level == 3 ) {
            return 15.0;
        }
        else if ( level == 4 ) {
            return 10.0;
        }

        return 10;
    }

    /**
     * How much a given bunny node needs to be scaled to be at its ideal width
     *
     * @param node  The bunny node
     * @param level The level
     * @return The necessary scaling factor
     */
    private static double getBunnyScale( GenerationBunnyNode node, int level ) {
        return desiredBunnyWidth( level ) / node.getBunnyWidth();
    }

    private static PPath line( double x1, double y1, double x2, double y2 ) {
        PPath ret = PPath.createLine( (float) x1, (float) y1, (float) x2, (float) y2 );
        ret.setStroke( new BasicStroke( 1.0f ) );
        ret.setPaint( new Color( 0, 0, 0, 0 ) );
        ret.setStrokePaint( Color.BLACK );
        return ret;
    }

    /**
     * Recursive function that returns a piccolo node (with extra dimension and location information) that contains the
     * entire pedigree for the particular bunny (where level <= maxLevel).
     *
     * @param bunny    The bunny for the pedigree
     * @param level    The level of the bunny
     * @param maxLevel The maximum level to display ancestors of that bunny
     * @return A bounded piccolo node with that bunny and its pedigree
     */
    private static PBoundedNode getBlock( Bunny bunny, int level, int maxLevel ) {
        PBoundedNode ret = new PBoundedNode();

        // get the piccolo node for our bunny
        GenerationBunnyNode bunnyNode = new GenerationBunnyNode( bunny );
        bunnyNode.setSelected( bunny.isSelected() );

        // scale it, and get its final (pixel) size
        double bunnyScale = getBunnyScale( bunnyNode, level );
        double bunnyWidth = bunnyScale * bunnyNode.getBunnyWidth();
        double bunnyHeight = bunnyScale * bunnyNode.getBunnyHeight();
        bunnyNode.scale( bunnyScale );

        ret.addChild( bunnyNode );

        // record this bunny size information in the node
        ret.setBunnyWidth( bunnyWidth );
        ret.setBunnyHeight( bunnyHeight );

        if ( level == maxLevel ) {
            // this is a leaf. we will display no more bunnies below this

            // center the bunny
            bunnyNode.setOffset( -bunnyWidth / 2, 0 );

            // record the bounds for the entire node
            ret.setBoundWidth( bunnyWidth );
            ret.setBoundHeight( bunnyHeight );
        }
        else {
            // we need to display the pedigree sub-blocks below this bunny

            // get the two ancestor blocks
            PBoundedNode fatherNode = getBlock( bunny.getFather(), level + 1, maxLevel );
            PBoundedNode motherNode = getBlock( bunny.getMother(), level + 1, maxLevel );

            // get their bounds
            double fatherWidth = fatherNode.getBoundWidth();
            double fatherHeight = fatherNode.getBoundHeight();
            double motherWidth = motherNode.getBoundWidth();
            double motherHeight = motherNode.getBoundHeight();

            // position the sub-blocks (with padding inbetween)
            double fatherOffset = -getHPad( maxLevel ) / 2 - fatherWidth / 2;
            fatherNode.setOffset( fatherOffset, 0 );
            double motherOffset = getHPad( maxLevel ) / 2 + motherWidth / 2;
            motherNode.setOffset( motherOffset, 0 );

            // record our block width inside the node
            ret.setBoundWidth( fatherWidth + motherWidth + getHPad( maxLevel ) );

            // position our bunny depending on the maximum parent block height
            double maxChildHeight = fatherHeight > motherHeight ? fatherHeight : motherHeight;
            double maxChildBunnyHeight = fatherNode.getBunnyHeight() > motherNode.getBunnyHeight() ? fatherNode.getBunnyHeight() : motherNode.getBunnyHeight();
            bunnyNode.setOffset( -bunnyWidth / 2, maxChildHeight + getVPad( level ) );

            // record our block height inside the node
            ret.setBoundHeight( maxChildHeight + getVPad( level ) + bunnyHeight );

            // add the child sub-blocks
            ret.addChild( fatherNode );
            ret.addChild( motherNode );

            // determine the height at which the parent bunnies centers in the sub-blocks are in our diagram
            double crossHeight = maxChildHeight - maxChildBunnyHeight / 2;

            // draw the line for the pedigree between the two parent bunnies
            ret.addChild( line( fatherOffset + fatherNode.getBunnyWidth() / 2 + LINE_PAD_HORIZ, crossHeight, motherOffset - motherNode.getBunnyWidth() / 2 - LINE_PAD_HORIZ, crossHeight ) );

            // draw the line down to our current bunny
            ret.addChild( line( 0, crossHeight, 0, maxChildHeight + getVPad( level ) - LINE_PAD_VERT ) );
        }

        return ret;
    }

    /**
     * Returns the largest depth at which all ancestors exist, or the maximum level, whichever is smaller.
     * <p/>
     * EX: (hopefully you have a monospaced viewer for this)
     * | level 3        A - B  C - D
     * |                  |      |
     * | level 2          E ---- F G - H
     * |                      |      |
     * | level 1              I ---- J
     * |                         |
     * | level 0                 K
     * <p/>
     * Above is the actual (full) pedigree for bunny K, which goes up to level 3. However since G and H do not have
     * parents in level 3, we can only display a full pedigree up to level 2. Thus this function should return level 2,
     * since a FULL pedigree can be displayed up to that level.
     *
     * @param bunny The bunny we want the pedigree of
     * @return The level
     */
    private static int getBunnyMaxLevel( Bunny bunny ) {
        return getBunnyMaxLevel( bunny, 0 );
    }

    /**
     * Recursive function to find the minimum depth of a leaf under the maximum depth in a bunny's pedigree. Ideally,
     * this should only be called by getBunnyMaxLevel( bunny )
     *
     * @param bunny The bunny
     * @param depth The current bunny's depth
     * @return The minimum depth of a leaf under this tree portion
     */
    private static int getBunnyMaxLevel( Bunny bunny, int depth ) {
        if ( depth == MAX_LEVEL ) {
            return depth;
        }

        Bunny father = bunny.getFather();
        Bunny mother = bunny.getMother();

        if ( father == null || mother == null ) {
            return depth;
        }

        int fatherLevel = getBunnyMaxLevel( father, depth + 1 );
        int motherLevel = getBunnyMaxLevel( mother, depth + 1 );

        // return min
        return fatherLevel > motherLevel ? motherLevel : fatherLevel;
    }

    /**
     * Piccolo node extension that contains full dimensions, and the bunny width/height (and thus implicitly, posotion)
     * of the youngest bunny in this containing block. This is necessary to display pedigree lines between bunnies.
     */
    private static class PBoundedNode extends PNode {
        private double boundWidth;
        private double boundHeight;
        private double bunnyWidth;
        private double bunnyHeight;

        public double getBoundWidth() {
            return boundWidth;
        }

        public void setBoundWidth( double boundWidth ) {
            this.boundWidth = boundWidth;
        }

        public double getBoundHeight() {
            return boundHeight;
        }

        public void setBoundHeight( double boundHeight ) {
            this.boundHeight = boundHeight;
        }

        public double getBunnyWidth() {
            return bunnyWidth;
        }

        public void setBunnyWidth( double bunnyWidth ) {
            this.bunnyWidth = bunnyWidth;
        }

        public double getBunnyHeight() {
            return bunnyHeight;
        }

        public void setBunnyHeight( double bunnyHeight ) {
            this.bunnyHeight = bunnyHeight;
        }
    }
}
