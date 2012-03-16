// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

import edu.colorado.phet.simsharinganalysis.Entry

/**
 * @author Sam Reid
 */

case class StateEntry[T <: {def time : Long}](start: T, entry: Entry, end: T) {
  lazy val time = end.time - start.time
}