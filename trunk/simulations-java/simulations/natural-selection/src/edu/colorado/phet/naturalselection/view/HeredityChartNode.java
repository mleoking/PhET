/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

// TODO: create GenerationLayoutNode as superclass of this and GnerationChartNode, for shared behavior

public class HeredityChartNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {

    private NaturalSelectionModel model;

    private LinkedList generations;

    private double availableWidth = NaturalSelectionDefaults.GENERATION_CHART_SIZE.getWidth();

    private static final double GENERATION_VERTICAL_SPACER = 50.0;

    private static final double INITIAL_Y_OFFSET = 0;

    private double yoffset = INITIAL_Y_OFFSET;

    public HeredityChartNode( NaturalSelectionModel model ) {
        this.model = model;

        generations = new LinkedList();

        int minGeneration = 0;
        int maxGeneration = model.getGeneration();
        if ( maxGeneration - minGeneration > NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            minGeneration = maxGeneration - NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD;
        }

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

    private void addGeneration( int genNumber ) {
        Generation gen = new Generation( genNumber );
        gen.setOffset( 0, yoffset );
        yoffset += gen.getGenerationHeight() + GENERATION_VERTICAL_SPACER;
        addChild( gen );
        if ( generations.size() != 0 ) {
            ( (Generation) generations.getLast() ).drawChildLines( gen );
        }
        generations.add( gen );
    }

    public void popGeneration() {
        PNode oldGen = (PNode) generations.get( 0 );

        generations.remove( 0 );

        removeChild( oldGen );

        // TODO: run cleanup on all of oldGen's child nodes (for memory purposes)

        double space = ( (PNode) generations.get( 0 ) ).getOffset().getY();

        System.out.println( "Space: " + space );

        yoffset -= space;

        Iterator iter = generations.iterator();

        while ( iter.hasNext() ) {
            PNode gen = (PNode) iter.next();
            Point2D offset = gen.getOffset();
            gen.setOffset( offset.getX(), offset.getY() - space );
        }
    }

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

    public class Generation extends PNode {

        private static final double BUNNY_SPACER_FRACTION = 0.7;
        private static final double MAX_BUNNY_WIDTH = 40.0;
        private static final double LINE_TAIL_LENGTH = 15.0;
        private static final double BUNNY_TOP_PADDING = 4.0;
        private static final double ABOVE_BUNNY_STRAIGHT = 10.0;

        public int generation;
        public ArrayList bunnies;
        public ArrayList bunnyNodes;

        private double cachedGenerationHeight = 0;

        public Generation( int generation ) {
            this.generation = generation;
            this.bunnies = model.getBunnyGenerationList( generation );
            bunnyNodes = new ArrayList();
            addBunnies();
            layoutBunnies();
        }

        private void layoutBunnies() {
            int numBunnies = bunnyNodes.size();
            double bunnyWidth = availableWidth / ( BUNNY_SPACER_FRACTION * ( numBunnies + 1 ) + numBunnies );
            double leftOffset = 0;

            // if bunnies would be way too large, just center all of it
            if ( bunnyWidth > MAX_BUNNY_WIDTH ) {
                bunnyWidth = MAX_BUNNY_WIDTH;
                double filledWidth = bunnyWidth * ( numBunnies + BUNNY_SPACER_FRACTION * ( numBunnies + 1 ) );
                leftOffset = ( availableWidth - filledWidth ) / 2;
            }

            double bunnySpacer = bunnyWidth * BUNNY_SPACER_FRACTION;

            Iterator iter = bunnyNodes.iterator();

            int bunnyCounter = 0;

            while ( iter.hasNext() ) {
                GenerationBunnyNode bunnyNode = (GenerationBunnyNode) iter.next();
                double bunnyScale = bunnyWidth / bunnyNode.getBunnyWidth();
                cachedGenerationHeight = bunnyScale * bunnyNode.getBunnyHeight();
                bunnyNode.scale( bunnyScale );
                bunnyNode.setOffset( bunnySpacer + leftOffset + ( bunnyWidth + bunnySpacer ) * ( bunnyCounter++ ), 0 );
            }
        }

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

        public Point2D getBunnyLocalOffset( Bunny bunny ) {
            GenerationBunnyNode bunnyNode = getBunnyNodeFromBunny( bunny );
            return bunnyNode.getOffset();
        }

        public Dimension getBunnyLocalDimension( Bunny bunny ) {
            GenerationBunnyNode bunnyNode = getBunnyNodeFromBunny( bunny );
            double scale = bunnyNode.getScale();

            // TODO: verify that passing doubles through setSize works, since no double constructor exists here
            Dimension ret = new Dimension();
            ret.setSize( scale * bunnyNode.getBunnyWidth(), scale * bunnyNode.getBunnyHeight() );
            return ret;
        }

        // WARNING: Assumes that bunnies are paired in order!!!
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

                Point2D leftOffset = getBunnyLocalOffset( bunnyLeft );
                Point2D rightOffset = getBunnyLocalOffset( bunnyRight );

                Dimension leftSize = getBunnyLocalDimension( bunnyLeft );
                Dimension rightSize = getBunnyLocalDimension( bunnyRight );

                double nextTop = nextGeneration.getOffset().getY() - getOffset().getY();
                double horizTop = nextTop - LINE_TAIL_LENGTH;
                double betweenTopX = ( leftOffset.getX() + leftSize.getWidth() + rightOffset.getX() ) / 2;
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

                double minChildX = Double.POSITIVE_INFINITY, maxChildX = Double.NEGATIVE_INFINITY;

                Iterator childIter = bunnyA.getChildren().iterator();

                while ( childIter.hasNext() ) {
                    Bunny child = (Bunny) childIter.next();
                    Point2D childOffset = nextGeneration.getBunnyLocalOffset( child );
                    Dimension childSize = nextGeneration.getBunnyLocalDimension( child );
                    Point2D topCenter = new Point2D.Double( childOffset.getX() + childSize.getWidth() / 2, childOffset.getY() + nextTop - BUNNY_TOP_PADDING );
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

                /*
                // vertical line between parents and child line
                addLine(
                        new Point2D.Double(
                                betweenTopX,
                                betweenTopY
                        ),
                        new Point2D.Double(
                                betweenTopX,
                                horizTop
                        )
                );
                */

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
