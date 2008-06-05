package edu.colorado.phet.fitness.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.control.CaloricItem;
import edu.colorado.phet.fitness.control.ExerciseItem;
import edu.colorado.phet.fitness.module.fitness.CaloricFoodItem;

/**
 * Created by: Sam
 * Apr 24, 2008 at 5:08:30 PM
 */
public class FitnessFileParser {

    public static CaloricFoodItem[] getFoodItems() {
        return (CaloricFoodItem[]) parse( "foods.properties", new FoodItemParser(), new CaloricFoodItem[0] );
    }

    public static CaloricItem[] getExerciseItems() {
        return (CaloricItem[]) parse( "exercise.properties", new ExerciseItemParser(), new CaloricItem[0] );
    }

    private static Object[] parse( String resource, IParser parser, Object[] type ) {
        try {
            ArrayList list = new ArrayList();
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( FitnessResources.getResourceLoader().getResourceAsStream( resource ) ) );
            for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
                line = line.trim();
                if ( !line.startsWith( "#" ) && line.length() > 0 ) {
                    list.add( parser.parseLine( line ) );
                }
            }
            return list.toArray( type );
        }
        catch( Exception e ) {
            throw new RuntimeException( e );
        }
    }


    private static class FoodItemParser implements IParser {
        public Object parseLine( String line ) {
            String name = line.substring( 0, line.indexOf( ":" ) );
            String remainder = line.substring( line.indexOf( ":" ) + 1 );
            StringTokenizer st = new StringTokenizer( remainder, "," );
            double cal = Double.parseDouble( st.nextToken() );
            double lipids = Double.parseDouble( st.nextToken() );
            double carbs = Double.parseDouble( st.nextToken() );
            double proteins = Double.parseDouble( st.nextToken() );
            String image = "";
            if ( st.hasMoreTokens() ) {
                image = st.nextToken().trim();
            }
            return new CaloricFoodItem( FitnessResources.getString( name ), image, cal, lipids, carbs, proteins );
        }
    }

    private interface IParser {
        public Object parseLine( String line );
    }

    private static class ExerciseItemParser implements IParser {
        public Object parseLine( String line ) {
            String name = line.substring( 0, line.indexOf( ":" ) );
            String remainder = line.substring( line.indexOf( ":" ) + 1 );
            StringTokenizer st = new StringTokenizer( remainder, "," );
            double cal = Double.parseDouble( st.nextToken() );
            String image = "";
            if ( st.hasMoreTokens() ) {
                image = st.nextToken().trim();
            }
            return new ExerciseItem( FitnessResources.getString( name ), image, cal );
        }
    }

    public static void main( String[] args ) throws IOException {
        for ( int i = 0; i < 10; i++ ) {
            System.out.println( "################\n" +
                                "# Started iteration " + i + "\n" +
                                "###############" );
            iterateAll();
        }
    }

    private static void iterateAll() {
        CaloricFoodItem[] c = FitnessFileParser.getFoodItems();
        for ( int i = 0; i < c.length; i++ ) {
            System.out.println( "i = " + i + ", c=" + c[i] );
            if ( c[i].getImage() != null && c[i].getImage().length() > 0 ) {
                BufferedImage im = FitnessResources.getImage( c[i].getImage() );
                System.out.println( "loaded image[" + i + "]=" + im );
            }
        }

        CaloricItem[] ex = FitnessFileParser.getExerciseItems();
        for ( int i = 0; i < ex.length; i++ ) {
            CaloricItem caloricItem = ex[i];
            System.out.println( "caloricItem = " + caloricItem );
            if ( ex[i].getImage() != null && ex[i].getImage().length() > 0 ) {
                BufferedImage im = FitnessResources.getImage( ex[i].getImage() );
                System.out.println( "loaded image[" + i + "]=" + im );
            }
        }
    }
}
