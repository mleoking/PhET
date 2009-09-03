package edu.colorado.phet.motionseries.model

import scalacommon.util.Observable
import motionseries.MotionSeriesResources
import motionseries.MotionSeriesResources._

//immutable memento for recording
case class MotionSeriesObjectState(name: String, mass: Double, kinFric: Double, statFric: Double, height: Double,
                                imageFilename: String, iconFilename: String, customizable: Boolean, points: Int, objectType: MotionSeriesObjectState => MotionSeriesObject) {
  def toObject = objectType(this)
}

class MotionSeriesObject(_name: String,
                      protected var _mass: Double,
                      protected var _kineticFriction: Double,
                      protected var _staticFriction: Double,
                      _height: Double,
                      _imageFilename: String,
                      val iconFilename: String,
                      _customizable: Boolean,
                      val points: Int) {
  val customizable = _customizable
  val name = _name

  def state = new MotionSeriesObjectState(name, mass, kineticFriction, staticFriction, height,
    imageFilename, iconFilename, customizable, points, createFactory)

  def createFactory(state: MotionSeriesObjectState) = new MotionSeriesObject(state.name, state.mass, state.kinFric, state.statFric, state.height,
    state.imageFilename, state.iconFilename, state.customizable, state.points)

  def kineticFriction = _kineticFriction

  def staticFriction = _staticFriction

  def mass: Double = _mass

  val imageFilename = _imageFilename

  def getDisplayText = "object.description.pattern.name_mass".translate.messageformat(name, mass.toString)

  def getDisplayTextHTML = "object.description.html.pattern.name_mass".translate.messageformat(name, mass.toString)

  def this(name: String, mass: Double, kineticFriction: Double, staticFriction: Double, height: Double, imageFilename: String, points: Int) = this (name, mass, kineticFriction, staticFriction, height, imageFilename, imageFilename, false, points)

  def height = _height

  val bufferedImage = MotionSeriesResources.getImage(imageFilename)

  def width = bufferedImage.getWidth * height / bufferedImage.getHeight.toDouble

  def displayTooltip = true

  override def equals(obj: Any) = {
    obj match {
      case a: MotionSeriesObject => a.name == name && a.mass == mass && a.height == height && a.kineticFriction == kineticFriction
      case _ => false
    }
  }

  override def hashCode = mass.hashCode + name.hashCode * 17
}

class CustomTextMotionSeriesObject(name: String, mass: Double, kineticFriction: Double, staticFriction: Double, height: Double, imageFilename: String, points: Int, iconFilename: String, customizable: Boolean)
        extends MotionSeriesObject(name, mass, kineticFriction, staticFriction, height, imageFilename, iconFilename, customizable, points) {
  override def getDisplayText = name

  override def getDisplayTextHTML = "object.custom.description.html.pattern.name".translate.messageformat(name)

  override def displayTooltip = false
}

class MutableMotionSeriesObject(name: String, __mass: Double, kineticFriction: Double, staticFriction: Double, height: Double, imageFilename: String, points: Int, iconFilename: String, customizable: Boolean)
        extends CustomTextMotionSeriesObject(name, __mass, kineticFriction, staticFriction, height, imageFilename, points, iconFilename, customizable) with Observable {
  def mass_=(m: Double) = {
    _mass = m
    notifyListeners()
  }

  override def height = mass / 20 / 2

  def kineticFriction_=(k: Double) = {
    _kineticFriction = k
    notifyListeners()
  }

  def staticFriction_=(s: Double) = {
    _staticFriction = s
    notifyListeners()
  }
}