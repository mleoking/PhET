package com.liftworkshop.model

import net.liftweb._
import mapper._
import http._
import SHtml._
import util._

class ToDo extends LongKeyedMapper[ToDo] with IdPK {
  def getSingleton = ToDo

  object done extends MappedBoolean(this)
  object owner extends MappedLongForeignKey(this, User)
  object priority extends MappedInt(this) {
    override def defaultValue = 5
  }
  object desc extends MappedPoliteString(this, 128)
}

object ToDo extends ToDo with LongKeyedMetaMapper[ToDo]