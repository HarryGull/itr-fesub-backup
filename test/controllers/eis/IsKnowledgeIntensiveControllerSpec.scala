/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.eis

import auth.{MockAuthConnector, MockConfig}
import common.{Constants, KeystoreKeys}
import config.FrontendAuthConnector
import connectors.{EnrolmentConnector, S4LConnector}
<<<<<<< HEAD:test/controllers/eis/IsKnowledgeIntensiveControllerSpec.scala
import controllers.helpers.ControllerSpec
=======
import helpers.BaseSpec
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/IsKnowledgeIntensiveControllerSpec.scala
import models._
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._

import scala.concurrent.Future

class IsKnowledgeIntensiveControllerSpec extends BaseSpec {

  object TestController extends IsKnowledgeIntensiveController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val s4lConnector = mockS4lConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
  }

  val updatedKIModel = KiProcessingModel(Some(true),Some(false), Some(false), Some(false), None, Some(false))
  val missingDateKIModel = KiProcessingModel(Some(true),None, Some(false), Some(false), None, Some(false))

  def setupShowMocks(isKnowledgeIntensiveModel: Option[IsKnowledgeIntensiveModel] = None): Unit =
    when(mockS4lConnector.fetchAndGetFormData[IsKnowledgeIntensiveModel](Matchers.any())(Matchers.any(), Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(isKnowledgeIntensiveModel))

  def setupSubmitMocks(kiProcessingModel: Option[KiProcessingModel] = None): Unit =
    when(mockS4lConnector.fetchAndGetFormData[KiProcessingModel](Matchers.eq(KeystoreKeys.kiProcessingModel))(Matchers.any(), Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(kiProcessingModel))

  "IsKnowledgeIntensiveController" should {
    "use the correct keystore connector" in {
      IsKnowledgeIntensiveController.s4lConnector shouldBe S4LConnector
    }
    "use the correct auth connector" in {
      IsKnowledgeIntensiveController.authConnector shouldBe FrontendAuthConnector
    }
    "use the correct enrolment connector" in {
      IsKnowledgeIntensiveController.enrolmentConnector shouldBe EnrolmentConnector
    }
  }

  "Sending a GET request to IsKnowledgeIntensiveController when authenticated and enrolled" should {
    "return a 200 when something is fetched from keystore" in {
      setupShowMocks(Some(isKnowledgeIntensiveModelYes))
      mockEnrolledRequest(eisSchemeTypesModel)
      showWithSessionAndAuth(TestController.show)(
        result => status(result) shouldBe OK
      )
    }

    "provide an empty model and return a 200 when nothing is fetched using keystore" in {
      setupShowMocks()
      mockEnrolledRequest(eisSchemeTypesModel)
      showWithSessionAndAuth(TestController.show)(
        result => status(result) shouldBe OK
      )
    }
  }

<<<<<<< HEAD:test/controllers/eis/IsKnowledgeIntensiveControllerSpec.scala
  "Sending a GET request to IsKnowledgeIntensiveController when authenticated and NOT enrolled" should {
    "redirect to the Subscription Service" in {
      setupShowMocks(Some(isKnowledgeIntensiveModelYes))
      mockNotEnrolledRequest()
      showWithSessionAndAuth(TestController.show)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(FrontendAppConfig.subscriptionUrl)
        }
      )
    }
  }

  "Sending an Unauthenticated request with a session to IsKnowledgeIntensiveController" should {
    "return a 302 and redirect to GG login" in {
      showWithSessionWithoutAuth(TestController.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-submission-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a request with no session to IsKnowledgeIntensiveController" should {
    "return a 302 and redirect to GG login" in {
      showWithoutSession(TestController.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-submission-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a timed-out request to IsKnowledgeIntensiveController" should {
    "return a 302 and redirect to the timeout page" in {
      showWithTimeout(IsKnowledgeIntensiveController.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.routes.TimeoutController.timeout().url)
        }
      )
    }
  }

=======
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/IsKnowledgeIntensiveControllerSpec.scala
  "Sending a valid 'Yes' form submit to the IsKnowledgeIntensiveController when authenticated and enrolled" should {
    "redirect to the operating costs page" in {
      setupSubmitMocks(Some(updatedKIModel))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = "isKnowledgeIntensive" -> Constants.StandardRadioButtonYesValue
      submitWithSessionAndAuth(TestController.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
<<<<<<< HEAD:test/controllers/eis/IsKnowledgeIntensiveControllerSpec.scala
          redirectLocation(result) shouldBe Some("/investment-tax-relief/eis/operating-costs")
=======
          redirectLocation(result) shouldBe Some(routes.OperatingCostsController.show().url)
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/IsKnowledgeIntensiveControllerSpec.scala
        }
      )
    }
  }

  "Sending a valid 'Yes' form submit with missing data in the KI Model to the IsKnowledgeIntensiveController when authenticated and enrolled" should {
    "redirect to the date of incorporation page" in {
      setupSubmitMocks(Some(missingDateKIModel))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = "isKnowledgeIntensive" -> Constants.StandardRadioButtonYesValue
      submitWithSessionAndAuth(TestController.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
<<<<<<< HEAD:test/controllers/eis/IsKnowledgeIntensiveControllerSpec.scala
          redirectLocation(result) shouldBe Some("/investment-tax-relief/eis/date-of-incorporation")
=======
          redirectLocation(result) shouldBe Some(routes.DateOfIncorporationController.show().url)
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/IsKnowledgeIntensiveControllerSpec.scala
        }
      )
    }
  }

  "Sending a valid 'No' form submit with a false KI Model to the IsKnowledgeIntensiveControlle when authenticated and enrolled" should {
    "redirect to the subsidiaries" in {
      setupSubmitMocks(Some(falseKIModel))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = "isKnowledgeIntensive" -> Constants.StandardRadioButtonNoValue
      submitWithSessionAndAuth(TestController.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
<<<<<<< HEAD:test/controllers/eis/IsKnowledgeIntensiveControllerSpec.scala
          redirectLocation(result) shouldBe Some("/investment-tax-relief/eis/subsidiaries")
=======
          redirectLocation(result) shouldBe Some(routes.SubsidiariesController.show().url)
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/IsKnowledgeIntensiveControllerSpec.scala
        }
      )
    }
  }

  "Sending a valid 'No' form submit without a KI Model to the IsKnowledgeIntensiveController when authenticated and enrolled" should {
    "redirect to the date of incorporation" in {
      setupSubmitMocks()
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = "isKnowledgeIntensive" -> Constants.StandardRadioButtonNoValue
      submitWithSessionAndAuth(TestController.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
<<<<<<< HEAD:test/controllers/eis/IsKnowledgeIntensiveControllerSpec.scala
          redirectLocation(result) shouldBe Some("/investment-tax-relief/eis/date-of-incorporation")
=======
          redirectLocation(result) shouldBe Some(routes.DateOfIncorporationController.show().url)
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/IsKnowledgeIntensiveControllerSpec.scala
        }
      )
    }
  }

  "Sending a valid 'No' form submit to the IsKnowledgeIntensiveController when authenticated and enrolled" should {
    "redirect to the subsidiaries" in {
      setupSubmitMocks(Some(updatedKIModel))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = "isKnowledgeIntensive" -> Constants.StandardRadioButtonNoValue
      submitWithSessionAndAuth(TestController.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
<<<<<<< HEAD:test/controllers/eis/IsKnowledgeIntensiveControllerSpec.scala
          redirectLocation(result) shouldBe Some("/investment-tax-relief/eis/subsidiaries")
=======
          redirectLocation(result) shouldBe Some(routes.SubsidiariesController.show().url)
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/IsKnowledgeIntensiveControllerSpec.scala
        }
      )
    }
  }

  "Sending an invalid form submission with validation errors to the IsKnowledgeIntensiveController when authenticated and enrolled" should {
    "redirect to itself" in {
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = "isKnowledgeIntensive" -> ""
      submitWithSessionAndAuth(TestController.submit,formInput)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

<<<<<<< HEAD:test/controllers/eis/IsKnowledgeIntensiveControllerSpec.scala
  "Sending a submission to the IsKnowledgeIntensiveController when not authenticated" should {

    "redirect to the GG login page when having a session but not authenticated" in {
      submitWithSessionWithoutAuth(TestController.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-submission-frontend&accountType=organisation")
        }
      )
    }

    "redirect to the GG login page with no session" in {
      submitWithoutSession(TestController.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-submission-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a submission to the IsKnowledgeIntensiveController when a timeout has occured" should {
    "redirect to the Timeout page when session has timed out" in {
      submitWithTimeout(TestController.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.routes.TimeoutController.timeout().url)
        }
      )
    }
  }

  "Sending a submission to the IsKnowledgeIntensiveController when NOT enrolled" should {
    "redirect to the Subscription Service" in {
      mockNotEnrolledRequest()
      submitWithSessionAndAuth(TestController.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(FrontendAppConfig.subscriptionUrl)
        }
      )
    }
  }

=======
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/IsKnowledgeIntensiveControllerSpec.scala
}
