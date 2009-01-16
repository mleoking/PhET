package edu.colorado.phet.movingman.ladybug

class Property[T](getter: () => T, setter: (T) => Unit, observable: ObservableS) extends ObservableS { //todo: make it a trait
  def getValue(): T = getter()

  def setValue(t: T): Unit = setter(t)

  observable.addListener(() => notifyListeners)
}