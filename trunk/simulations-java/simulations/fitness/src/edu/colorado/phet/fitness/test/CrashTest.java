package edu.colorado.phet.fitness.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.control.CaloricItem;
import edu.colorado.phet.fitness.module.fitness.CaloricFoodItem;
import edu.colorado.phet.fitness.util.FileParser;

/**
 * Created by: Sam
 * May 9, 2008 at 8:07:48 AM
 */
public class CrashTest {
    private JFrame frame;
    private String text = "Next Test";
    private ContentPane contentPane;
    private int testIndex = 0;

    public class Test {
        private String name;

        public Test( String s ) {
            this.name = s;
        }

        public String getName() {
            return name;
        }

        public boolean runTest() {
            return true;
        }
    }

    private CaloricFoodItem[] foodList;
    private CaloricItem[] exerciseList;
    private ArrayList test = new ArrayList();
    private ImageFrame imageFrame = new ImageFrame();

    public CrashTest() {
        frame = new JFrame( "Mac Crash Test" );
        contentPane = new ContentPane();
        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );

        imageFrame.setSize( 200,768 );

        test.add( new Test( "Test Button" ) {
            public boolean runTest() {
                addMessage( "Button Pressed" );
                return super.runTest();
            }
        } );
        test.add( new Test( "Load Food List" ) {
            public boolean runTest() {
                foodList = FileParser.getFoodItems();
                return super.runTest();
            }
        } );
        test.add( new Test( "Load Exercise List" ) {
            public boolean runTest() {
                exerciseList = FileParser.getExerciseItems();
                return super.runTest();
            }
        } );
        test.add( new Test( "Create image tests" ) {
            public boolean runTest() {
                for ( int i = 0; i < foodList.length; i++ ) {
                    test.add( new ItemTest( foodList[i] ) );
                }
                for ( int i = 0; i < exerciseList.length; i++ ) {
                    test.add( new ItemTest( exerciseList[i] ) );
                }
                for ( int i = 0; i < foodList.length; i++ ) {
                    test.add( new ShowItemTest( imageFrame, foodList[i] ) );
                }
                for ( int i = 0; i < exerciseList.length; i++ ) {
                    test.add( new ShowItemTest( imageFrame, exerciseList[i] ) );
                }
                return super.runTest();
            }
        } );


    }



    public static void main( String[] args ) {
        new CrashTest().start();
    }

    private void start() {
        frame.setVisible( true );
        addMessage( "Test started, press \"" + text + "\" to run test: " + getCurrentTest().getName() );
    }

    private void addMessage( String s ) {
        contentPane.addMessage( s );
    }

    private class ContentPane extends JPanel {
        private JTextArea jTextArea = new JTextArea();

        private JButton nextButton = new JButton( text );
        private JButton auto = new JButton( "Auto-Run All Tests" );
        private JScrollPane scrollPane;

        private ContentPane() {
            scrollPane = new JScrollPane( jTextArea );
            scrollPane.setPreferredSize( new Dimension( 600, 400 ) );
            add( scrollPane );

            nextButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    runTest();
                }
            } );
            add( nextButton );

            auto.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    while ( testIndex < test.size() ) {
                        runTest();
                    }
                }
            } );
            add( auto );
        }

        public void addMessage( String s ) {
            jTextArea.append( s + "\n" );
            jTextArea.scrollRectToVisible(
                    new Rectangle( 0, jTextArea.getHeight() - 2, 1, 1 ) );
        }
    }

    private void runTest() {
        addMessage( "Starting test [" + testIndex + "]: " + getCurrentTest().getName() );
        getCurrentTest().runTest();
        addMessage( "Finished test [" + testIndex + "]: " + getCurrentTest().getName() );
        testIndex++;
        if ( testIndex < test.size() ) {
            addMessage( "Next test: " + getCurrentTest().getName() );
        }
        else {
            addMessage( "Tests finished" );
            contentPane.nextButton.setEnabled( false );
        }
    }

    private Test getCurrentTest() {
        return (Test) test.get( testIndex );
    }

    private class ItemTest extends Test {
        private CaloricItem item;

        public ItemTest( CaloricItem caloricFoodItem ) {
            super( "Load image: " + caloricFoodItem.getImage() );
            this.item = caloricFoodItem;
        }

        public boolean runTest() {
            if ( item.getImage() != null && item.getImage().trim().length() > 0 ) {
                BufferedImage image = BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( item.getImage() ), 30 );
                addMessage( "image dim=" + image.getWidth() + " x " + image.getHeight() );
            }
            else {
                addMessage( "no image" );
            }
            return super.runTest();
        }
    }

    private class ShowItemTest extends ItemTest {
        private ImageFrame frame;
        private CaloricItem item;

        public ShowItemTest( ImageFrame frame, CaloricItem caloricFoodItem ) {
            super( caloricFoodItem );
            this.frame = frame;
            this.item = caloricFoodItem;
        }

        public boolean runTest() {
            frame.setVisible( true );
            boolean superT = super.runTest();
            if ( item.getImage() != null && item.getImage().trim().length() > 0 ) {
                BufferedImage image = BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( item.getImage() ), 30 );
                frame.addImage( image );
                addMessage( "displayed image dim=" + image.getWidth() + " x " + image.getHeight() );
            }
            else {
                addMessage( "no image to display" );
            }
            return super.runTest();
        }
    }

    private class ImageFrame extends JFrame {
        private VerticalLayoutPanel panel;

        private ImageFrame() {
            panel = new VerticalLayoutPanel();
            setContentPane( new JScrollPane( panel ) );
        }

        public void addImage( BufferedImage image ) {
            panel.add( new JLabel( new ImageIcon( image ) ) );
        }
    }
}
