package com.example

import akka.actor.{ ActorSystem, InvalidActorNameException }
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport.defaultNodeSeqMarshaller
import akka.http.scaladsl.server.{ HttpApp, Route }
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.example.actors.UserActor.GetUserRequest
import com.example.actors.UserSupervisorActor._
import com.example.actors._
import com.example.model._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{ Failure, Success }

object WebServerHttpApp extends HttpApp with App {

  implicit val system = ActorSystem("actor-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5 second)

  val userSupervisorActor = system.actorOf(UserSupervisorActor.props)

  def routes: Route =
    pathEndOrSingleSlash { // Listens to the top `/`
      complete("Server up and running") // Completes with some text
    } ~
      path("hello") { // Listens to paths that are exactly `/hello`
        get { // Listens only to GET requests
          complete(
            <html>
              <body>
                <h1>Say hello to akka-http</h1>
              </body>
            </html>
          ) // Completes with some text
        }
      } ~
      path("user") {
        post {
          formFields("email", "pass".?) { (email, pass) =>
            val result = try {
              userSupervisorActor ! Register(email, pass)

              s"$email saved [success]"
            } catch {
              case invalidName: InvalidActorNameException => {
                println("invalid act name")
                "User already created. [fail]"
              }
            }

            println(result)

            complete(result)
          }
        }
      } ~
      path("user" / IntNumber) { id =>
        get {
          val resultFuture = (userSupervisorActor ? FindUserById(id)).mapTo[User]
          val response = resultFuture.map { user =>
            s"Email: ${user.email}"
          }
          complete(response)
        }
      } ~
      path("user" / "list") {
        complete("")
      }

  // This will start the server until the return key is pressed
  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ system.terminate()) // and shutdown when done
}
