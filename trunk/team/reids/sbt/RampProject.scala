import sbt._

/**
 * This SBT project file can be used to build motion-series and all its dependencies.
 * I investigated this for convenience of continuous build with ~ compile
 * @author Sam Reid
 * 6-24-2010
 */
class RampProject(info: ProjectInfo) extends ParentProject(info) {
  lazy val junit = project("contrib" / "junit", "junit", new JarProject(_))
  lazy val jsci = project("contrib" / "JSci", "jsci", new JarProject(_))
  lazy val jfreechart = project("contrib" / "jfreechart", "jfreechart", new JarProject(_))
  lazy val javaws = project("contrib" / "javaws", "javaws", new JarProject(_))
  lazy val scala = project("contrib" / "scala", "scala", new JarProject(_))
  lazy val phetcommon = project("common" / "phetcommon", "phetcommon", new PhetProject(_), junit, javaws)
  lazy val piccolo2dcore = project("contrib" / "piccolo2d" / "core", "piccolo2d-core", new DefaultProject(_))
  lazy val piccolo2dextras = project("contrib" / "piccolo2d" / "extras", "piccolo2d-extras", new DefaultProject(_), piccolo2dcore)
  lazy val piccolo2d = project("contrib" / "piccolo2d", "piccolo2d", new DefaultProject(_), piccolo2dextras)
  lazy val piccolophet = project("common" / "piccolo-phet", "piccolo-phet", new PhetProject(_), phetcommon, piccolo2d)
  lazy val jfreechartphet = project("common" / "jfreechart-phet", "jfreechart-phet", new PhetProject(_), jfreechart, piccolophet)
  lazy val scalacommon = project("common" / "scala-common", "scalacommon", new PhetProject(_), phetcommon, piccolo2d, piccolophet, scala)
  lazy val timeseries = project("common" / "timeseries", "timeseries", new PhetProject(_), piccolo2d, phetcommon)
  lazy val recordandplayback = project("common" / "record-and-playback", "recordandplayback", new PhetProject(_), piccolo2d, phetcommon, piccolophet)
  lazy val motion = project("common" / "motion", "motion", new PhetProject(_), timeseries, piccolo2d, phetcommon, jsci, jfreechartphet)
  lazy val motionseries = project("simulations" / "motion-series", "motion-series", new PhetProject(_), scalacommon, phetcommon, motion, recordandplayback, timeseries)

  class PhetProject(info: ProjectInfo) extends DefaultProject(info) {
    override def mainScalaSourcePath = "scala-src"

    override def mainJavaSourcePath = "src"

    override def mainResourcesPath = "data"
  }

  class JarProject(info: ProjectInfo) extends DefaultProject(info) {
    override def dependencyPath = "."
  }
}
