// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab

object ForceLawLabDefaults {

  //  sun earth distance in m
  val sunMercuryDist = 5.791E10

  //  sun earth distance in m
  val sunEarthDist = 1.496E11
  val earthRadius = 6.371E6
  val sunRadius = 6.955E8

  //masses are in kg
  val earthMass = 5.9742E24
  val sunMass = 1.9891E30

  //Gravitational constant in MKS
  val G = 6.67E-11
  val metersPerLightMinute = 5.5594E-11

  def metersToLightMinutes(a: Double) = a * metersPerLightMinute

  def lightMinutesToMeters(a: Double) = a / metersPerLightMinute

  def kgToEarthMasses(a: Double) = a / earthMass

  def earthMassesToKg(a: Double) = a * earthMass
}