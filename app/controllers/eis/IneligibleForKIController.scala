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

package controllers.eis

import auth.AuthorisedAndEnrolledForTAVC
import common.{Constants, KeystoreKeys}
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, S4LConnector}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.eis.knowledgeIntensive.IneligibleForKI
import controllers.Helpers.ControllerHelpers
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

import scala.concurrent.Future
import views.html._

object IneligibleForKIController extends IneligibleForKIController{
  val s4lConnector: S4LConnector = S4LConnector
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val enrolmentConnector = EnrolmentConnector
}

trait IneligibleForKIController extends FrontendController with AuthorisedAndEnrolledForTAVC {

  val s4lConnector: S4LConnector

  val show = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    def routeRequest(backUrl: Option[String]) = {
      if (backUrl.isDefined) {
        Future.successful(Ok(IneligibleForKI(backUrl.get)))
      } else {
        // no back link - send back to start of flow
        Future.successful(Redirect(routes.OperatingCostsController.show()))
      }
    }
    for {
      link <- ControllerHelpers.getSavedBackLink(KeystoreKeys.backLinkIneligibleForKI, s4lConnector)
      route <- routeRequest(link)
    } yield route
  }

  val submit = AuthorisedAndEnrolled.async { implicit user => implicit request => {
    s4lConnector.saveFormData(KeystoreKeys.backLinkSubsidiaries, routes.IneligibleForKIController.show().toString())
    Future.successful(Redirect(routes.SubsidiariesController.show()))
    }
  }

}
