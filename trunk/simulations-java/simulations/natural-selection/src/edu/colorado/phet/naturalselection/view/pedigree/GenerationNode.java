package edu.colorado.phet.naturalselection.view.pedigree;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.GenerationBunnyNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * The visual object that represents a generation. handles layout of bunnies and drawing lines to represent
 * genetic replationships
 *
 * @author Jonathan Olson
 */
public class GenerationNode extends PNode {

    /**
     * The fraction of a bunny's width to use as padding between bunnies
     */
    private static final double BUNNY_SPACER_FRACTION = 0.7;

    /**
     * The maximum bunny width. If bunnies would be above this width, they are capped and the bunnies are
     * centered horizontally
     */
    private static final double MAX_BUNNY_WIDTH = 30.0;

    private static final double MAX_SECONDARY_BUNNY_WIDTH = 25.0;

    /**
     * The length of the line pointing to each individual child bunny
     */
    private static final double LINE_TAIL_LENGTH = 6.0;

    /**
     * Space between the above referenced line and the top of each bunny
     */
    private static final double BUNNY_TOP_PADDING = 3.0;

    /**
     * Vertical distance above the child horizontal bar that is perfectly vertical
     */
    private static final double ABOVE_BUNNY_STRAIGHT = 5.0;

    /**
     * Generation number
     */
    public int generation;

    /**
     * List of bunny nodes
     */
    public List<Bunny> bunnies;

    /**
     * List of the corresponding visual GenerationBunnyNodes
     */
    public List<GenerationBunnyNode> bunnyNodes;

    /**
     * Height of bunnies if there are any
     */
    private double cachedGenerationHeight = 0;
    private PedigreeNode pedigreeNode;

    private NaturalSelectionModel model;

    /**
     * Constructor
     *
     * @param model        The model
     * @param pedigreeNode The "parent" pedigree node
     * @param generation   The generation number to fetch and display
     */
    public GenerationNode( NaturalSelectionModel model, PedigreeNode pedigreeNode, int generation ) {
        this.model = model;
        this.pedigreeNode = pedigreeNode;
        this.generation = generation;

        // get all bunnies of this generation
        bunnies = model.getBunnyGenerationList( generation );
        bunnyNodes = new LinkedList<GenerationBunnyNode>();

        // add the bunny nodes to this node
        addBunnies();

        // lay out all of the bunny nodes
        layoutBunnies();
    }

    /**
     * Lay out all of the bunnes. If all of the bunnies fill horizontally, they will be scaled down to fit.
     * If they would be larger than the maximum size, they will be padded and fitted into the middle horizontally
     */
    private void layoutBunnies() {
        /*
        int numBunnies = bunnyNodes.size();

        // ideal with that would precisely fit all of the bunnies with padding between each one and the sides
        double bunnyWidth = pedigreeNode.AVAILABLE_WIDTH / ( BUNNY_SPACER_FRACTION * ( numBunnies + 1 ) + numBunnies );

        // left padding if needed
        double leftOffset = 0;

        double maxBunnyWidth = ( generation == 0 ? MAX_BUNNY_WIDTH : MAX_SECONDARY_BUNNY_WIDTH );

        // if bunnies would be way too large, just center all of it
        if ( bunnyWidth > maxBunnyWidth ) {
            bunnyWidth = maxBunnyWidth;

            // calculate how much would be filled with bunnies and padding
            double filledWidth = bunnyWidth * ( numBunnies + BUNNY_SPACER_FRACTION * ( numBunnies + 1 ) );

            // we must pad the rest
            leftOffset = ( pedigreeNode.AVAILABLE_WIDTH - filledWidth ) / 2;
        }

        // the padding between bunnies is proportional to the bunnies themselves
        double bunnySpacer = bunnyWidth * BUNNY_SPACER_FRACTION;

        Iterator iter = bunnyNodes.iterator();

        int bunnyCounter = 0;

        // lay out the bunnies
        while ( iter.hasNext() ) {
            GenerationBunnyNode bunnyNode = (GenerationBunnyNode) iter.next();

            // calculate the exact scale for this bunny (in case it varies in the future)
            double bunnyScale = bunnyWidth / bunnyNode.getBunnyWidth();
            cachedGenerationHeight = bunnyScale * bunnyNode.getBunnyHeight();
            bunnyNode.scale( bunnyScale );

            // position horizontally
            bunnyNode.setOffset( bunnySpacer + leftOffset + ( bunnyWidth + bunnySpacer ) * ( bunnyCounter++ ), 0 );
        }
        */
        int numBunnies = bunnyNodes.size();

        double bunnyWidth = MAX_BUNNY_WIDTH;
        double bunnySpacer = bunnyWidth * BUNNY_SPACER_FRACTION;

        double totalWidth = numBunnies * bunnyWidth + ( numBunnies + 1 ) * bunnySpacer;

        double count = 0;

        for ( Iterator iterator = bunnyNodes.iterator(); iterator.hasNext(); count++ ) {
            GenerationBunnyNode bunnyNode = (GenerationBunnyNode) iterator.next();
            double bunnyScale = bunnyWidth / bunnyNode.getBunnyWidth();
            cachedGenerationHeight = bunnyScale * bunnyNode.getBunnyHeight();
            bunnyNode.scale( bunnyScale );
            bunnyNode.setOffset( bunnySpacer + count * ( bunnySpacer + bunnyWidth ) - totalWidth / 2, 0 );
        }
    }

    /**
     * Add all bunny nodes to this piccolo node
     */

    private void addBunnies() {
        for ( Bunny bunny : bunnies ) {
            GenerationBunnyNode bunnyNode = new GenerationBunnyNode( bunny );
            bunnyNodes.add( bunnyNode );
            addChild( bunnyNode );
        }
    }

    public double getGenerationHeight() {
        return cachedGenerationHeight;
    }

    /**
     * Find the piccolo bunny node corresponding to the bunny model
     *
     * @param bunny The bunny model
     * @return The corresponding GenerationBunnyNode
     */
    public GenerationBunnyNode getBunnyNodeFromBunny( Bunny bunny ) {
        for ( GenerationBunnyNode bunnyNode : bunnyNodes ) {
            if ( bunnyNode.getBunny() == bunny ) {
                return bunnyNode;
            }
        }

        return null;
    }

    /**
     * Return the position of the upper-left of the piccolo node corresponding to the model bunny
     *
     * @param bunny The bunny model
     * @return Upper-left position relative to this generation
     */
    public Point2D getBunnyLocalOffset( Bunny bunny ) {
        GenerationBunnyNode bunnyNode = getBunnyNodeFromBunny( bunny );
        return bunnyNode.getOffset();
    }

    /**
     * Return the size of the piccolo bunny node corresponding to the model bunny
     *
     * @param bunny The bunny model
     * @return The scaled size of the GenerationBunnyNode
     */
    public Dimension getBunnyLocalDimension( Bunny bunny ) {
        GenerationBunnyNode bunnyNode = getBunnyNodeFromBunny( bunny );
        double scale = bunnyNode.getScale();

        // TODO: verify that passing doubles through setSize works, since no double constructor exists here
        Dimension ret = new Dimension();
        ret.setSize( scale * bunnyNode.getBunnyWidth(), scale * bunnyNode.getBunnyHeight() );
        return ret;
    }

    // WARNING: Assumes that bunnies are paired in order!!!
    /**
     * Draw hierarchical genetic lines between this generation and the one below it.
     * Lines are stored in the top generation, so when the older generations disappear the lines
     * will disappear at the same time.
     *
     * @param nextGenerationNode The next generation that we will draw lines to
     */
    public void drawChildLines( GenerationNode nextGenerationNode ) {
        Iterator<GenerationBunnyNode> iter = bunnyNodes.iterator();

        while ( iter.hasNext() ) {
            GenerationBunnyNode nodeA = iter.next();
            Bunny bunnyA = nodeA.getBunny();
            Bunny bunnyB = bunnyA.getPotentialMate();
            GenerationBunnyNode nodeB = getBunnyNodeFromBunny( bunnyB );

            // ignore bunnies with no children, and only do the rest for one of the parents
            if ( !bunnyA.hasChildren() || bunnyA.getId() < bunnyB.getId() ) {
                continue;
            }

            //----------------------------------------------------------------------------
            // Calculate locations relative to parent bunnies and lower generation
            //----------------------------------------------------------------------------

            // figure out which parents are on which side
            GenerationBunnyNode nodeLeft, nodeRight;
            Bunny bunnyLeft, bunnyRight;

            if ( nodeA.getOffset().getX() > nodeB.getOffset().getX() ) {
                nodeRight = nodeA;
                bunnyRight = bunnyA;
                nodeLeft = nodeB;
                bunnyLeft = bunnyB;
            }
            else {
                nodeRight = nodeB;
                bunnyRight = bunnyB;
                nodeLeft = nodeA;
                bunnyLeft = bunnyA;
            }

            // parent bunny locations
            Point2D leftOffset = getBunnyLocalOffset( bunnyLeft );
            Point2D rightOffset = getBunnyLocalOffset( bunnyRight );

            // parent bunny sizes
            Dimension leftSize = getBunnyLocalDimension( bunnyLeft );
            Dimension rightSize = getBunnyLocalDimension( bunnyRight );

            // the offset from this generation to the top of the next generation
            double nextTop = nextGenerationNode.getOffset().getY() - getOffset().getY();

            // the vertical location of the large horizontal children bar (offset from the location of this top generation)
            double horizTop = nextTop - LINE_TAIL_LENGTH;

            // horizontal position precisely between the two parents
            double betweenTopX = ( leftOffset.getX() + leftSize.getWidth() + rightOffset.getX() ) / 2;

            // vertical position between the two parents
            double betweenTopY = leftOffset.getY() + leftSize.getHeight() / 2;

            // horizontal line between parents
            addLine(
                    new Point2D.Double(
                            leftOffset.getX() + leftSize.getWidth(),
                            betweenTopY
                    ),
                    new Point2D.Double(
                            rightOffset.getX(),
                            betweenTopY
                    )
            );

            // we need to find the leftmost and rightmost horizontal positions of the children to draw the
            // horizontal children line
            double minChildX = Double.POSITIVE_INFINITY, maxChildX = Double.NEGATIVE_INFINITY;

            Iterator<Bunny> childIter = bunnyA.getChildren().iterator();

            //----------------------------------------------------------------------------
            // Draw lines and calculate positions for each child
            //----------------------------------------------------------------------------

            // for each child, draw the vertical line pointing to it, and record its position
            while ( childIter.hasNext() ) {
                try {
                    Bunny child = childIter.next();

                    // location of child
                    Point2D childOffset = nextGenerationNode.getBunnyLocalOffset( child );

                    // size of child
                    Dimension childSize = nextGenerationNode.getBunnyLocalDimension( child );

                    // the vertical top but horizontally centered point of the child bunny (Relative to this top generation)
                    Point2D topCenter = new Point2D.Double( childOffset.getX() + childSize.getWidth() / 2, childOffset.getY() + nextTop - BUNNY_TOP_PADDING );

                    // change min or max as necessary
                    minChildX = Math.min( minChildX, topCenter.getX() );
                    maxChildX = Math.max( maxChildX, topCenter.getX() );

                    // line from horizontal child bar to top of child
                    addLine(
                            topCenter,
                            new Point2D.Double(
                                    topCenter.getX(),
                                    horizTop
                            )
                    );
                }
                catch( NullPointerException e ) {
                    // just ignore errors for line placement
                }
            }

            // horizontal bar above children
            addLine(
                    new Point2D.Double(
                            minChildX,
                            horizTop
                    ),
                    new Point2D.Double(
                            maxChildX,
                            horizTop
                    )
            );

            //----------------------------------------------------------------------------
            // Connect the top and bottom lines
            //----------------------------------------------------------------------------


            // top vertical part
            addLine(
                    new Point2D.Double(
                            betweenTopX,
                            betweenTopY
                    ),
                    new Point2D.Double(
                            betweenTopX,
                            betweenTopY * 2
                    )
            );

            // middle diagonal part
            addLine(
                    new Point2D.Double(
                            betweenTopX,
                            betweenTopY * 2
                    ),
                    new Point2D.Double(
                            ( minChildX + maxChildX ) / 2,
                            horizTop - ABOVE_BUNNY_STRAIGHT
                    )
            );

            // bottom vertical part
            addLine(
                    new Point2D.Double(
                            ( minChildX + maxChildX ) / 2,
                            horizTop - ABOVE_BUNNY_STRAIGHT
                    ),
                    new Point2D.Double(
                            ( minChildX + maxChildX ) / 2,
                            horizTop
                    )
            );
        }
    }

    /**
     * Helper function to draw lines cleanly
     *
     * @param a From this point
     * @param b To this point
     */
    public void addLine( Point2D a, Point2D b ) {
        PPath pathNode = new PPath();

        pathNode.setStroke( new BasicStroke( 1 ) );
        pathNode.setStrokePaint( Color.BLACK );

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( a.getX(), a.getY() );
        path.lineTo( b.getX(), b.getY() );
        pathNode.setPathTo( path.getGeneralPath() );

        addChild( pathNode );
    }

}
