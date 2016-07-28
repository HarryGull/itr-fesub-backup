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

import builders.SessionBuilder
import connectors.KeystoreConnector
import controllers.{IsKnowledgeIntensiveController, WhatWillUseForController, routes}
import models.{IsKnowledgeIntensiveModel, WhatWillUseForModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.i18n.Messages
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class WhatWillUseForSpec extends UnitSpec with WithFakeApplication with MockitoSugar{

  val mockKeystoreConnector = mock[KeystoreConnector]

  val whatWillUseForModel = new WhatWillUseForModel("Business")
  val emptyWhatWillUseForeModel = new WhatWillUseForModel("")

  class SetupPage {

    val controller = new WhatWillUseForController{
      val keyStoreConnector: KeystoreConnector = mockKeystoreConnector
    }
  }

  "Verify that the WhatWillUseFor page contains the correct elements " +
    "when a valid WhatWillUseForModel is passed as returned from keystore" in new SetupPage {
    val document : Document = {
      val userId = s"user-${UUID.randomUUID}"
      when(mockKeystoreConnector.fetchAndGetFormData[WhatWillUseForModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(whatWillUseForModel)))
      val result = controller.show.apply(SessionBuilder.buildRequestWithSession(userId))
      Jsoup.parse(contentAsString(result))
    }

    document.body.getElementById("back-link").attr("href") shouldEqual routes.ProposedInvestmentController.show().toString()
    document.title() shouldBe Messages("page.investment.whatWillUseFor.title")
    document.getElementById("main-heading").text() shouldBe Messages("page.investment.whatWillUseFor.title")
    document.getElementById("whatWillUseFor-businessLabel").text() shouldBe Messages("page.investment.whatWillUseFor.business")
    document.getElementById("whatWillUseFor-preparationLabel").text() shouldBe Messages("page.investment.whatWillUseFor.preparing")
    document.getElementById("whatWillUseFor-r&dLabel").text() shouldBe Messages("page.investment.whatWillUseFor.rAndD")
    document.getElementById("next").text() shouldBe Messages("common.button.continue")




  }

  "Verify that WhatWillUseFor page contains the correct elements when an empty model " +
    "is passed because nothing was returned from keystore" in new SetupPage {
    val document : Document = {
      val userId = s"user-${UUID.randomUUID}"
      when(mockKeystoreConnector.fetchAndGetFormData[WhatWillUseForModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(whatWillUseForModel)))
      val result = controller.show.apply(SessionBuilder.buildRequestWithSession(userId))
      Jsoup.parse(contentAsString(result))
    }

    document.body.getElementById("back-link").attr("href") shouldEqual routes.ProposedInvestmentController.show().toString()
    document.title() shouldBe Messages("page.investment.whatWillUseFor.title")
    document.getElementById("main-heading").text() shouldBe Messages("page.investment.whatWillUseFor.title")
    document.getElementById("whatWillUseFor-businessLabel").text() shouldBe Messages("page.investment.whatWillUseFor.business")
    document.getElementById("whatWillUseFor-preparationLabel").text() shouldBe Messages("page.investment.whatWillUseFor.preparing")
    document.getElementById("whatWillUseFor-r&dLabel").text() shouldBe Messages("page.investment.whatWillUseFor.rAndD")
    document.getElementById("next").text() shouldBe Messages("common.button.continue")
  }

  "Verify that WhatWillUseForModel page contains show the error summary when an invalid model (no radio button selection) is submitted" in new SetupPage {
    val document : Document = {
      val userId = s"user-${UUID.randomUUID}"
      // submit the model with no radio selected as a post action
      val result = controller.submit.apply(SessionBuilder.buildRequestWithSession(userId))
      Jsoup.parse(contentAsString(result))
    }

    // Make sure we have the expected error summary displayed
    document.getElementById("error-summary-display").hasClass("error-summary--show")
    document.title() shouldBe Messages("page.investment.whatWillUseFor.title")

  }
}
