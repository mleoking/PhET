package com.liftworkshop.snippet

import net.liftweb.http.S

case class SimulationEntry(project: String, sim: String) {
  def this(name: String) = this(name, name)
}

trait SimInfo{
  def getTitle(entry:SimulationEntry):String
}

class SimTable {
  object StaticInfo extends SimInfo {
    def getTitle(entry: SimulationEntry) = entry.sim.toUpperCase //todo: actually return sim title
  }

  //TODO load simulations from database/disk
  val simInfo=StaticInfo
  def sims = SimulationEntry("circuit-construction-kit", "circuit-construction-kit-dc") ::
          SimulationEntry("glaciers","glaciers")::SimulationEntry("projectile-motion","projectile-motion")::Nil

  //Generate HTML For the simulation table
  def display =
    <table id="mini_sim_table">
    <tr>{getEntries}</tr>
    </table>

  //Generate a sequence of sim entries
  def getEntries = for (entry <- sims) yield {
    <td>
    <div class="mini_sim_group">
    <a href={"simpage.html?sim=" + entry.sim}>
    <img src={"data/images/" + entry.sim + "-thumbnail.jpg"}/>
    <div class="mini_sim_title">{simInfo.getTitle(entry)}</div>
    </a>
    </div>
    </td>
  }
}

class SimPage {
  def getSim = S.param("sim").open_!

  def render = <a>Simulation page for {getSim}</a>

  def renderTitle = <a>Simulation page for {getSim}</a>

  def renderImage = {
    <a href={"runsim/" + getSim}>
    <img src={"data/images/" + getSim + "-thumbnail.jpg"} alt="Screenshot for the simulation 'Arithmetic'"/>
    </a>
  }

  def renderDescription = <a>Remember your multiplication tables?...me neither.Brush up on your multiplication, division and factoring skills with this exciting game.No calculators allowed!</a>
}