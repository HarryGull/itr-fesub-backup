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

import auth.AuthorisedAndEnrolledForTAVC
import common.KeystoreKeys
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, S4LConnector}
import forms.SubsidiariesNinetyOwnedForm._
import models.SubsidiariesNinetyOwnedModel
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.investment.SubsidiariesNinetyOwned

import scala.concurrent.Future

object SubsidiariesNinetyOwnedController extends SubsidiariesNinetyOwnedController  {
  val s4lConnector: S4LConnector =  S4LConnector
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val enrolmentConnector = EnrolmentConnector
}

trait SubsidiariesNinetyOwnedController extends FrontendController with AuthorisedAndEnrolledForTAVC {

  val s4lConnector: S4LConnector

  val show = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    s4lConnector.fetchAndGetFormData[SubsidiariesNinetyOwnedModel](KeystoreKeys.subsidiariesNinetyOwned).map {
      case Some(data) => Ok(SubsidiariesNinetyOwned(subsidiariesNinetyOwnedForm.fill(data)))
      case None => Ok(SubsidiariesNinetyOwned(subsidiariesNinetyOwnedForm))
    }
  }

  val submit = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    subsidiariesNinetyOwnedForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(SubsidiariesNinetyOwned(formWithErrors)))
      },
      validFormData => {
        s4lConnector.saveFormData(KeystoreKeys.subsidiariesNinetyOwned, validFormData)
        s4lConnector.saveFormData(KeystoreKeys.backLinkInvestmentGrow, routes.SubsidiariesNinetyOwnedController.show().toString())
        Future.successful(Redirect(routes.InvestmentGrowController.show()))
      }
    )
  }
}
