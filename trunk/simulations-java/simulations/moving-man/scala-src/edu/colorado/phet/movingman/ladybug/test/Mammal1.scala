package edu.colorado.phet.movingman.ladybug.test

case class MammalFood(name: String)
case class Grass(color: String) extends MammalFood("Grass")

class Mammal1 {
  val food = createMammalFood()
  def createMammalFood() = new MammalFood("Mammal Food")
}

class Cow1 extends Mammal1 {
  val myGrass = new Grass("blue")
  override def createMammalFood() = myGrass
  override def toString = "cow1 with food="+food
}

class Mammal2[FoodType](initFood: (Mammal2[FoodType]) => FoodType) {
  val food: FoodType = initFood(this)
}

class Cow2 extends Mammal2[Grass]((m: Mammal2[Grass]) => new Grass("blue")){
  val myGrass = new Grass("blue")
  override def toString = "cow2 with food="+food
}

object TestSubclasses {
  def main(args: Array[String]) {
    println(new Cow1)
    println(new Cow2)
  }
}