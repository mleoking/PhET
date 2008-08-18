package edu.colorado.phet.eatingandexercise.util;

import java.text.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.model.EatingAndExerciseUnits;

/**
 * Created by: Sam
 * May 7, 2008 at 11:27:55 AM
 */
public class YearMonthFormat extends NumberFormat {
    private DecimalFormat format = new DecimalFormat( "0" );

    public Number parse( String source, ParsePosition parsePosition ) {
        StringTokenizer st = new StringTokenizer( source.substring( parsePosition.getIndex() ), "'\" /" );
        parsePosition.setIndex( source.length() );
        if ( st.countTokens() == 0 ) {
            return new Long( 0 );
        }
        else if ( st.countTokens() == 1 ) {
            try {
                return format.parse( st.nextToken() );
            }
            catch( ParseException e ) {
                e.printStackTrace();
            }
        }
        else {

            ArrayList values = new ArrayList();
            while ( st.hasMoreTokens() ) {
                try {
                    double value = Double.parseDouble( st.nextToken() );
                    values.add( new Double( value ) );
                }
                catch( NumberFormatException ignored ) {

                }
            }
            double years = 0;
            double months = 0;
            if ( values.size() >= 1 ) {
                years = ( (Double) values.get( 0 ) ).doubleValue();
            }
            if ( values.size() >= 2 ) {
                months = ( (Double) values.get( 1 ) ).doubleValue();
            }

            return new Double( years + EatingAndExerciseUnits.monthsToYears( months ) );
        }
        return new Long( 0 );
    }

    public StringBuffer format( double number, StringBuffer toAppendTo, FieldPosition pos ) {
        double totalYears = number;
        double yearsDecimal = number - (int) number;
        double remainder = totalYears - yearsDecimal;
        double months = EatingAndExerciseUnits.yearsToMonths( yearsDecimal );

        String monthText = format.format( months );
        if ( monthText.equals( "12" ) ) {
            monthText = "0";
            remainder++;
        }
//        toAppendTo.append( "" + format.format( remainder ) + "/ " + monthText + "\"" );
//        toAppendTo.append( "" + format.format( remainder ) + "/" + monthText  );
        String yrString = EatingAndExerciseResources.getString( "time.year.abbr" );
        String mString = EatingAndExerciseResources.getString( "time.month.abbr" );
        toAppendTo.append( "" + format.format( remainder ) + " " + yrString );
        if ( !monthText.trim().equals( "0" ) ) {
            toAppendTo.append( " " + monthText + " " + mString );
        }
//        toAppendTo.append( "" + format.format( remainder ) + " y " + monthText +" m" );
        return toAppendTo;
    }

    public StringBuffer format( long number, StringBuffer toAppendTo, FieldPosition pos ) {
        return format( (double) number, toAppendTo, pos );
    }
}