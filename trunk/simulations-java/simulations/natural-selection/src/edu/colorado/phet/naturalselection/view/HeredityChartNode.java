/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

// TODO: create GenerationLayoutNode as superclass of this and GnerationChartNode, for shared behavior

/**
 * Shows generations of bunnies in the classical hereditary format with a "family tree".
 *
 * @author Jonathan Olson
 */
public class HeredityChartNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {

    // model reference
    private NaturalSelectionModel model;

    // Generation nodes in order of display
    private LinkedList generations;

    // available horizontal width in the canvas area
    private double availableWidth = NaturalSelectionDefaults.GENERATION_CHART_SIZE.getWidth();

    // vertical space between generations
    private static final double GENERATION_VERTICAL_SPACER = 50.0;

    // initial offset at the top
    private static final double INITIAL_Y_OFFSET = 0;

    // incremented offset for positioning generations
    private double yoffset = INITIAL_Y_OFFSET;

    /**
     * Constructor
     *
     * @param model The natural selection model
     */
    public HeredityChartNode( NaturalSelectionModel model ) {
        this.model = model;

        generations = new LinkedList();

        int minGeneration = 0;
        int maxGeneration = model.getGeneration();
        if ( maxGeneration - minGeneration > NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            minGeneration = maxGeneration - NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD;
        }

        // add only the latest generations if we have at least 2 dead generations

        for ( int i = minGeneration; i <= maxGeneration; i++ ) {
            addGeneration( i );
        }

        model.addListener( this );
    }

    public void reset() {
        Iterator iter = generations.iterator();

        while ( iter.hasNext() ) {
            removeChild( (PNode) iter.next() );
        }

        generations = new LinkedList();

        yoffset = INITIAL_Y_OFFSET;

        addGeneration( 0 );
    }

    /**
     * Add a generation onto the bottom
     *
     * @param genNumber The generation number to fetch and display
     */
    private void addGeneration( int genNumber ) {
        // create a Generation display node
        Generation gen = new Generation( genNumber );

        // position it at the y offset
        gen.setOffset( 0, yoffset );

        // add its height + the spacer to the y offset
        yoffset += gen.getGenerationHeight() + GENERATION_VERTICAL_SPACER;

        // visually add it
        addChild( gen );

        // if there is a generation above, draw the lines on it to show the genetic relationships
        if ( generations.size() != 0 ) {
            ( (Generation) generations.getLast() ).drawChildLines( gen );
        }

        // add this to the end of the generations list
        generations.add( gen );
    }

    /**
     * Gets rid of the oldest generation (visually), and moves the rest up vertically
     */
    public void popGeneration() {
        PNode oldGen = (PNode) generations.get( 0 );

        generations.remove( 0 );

        removeChild( oldGen );

        // TODO: run cleanup on all of oldGen's child nodes (for memory purposes)

        // the space to move the newer generations up
        double space = ( (PNode) generations.get( 0 ) ).getOffset().getY();

        System.out.println( "Space: " + space );

        yoffset -= space;

        Iterator iter = generations.iterator();

        // move all of the newer generations up
        while ( iter.hasNext() ) {
            PNode gen = (PNode) iter.next();
            Point2D offset = gen.getOffset();
            gen.setOffset( offset.getX(), offset.getY() - space );
        }
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void onMonthChange( String monthName ) {

    }

    public void onGenerationChange( int generation ) {
        if ( generation > NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            popGeneration();
        }
        addGeneration( generation );
    }

    public void onNewBunny( Bunny bunny ) {

    }

    public void onClimateChange( int climate ) {

    }

    public void onSelectionFactorChange( int selectionFactor ) {

    }

    /**
     * The visual object that represents a generation. handles layout of bunnies and drawing lines to represent
     * genetic replationships
     *
     * @author Jonathan Olson
     */
    public class Generation extends PNode {

        /**
         * The fraction of a bunny's width to use as padding between bunnies
         */
        private static final double BUNNY_SPACER_FRACTION = 0.7;

        /**
         * The maximum bunny width. If bunnies would be above this width, they are capped and the bunnies are
         * centered horizontally
         */
        private static final double MAX_BUNNY_WIDTH = 40.0;

        /**
         * The length of the line pointing to each individual child bunny
         */
        private static final double LINE_TAIL_LENGTH = 15.0;

        /**
         * Space between the above referenced line and the top of each bunny
         */
        private static final double BUNNY_TOP_PADDING = 4.0;

        /**
         * Vertical distance above the child horizontal bar that is perfectly vertical
         */
        private static final double ABOVE_BUNNY_STRAIGHT = 10.0;

        /**
         * Generation number
         */
        public int generation;

        /**
         * List of bunny nodes
         */
        public ArrayList bunnies;

        /**
         * List of the corresponding visual GenerationBunnyNodes
         */
        public ArrayList bunnyNodes;

        /**
         * Height of bunnies if there are any
         */
        private double cachedGenerationHeight = 0;

        /**
         * Constructor
         *
         * @param generation The generation number to fetch and display
         */
        public Generation( int generation ) {
            this.generation = generation;

            // get all bunnies of this generation
            this.bunnies = model.getBunnyGenerationList( generation );
            bunnyNodes = new ArrayList();

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
            int numBunnies = bunnyNodes.size();

            // ideal with that would precisely fit all of the bunnies with padding between each one and the sides
            double bunnyWidth = availableWidth / ( BUNNY_SPACER_FRACTION * ( numBunnies + 1 ) + numBunnies );

            // left padding if needed
            double leftOffset = 0;

            // if bunnies would be way too large, just center all of it
            if ( bunnyWidth > MAX_BUNNY_WIDTH ) {
                bunnyWidth = MAX_BUNNY_WIDTH;

                // calculate how much would be filled with bunnies and padding
                double filledWidth = bunnyWidth * ( numBunnies + BUNNY_SPACER_FRACTION * ( numBunnies + 1 ) );

                // we must pad the rest
                leftOffset = ( availableWidth - filledWidth ) / 2;
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
        }

        /**
         * Add all bunny nodes to this piccolo node
         */
        private void addBunnies() {
            Iterator iter = bunnies.iterator();

            while ( iter.hasNext() ) {
                Bunny bunny = (Bunny) iter.next();
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
            Iterator iter = bunnyNodes.iterator();

            while ( iter.hasNext() ) {
                GenerationBunnyNode bunnyNode = (GenerationBunnyNode) iter.next();

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
         * @param nextGeneration The next generation that we will draw lines to
         */
        public void drawChildLines( Generation nextGeneration ) {
            Iterator iter = bunnyNodes.iterator();

            while ( iter.hasNext() ) {
                GenerationBunnyNode nodeA = (GenerationBunnyNode) iter.next();
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
                double nextTop = nextGeneration.getOffset().getY() - getOffset().getY();

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

                Iterator childIter = bunnyA.getChildren().iterator();

                //----------------------------------------------------------------------------
                // Draw lines and calculate positions for each child
                //----------------------------------------------------------------------------

                // for each child, draw the vertical line pointing to it, and record its position
                while ( childIter.hasNext() ) {
                    Bunny child = (Bunny) childIter.next();

                    // location of child
                    Point2D childOffset = nextGeneration.getBunnyLocalOffset( child );

                    // size of child
                    Dimension childSize = nextGeneration.getBunnyLocalDimension( child );

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
}
