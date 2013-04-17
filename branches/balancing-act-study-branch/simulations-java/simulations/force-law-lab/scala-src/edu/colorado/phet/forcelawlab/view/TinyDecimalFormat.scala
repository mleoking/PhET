// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab.view

import java.text.{FieldPosition, DecimalFormat}

class TinyDecimalFormat extends DecimalFormat("0.00000000000") {
  override def format(number: Double, result: StringBuffer, fieldPosition: FieldPosition) = {
    val s = super.format(number, result, fieldPosition).toString
    //start at the end, and insert spaces after every 3 elements, may not work for every locale
    var str = ""
    for ( ch <- s ) {
      str = str + ch
      //4 instead of 3 because space adds another element
      if ( str.length % 4 == 0 ) {
        str = str + " "
      }
    }
    new StringBuffer(str)
  }
}