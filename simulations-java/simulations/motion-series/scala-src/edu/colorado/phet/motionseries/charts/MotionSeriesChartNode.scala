package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.motionseries.graphics.MotionSeriesCanvas
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.colorado.phet.common.motion.graphs._
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.MotionSeriesDefaults._

//This adds information about charts and chart serieses
abstract class MotionSeriesChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel)
        extends AbstractChartNode(canvas, model) {
  def forceGraph: MotionSeriesGraph = forceGraph(true)

  def forceGraph(showGravitySeries: Boolean): MotionSeriesGraph = {
    val parallelAppliedForceVariable = new MotionSeriesDefaultTemporalVariable(model) {
      override def setValue(value: Double) = model.bead.parallelAppliedForce = value
    }
    model.stepListeners += (() => parallelAppliedForceVariable.doAddValue(model.bead.appliedForce.dot(model.bead.getRampUnitVector), model.getTime))

    val appliedForceSeries = new ControlGraphSeries(formatForce("forces.applied".translate), appliedForceColor, abbrevUnused, N, characterUnused, parallelAppliedForceVariable)
    val frictionSeries = new ControlGraphSeries(formatForce("forces.friction".translate), frictionForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.frictionForce))
    val netForceSeries = new ControlGraphSeries(formatForce("forces.Net".translate), totalForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.totalForce))
    val wallSeries = new ControlGraphSeries(formatForce("forces.Wall".translate), wallForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.wallForce))
    val forceSeriesList = if (showGravitySeries) {
      val gravitySeries = new ControlGraphSeries(formatForce("forces.Gravity".translate), gravityForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.gravityForce))
      appliedForceSeries :: frictionSeries :: gravitySeries :: wallSeries :: netForceSeries :: Nil
    }
    else appliedForceSeries :: frictionSeries :: wallSeries :: netForceSeries :: Nil

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
    val energySeriesList = totalEnergySeries :: keSeries :: peSeries :: thermalEnergySeries :: Nil

    val theEnergyGraph = new MotionSeriesGraph(totalEnergySeries, canvas, timeseriesModel, updateableObject, model) {
      setEditable(false)
      setDomainUpperBound(20)
      getJFreeChartNode.setBuffered(false)
      getJFreeChartNode.setPiccoloSeries()
      for (s <- energySeriesList.tail) addSeries(s)
      addControl(new SeriesSelectionControl("forces.energy-title-with-units".translate, 7) {
        for (s <- energySeriesList) addToGrid(s)
      })
    }

    theEnergyGraph
  }

  def workEnergyGraph = {
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
      addControl(new SeriesSelectionControl("forces.work-energy-title-with-units".translate, 7) {
        for (s <- energyWorkSeriesList) addToGrid(s)
      })
    }

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
      addControl(new SeriesSelectionControl("forces.work-energy-title-with-units".translate, 7) {
        addToGrid(keSeries)
      })
    }
    keGraph
  }

  def accelerationGraph(editable: Boolean) = {
    val accelerationVariable = new MotionSeriesDefaultTemporalVariable(model) {
      override def setValue(accel: Double) = {
        model.bead.setAccelerationMode()
        val desiredTotalForce = accel * model.bead.mass
        val currentTotalForceWithoutAppliedForce = model.bead.getParallelComponent(model.bead.totalForce - model.bead.appliedForce)
        val appliedForce = desiredTotalForce - currentTotalForceWithoutAppliedForce
        model.bead.parallelAppliedForce = appliedForce
      }
    }
    model.stepListeners += (() => accelerationVariable.doAddValue(model.bead.acceleration, model.getTime))
    val accelerationSeries = new ControlGraphSeries("Acceleration", MotionSeriesDefaults.accelerationColor, "accel", "m/s/s", characterUnused, accelerationVariable)
    val accelerationGraph = new MotionSeriesGraph(accelerationSeries, canvas, timeseriesModel, updateableObject, model) {
      setVerticalRange(-100, 100)
      addControl(new SeriesSelectionControl("", 1) {
        addComponent(new SeriesControlTitleLabel(accelerationSeries))
        addComponent(if (editable) createEditableLabel(accelerationSeries) else createLabel(accelerationSeries))
      })
      setEditable(editable)
    }
    accelerationGraph
  }

  def velocityGraph(editable: Boolean) = {
    val velocityVariable = new MotionSeriesDefaultTemporalVariable(model) {
      override def setValue(v: Double) = {
        model.bead.setVelocityMode()
        model.bead.setVelocity(v)
        //todo: switch into a mode where position is computed by integration and acceleration is computed by derivative
        //we already have integration for position by default
      }
    }
    model.stepListeners += (() => velocityVariable.doAddValue(model.bead.velocity, model.getTime))
    val velocitySeries = new ControlGraphSeries("Velocity", MotionSeriesDefaults.velocityColor, "vel", "m/s", characterUnused, velocityVariable)
    val velocityGraph = new MotionSeriesGraph(velocitySeries, canvas, timeseriesModel, updateableObject, model) {
      setVerticalRange(-50, 50)
      addControl(new SeriesSelectionControl("", 1) {
        addComponent(new SeriesControlTitleLabel(velocitySeries))
        addComponent(if (editable) createEditableLabel(velocitySeries) else createLabel(velocitySeries))
      })
      setEditable(editable)
    }
    velocityGraph
  }

  def positionGraph(editable: Boolean) = {
    val positionVariable = new MotionSeriesDefaultTemporalVariable(model) {
      override def setValue(x: Double) = {
        model.bead.setPositionMode()
        model.bead.setDesiredPosition(x)
      }
    }
    model.stepListeners += (() => positionVariable.doAddValue(model.bead.position, model.getTime))
    val positionSeries = new ControlGraphSeries("Position", MotionSeriesDefaults.positionColor, "x", "m", characterUnused, positionVariable)
    val positionGraph = new MotionSeriesGraph(positionSeries, canvas, timeseriesModel, updateableObject, model) {
      setVerticalRange(-10, 10)
      addControl(new SeriesSelectionControl("", 1) {
        addComponent(new SeriesControlTitleLabel(positionSeries))
        addComponent(if (editable) createEditableLabel(positionSeries) else createLabel(positionSeries))
      })
      setEditable(editable)
    }
    positionGraph
  }
}