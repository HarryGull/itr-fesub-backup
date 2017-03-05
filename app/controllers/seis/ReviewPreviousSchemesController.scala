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

package controllers.seis

import auth.{AuthorisedAndEnrolledForTAVC, SEIS}
import common.KeystoreKeys
import config.FrontendGlobal._
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, S4LConnector, SubmissionConnector}
import controllers.Helpers.{ControllerHelpers, PreviousSchemesHelper}
import controllers.predicates.FeatureSwitch
import models.HadPreviousRFIModel
import play.Logger
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.seis.previousInvestment.ReviewPreviousSchemes
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.mvc._

import scala.concurrent.Future

object ReviewPreviousSchemesController extends ReviewPreviousSchemesController {
  override lazy val s4lConnector = S4LConnector
  val submissionConnector: SubmissionConnector = SubmissionConnector
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val enrolmentConnector = EnrolmentConnector
}

trait ReviewPreviousSchemesController extends FrontendController with AuthorisedAndEnrolledForTAVC with
  PreviousSchemesHelper with FeatureSwitch {

  override val acceptedFlows = Seq(Seq(SEIS))


  val submissionConnector: SubmissionConnector

  val show = featureSwitch(applicationConfig.seisFlowEnabled) {
    AuthorisedAndEnrolled.async { implicit user => implicit request =>
      def routeRequest(backUrl: Option[String]) = {
        if (backUrl.isDefined) {
          PreviousSchemesHelper.getAllInvestmentFromKeystore(s4lConnector).flatMap {
            previousSchemes =>
              if (previousSchemes.nonEmpty) {
                Future.successful(Ok(ReviewPreviousSchemes(previousSchemes, backUrl.get)))
              }
              else
                Future.successful(Redirect(routes.HadPreviousRFIController.show()))
          }
        } else {
          // no back link - send to beginning of flow
          Future.successful(Redirect(routes.HadPreviousRFIController.show()))
        }
      }
      for {
        link <- ControllerHelpers.getSavedBackLink(KeystoreKeys.backLinkReviewPreviousSchemes, s4lConnector)
        route <- routeRequest(link)
      } yield route
    }
  }

  def add: Action[AnyContent] = featureSwitch(applicationConfig.seisFlowEnabled) {
    AuthorisedAndEnrolled.async { implicit user => implicit request =>
      s4lConnector.saveFormData(KeystoreKeys.backLinkPreviousScheme, routes.ReviewPreviousSchemesController.show().url)
      Future.successful(Redirect(routes.PreviousSchemeController.show(None)))
    }
  }

  def change(id: Int): Action[AnyContent] = featureSwitch(applicationConfig.seisFlowEnabled) {
    AuthorisedAndEnrolled.async { implicit user => implicit request =>
      s4lConnector.saveFormData(KeystoreKeys.backLinkPreviousScheme, routes.ReviewPreviousSchemesController.show().url)
      Future.successful(Redirect(routes.PreviousSchemeController.show(Some(id))))
    }
  }

  def remove(id: Int): Action[AnyContent] = featureSwitch(applicationConfig.seisFlowEnabled) {
    AuthorisedAndEnrolled.async { implicit user => implicit request =>
      s4lConnector.saveFormData(KeystoreKeys.backLinkPreviousScheme, routes.ReviewPreviousSchemesController.show().url)
      PreviousSchemesHelper.removeKeystorePreviousInvestment(s4lConnector, id).map {
        _ => Redirect(routes.ReviewPreviousSchemesController.show())
      }
    }
  }

  val submit = featureSwitch(applicationConfig.seisFlowEnabled) {
    AuthorisedAndEnrolled.async { implicit user => implicit request =>

      def routeRequest(previousSchemesExist: Boolean): Future[Result] = {
        if (!previousSchemesExist) {
          Future.successful(Redirect(routes.ReviewPreviousSchemesController.show()))
        }
        else {
            Future.successful(Redirect(routes.ProposedInvestmentController.show()))
        }
      }

      s4lConnector.saveFormData(KeystoreKeys.backLinkProposedInvestment, routes.ReviewPreviousSchemesController.show().url)
      (for {
        previousSchemesExist <- PreviousSchemesHelper.previousInvestmentsExist(s4lConnector)
        investmentsSinceStartDate <- PreviousSchemesHelper.getPreviousInvestmentsFromStartDateTotal(s4lConnector)
        hadPrevRFI <- s4lConnector.fetchAndGetFormData[HadPreviousRFIModel](KeystoreKeys.hadPreviousRFI)
        route <- routeRequest(previousSchemesExist)
      } yield route) recover {
        case e: NoSuchElementException => Redirect(routes.ProposedInvestmentController.show())
        case e: Exception => {
          InternalServerError(internalServerErrorTemplate)
        }
      }
    }
  }
}
