package edu.luc.edu.cs473.pp

import scala.actors._

case class Lynx(
  age: Int, //initial age
  maxLifeSpan: Int, //max-lynx-age
  energy: Int, //initialize energy  
  energyGain: Int, //energy-per-hare-eaten
  energyUse: Int, //lynx-energy-to-reproduce
  startX: Int, startY: Int //initial lynx position
  )
  extends PredatorPreyAgent(age, maxLifeSpan, startX, startY) {

  private var currentEnergy: Int = energy

  def act() {
    Actor.loop {
      react {
        case "alive" => {
          run()
          consumeEnergy() //"set-energy"
          isDying()
          tryToEat() //search hare
          tryToMakeKitten()
          setAge()
          isDying()
        }
        case h: Hare => killHare(h) //got the hare
        case "die" => quit()
        case _ => displayMessage("Lynx:" + hashCode() + " got message. ")
      }
    }
  }

  /**
   * return current Energy
   */
  def getEnergy(): Int = currentEnergy

  /**
   * Consume energy
   */
  private def consumeEnergy() = currentEnergy -= 1

  /**
   * Gain energy from eat hare
   */
  private def addEnergy() = currentEnergy += energyGain

  /**
   * Try to catch hare
   */
  private def tryToEat() = WorldActor ! ("whereishare", this)

  /**
   * eat the hare
   */
  private def killHare(hare: Hare) = {
    if (!hare.getDying()) {
      hare ! "die" //kill this hare
      addEnergy()
    }
  }

  /**
   * Try to Make Kittens
   */
  private def tryToMakeKitten() = {
    if (canReproduce()) {
      //send world message to generate a new Kittens
      WorldActor !
        new Lynx(0, maxLifeSpan,
          currentEnergy / 2 + 1, // Kitten starts with 1/2 of the parents energy
          energyGain, energyUse,
          getX(), getY())

      //reduce current energy
      currentEnergy /= 2
    }
  }

  /**
   * to check energy is meet the require condition
   */
  private def canReproduce(): Boolean =
    (currentEnergy > energyUse) && getAge() > 0

  override def die() = WorldActor ! this

  override def isDying() = {
    //println("Age: " +getAge() + " Energy: " + currentEnergy)
    if (getAge() > maxLifeSpan || currentEnergy < 0) {
      quit()
    }
  }
}

