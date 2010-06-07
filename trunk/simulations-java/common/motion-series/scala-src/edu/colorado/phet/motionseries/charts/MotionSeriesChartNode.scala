package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.motionseries.graphics.MotionSeriesCanvas
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.colorado.phet.common.motion.graphs._
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.MotionSeriesDefaults._

//This subclass of AbstractChartNode adds information about charts and chart serieses
abstract class MotionSeriesChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel)
        extends AbstractChartNode(canvas, model) {
  def goButtonModel = model.goButtonModel

  def forceGraph: MotionSeriesGraph = forceGraph(true)

  def forceGraph(showGravitySeries: Boolean): MotionSeriesGraph = {
    val parallelAppliedForceVariable = new MotionSeriesDefaultTemporalVariable(model) {
      override def setValue(value: Double) = {
        super.setValue(value)
        model.bead.parallelAppliedForce = value
      }
    }
    model.stepListeners += (() => parallelAppliedForceVariable.doAddValue(model.bead.appliedForce.dot(model.bead.getRampUnitVector), model.getTime))
    model.resetListeners_+=(() => parallelAppliedForceVariable.doAddValue(model.bead.appliedForce.dot(model.bead.getRampUnitVector), model.getTime))
    model.playbackListeners += (() => parallelAppliedForceVariable.setValue(model.bead.appliedForce.dot(model.bead.getRampUnitVector)))

    val appliedForceSeries = new ControlGraphSeries(formatForce("forces.applied".translate), appliedForceColor, abbrevUnused, N, characterUnused, parallelAppliedForceVariable)
    val frictionSeries = new ControlGraphSeries(formatForce("forces.friction".translate), frictionForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.frictionForce))
    val netForceSeries = new ControlGraphSeries(formatForce("forces.Net".translate), totalForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.totalForce))
    val wallSeries = new ControlGraphSeries(formatForce("forces.Wall".translate), wallForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.wallForce))
    val forceSeriesList = if (showGravitySeries) {
      val gravitySeries = new ControlGraphSeries(formatForce("forces.Gravity".translate), gravityForceColor, abbrevUnused, N, characterUnused, createParallelVariable(() => model.bead.gravityForce))
      appliedForceSeries :: frictionSeries :: gravitySeries :: wallSeries :: netForceSeries :: Nil
    }
    else appliedForceSeries :: frictionSeries :: wallSeries :: netForceSeries :: Nil

    val parallelForceControlGraph = new MotionSeriesGraph(appliedForceSeries, canvas, timeseriesModel, updateableObject, model, -2000, 2000) {
      for (s <- forceSeriesList.tail) addSeries(s)
      setAdditionalControlPanelFillNone()
      setAdditionalControlPanelBackground(MotionSeriesDefaults.EARTH_COLOR)
      addControl(new GoButton(goButtonModel, () => model.setPaused(false)))
      addControl(new SeriesSelectionControl("forces.parallel-title-with-units".translate, 5) {
        addToGrid(appliedForceSeries, createEditableLabel)
        for (s <- forceSeriesList.tail) addToGrid(s)
      })
    }

    def resetSeriesVisibility() = for (s <- forceSeriesList) s.setVisible(s == netForceSeries) //only show the net force series on startup

    //do this after adding to the list so the chart gets updated properly
    resetSeriesVisibility()
    model.resetListeners_+=(resetSeriesVisibility)

    parallelForceControlGraph
  }

  def energyGraph = {
    val totalEnergySeries = new ControlGraphSeries(formatEnergy("energy.total".translate), totalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getTotalEnergy))
    val keSeries = new ControlGraphSeries(formatEnergy("energy.kinetic".translate), kineticEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.kineticEnergy))
    val peSeries = new ControlGraphSeries(formatEnergy("energy.potential".translate), potentialEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.potentialEnergy))
    val thermalEnergySeries = new ControlGraphSeries(formatEnergy("energy.thermal".translate), thermalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.thermalEnergy))
    val energySeriesList = totalEnergySeries :: keSeries :: peSeries :: thermalEnergySeries :: Nil

    val theEnergyGraph = new MotionSeriesGraph(totalEnergySeries, canvas, timeseriesModel, updateableObject, model, -10000, 10000) {
      setEditable(false)

      //todo: can these next 2 lines be moved to parent class?
      getJFreeChartNode.setBuffered(false)
      getJFreeChartNode.setPiccoloSeries()
      for (s <- energySeriesList.tail) addSeries(s)
      setAdditionalControlPanelFillNone()
      setAdditionalControlPanelBackground(MotionSeriesDefaults.EARTH_COLOR)
      addControl(new GoButton(goButtonModel, () => model.setPaused(false)))
      addControl(new SeriesSelectionControl("forces.energy-title-with-units".translate, 7) {
        for (s <- energySeriesList) addToGrid(s)
      })
    }

    def resetSeriesVisibility() = for (s <- energySeriesList) s.setVisible(s == totalEnergySeries) //only show the net energy series on startup

    //do this after adding to the list so the chart gets updated properly
    resetSeriesVisibility()
    model.resetListeners_+=(resetSeriesVisibility)

    theEnergyGraph
  }

  def workEnergyGraph = {
    val totalEnergySeries = new ControlGraphSeries(formatEnergy("energy.total".translate), totalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getTotalEnergy))
    val keSeries = new ControlGraphSeries(formatEnergy("energy.kinetic".translate), kineticEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.kineticEnergy))
    val peSeries = new ControlGraphSeries(formatEnergy("energy.potential".translate), potentialEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.potentialEnergy))
    val thermalEnergySeries = new ControlGraphSeries(formatEnergy("energy.thermal".translate), thermalEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.thermalEnergy))
    val appliedWorkSeries = new ControlGraphSeries(formatWork("work.applied".translate), appliedWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getAppliedWork))
    val gravityWorkSeries = new ControlGraphSeries(formatWork("work.gravity".translate), gravityWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getGravityWork))
    val frictionWorkSeries = new ControlGraphSeries(formatWork("work.friction".translate), frictionWorkColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.getFrictiveWork))
    val energyWorkSeriesList = totalEnergySeries :: keSeries :: peSeries :: thermalEnergySeries :: appliedWorkSeries :: gravityWorkSeries :: frictionWorkSeries :: Nil

    val workEnergyGraph = new MotionSeriesGraph(totalEnergySeries, canvas, timeseriesModel, updateableObject, model, -2000, 2000) {
      setEditable(false)
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
    val keSeries = new ControlGraphSeries(formatEnergy("energy.kinetic".translate), kineticEnergyColor, abbrevUnused, J, characterUnused, createVariable(() => model.bead.kineticEnergy))

    val keGraph = new MotionSeriesGraph(keSeries, canvas, timeseriesModel, updateableObject, model, -10000, 10000) {
      setEditable(false)
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
        super.setValue(accel)
        model.bead.setAccelerationMode()
        val desiredTotalForce = accel * model.bead.mass
        val currentTotalForceWithoutAppliedForce = model.bead.getParallelComponent(model.bead.totalForce - model.bead.appliedForce)
        val appliedForce = desiredTotalForce - currentTotalForceWithoutAppliedForce
        model.bead.parallelAppliedForce = appliedForce
      }
    }
    val updater = () => accelerationVariable.doAddValue(model.bead.acceleration, model.getTime)
    model.stepListeners += updater
    model.resetListeners_+=(updater)
    val accelerationSeries = new ControlGraphSeries("properties.acceleration".translate, MotionSeriesDefaults.accelerationColor, "accel".literal, "properties.acceleration.units".translate, characterUnused, accelerationVariable)
    val accelerationGraph = new MotionSeriesGraph(accelerationSeries, canvas, timeseriesModel, updateableObject, model, -50, 50) {
      addControl(new SeriesSelectionControl(1) {
        addComponentsToGrid(new SeriesControlTitleLabel(accelerationSeries), if (editable) createEditableLabel(accelerationSeries) else createLabel(accelerationSeries))
      })
      setEditable(editable)
    }
    accelerationGraph
  }

  def velocityGraph(editable: Boolean) = {
    val velocityVariable = new MotionSeriesDefaultTemporalVariable(model) {
      override def setValue(v: Double) = {
        super.setValue(v)
        model.bead.setVelocityMode()
        model.bead.setVelocity(v)
        //todo: switch into a mode where position is computed by integration and acceleration is computed by derivative
        //we already have integration for position by default
      }
    }
    val updater = () => velocityVariable.doAddValue(model.bead.velocity, model.getTime)
    model.stepListeners += updater
    model.resetListeners_+=(updater)
    val velocitySeries = new ControlGraphSeries("properties.velocity".translate, MotionSeriesDefaults.velocityColor, "vel".literal, "properties.velocity.units".translate, characterUnused, velocityVariable)
    val velocityGraph = new MotionSeriesGraph(velocitySeries, canvas, timeseriesModel, updateableObject, model, -25, 25) {
      addControl(new SeriesSelectionControl(1) {
        addComponentsToGrid(new SeriesControlTitleLabel(velocitySeries), if (editable) createEditableLabel(velocitySeries) else createLabel(velocitySeries))
      })
      setEditable(editable)
    }
    velocityGraph
  }

  def positionGraph(editable: Boolean) = {
    val positionVariable = new MotionSeriesDefaultTemporalVariable(model) {
      override def setValue(x: Double) = {
        super.setValue(x)
        model.bead.setPositionMode()
        model.bead.setDesiredPosition(x)
      }
    }
    val updater = () => positionVariable.doAddValue(model.bead.position, model.getTime)
    model.stepListeners += updater
    model.resetListeners_+=(updater)
    val positionSeries = new ControlGraphSeries("properties.position".translate, MotionSeriesDefaults.positionColor, "x".literal, "properties.position.units".translate, characterUnused, positionVariable)
    val positionGraph = new MotionSeriesGraph(positionSeries, canvas, timeseriesModel, updateableObject, model, -10, 10) {
      addControl(new SeriesSelectionControl(1) {
        addComponentsToGrid(new SeriesControlTitleLabel(positionSeries), if (editable) createEditableLabel(positionSeries) else createLabel(positionSeries))
      })
      setEditable(editable)
    }
    positionGraph
  }
}