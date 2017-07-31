package com.example.actors

import akka.actor.{ActorSystem, PoisonPill}
import akka.testkit.{ImplicitSender, TestKit}
import com.example.actors.UserActor.{GetUserRequest, GetUserResponse, UpdateUserCommand, UpdateUserResponse}
import com.example.model.User
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class UserActorSpec
  extends TestKit(ActorSystem("UserActorSpec"))
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "UserActor" should {
    "add an item and preserve after restart" in {
      val email = "example@example.com"
      val userActor = system.actorOf(UserActor.props(email))

      val userObj = User(email, name = Some("Oguzhan"), isAdmin = Some(true))

      userActor ! UpdateUserCommand(userObj)
      expectMsg(UpdateUserResponse(userObj))

      userActor ! PoisonPill

      val userActor2 = system.actorOf(UserActor.props(email))
      userActor2 ! GetUserRequest
      expectMsg(GetUserResponse(userObj))
    }
  }
}
