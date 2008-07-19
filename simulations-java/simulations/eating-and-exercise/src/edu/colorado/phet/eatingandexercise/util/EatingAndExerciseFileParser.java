package edu.colorado.phet.eatingandexercise.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.control.CaloricItem;
import edu.colorado.phet.eatingandexercise.control.ExerciseItem;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.CaloricFoodItem;

/**
 * Created by: Sam
 * Apr 24, 2008 at 5:08:30 PM
 */
public class EatingAndExerciseFileParser {

    public static CaloricFoodItem[] getFoodItems( Human human ) {
        return (CaloricFoodItem[]) parse( "foods.properties", new FoodItemParser(), new CaloricFoodItem[0], human );
    }

    public static CaloricItem[] getExerciseItems( Human human ) {
        return (CaloricItem[]) parse( "exercise.properties", new ExerciseItemParser(), new CaloricItem[0], human );
    }

    private static Object[] parse( String resource, IParser parser, Object[] type, Human human ) {
        try {
            double referenceWeightPounds = 0;
            ArrayList list = new ArrayList();
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( EatingAndExerciseResources.getResourceLoader().getResourceAsStream( resource ) ) );
            for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
                line = line.trim();
                if ( !line.startsWith( "#" ) && line.length() > 0 ) {
                    if ( line.startsWith( "reference-weight-pounds" ) ) {
                        StringTokenizer st = new StringTokenizer( line, "=" );
                        st.nextToken();
                        referenceWeightPounds = Double.parseDouble( st.nextToken() );
                    }
                    else {
                        list.add( parser.parseLine( line, referenceWeightPounds, human ) );
                    }
                }
            }
            return list.toArray( type );
        }
        catch( Exception e ) {
            throw new RuntimeException( e );
        }
    }


    private static class FoodItemParser implements IParser {
        public Object parseLine( String line, double referenceWeightPounds, Human human ) {
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
            return new CaloricFoodItem( EatingAndExerciseResources.getString( name ), image, lipids, carbs, proteins );
        }
    }

    private interface IParser {
        public Object parseLine( String line, double referenceWeightPounds, Human human );
    }

    private static class ExerciseItemParser implements IParser {
        public Object parseLine( String line, double referenceWeightPounds, Human human ) {
            String key = line.substring( 0, line.indexOf( ":" ) );
            String remainder = line.substring( line.indexOf( ":" ) + 1 );
            StringTokenizer st = new StringTokenizer( remainder, "," );
            double baseCalories = Double.parseDouble( st.nextToken() ) / 4;
            double weightDependence = Double.parseDouble( st.nextToken() );
            String image = "";
            if ( st.hasMoreTokens() ) {
                image = st.nextToken().trim();
            }
            return new ExerciseItem( EatingAndExerciseResources.getString( "fifteen.minutes" )+" " + EatingAndExerciseResources.getString( key ), image, baseCalories, weightDependence, referenceWeightPounds, human );
        }
    }
}
