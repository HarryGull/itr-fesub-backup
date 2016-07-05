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

package forms

import models.{SubsidiariesModel}
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec

class SubsidiariesFormSpec extends UnitSpec {

  private def bindSuccess(request: FakeRequest[AnyContentAsFormUrlEncoded]) = {
    SubsidiariesForm.subsidiariesForm.bindFromRequest()(request).fold(
      formWithErrors => None,
      userData => Some(userData)
    )
  }

  private def bindWithError(request: FakeRequest[AnyContentAsFormUrlEncoded]): Option[FormError] = {
    SubsidiariesForm.subsidiariesForm.bindFromRequest()(request).fold(
      formWithErrors => Some(formWithErrors.errors(0)),
      userData => None
    )
  }

  val subsidiariesJson = """{"ownSubsidiaries":"Yes"}"""
  val subsidiariesModel = SubsidiariesModel("Yes")

  // address line 1 validation
  "The Subsidiaries Form" should {
    "Return an error if no radio button is selected" in {
      val request = FakeRequest("GET", "/").withFormUrlEncodedBody(
        "subsidiaries" -> ""
      )
      bindWithError(request) match {
        case Some(err) => {
          err.key shouldBe "subsidiaries"
          err.message shouldBe Messages("error.required")
          err.args shouldBe Array()
        }
        case _ => {
          fail("Missing error")
        }
      }
    }
  }

  "The Subsidiaries Form" should {
    "not return an error if the 'Yes' option is selected" in {
      val request = FakeRequest("GET", "/").withFormUrlEncodedBody(
        "subsidiaries" -> "Yes"
      )
      bindWithError(request) match {
        case Some(err) => {
          fail("Validation error not expected")
        }
        case _ => ()
      }
    }
  }


  "The Subsidiaries Form" should {
    "not return an error if the 'No' option is selected" in {
      val request = FakeRequest("GET", "/").withFormUrlEncodedBody(
        "subsidiaries" -> "No"
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
  "The Subsidiaries Form model" should {
    "load convert to JSON successfully" in {

      implicit val formats = Json.format[SubsidiariesModel]

      val subsidiaries= Json.toJson(subsidiariesModel).toString()
      subsidiaries shouldBe subsidiariesJson

    }
  }

  // form model to json - apply
  "The Subsidiaries Form model" should {
    "call apply corrctly on the model" in {
      implicit val formats = Json.format[SubsidiariesModel]
      val subsidiariesForm =SubsidiariesForm.subsidiariesForm.fill(subsidiariesModel)
      subsidiariesForm.get.ownSubsidiaries shouldBe "Yes"
    }

    // form json to model - unapply
    "call unapply successfully to create expected Json" in {
      implicit val formats = Json.format[SubsidiariesModel]
      val subsidiariesForm = SubsidiariesForm.subsidiariesForm.fill(subsidiariesModel)
      val formJson = Json.toJson(subsidiariesForm.get).toString()
      formJson shouldBe subsidiariesJson
    }
  }
}