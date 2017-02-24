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

package views.eisseis

import auth.MockAuthConnector
import controllers.eisseis.{TradeStartDateErrorController, routes}
import fixtures.MockSeiseisConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.test.Helpers._
import views.helpers.ViewSpec
import play.api.i18n.Messages.Implicits._

class TradeStartDateErrorSpec extends ViewSpec {

  object TestController extends TradeStartDateErrorController {
    override lazy val applicationConfig = MockSeiseisConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
    override lazy val s4lConnector = mockS4lConnector
  }

  "The Treade Start Date error page" should {

    "Verify that start page contains the correct elements" in new SEISEISSetup {
      val document: Document = {
        val result = TestController.show.apply(authorisedFakeRequest)
        Jsoup.parse(contentAsString(result))
      }
      document.title shouldEqual Messages("page.eisseis.companyDetails.tradeStartDateError.title")
      document.body.getElementById("main-heading").text() shouldEqual Messages("page.esisseis.companyDetails.tradeStartDateError.heading")
      document.body.getElementById("trading-over-two-years").text() shouldEqual Messages("page.esisseis.companyDetails.tradeStartDateError.trading.over.two.years")
      document.body.getElementById("what-next-heading").text() shouldEqual Messages("page.esisseis.companyDetails.tradeStartDateError.whatNext.heading")
      document.body.getElementById("continue-text").text() shouldEqual Messages("page.esisseis.companyDetails.tradeStartDateError.whatNext.continue")
      document.body.getElementById("bullet-one").text() shouldEqual Messages("page.esisseis.companyDetails.tradeStartDateError.whatNext.scheme.one")
      document.body.getElementById("incorrect-info").text() shouldEqual Messages("page.seis.companyDetails.tradeStartDateError.incorrect.info") +
        " " + Messages("page.seis.companyDetails.tradeStartDateError.link.changeAnswers") + "."
      document.body.getElementById("change-answers").attr("href") shouldEqual controllers.eisseis.routes.TradeStartDateController.show().url
      document.body.getElementById("back-link").attr("href") shouldEqual routes.TradeStartDateController.show().url

    }
  }
}
