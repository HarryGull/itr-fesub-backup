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

package views

import java.util.UUID

import connectors.KeystoreConnector

import controllers.{NatureOfBusinessController, routes}
import controllers.helpers.FakeRequestHelper
import models.NatureOfBusinessModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.i18n.Messages
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class NatureOfBusinessSpec extends UnitSpec with WithFakeApplication with MockitoSugar with FakeRequestHelper{

  val mockKeystoreConnector = mock[KeystoreConnector]

  val natureOfBusinessModel = new NatureOfBusinessModel("1234567890")
  val emptyNatureOfBusinessModel = new NatureOfBusinessModel("")

  class SetupPage {

    val controller = new NatureOfBusinessController{
      val keyStoreConnector: KeystoreConnector = mockKeystoreConnector
    }
  }

  "The Nature of business page" should {

    "Verify that the page contains the correct elements when a valid NatureOfBusinessModel is passed" in new SetupPage {
      val document: Document = {
        val userId = s"user-${UUID.randomUUID}"

        when(mockKeystoreConnector.fetchAndGetFormData[NatureOfBusinessModel](Matchers.any())(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(Option(natureOfBusinessModel)))
        val result = controller.show.apply(fakeRequestWithSession.withFormUrlEncodedBody(
          "natureofbusiness" -> "selling advertising"
        ))
        Jsoup.parse(contentAsString(result))
      }

      document.title() shouldBe Messages("page.companyDetails.natureofbusiness.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.companyDetails.natureofbusiness.heading")
      document.getElementById("label-natureofbusiness").select("span").hasClass("visuallyhidden") shouldBe true
      document.getElementById("label-natureofbusiness").select(".visuallyhidden").text() shouldBe Messages("page.companyDetails.natureofbusiness.heading")
      document.getElementById("label-natureofbusiness-hint").text() shouldBe Messages("page.companyDetails.natureofbusiness.question.hint")
      document.getElementById("description-two").text() shouldBe Messages("page.companyDetails.natureofbusiness.example.text")
      document.getElementById("bullet-one").text() shouldBe Messages("page.companyDetails.natureofbusiness.bullet.one")
      document.getElementById("bullet-two").text() shouldBe Messages("page.companyDetails.natureofbusiness.bullet.two")
      document.getElementById("bullet-three").text() shouldBe Messages("page.companyDetails.natureofbusiness.bullet.three")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.DateOfIncorporationController.show.toString()
      document.body.getElementById("progress-section").text shouldBe  Messages("common.section.progress.company.details.one")
    }

    "Verify that the nature of business page contains the correct elements when an invalid NatureOfBusinessModel model is passed" in new SetupPage {
      val document: Document = {
        val userId = s"user-${UUID.randomUUID}"

        when(mockKeystoreConnector.fetchAndGetFormData[NatureOfBusinessModel](Matchers.any())(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(Option(emptyNatureOfBusinessModel)))
        val result = controller.submit.apply((fakeRequestWithSession))
        Jsoup.parse(contentAsString(result))
      }

      // Check the error summary is displayed - the whole purpose of this test
      document.getElementById("error-summary-display").hasClass("error-summary--show")

      // additional page checks to make sure everthing else still as expected if errors on page
      document.title() shouldBe Messages("page.companyDetails.natureofbusiness.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.companyDetails.natureofbusiness.heading")
      document.getElementById("label-natureofbusiness").select("span").hasClass("visuallyhidden") shouldBe true
      document.getElementById("label-natureofbusiness").select(".visuallyhidden").text() shouldBe Messages("page.companyDetails.natureofbusiness.heading")
      document.getElementById("label-natureofbusiness-hint").text() shouldBe Messages("page.companyDetails.natureofbusiness.question.hint")
      document.getElementById("description-two").text() shouldBe Messages("page.companyDetails.natureofbusiness.example.text")
      document.getElementById("bullet-one").text() shouldBe Messages("page.companyDetails.natureofbusiness.bullet.one")
      document.getElementById("bullet-two").text() shouldBe Messages("page.companyDetails.natureofbusiness.bullet.two")
      document.getElementById("bullet-three").text() shouldBe Messages("page.companyDetails.natureofbusiness.bullet.three")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.DateOfIncorporationController.show.toString()
      document.body.getElementById("progress-section").text shouldBe  Messages("common.section.progress.company.details.one")

    }

  }

}