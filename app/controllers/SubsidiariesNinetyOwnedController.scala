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

import common.KeystoreKeys
import connectors.KeystoreConnector
import forms.SubsidiariesNinetyOwnedForm._
import controllers.predicates.ValidActiveSession
import models.SubsidiariesNinetyOwnedModel
import play.api.mvc.Action
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.investment.SubsidiariesNinetyOwned

import scala.concurrent.Future

object SubsidiariesNinetyOwnedController extends SubsidiariesNinetyOwnedController  {
  val keyStoreConnector: KeystoreConnector =  KeystoreConnector
}

trait SubsidiariesNinetyOwnedController extends FrontendController with ValidActiveSession{

  val keyStoreConnector: KeystoreConnector

  val show = ValidateSession.async{ implicit request =>
    keyStoreConnector.fetchAndGetFormData[SubsidiariesNinetyOwnedModel](KeystoreKeys.subsidiariesNinetyOwned).map {
      case Some(data) => Ok(SubsidiariesNinetyOwned(subsidiariesNinetyOwnedForm.fill(data)))
      case None => Ok(SubsidiariesNinetyOwned(subsidiariesNinetyOwnedForm))
    }
  }

  val submit = Action.async{ implicit  request =>
    val response = subsidiariesNinetyOwnedForm.bindFromRequest().fold(
      formWithErrors => {
        BadRequest(SubsidiariesNinetyOwned(formWithErrors))
      },
      validFormData => {
        keyStoreConnector.saveFormData(KeystoreKeys.subsidiariesNinetyOwned, validFormData)
        Redirect(routes.InvestmentGrowController.show())
      }
    )
    Future.successful(response)
  }
}