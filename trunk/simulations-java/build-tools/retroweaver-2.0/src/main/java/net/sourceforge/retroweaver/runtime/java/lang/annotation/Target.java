
package net.sourceforge.retroweaver.runtime.java.lang.annotation;

public @Documented @Retention( RetentionPolicy.RUNTIME ) @Target( ElementType.ANNOTATION_TYPE ) @interface Target {
  public ElementType[] value();
}

