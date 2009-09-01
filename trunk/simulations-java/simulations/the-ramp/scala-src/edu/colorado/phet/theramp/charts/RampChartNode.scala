package edu.colorado.phet.theramp.charts

import phet.common.motion.graphs._
import phet.common.motion.model._
import phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import phet.common.piccolophet.{PhetPCanvas}
import model.{RampModel}
import RampResources._
import sims.theramp.RampDefaults._
class RampChartNode(transform: ModelViewTransform2D, canvas: PhetPCanvas, model: RampModel, showEnergyGraph: Boolean)
        extends AbstractChartNode(transform, canvas, model) {
  val parallelAppliedForceVariable = new DefaultTemporalVariable() {
    override def setValue(value: Double) = model.bead.parallelAppliedForce = value
  }
  model.stepListeners += (() => {
    if (inTimeRange(model.getTime))
      parallelAppliedForceVariable.addValue(model.bead.appliedForce.dot(model.bead.getRampUnitVector), model.getTime)
  })

  val appliedForceSeries = new ControlGraphSeries(formatForce("forces.applied".translate), appliedForceColor, abbrevUnused, N, characterUnused, parallelAppliedForceVariable)
  val frictionSeries = new ControlGraphSeries(formatForce("forces.friction".translate), frictionForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.frictionForce))
  val gravitySeries = new ControlGraphSeries(formatForce("forces.Gravity".translate), gravityForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.gravityForce))
  val wallSeries = new ControlGraphSeries(formatForce("forces.Wall".translate), wallForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.wallForce))
  val netForceSeries = new ControlGraphSeries(formatForce("forces.Net".translate), totalForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.totalForce))
  val forceSeriesList = appliedForceSeries :: frictionSeries :: gravitySeries :: wallSeries :: netForceSeries :: Nil

  val totalEnergySeries = new ControlGraphSeries(formatEnergy("energy.total".translate), totalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getTotalEnergy))
  val keSeries = new ControlGraphSeries(formatEnergy("energy.kinetic".translate), kineticEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getKineticEnergy))
  val peSeries = new ControlGraphSeries(formatEnergy("energy.potential".translate), potentialEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getPotentialEnergy))
  val thermalEnergySeries = new ControlGraphSeries(formatEnergy("energy.thermal".translate), thermalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getThermalEnergy))

  val appliedWorkSeries = new ControlGraphSeries(formatWork("work.applied".translate), appliedWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getAppliedWork))
  val gravityWorkSeries = new ControlGraphSeries(formatWork("work.gravity".translate), gravityWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getGravityWork))
  val frictionWorkSeries = new ControlGraphSeries(formatWork("work.friction".translate), frictionWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getFrictiveWork))
  val energyWorkSeriesList = totalEnergySeries :: keSeries :: peSeries :: thermalEnergySeries :: appliedWorkSeries :: gravityWorkSeries :: frictionWorkSeries :: Nil

  val parallelForceControlGraph = new RampGraph(appliedForceSeries, canvas, timeseriesModel, updateableObject, model) {
    setDomainUpperBound(20)
    for (s <- forceSeriesList.tail) addSeries(s)
  }

  parallelForceControlGraph.addControl(new SeriesSelectionControl("forces.parallel-title-with-units".translate, 5) {
    addToGrid(appliedForceSeries, createEditableLabel)
    for (s <- forceSeriesList.tail) addToGrid(s)
  })

  val workEnergyGraph = new RampGraph(totalEnergySeries, canvas, timeseriesModel, updateableObject, model) {
    setEditable(false)
    setDomainUpperBound(20)
    for (s <- energyWorkSeriesList.tail) addSeries(s)
  }
  workEnergyGraph.addControl(new SeriesSelectionControl("forces.work-energy-title-with-units".translate, 7) {
    for (s <- energyWorkSeriesList) addToGrid(s)
  })

  val parallelForcesString = "forces.parallel-title".translate
  val graphs = if (showEnergyGraph) Array(new MinimizableControlGraph(parallelForcesString, parallelForceControlGraph), new MinimizableControlGraph("forces.work-energy-title".translate, workEnergyGraph))
  else Array(new MinimizableControlGraph(parallelForcesString, parallelForceControlGraph))

  val graphSetNode = new GraphSetNode(new GraphSetModel(new GraphSuite(graphs))) {
    override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
    setAlignedLayout()
  }

  addChild(graphSetNode)
  updatePosition()
}
