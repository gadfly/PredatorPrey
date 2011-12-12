package edu.luc.edu.cs473.pp

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class TestPrey extends FunSuite {

  /**
   * Test Lynx age and energy
   */
  def testActor(description: String, l: Lynx, message: String, age: Int, energy: Int, dying: Boolean) = {
    test(description) {
      
      l ! message
      Thread.sleep(100)
      
      assert(l.getAge() === age)
      assert(l.getEnergy() === energy)
      assert(l.getX() != -1)
      assert(l.getY() != -1)
      
      if (dying) {
        assert(l.getDying() === true)
      }
    }
  }

  /** test move to X,Y
   * 
   */
  def testMoveToXY(description: String, l: Lynx, x: Int, y: Int) = {
    test(description) {
      l ! (x, y)
      Thread.sleep(100)
      
      assert(l.getX() === x)
      assert(l.getY() === y)
    }
  }
  
  
  val l: Lynx = new Lynx(
    0,
    Configure.LynxMaxAge,
    Configure.EnergyUseReproduce,
    Configure.EnergyGainPreHare,
    Configure.EnergyUseReproduce,
    -1,
    -1)
  l.start()
  

  testMoveToXY("test move to new position", l, 100, 100)

  /**
   * test lynx actor with alive message
   */
  for (i <- (1 to Configure.LynxMaxAge -1 )) {
    testActor("testcase" + i, l, "alive", i, Configure.EnergyUseReproduce - i,
      (Configure.EnergyUseReproduce - i < 0 || i > Configure.LynxMaxAge))
  }
}
