package edu.colorado.phet.motionseries.charts

import charts.{SeriesControlTitleLabel, AbstractChartNode, SeriesSelectionControl, MotionSeriesGraph}
import graphics.MotionSeriesCanvas
import model.MotionSeriesModel
import phet.common.motion.model.DefaultTemporalVariable
import phet.common.motion.graphs._
import motionseries.MotionSeriesResources._
import motionseries.MotionSeriesDefaults
import motionseries.MotionSeriesDefaults._

//This adds information about charts and chart serieses
abstract class MotionSeriesChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel)
        extends AbstractChartNode(canvas, model) {
  def forceGraph:MotionSeriesGraph = forceGraph(true)
  def forceGraph(showGravityWallNormal: Boolean):MotionSeriesGraph= {
    val parallelAppliedForceVariable = new DefaultTemporalVariable() {
      override def setValue(value: Double) = model.bead.parallelAppliedForce = value
    }
    model.stepListeners += (() => {
      if (inTimeRange(model.getTime))
        parallelAppliedForceVariable.addValue(model.bead.appliedForce.dot(model.bead.getRampUnitVector), model.getTime)
    })

    val appliedForceSeries = new ControlGraphSeries(formatForce("forces.applied".translate), appliedForceColor, abbrevUnused, N, characterUnused, parallelAppliedForceVariable)
    val frictionSeries = new ControlGraphSeries(formatForce("forces.friction".translate), frictionForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.frictionForce))
    val netForceSeries = new ControlGraphSeries(formatForce("forces.Net".translate), totalForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.totalForce))
    val forceSeriesList = if (showGravityWallNormal) {
      val gravitySeries = new ControlGraphSeries(formatForce("forces.Gravity".translate), gravityForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.gravityForce))
      val wallSeries = new ControlGraphSeries(formatForce("forces.Wall".translate), wallForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.wallForce))
      appliedForceSeries :: frictionSeries :: gravitySeries :: wallSeries :: netForceSeries :: Nil
    }
    else appliedForceSeries :: frictionSeries :: netForceSeries :: Nil

    val parallelForceControlGraph = new MotionSeriesGraph(appliedForceSeries, canvas, timeseriesModel, updateableObject, model) {
      for (s <- forceSeriesList.tail) addSeries(s)
      addControl(new SeriesSelectionControl("forces.parallel-title-with-units".translate, 5) {
        addToGrid(appliedForceSeries, createEditableLabel)
        for (s <- forceSeriesList.tail) addToGrid(s)
      })
    }
    parallelForceControlGraph
  }

  def energyGraph = {
    val totalEnergySeries = new ControlGraphSeries(formatEnergy("energy.total".translate), totalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getTotalEnergy))
    val keSeries = new ControlGraphSeries(formatEnergy("energy.kinetic".translate), kineticEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getKineticEnergy))
    val peSeries = new ControlGraphSeries(formatEnergy("energy.potential".translate), potentialEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getPotentialEnergy))
    val thermalEnergySeries = new ControlGraphSeries(formatEnergy("energy.thermal".translate), thermalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getThermalEnergy))
    val appliedWorkSeries = new ControlGraphSeries(formatWork("work.applied".translate), appliedWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getAppliedWork))
    val gravityWorkSeries = new ControlGraphSeries(formatWork("work.gravity".translate), gravityWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getGravityWork))
    val frictionWorkSeries = new ControlGraphSeries(formatWork("work.friction".translate), frictionWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getFrictiveWork))
    val energyWorkSeriesList = totalEnergySeries :: keSeries :: peSeries :: thermalEnergySeries :: appliedWorkSeries :: gravityWorkSeries :: frictionWorkSeries :: Nil

    val workEnergyGraph = new MotionSeriesGraph(totalEnergySeries, canvas, timeseriesModel, updateableObject, model) {
      setEditable(false)
      setDomainUpperBound(20)
      getJFreeChartNode.setBuffered(false)
      getJFreeChartNode.setPiccoloSeries()
      for (s <- energyWorkSeriesList.tail) addSeries(s)
    }
    workEnergyGraph.addControl(new SeriesSelectionControl("forces.work-energy-title-with-units".translate, 7) {
      for (s <- energyWorkSeriesList) addToGrid(s)
    })
    workEnergyGraph
  }

  def kineticEnergyGraph = {
    val keSeries = new ControlGraphSeries(formatEnergy("energy.kinetic".translate), kineticEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getKineticEnergy))

    val keGraph = new MotionSeriesGraph(keSeries, canvas, timeseriesModel, updateableObject, model) {
      setEditable(false)
      setDomainUpperBound(20)
      setVerticalRange(-10000, 10000)
      getJFreeChartNode.setBuffered(false)
      getJFreeChartNode.setPiccoloSeries()
    }
    keGraph.addControl(new SeriesSelectionControl("forces.work-energy-title-with-units".translate, 7) {
      addToGrid(keSeries)
    })
    keGraph
  }

  def accelerationGraph = {
    val accelerationVariable = new DefaultTemporalVariable() {
      override def setValue(accel: Double) = {
        model.bead.setAccelerationMode()
        val desiredTotalForce = accel * model.bead.mass
        val currentTotalForceWithoutAppliedForce = model.bead.getParallelComponent(model.bead.totalForce - model.bead.appliedForce)
        val appliedForce = desiredTotalForce - currentTotalForceWithoutAppliedForce
        model.bead.parallelAppliedForce = appliedForce
      }
    }
    model.stepListeners += (() => {if (inTimeRange(model.getTime)) accelerationVariable.addValue(model.bead.acceleration, model.getTime)})
    val accelerationSeries = new ControlGraphSeries("Acceleration", MotionSeriesDefaults.accelerationColor, "accel", "m/s/s", characterUnused, accelerationVariable)
    val accelerationGraph = new MotionSeriesGraph(accelerationSeries, canvas, timeseriesModel, updateableObject, model) {
      setVerticalRange(-100, 100)
      addControl(new SeriesSelectionControl("", 1) {
        addComponentsToGrid(new SeriesControlTitleLabel(accelerationSeries), createEditableLabel(accelerationSeries))
      })
    }
    accelerationGraph
  }

  def velocityGraph = {
    val velocityVariable = new DefaultTemporalVariable() {
      override def setValue(v: Double) = {
        model.bead.setVelocityMode()
        model.bead.setVelocity(v)
        //todo: switch into a mode where position is computed by integration and acceleration is computed by derivative
        //we already have integration for position by default
      }
    }
    model.stepListeners += (() => {if (inTimeRange(model.getTime)) velocityVariable.addValue(model.bead.velocity, model.getTime)})
    val velocitySeries = new ControlGraphSeries("Velocity", MotionSeriesDefaults.velocityColor, "vel", "m/s", characterUnused, velocityVariable)
    val velocityGraph = new MotionSeriesGraph(velocitySeries, canvas, timeseriesModel, updateableObject, model) {
      setVerticalRange(-50, 50)
      addControl(new SeriesSelectionControl("", 1) {
        addToGrid(velocitySeries, createEditableLabel)
      })
    }
    velocityGraph
  }

  def positionGraph = {
    val positionVariable = new DefaultTemporalVariable() {
      override def setValue(x: Double) = {
        model.bead.setPositionMode()
        model.bead.setDesiredPosition(x)
      }
    }
    model.stepListeners += (() => {if (inTimeRange(model.getTime)) positionVariable.addValue(model.bead.position, model.getTime)})
    val positionSeries = new ControlGraphSeries("Position", MotionSeriesDefaults.positionColor, "x", "m", characterUnused, positionVariable)
    val positionGraph = new MotionSeriesGraph(positionSeries, canvas, timeseriesModel, updateableObject, model) {
      setVerticalRange(-10, 10)
      addControl(new SeriesSelectionControl("", 1) {
        addToGrid(positionSeries, createEditableLabel)
      })
    }
    positionGraph
  }
}