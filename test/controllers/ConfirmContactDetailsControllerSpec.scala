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

package controllers

import java.net.URLEncoder

import auth.{MockAuthConnector, MockConfig}
import common.{Constants, KeystoreKeys}
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, S4LConnector}
import controllers.helpers.ControllerSpec
import models.{ConfirmContactDetailsModel, ContactDetailsModel, SubscriptionDetailsModel}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._
import services.SubscriptionService
import data.SubscriptionTestData._
import org.jsoup.Jsoup
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

class ConfirmContactDetailsControllerSpec extends ControllerSpec {

  implicit lazy val actorSystem = ActorSystem()
  implicit lazy val mat = ActorMaterializer()

  object TestController extends ConfirmContactDetailsController {
    override lazy val subscriptionService = mock[SubscriptionService]
    override lazy val applicationConfig = FrontendAppConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val s4lConnector = mockS4lConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
  }

  def mockSaveForLaterResponse
  (
    confirmContactDetailsModel: Option[ConfirmContactDetailsModel] = None
  ): Unit = {
    when(mockS4lConnector.fetchAndGetFormData[ConfirmContactDetailsModel](Matchers.eq(KeystoreKeys.confirmContactDetails))
      (Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(confirmContactDetailsModel)
  }

  def mockSubscriptionServiceResponse(contactDetails: Option[ContactDetailsModel] = None): Unit =
    when(TestController.subscriptionService.getSubscriptionContactDetails(Matchers.any())(Matchers.any(),Matchers.any()))
      .thenReturn(contactDetails)

  "ConfirmContactDetailsController" should {
    "use the correct auth connector" in {
      ConfirmContactDetailsController.authConnector shouldBe FrontendAuthConnector
    }
    "use the correct keystore connector" in {
      ConfirmContactDetailsController.s4lConnector shouldBe S4LConnector
    }
    "use the correct enrolment connector" in {
      ConfirmContactDetailsController.enrolmentConnector shouldBe EnrolmentConnector
    }
  }

  "Sending an Authenticated and Enrolled GET request with a session to ConfirmContactDetailsController" when {

    "there is data stored in S4L for Confirm Contact Details" should {

      lazy val result = await(TestController.show()(authorisedFakeRequest))
      lazy val document = Jsoup.parse(bodyOf(result))

      "return a Status OK (200) when something is fetched from keystore" in {
        mockSaveForLaterResponse(Some(confirmContactDetailsModel))
        mockSubscriptionServiceResponse()
        mockEnrolledRequest()
        status(result) shouldBe OK
      }

      "Use the S4L stored forename on the rendered page" in {
        document.getElementById("contactDetails.forename").attr("value") shouldBe contactDetailsModel.forename
      }

      "Use the S4L stored surname on the rendered page" in {
        document.getElementById("contactDetails.surname").attr("value") shouldBe contactDetailsModel.surname
      }

      "Use the S4L stored telephoneNumber on the rendered page" in {
        document.getElementById("contactDetails.telephoneNumber").attr("value") shouldBe contactDetailsModel.telephoneNumber.get
      }

      "Use the S4L stored email on the rendered page" in {
        document.getElementById("contactDetails.email").attr("value") shouldBe contactDetailsModel.email
      }
    }

    "there is no data stored in S4L and data from Subscription Service is retrieved" should {

      lazy val result = await(TestController.show()(authorisedFakeRequest))
      lazy val document = Jsoup.parse(bodyOf(result))

      "return Status OK (200)" in {
        mockSaveForLaterResponse()
        mockEnrolledRequest()
        mockSubscriptionServiceResponse(Some(expectedContactDetailsFull))
        status(result) shouldBe OK
      }

      "Use the ETMP forename on the rendered page" in {
        document.getElementById("contactDetails.forename").attr("value") shouldBe expectedContactDetailsFull.forename
      }

      "Use the ETMP surname on the rendered page" in {
        document.getElementById("contactDetails.surname").attr("value") shouldBe expectedContactDetailsFull.surname
      }

      "Use the ETMP telephoneNumber on the rendered page" in {
        document.getElementById("contactDetails.telephoneNumber").attr("value") shouldBe expectedContactDetailsFull.telephoneNumber.get
      }

      "Use the ETMP mobileNumber on the rendered page" in {
        document.getElementById("contactDetails.mobileNumber").attr("value") shouldBe expectedContactDetailsFull.mobileNumber.get
      }

      "Use the ETMP email on the rendered page" in {
        document.getElementById("contactDetails.email").attr("value") shouldBe expectedContactDetailsFull.email
      }
    }

    "there is no data stored in S4L and no data returned from ETMP" should {

      lazy val result = await(TestController.show()(authorisedFakeRequest))
      lazy val document = Jsoup.parse(bodyOf(result))

      "return Status INTERNAL_SERVER_ERROR (500)" in {
        mockSaveForLaterResponse()
        mockEnrolledRequest()
        mockSubscriptionServiceResponse()
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "Sending an Authenticated and NOT Enrolled GET request with a session to ConfirmContactDetailsController" should {

    "return a 303 to the subscription url" in {
      mockSaveForLaterResponse(Some(confirmContactDetailsModel))
      mockNotEnrolledRequest()
      showWithSessionAndAuth(TestController.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(FrontendAppConfig.subscriptionUrl)
        }
      )
    }
  }

  "Sending an Unauthenticated request with a session to ConfirmContactDetailsController" should {
    "return a 302 and redirect to the GG login page" in {
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

  "Sending a request with no session to ConfirmContactDetailsController" should {
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

  "Sending a timed-out request to ConfirmContactDetailsController" should {
    "return a 302 and redirect to the timeout page" in {
      showWithTimeout(TestController.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
        }
      )
    }
  }


  "Submitting a valid form submission to ConfirmContactDetailsController while authenticated and enrolled" should {
    "redirect Confirm Correspondence Address Page when the Yes option is selected" in {
      mockSaveForLaterResponse(Some(confirmContactDetailsModel))
      mockEnrolledRequest()
      val formInput = Seq(
        "contactDetailsUse" -> Constants.StandardRadioButtonYesValue,
        "contactDetails.forename" -> "first",
        "contactDetails.surname" -> "last",
        "contactDetails.telephoneNumber" -> "07000 111222",
        "contactDetails.mobileNumber" -> "07000 111222",
        "contactDetails.email" -> "test@test.com")
      submitWithSessionAndAuth(TestController.submit, formInput: _*)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ConfirmCorrespondAddressController.show().url)
        }
      )
    }
  }
  "Submitting a valid form submission to ConfirmContactDetailsController while authenticated and enrolled" should {
    "redirect to Contact Address page when the 'No' option is selected" in {
      mockSaveForLaterResponse(Some(confirmContactDetailsModel))
      mockEnrolledRequest()
      val formInput = Seq(
        "contactDetailsUse" -> Constants.StandardRadioButtonNoValue,
        "contactDetails.forename" -> "first",
        "contactDetails.surname" -> "last",
        "contactDetails.telephoneNumber" -> "07000 111222",
        "contactDetails.mobileNumber" -> "07000 111222",
        "contactDetails.email" -> "test@test.com")
      submitWithSessionAndAuth(TestController.submit, formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ContactDetailsController.show().url)
        }
      )
    }
  }

  "Submitting a invalid form submission to ConfirmContactDetailsController while authenticated and enrolled" should {
    "redirect to itself when there is validation errors" in {
      mockSaveForLaterResponse(Some(confirmContactDetailsModel))
      mockEnrolledRequest()
      val formInput = Seq(
        "contactDetailsUse" -> "",
        "contactDetails.forename" -> "first",
        "contactDetails.surname" -> "last",
        "contactDetails.telephoneNumber" -> "07000 111222",
        "contactDetails.mobileNumber" -> "07000 111222",
        "contactDetails.email" -> "test@test.com")
      submitWithSessionAndAuth(TestController.submit, formInput:_*)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Submitting a form to ConfirmContactDetailsController with a session but not authenticated" should {

    val formInput = "contactAddressUse" -> Constants.StandardRadioButtonYesValue
    "return a 303 and redirect to the GG login page" in {
      mockSaveForLaterResponse(Some(confirmContactDetailsModel))
      submitWithSessionWithoutAuth(TestController.submit, formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-submission-frontend&accountType=organisation")
        }
      )
    }
  }

  "Submitting a form to ConfirmContactDetailsController with no session" should {

    val formInput = "contactAddressUse" -> Constants.StandardRadioButtonYesValue
    "return a 303 and redirect to the GG login page" in {
      mockSaveForLaterResponse(Some(confirmContactDetailsModel))
      submitWithoutSession(TestController.submit, formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-submission-frontend&accountType=organisation")
        }
      )
    }
  }

  "Submitting a form to ConfirmContactDetailsController with a timeout" should {

    val formInput = "contactAddressUse" -> Constants.StandardRadioButtonYesValue
    "return a 303 and redirect to the timeout page" in {
      mockSaveForLaterResponse(Some(confirmContactDetailsModel))
      submitWithTimeout(TestController.submit, formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
        }
      )
    }
  }

  "Submitting a form to ConfirmContactDetailsController when NOT enrolled" should {
    "return a 303 and redirect to the Subscription Service" in {
      mockSaveForLaterResponse(Some(confirmContactDetailsModel))
      mockNotEnrolledRequest()
      val formInput = "contactAddressUse" -> Constants.StandardRadioButtonYesValue
      submitWithSessionAndAuth(TestController.submit, formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(FrontendAppConfig.subscriptionUrl)
        }
      )
    }
  }
}
