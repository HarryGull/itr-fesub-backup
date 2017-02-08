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

import auth.{AuthorisedAndEnrolledForTAVC, TAVCUser}
import common.KeystoreKeys
import config.{FrontendAppConfig, FrontendAuthConnector}
import config.FrontendGlobal.internalServerErrorTemplate
import connectors.{EnrolmentConnector, S4LConnector}
import controllers.Helpers.ControllerHelpers
import models._
import models.submission.SchemeTypesModel
import play.api.mvc.Result
import services.{RegistrationDetailsService, SubscriptionService}
import play.Logger
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrier
import views.html.introduction._
import views.html.hubPartials._
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.i18n.Messages

import scala.concurrent.Future

object ApplicationHubController extends ApplicationHubController{
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val enrolmentConnector = EnrolmentConnector
  val s4lConnector: S4LConnector = S4LConnector
  val subscriptionService: SubscriptionService = SubscriptionService
  val registrationDetailsService: RegistrationDetailsService = RegistrationDetailsService
}

trait ApplicationHubController extends FrontendController with AuthorisedAndEnrolledForTAVC {

  val s4lConnector: S4LConnector
  val subscriptionService: SubscriptionService
  val registrationDetailsService: RegistrationDetailsService

  val show = AuthorisedAndEnrolled.async { implicit user => implicit request =>

    def routeRequest(applicationHubModel: Option[ApplicationHubModel]): Future[Result] = {
      if (applicationHubModel.nonEmpty) {

        s4lConnector.fetchAndGetFormData[Boolean](KeystoreKeys.applicationInProgress).map {
          case Some(true) => Ok(ApplicationHub(applicationHubModel.get,
            ApplicationHubExisting(applicationHubModel.get.schemeTypes.fold(controllers.routes.NatureOfBusinessController.show().url)(ControllerHelpers.routeToScheme),
              ControllerHelpers.schemeDescriptionFromTypes(applicationHubModel.get.schemeTypes))))
          case _ => Ok(ApplicationHub(applicationHubModel.get, ApplicationHubNew()))
        }
      }
      else Future.successful(InternalServerError(internalServerErrorTemplate))
    }

    def getApplicationHubModel()(implicit hc: HeaderCarrier, user: TAVCUser): Future[Option[ApplicationHubModel]] = (for {
      tavcRef <- getTavCReferenceNumber()
      registrationDetailsModel <- registrationDetailsService.getRegistrationDetails(tavcRef)
      subscriptionDetailsModel <- subscriptionService.getSubscriptionContactDetails(tavcRef)
      schemeTypesModel <-   s4lConnector.fetchAndGetFormData[SchemeTypesModel](KeystoreKeys.selectedSchemes)
    } yield Some(ApplicationHubModel(registrationDetailsModel.get.organisationName, registrationDetailsModel.get.addressModel,
      subscriptionDetailsModel.get, schemeTypesModel))).recover {
      case _ =>
        Logger.warn(s"[ApplicationHubController][getApplicationModel] - ApplicationHubModel components not found")
        None
    }

    for {
      applicationHubModel <- getApplicationHubModel()
      route <- routeRequest(applicationHubModel)
    } yield route

  }

  val newApplication = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    if(applicationConfig.seisFlowEnabled) {
      Future.successful(Redirect(controllers.schemeSelection.routes.SchemeSelectionController.show()))
    } else {
      s4lConnector.saveFormData(KeystoreKeys.applicationInProgress, true)
      Future.successful(Redirect(routes.NatureOfBusinessController.show()))
    }
  }

  val delete = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    s4lConnector.clearCache().map {
      case _ => Redirect(routes.ApplicationHubController.show())
    }
  }

}
