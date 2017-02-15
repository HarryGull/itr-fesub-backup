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
import common.KeystoreKeys
import config.FrontendAuthConnector
import connectors.{EnrolmentConnector, S4LConnector, SubmissionConnector}
<<<<<<< HEAD:test/controllers/eis/OperatingCostsControllerSpec.scala
import controllers.helpers.ControllerSpec
=======
import helpers.BaseSpec
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/OperatingCostsControllerSpec.scala
import models._
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._

import scala.concurrent.Future

class OperatingCostsControllerSpec extends BaseSpec {

  object TestController extends OperatingCostsController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val s4lConnector = mockS4lConnector
    override lazy val submissionConnector = mockSubmissionConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
  }

  "OperatingCostsController" should {
    "use the correct keystore connector" in {
      OperatingCostsController.s4lConnector shouldBe S4LConnector
    }
    "use the correct auth connector" in {
      OperatingCostsController.authConnector shouldBe FrontendAuthConnector
    }
    "use the correct enrolment connector" in {
      OperatingCostsController.enrolmentConnector shouldBe EnrolmentConnector
    }
    "use the correct submission connector" in {
      OperatingCostsController.submissionConnector shouldBe SubmissionConnector
    }
  }

  def setupShowMocks(operatingCostsModel: Option[OperatingCostsModel] = None): Unit =
    when(mockS4lConnector.fetchAndGetFormData[OperatingCostsModel](Matchers.any())(Matchers.any(), Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(operatingCostsModel))

  def setupSubmitMocks(validConditions: Option[Boolean] = None, kiProcessingModel: Option[KiProcessingModel] = None): Unit = {
    when(mockSubmissionConnector.validateKiCostConditions(Matchers.any(),Matchers.any(),Matchers.any(),Matchers.any(),Matchers.any(),Matchers.any())
    (Matchers.any())).thenReturn(Future.successful(validConditions))
    when(mockS4lConnector.fetchAndGetFormData[KiProcessingModel](Matchers.eq(KeystoreKeys.kiProcessingModel))(Matchers.any(), Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(kiProcessingModel))
  }

  "Sending a GET request to OperatingCostsController when authenticated and enrolled" should {
    "return a 200 when something is fetched from keystore" in {
      setupShowMocks(Some(operatingCostsModel))
      mockEnrolledRequest(eisSchemeTypesModel)
      showWithSessionAndAuth(TestController.show)(
        result => status(result) shouldBe OK
      )
    }

    "provide an empty model and return a 200 when nothing is fetched using keystore when authenticated and enrolled" in {
      setupShowMocks()
      mockEnrolledRequest(eisSchemeTypesModel)
      showWithSessionAndAuth(TestController.show)(
        result => status(result) shouldBe OK
      )
    }
  }

<<<<<<< HEAD:test/controllers/eis/OperatingCostsControllerSpec.scala
  "Sending a GET request to OperatingCostsController when authenticated and NOT enrolled" should {
    "return a 200 when something is fetched from keystore" in {
      setupShowMocks(Some(operatingCostsModel))
      mockNotEnrolledRequest()
      showWithSessionAndAuth(TestController.show)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(FrontendAppConfig.subscriptionUrl)
        }
      )
    }
  }

  "Sending an Unauthenticated request with a session to OperatingCostsController" should {
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

  "Sending a request with no session to OperatingCostsController" should {
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

  "Sending a timed-out request to OperatingCostsController" should {
    "return a 302 and redirect to the timeout page" in {
      showWithTimeout(TestController.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.routes.TimeoutController.timeout().url)
        }
      )
    }
  }

=======
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/OperatingCostsControllerSpec.scala
  "Sending a valid form submit to the OperatingCostsController when authenticated and enrolled" should {
    "redirect to the Percentage Of Staff With Masters page (for now)" in {
      setupSubmitMocks(Some(true), Some(trueKIModel))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = Seq(
        "operatingCosts1stYear" -> "1000",
        "operatingCosts2ndYear" -> "1000",
        "operatingCosts3rdYear" -> "1000",
        "rAndDCosts1stYear" -> "100",
        "rAndDCosts2ndYear" -> "100",
        "rAndDCosts3rdYear" -> "100",
        "firstYear" -> "2005",
        "secondYear" -> "2004",
        "thirdYear" -> "2003"
      )

      submitWithSessionAndAuth(TestController.submit,formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
<<<<<<< HEAD:test/controllers/eis/OperatingCostsControllerSpec.scala
          redirectLocation(result) shouldBe Some("/investment-tax-relief/eis/percentage-of-staff-with-masters")
=======
          redirectLocation(result) shouldBe Some(routes.PercentageStaffWithMastersController.show().url)
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/OperatingCostsControllerSpec.scala
        }
      )
    }
  }

  "Sending a valid form submit to the OperatingCostsController but not KI when authenticated and enrolled" should {
    "redirect to the Ineligible For KI page" in {
      setupSubmitMocks(Some(false), Some(trueKIModel))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = Seq(
        "operatingCosts1stYear" -> "1000",
        "operatingCosts2ndYear" -> "1000",
        "operatingCosts3rdYear" -> "1000",
        "rAndDCosts1stYear" -> "100",
        "rAndDCosts2ndYear" -> "100",
        "rAndDCosts3rdYear" -> "100",
        "firstYear" -> "2005",
        "secondYear" -> "2004",
        "thirdYear" -> "2003"
      )

      submitWithSessionAndAuth(TestController.submit,formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
<<<<<<< HEAD:test/controllers/eis/OperatingCostsControllerSpec.scala
          redirectLocation(result) shouldBe Some("/investment-tax-relief/eis/ineligible-for-knowledge-intensive")
=======
          redirectLocation(result) shouldBe Some(routes.IneligibleForKIController.show().url)
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/OperatingCostsControllerSpec.scala
        }
      )
    }
  }

  "Sending a invalid form submit to the OperatingCostsController when authenticated and enrolled" should {
    "return a bad request" in {
      setupSubmitMocks(kiProcessingModel = Some(trueKIModel))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = Seq(
        "operatingCosts1stYear" -> "0",
        "operatingCosts2ndYear" -> "0",
        "operatingCosts3rdYear" -> "0",
        "rAndDCosts1stYear" -> "0",
        "rAndDCosts2ndYear" -> "0",
        "rAndDCosts3rdYear" -> "0",
        "firstYear" -> "0",
        "secondYear" -> "0",
        "thirdYear" -> "0"
      )

      submitWithSessionAndAuth(TestController.submit,formInput:_*)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Sending an empty KI Model to the OperatingCostsController when authenticated and enrolled" should {
    "redirect to DateOfIncorporation page" in {
      setupSubmitMocks(Some(false))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = Seq(
        "operatingCosts1stYear" -> "100",
        "operatingCosts2ndYear" -> "100",
        "operatingCosts3rdYear" -> "100",
        "rAndDCosts1stYear" -> "10",
        "rAndDCosts2ndYear" -> "10",
        "rAndDCosts3rdYear" -> "10",
        "firstYear" -> "2005",
        "secondYear" -> "2004",
        "thirdYear" -> "2003"
      )

      submitWithSessionAndAuth(TestController.submit,formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
<<<<<<< HEAD:test/controllers/eis/OperatingCostsControllerSpec.scala
          redirectLocation(result) shouldBe Some("/investment-tax-relief/eis/date-of-incorporation")
=======
          redirectLocation(result) shouldBe Some(routes.DateOfIncorporationController.show().url)
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/OperatingCostsControllerSpec.scala
        }
      )
    }
  }

  "Sending an KI Model with missing data to the OperatingCostsController when authenticated and enrolled" should {
    "redirect to DateOfIncorporation page" in {
      setupSubmitMocks(Some(false), Some(missingDataKIModel))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = Seq(
        "operatingCosts1stYear" -> "100",
        "operatingCosts2ndYear" -> "100",
        "operatingCosts3rdYear" -> "100",
        "rAndDCosts1stYear" -> "10",
        "rAndDCosts2ndYear" -> "10",
        "rAndDCosts3rdYear" -> "10",
        "firstYear" -> "2005",
        "secondYear" -> "2004",
        "thirdYear" -> "2003"
      )

      submitWithSessionAndAuth(TestController.submit,formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
<<<<<<< HEAD:test/controllers/eis/OperatingCostsControllerSpec.scala
          redirectLocation(result) shouldBe Some("/investment-tax-relief/eis/date-of-incorporation")
=======
          redirectLocation(result) shouldBe Some(routes.DateOfIncorporationController.show().url)
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/OperatingCostsControllerSpec.scala
        }
      )
    }
  }

  "Sending an non KI Model to the OperatingCostsController when authenticated and enrolled" should {
    "redirect to IsKI page" in {
      setupSubmitMocks(Some(false), Some(falseKIModel))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = Seq(
        "operatingCosts1stYear" -> "100",
        "operatingCosts2ndYear" -> "100",
        "operatingCosts3rdYear" -> "100",
        "rAndDCosts1stYear" -> "0",
        "rAndDCosts2ndYear" -> "0",
        "rAndDCosts3rdYear" -> "0",
        "firstYear" -> "2005",
        "secondYear" -> "2004",
        "thirdYear" -> "2003"
      )

      submitWithSessionAndAuth(TestController.submit,formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
<<<<<<< HEAD:test/controllers/eis/OperatingCostsControllerSpec.scala
          redirectLocation(result) shouldBe Some("/investment-tax-relief/eis/is-knowledge-intensive")
=======
          redirectLocation(result) shouldBe Some(routes.IsKnowledgeIntensiveController.show().url)
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/OperatingCostsControllerSpec.scala
        }
      )
    }
  }

  "Sending an empty invalid form submission with validation errors to the CommercialSaleController when authenticated and enrolled" should {
    "return a bad request" in {
      setupShowMocks(Some(operatingCostsModel))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = Seq(
        "operatingCosts1stYear" -> " ",
        "operatingCosts2ndYear" -> " ",
        "operatingCosts3rdYear" -> " ",
        "rAndDCosts1stYear" -> " ",
        "rAndDCosts2ndYear" -> " ",
        "rAndDCosts3rdYear" -> " ",
        "firstYear" -> " ",
        "secondYear" -> " ",
        "thirdYear" -> " "
      )

      submitWithSessionAndAuth(TestController.submit,formInput:_*)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }


  "Sending an invalid form with missing data submission with validation errors to the OperatingCostsController when authenticated and enrolled" should {
    "return a bad request" in {
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = Seq(
        "operatingCosts1stYear" -> "230000",
        "operatingCosts2ndYear" -> "189250",
        "operatingCosts3rdYear" -> "300000",
        "rAndDCosts1stYear" -> "",
        "rAndDCosts2ndYear" -> "",
        "rAndDCosts3rdYear" -> "",
        "firstYear" -> "2005",
        "secondYear" -> "2004",
        "thirdYear" -> "2003")

      submitWithSessionAndAuth(TestController.submit,formInput:_*)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Sending an invalid form with invalid data submission with validation errors to the OperatingCostsController when authenticated and enrolled" should {
    "return a bad request" in {
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = Seq(
        "operatingCosts1stYear" -> "230000",
        "operatingCosts2ndYear" -> "189250",
        "operatingCosts3rdYear" -> "300000",
        "rAndDCosts1stYear" -> "aaaaa",
        "rAndDCosts2ndYear" -> "10000",
        "rAndDCosts3rdYear" -> "12000",
        "firstYear" -> "2005",
        "secondYear" -> "2004",
        "thirdYear" -> "2003")

      submitWithSessionAndAuth(TestController.submit,formInput:_*)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }
<<<<<<< HEAD:test/controllers/eis/OperatingCostsControllerSpec.scala

  "Sending a submission to the ContactDetailsController when a timeout has occured" should {
    "redirect to the Timeout page when session has timed out" in {
      submitWithTimeout(TestController.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.routes.TimeoutController.timeout().url)
        }
      )
    }
  }

  "Sending a submission to the ContactDetailsController when NOT enrolled" should {
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
>>>>>>> 790bbb8a2c7610e9682aaf069dc37315ab8a0b7f:test/controllers/OperatingCostsControllerSpec.scala
}
