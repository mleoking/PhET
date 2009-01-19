/*
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Jan 18, 2009
 * Time: 2:58:44 PM
 */
package edu.colorado.phet.movingman.ladybug.util

import java.lang.reflect.{Field, Method, Modifier}
import scala.collection.mutable.ArrayBuffer

object TestEnum {
    object DayEnum {
        private var list = new ArrayBuffer[Day] //temporary list
        class Day() {
            list += this
            override def toString = (DayEnum.getClass.getDeclaredMethods.find((a: Method) => {Modifier.isPublic(a.getModifiers) && a.invoke(DayEnum.this) == this})).get.getName
        }

        val MONDAY, TUESDAY, WEDNESDAY = new Day
        val values = list.toList                             
    }
    def main(args: Array[String]) = {
        println(DayEnum.MONDAY)
        println(DayEnum.values)
    }
}