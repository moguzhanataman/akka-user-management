package com.example.model

case class User(
  var email: String,
  var id: Option[Long] = None,
  var name: Option[String] = None,
  var pass: Option[String] = None,

  var isAdmin: Option[Boolean] = Some(false),
  var isPersonnel: Option[Boolean] = Some(false),
  var isVerified: Option[Boolean] = Some(false)
)
