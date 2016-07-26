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

///*
// * Copyright 2016 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
package forms

import models.{SubsidiariesSpendingInvestmentModel, NewProductModel}
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec

class SubsidiariesSpendingInvestmentFormSpec extends UnitSpec {

  private def bindSuccess(request: FakeRequest[AnyContentAsFormUrlEncoded]) = {
    SubsidiariesSpendingInvestmentForm.subsidiariesSpendingInvestmentForm.bindFromRequest()(request).fold(
      formWithErrors => None,
      userData => Some(userData)
    )
  }

  private def bindWithError(request: FakeRequest[AnyContentAsFormUrlEncoded]): Option[FormError] = {
    SubsidiariesSpendingInvestmentForm.subsidiariesSpendingInvestmentForm.bindFromRequest()(request).fold(
      formWithErrors => Some(formWithErrors.errors(0)),
      userData => None
    )
  }

  val subsidiariesSpendingInvestmentJson = """{"subSpendingInvestment":"Yes"}"""
  val subsidiariesSpendingInvestmentModel = SubsidiariesSpendingInvestmentModel("Yes")

  "The Subsidiaries Spending Investment Form" should {
    "Return an error if no radio button is selected" in {
      val request = FakeRequest("GET", "/").withFormUrlEncodedBody(
        "subSpendingInvestment" -> ""
      )
      bindWithError(request) match {
        case Some(err) => {
          err.key shouldBe "subSpendingInvestment"
          err.message shouldBe Messages("error.required")
          err.args shouldBe Array()
        }
        case _ => {
          fail("Missing error")
        }
      }
    }
  }

  "The Subsidiaries Spending Investment  Form" should {
    "not return an error if the 'Yes' option is selected" in {
      val request = FakeRequest("GET", "/").withFormUrlEncodedBody(
        "subSpendingInvestment" -> "Yes"
      )
      bindWithError(request) match {
        case Some(err) => {
          fail("Validation error not expected")
        }
        case _ => ()
      }
    }
  }

  "The Subsidiaries Spending Investment  Form" should {
    "not return an error if the 'No' option is selected" in {
      val request = FakeRequest("GET", "/").withFormUrlEncodedBody(
        "subSpendingInvestment" -> "No"
      )
      bindWithError(request) match {
        case Some(err) => {
          fail("Validation error not expected")
        }
        case _ => ()
      }
    }
  }

  // model to json
  "The Subsidiaries Spending Investment  Form model" should {
    "load convert to JSON successfully" in {

      implicit val formats = Json.format[SubsidiariesSpendingInvestmentModel]

      val subsidiariesSpendingInvestment  = Json.toJson(subsidiariesSpendingInvestmentModel).toString()
      subsidiariesSpendingInvestment  shouldBe subsidiariesSpendingInvestmentJson

    }
  }

  // form model to json - apply
  "The Subsidiaries Spending Investment  Form model" should {
    "call apply correctly on the model" in {
      implicit val formats = Json.format[NewProductModel]
      val subsidiariesSpendingInvestmentForm = SubsidiariesSpendingInvestmentForm.subsidiariesSpendingInvestmentForm.fill(subsidiariesSpendingInvestmentModel)
      subsidiariesSpendingInvestmentForm.get.subSpendingInvestment shouldBe "Yes"
    }

    // form json to model - unapply
    "call unapply successfully to create expected Json" in {
      implicit val formats = Json.format[NewProductModel]
      val subsidiariesSpendingInvestmentForm = SubsidiariesSpendingInvestmentForm.subsidiariesSpendingInvestmentForm.fill(subsidiariesSpendingInvestmentModel)
      val formJson = Json.toJson(subsidiariesSpendingInvestmentForm.get).toString()
      formJson shouldBe subsidiariesSpendingInvestmentJson
    }
  }
}
