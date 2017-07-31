/*
Heavily influenced by this blog post:
http://tudorzgureanu.com/akka-persistence-testing-persistent-actors/
 */

package com.example.actors

import akka.actor._
import akka.persistence.{PersistentActor, RecoveryCompleted}
import com.example.actors.UserActor._
import com.example.model.User

// User must have email, everything else will be recoverable & editable
class UserActor(email: String) extends PersistentActor with ActorLogging {
  var state: User = User(email)
  override def persistenceId: String = email

  override def receiveCommand = {
//    case InitUserCommand(email, id) =>
//      persist(UserInitialized(email, id)) { evt =>
//        state = applyEvent(evt)
//        sender() ! InitUserResponse(state)
//      }

    case UpdateUserCommand(user: User) =>
      persist(UserUpdated(user)) { evt =>
        state = applyEvent(evt)
        sender() ! UpdateUserResponse(user)
      }

    case GetUserRequest =>
      sender ! GetUserResponse(state)
  }


  override def receiveRecover: Receive = {
    case evt: UserEvent => state = applyEvent(evt)
    case RecoveryCompleted => log.info("User recovered.")
  }

  private def applyEvent(userEvent: UserEvent): User = userEvent match {
    case UserInitialized(email, id) => User(email, Some(id))
    case UserUpdated(user) => user
  }
}

object UserActor {
  def props(email: String): Props = Props(new UserActor(email))

  // protocol
//  case class InitUserCommand(email: String, id: Long)
//  case class InitUserResponse(user: User)

  case class UpdateUserCommand(user: User)
  case class UpdateUserResponse(user: User)

  case object GetUserRequest
  case class GetUserResponse(user: User)

  // events
  sealed trait UserEvent
  case class UserInitialized(email: String, id: Long) extends UserEvent
  case class UserUpdated(user: User) extends UserEvent
}
