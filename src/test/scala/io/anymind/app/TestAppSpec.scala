package io.anymind.app

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

trait TestAppSpec
  extends FeatureSpec
    with GivenWhenThen
    with MockFactory
    with Matchers

