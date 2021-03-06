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

package controllers.eisseis

import auth.{AuthorisedAndEnrolledForTAVC, EIS, SEIS, VCT}
import common.KeystoreKeys
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, S4LConnector}
import models.submission.SchemeTypesModel
import play.api.Logger
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import views.html.eisseis.companyDetails.NotFirstTradeError

import scala.concurrent.Future

object NotFirstTradeErrorController extends NotFirstTradeErrorController
{
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val enrolmentConnector = EnrolmentConnector
  override lazy val s4lConnector = S4LConnector
}

trait NotFirstTradeErrorController extends FrontendController with AuthorisedAndEnrolledForTAVC {

  override val acceptedFlows = Seq(Seq(EIS, SEIS, VCT), Seq(SEIS, VCT), Seq(EIS, SEIS))

  val show = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    s4lConnector.fetchAndGetFormData[SchemeTypesModel](KeystoreKeys.selectedSchemes).map {
      case Some(schemeTypesModel) => Ok(NotFirstTradeError(schemeTypesModel))
      case None => Redirect(controllers.routes.ApplicationHubController.show())
    }.recover {
      case e: Exception => Logger.warn(s"[NotFirstTradeErrorController][show] Error calling fetchAndGetFormData: ${e.getMessage}")
        Redirect(controllers.routes.ApplicationHubController.show())
    }
  }

  val submit = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    Future.successful(Redirect(routes.CommercialSaleController.show()))
  }
}
