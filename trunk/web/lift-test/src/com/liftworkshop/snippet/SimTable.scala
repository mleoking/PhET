package com.liftworkshop.snippet

import net.liftweb.http.S

case class Simulation(title:String,simname:String)

class SimTable{
  //TODO load simulations from database/disk
  def simulations =
    new Simulation("Circuit Construction Kit", "circuit-construction-kit-dc") ::
            new Simulation("Glaciers", "glaciers") ::
            new Simulation("Projectile Motion", "projectile-motion") :: Nil

  //Generate HTML For the simulation table
  def display=
    <table id="mini_sim_table">
        <tr>
        {getEntries}
        </tr>
    </table>

  //Generate a sequence of sim entries
  def getEntries=for (sim<-simulations) yield {
    <td>
        <div class="mini_sim_group">
            <a href={"simpage.html?sim="+sim.simname}>
                <img src={"data/images/"+sim.simname+"-thumbnail.jpg"}/>
                <div class="mini_sim_title">{sim.title}</div>
            </a>
        </div>
    </td>
  }
}

class SimPage{
  def render = <b> hello and welcome to {S.param("sim").open_!}</b>
}