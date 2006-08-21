package org.opensourcephysics.ejs.control;

import org.opensourcephysics.ejs.control.value.*;

/**
 * An Ejs control that behaves like a standard OSP control insofar as it parses mathematical expressions
 * stored as strings to produce integers and doubles.
 *
 * @author W. Christian
 * @version 1.0
 */
public class ParsedEjsControl extends EjsControl {

   public ParsedEjsControl(Object simulation){
      super(simulation);
   }

   /**
    * Gets the double keyed to this value.
    *
    * String values are converted to double using a math expression parser.
    *
    * @param var String
    * @return double
    */
   public double getDouble(String var){
      Value value = getValue(var);
      if (value instanceof DoubleValue){
         return super.getDouble(var);
      }else if (value instanceof IntegerValue){
         return super.getInt(var);
      } else {
         String str= super.getString(var);
         try{
            return Double.parseDouble(str);
         }catch (NumberFormatException ex){
            return org.opensourcephysics.numerics.Util.evalMath(str);
         }
      }
   }

   /**
    * Gets the integer keyed to this value.
    *
    * String values are converted to int using a math expression parser.
    *
    * @param var String
    * @return double
    */
   public int getInt(String var){
      Value value = getValue(var);
      if (value instanceof IntegerValue){
         return super.getInt(var);
      }else if (value instanceof DoubleValue){
         return (int)super.getDouble(var);
      } else {
         String str= super.getString(var);
         try{
            return Integer.parseInt(str);
         }catch(NumberFormatException ex){
            return (int) org.opensourcephysics.numerics.Util.evalMath(str);
         }
      }
   }

}
