package com.example.actors

import akka.actor._
import com.example.actors.UserSupervisorActor._
import com.example.model._

import scala.collection.mutable.HashMap

class UserSupervisorActor extends Actor {

  var lastId = 0L
  val users = HashMap.empty[Long, ActorRef]

  def getUsers = users

  override def receive = {

    // Register user: Create new user actor
    case Register(email: String, pass: Option[String]) => {

      val userActor = context.actorOf(UserActor.props(email), email)
      val currentId = lastId
      users += (currentId -> userActor)
//      userActor ! InitUserCommand(email, currentId)
      lastId = lastId + 1
    }

    case FindUser(user: User) => {
      sender() ! user
    }

    case FindUserById(id: Long) => {
//      users.get(id).get forward GetUser
    }
  }
}

object UserSupervisorActor {
  def props = Props[UserSupervisorActor]

  case class Register(email: String, pass: Option[String])

  case class FindUser(user: User)

  case class FindUserById(id: Long)

}