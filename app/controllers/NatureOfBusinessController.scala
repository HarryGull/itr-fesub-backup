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
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, S4LConnector}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.mvc._
import models.NatureOfBusinessModel
import common._
import forms.NatureOfBusinessForm._
import models.submission.SchemeTypesModel
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

import scala.concurrent.Future
import views.html.companyDetails.NatureOfBusiness

object NatureOfBusinessController extends NatureOfBusinessController
{
  val s4lConnector: S4LConnector = S4LConnector
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val enrolmentConnector = EnrolmentConnector
}

trait NatureOfBusinessController extends FrontendController with AuthorisedAndEnrolledForTAVC{

  val s4lConnector: S4LConnector

  val show = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    s4lConnector.fetchAndGetFormData[NatureOfBusinessModel](KeystoreKeys.natureOfBusiness).map {
      case Some(data) => Ok(NatureOfBusiness(natureOfBusinessForm.fill(data)))
      case None => Ok(NatureOfBusiness(natureOfBusinessForm))
    }
  }

  val submit = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    natureOfBusinessForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(NatureOfBusiness(formWithErrors)))
      },
      validFormData => {
        // TODO: remove this once scheme selection page is built in to main app in all flows
        s4lConnector.saveFormData(KeystoreKeys.selectedSchemes, SchemeTypesModel(eis = true))
        s4lConnector.saveFormData(KeystoreKeys.natureOfBusiness, validFormData)
        Future.successful(Redirect(routes.DateOfIncorporationController.show()))
      }
    )
  }
}
