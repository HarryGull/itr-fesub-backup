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

package forms

import common.Constants
import models.NewProductModel
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

class NewProductFormSpec extends UnitSpec {

  private def bindSuccess(request: FakeRequest[AnyContentAsFormUrlEncoded]) = {
    NewProductForm.newProductForm.bindFromRequest()(request).fold(
      formWithErrors => None,
      userData => Some(userData)
    )
  }

  private def bindWithError(request: FakeRequest[AnyContentAsFormUrlEncoded]): Option[FormError] = {
    NewProductForm.newProductForm.bindFromRequest()(request).fold(
      formWithErrors => Some(formWithErrors.errors(0)),
      userData => None
    )
  }

  val newProductJson = """{"isNewProduct":"Yes"}"""
  val newProductModel = NewProductModel("Yes")

  "TheNew Product Before Form" should {
    "Return an error if no radio button is selected" in {
      val request = FakeRequest("GET", "/").withFormUrlEncodedBody(
        "isNewProduct" -> ""
      )
      bindWithError(request) match {
        case Some(err) => {
          err.key shouldBe "isNewProduct"
          err.message shouldBe Messages("error.required")
          err.args shouldBe Array()
        }
        case _ => {
          fail("Missing error")
        }
      }
    }
  }

  "TheNew Product Before Form" should {
    "not return an error if the 'Yes' option is selected" in {
      val request = FakeRequest("GET", "/").withFormUrlEncodedBody(
        "isNewProduct" -> "Yes"
      )
      bindWithError(request) match {
        case Some(err) => {
          fail("Validation error not expected")
        }
        case _ => ()
      }
    }
  }

  "TheNew Product Before Form" should {
    "not return an error if the 'No' option is selected" in {
      val request = FakeRequest("GET", "/").withFormUrlEncodedBody(
        "isNewProduct" -> Constants.StandardRadioButtonNoValue
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
  "TheNew Product Before Form model" should {
    "load convert to JSON successfully" in {

      implicit val formats = Json.format[NewProductModel]

      val newProduct = Json.toJson(newProductModel).toString()
      newProduct shouldBe newProductJson

    }
  }

  // form model to json - apply
  "TheNew Product Before Form model" should {
    "call apply correctly on the model" in {
      implicit val formats = Json.format[NewProductModel]
      val newProductForm = NewProductForm.newProductForm.fill(newProductModel)
      newProductForm.get.isNewProduct shouldBe Constants.StandardRadioButtonYesValue
    }

    // form json to model - unapply
    "call unapply successfully to create expected Json" in {
      implicit val formats = Json.format[NewProductModel]
      val newProductForm = NewProductForm.newProductForm.fill(newProductModel)
      val formJson = Json.toJson(newProductForm.get).toString()
      formJson shouldBe newProductJson
    }
  }
}
