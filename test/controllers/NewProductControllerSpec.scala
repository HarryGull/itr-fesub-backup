/*
 * Copyright 2016 HM Revenue & Customs
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

import java.util.UUID

import builders.SessionBuilder
import common.KeystoreKeys
import connectors.KeystoreConnector
import models._
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneServerPerSuite
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class NewProductControllerSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach with OneServerPerSuite {

  val mockKeyStoreConnector = mock[KeystoreConnector]

  object NewProductControllerTest extends NewProductController {
    val keyStoreConnector: KeystoreConnector = mockKeyStoreConnector
  }

  val modelYes = NewProductModel("Yes")
  val modelNo = NewProductModel("No")
  val emptyModel = NewProductModel("")
  val modelSubsidiariesYes = SubsidiariesModel("Yes")
  val modelSubsidiariesNo = SubsidiariesModel("No")
  val emptySubsidiariesModel = SubsidiariesModel("")
  val cacheMap: CacheMap = CacheMap("", Map("" -> Json.toJson(modelYes)))
  val keyStoreSavedNewProduct = NewProductModel("Yes")

  def showWithSession(test: Future[Result] => Any) {
    val sessionId = s"user-${UUID.randomUUID}"
    val result = NewProductControllerTest.show().apply(SessionBuilder.buildRequestWithSession(sessionId))
    test(result)
  }

  def submitWithSession(request: FakeRequest[AnyContentAsFormUrlEncoded])(test: Future[Result] => Any) {
    val sessionId = s"user-${UUID.randomUUID}"
    val result = NewProductControllerTest.submit.apply(SessionBuilder.updateRequestFormWithSession(request, sessionId))
    test(result)
  }

  implicit val hc = HeaderCarrier()

  override def beforeEach() {
    reset(mockKeyStoreConnector)
  }

  "NewProductController" should {
    "use the correct keystore connector" in {
      NewProductController.keyStoreConnector shouldBe KeystoreConnector
    }
  }

  "Sending a GET request to NewProductController" should {
    "return a 200 when something is fetched from keystore" in {
      when(mockKeyStoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeyStoreConnector.fetchAndGetFormData[NewProductModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(keyStoreSavedNewProduct)))
      showWithSession(
        result => status(result) shouldBe OK
      )
    }

    "provide an empty model and return a 200 when nothing is fetched using keystore" in {
      when(mockKeyStoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeyStoreConnector.fetchAndGetFormData[NewProductModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(None))
      showWithSession(
        result => status(result) shouldBe OK
      )
    }
  }

  "Sending a valid 'Yes' form submit to the NewProductController" should {
    "redirect to the subsidiaries page when the subsidiaries value is Yes" in {
      when(mockKeyStoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeyStoreConnector.fetchAndGetFormData[SubsidiariesModel](Matchers.eq(KeystoreKeys.subsidiaries))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(modelSubsidiariesYes)))
      val request = FakeRequest().withFormUrlEncodedBody(
        "isNewProduct" -> "Yes")
      submitWithSession(request)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief/proposed-investment")
        }
      )
    }
  }

  "Sending a valid 'Yes' form submit to the NewProductController" should {
    "redirect to the subsidiaries page when the subsidiaries value is No" in {
      when(mockKeyStoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeyStoreConnector.fetchAndGetFormData[SubsidiariesModel](Matchers.eq(KeystoreKeys.subsidiaries))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(modelSubsidiariesNo)))
      val request = FakeRequest().withFormUrlEncodedBody(
        "isNewProduct" -> "Yes")
      submitWithSession(request)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief/is-knowledge-intensive")
        }
      )
    }
  }

  "Sending a valid 'Yes' form submit to the NewProductController" should {
    "redirect to the subsidiaries page when the subsidiaries value is not present" in {
      when(mockKeyStoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeyStoreConnector.fetchAndGetFormData[SubsidiariesModel](Matchers.eq(KeystoreKeys.subsidiaries))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(None))
      val request = FakeRequest().withFormUrlEncodedBody(
        "isNewProduct" -> "Yes")
      submitWithSession(request)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief/subsidiaries")
        }
      )
    }
  }

  //TODO:
  // the No sections below will be much simplified later as they will just go to the required error page
  // (or in page javascript to make it red in which case not part of navigation at all and no controlelr test required)
  // The subsidiaries logic test is not required in the 3 tests below can be replaced by a single test  top the error page
  "Sending a valid 'No' form submit to the NewProductController" should {
    "redirect to the subsidiaries page when the subsidiaries value is Yes" in {
      when(mockKeyStoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeyStoreConnector.fetchAndGetFormData[SubsidiariesModel](Matchers.eq(KeystoreKeys.subsidiaries))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(modelSubsidiariesYes)))
      val request = FakeRequest().withFormUrlEncodedBody(
        "isNewProduct" -> "No")
      submitWithSession(request)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief/proposed-investment")
        }
      )
    }
  }

  "Sending a valid 'No' form submit to the NewProductController" should {
    "redirect to the subsidiaries page when the subsidiaries value is No" in {
      when(mockKeyStoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeyStoreConnector.fetchAndGetFormData[SubsidiariesModel](Matchers.eq(KeystoreKeys.subsidiaries))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(modelSubsidiariesNo)))
      val request = FakeRequest().withFormUrlEncodedBody(
        "isNewProduct" -> "No")
      submitWithSession(request)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief/is-knowledge-intensive")
        }
      )
    }
  }

  "Sending a valid 'No' form submit to the NewProductController" should {
    "redirect to the subsidiaries page when the subsidiaries value is not present" in {
      when(mockKeyStoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeyStoreConnector.fetchAndGetFormData[SubsidiariesModel](Matchers.eq(KeystoreKeys.subsidiaries))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(None))
      val request = FakeRequest().withFormUrlEncodedBody(
        "isNewProduct" -> "No")
      submitWithSession(request)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief/subsidiaries")
        }
      )
    }
  }

  "Sending an invalid form submission with validation errors to the NewProductController" should {
    "redirect to itself" in {
      val request = FakeRequest().withFormUrlEncodedBody(
        "isNewProduct" -> "")
      submitWithSession(request)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

}