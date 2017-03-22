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

import auth._
import common.{Constants, KeystoreKeys}
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, S4LConnector}
import controllers.Helpers.{ControllerHelpers, PreviousSchemesHelper}
import controllers.predicates.FeatureSwitch
import forms.HadOtherInvestmentsForm._
import models.{HadOtherInvestmentsModel, HadPreviousRFIModel}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Result
import uk.gov.hmrc.play.http.HeaderCarrier
import views.html.eisseis.previousInvestment
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

object HadOtherInvestmentsController extends HadOtherInvestmentsController{
  override lazy val s4lConnector = S4LConnector
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val enrolmentConnector = EnrolmentConnector
}

trait HadOtherInvestmentsController extends FrontendController with AuthorisedAndEnrolledForTAVC with PreviousSchemesHelper with FeatureSwitch{

  override val acceptedFlows = Seq(Seq(EIS,SEIS,VCT),Seq(SEIS,VCT), Seq(EIS,SEIS))

  val show = featureSwitch(applicationConfig.eisseisFlowEnabled) {
    AuthorisedAndEnrolled.async { implicit user => implicit request =>
      def routeRequest(backUrl: Option[String]) = {
        if (backUrl.isDefined) {
          s4lConnector.fetchAndGetFormData[HadOtherInvestmentsModel](KeystoreKeys.hadOtherInvestments).map {
            case Some(data) => Ok(previousInvestment.HadOtherInvestments(hadOtherInvestmentsForm.fill(data), backUrl.getOrElse("")))
            case None => Ok(previousInvestment.HadOtherInvestments(hadOtherInvestmentsForm, backUrl.getOrElse("")))
          }
        }
        else Future.successful(Redirect(routes.HadPreviousRFIController.show()))
      }
      for {
        link <- ControllerHelpers.getSavedBackLink(KeystoreKeys.backLinkHadRFI, s4lConnector)
        route <- routeRequest(link)
      } yield route
    }
  }


  val submit = featureSwitch(applicationConfig.eisseisFlowEnabled) {
    AuthorisedAndEnrolled.async { implicit user =>
      implicit request =>
        hadOtherInvestmentsForm.bindFromRequest().fold(
          formWithErrors => {
            ControllerHelpers.getSavedBackLink(KeystoreKeys.backLinkHadRFI, s4lConnector).flatMap {
              case Some(data) => Future.successful(BadRequest(previousInvestment.HadOtherInvestments(formWithErrors, data)))
              case None => Future.successful(Redirect(routes.HadPreviousRFIController.show()))
            }
          },
          validFormData => {
            s4lConnector.saveFormData(KeystoreKeys.hadOtherInvestments, validFormData)
            for {
              f1 <- s4lConnector.fetchAndGetFormData[HadPreviousRFIModel](KeystoreKeys.hadPreviousRFI).map(x => x.get)
              f2 <- process(f1.hadPreviousRFI, validFormData)
            } yield (f2)

          }
        )
    }
  }

  def process(hadPreviousRFI : String, validFormData:HadOtherInvestmentsModel)
             (implicit headerCarrier: HeaderCarrier, tavcUser:TAVCUser): Future[Result]= {
    validFormData.hadOtherInvestments match {
      case Constants.StandardRadioButtonYesValue => {
        getAllInvestmentFromKeystore(s4lConnector).flatMap {
          previousSchemes =>
            if (previousSchemes.nonEmpty) {
              s4lConnector.saveFormData(KeystoreKeys.backLinkReviewPreviousSchemes, routes.HadOtherInvestmentsController.show().url)
              Future.successful(Redirect(routes.ReviewPreviousSchemesController.show()))
            }
            else {
              s4lConnector.saveFormData(KeystoreKeys.backLinkPreviousScheme, routes.HadOtherInvestmentsController.show().url)
              Future.successful(Redirect(routes.PreviousSchemeController.show()))
            }
        }
      }
      case Constants.StandardRadioButtonNoValue => {

        if (hadPreviousRFI.equals(Constants.StandardRadioButtonYesValue)) {
          getAllInvestmentFromKeystore(s4lConnector).flatMap {
            previousSchemes =>
              if (previousSchemes.nonEmpty) {
                s4lConnector.saveFormData(KeystoreKeys.backLinkReviewPreviousSchemes, routes.HadOtherInvestmentsController.show().url)
                Future.successful(Redirect(routes.ReviewPreviousSchemesController.show()))
              }
              else {
                s4lConnector.saveFormData(KeystoreKeys.backLinkPreviousScheme, routes.HadOtherInvestmentsController.show().url)
                Future.successful(Redirect(routes.PreviousSchemeController.show()))
              }
          }
        }
        else {
          s4lConnector.saveFormData(KeystoreKeys.backLinkProposedInvestment, routes.HadOtherInvestmentsController.show().url)
          clearPreviousInvestments(s4lConnector)
          Future.successful(Redirect(routes.ProposedInvestmentController.show()))
        }
      }
    }
  }
}
