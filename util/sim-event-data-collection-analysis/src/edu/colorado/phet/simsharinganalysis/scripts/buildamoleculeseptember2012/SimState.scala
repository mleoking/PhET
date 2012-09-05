package edu.colorado.phet.simsharinganalysis.scripts.buildamoleculeseptember2012

/**
 * @author Sam Reid
 */
case class SimState(time: Long, tab: Int, tab0: TabState, tab1: TabState, tab2: TabState) {
  def currentTab = tab match {
    case 0 => tab0
    case 1 => tab1
    case 2 => tab2
    case _ => throw new RuntimeException("Tab not found")
  }
}

case class TabState(collected: List[String]) {
  def collect(commonName: String) = copy(collected = collected ::: List(commonName))
}
