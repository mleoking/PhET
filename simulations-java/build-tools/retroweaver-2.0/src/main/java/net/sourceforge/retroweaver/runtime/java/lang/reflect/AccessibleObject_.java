
package net.sourceforge.retroweaver.runtime.java.lang.reflect;

import net.sourceforge.retroweaver.runtime.java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * @author Toby Reyelts
 *         Date: Feb 21, 2005
 *         Time: 10:30:37 PM
 */
public class AccessibleObject_ {

  private AccessibleObject_() {
	  // private constructor
  }
 
  public static <T extends Annotation> T getAnnotation(final AccessibleObject obj, final Class<T> annotationClass) {
    if ( obj instanceof Constructor ) {
      return Constructor_.getAnnotation( ( Constructor ) obj, annotationClass );
    }
    else if ( obj instanceof Method ) {
      return Method_.getAnnotation( ( Method ) obj, annotationClass );
    }
    else if ( obj instanceof Field ) {
      return Field_.getAnnotation( ( Field ) obj, annotationClass );
    }
    else {
      throw new UnsupportedOperationException( "Unexpected AccessibleObject type: " + obj.getClass() );
    }
  }

  public static Annotation[] getAnnotations(final AccessibleObject obj) {
    return getDeclaredAnnotations( obj );
  }

  public static Annotation[] getDeclaredAnnotations(final AccessibleObject obj) {
    if ( obj instanceof Constructor ) {
      return Constructor_.getDeclaredAnnotations( ( Constructor ) obj );
    }
    else if ( obj instanceof Method ) {
      return Method_.getDeclaredAnnotations( ( Method ) obj );
    }
    else if ( obj instanceof Field ) {
      return Field_.getDeclaredAnnotations( ( Field ) obj );
    }
    else {
      throw new UnsupportedOperationException( "Unexpected AccessibleObject type: " + obj.getClass() );
    }
  }

   public static boolean isAnnotationPresent(final AccessibleObject obj, final Class<? extends Annotation> annotationClass) {
     if ( obj instanceof Constructor ) {
       return Constructor_.isAnnotationPresent( ( Constructor ) obj, annotationClass );
     }
     else if ( obj instanceof Method ) {
       return Method_.isAnnotationPresent( ( Method ) obj, annotationClass );
     }
     else if ( obj instanceof Field ) {
       return Field_.isAnnotationPresent( ( Field ) obj, annotationClass );
     }
     else {
       throw new UnsupportedOperationException( "Unexpected AccessibleObject type: " + obj.getClass() );
     }
   }

}

