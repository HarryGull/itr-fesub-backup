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

import connectors.KeystoreConnector
import controllers.predicates.ValidActiveSession
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.mvc._
import models.{CommercialSaleModel, KiProcessingModel}
import common._
import forms.CommercialSaleForm._
import views.html.companyDetails.CommercialSale

import scala.concurrent.Future

object CommercialSaleController extends CommercialSaleController {
  val keyStoreConnector: KeystoreConnector = KeystoreConnector
}

trait CommercialSaleController extends FrontendController with ValidActiveSession {

  val keyStoreConnector: KeystoreConnector

  val show = ValidateSession.async { implicit request =>
    keyStoreConnector.fetchAndGetFormData[CommercialSaleModel](KeystoreKeys.commercialSale).map {
      case Some(data) => Ok(CommercialSale(commercialSaleForm.fill(data)))
      case None => Ok(CommercialSale(commercialSaleForm))
    }
  }

  val submit = Action.async { implicit request =>

    def routeRequest(kiModel: Option[KiProcessingModel]): Future[Result] = {
      kiModel match {
        case Some(data) if data.dateConditionMet.isEmpty =>
          Future.successful(Redirect(routes.DateOfIncorporationController.show()))
        case Some(dataWithDateCondition) => {
          if (dataWithDateCondition.dateConditionMet.get) {
            Future.successful(Redirect(routes.IsKnowledgeIntensiveController.show()))
          }
          else {
            keyStoreConnector.saveFormData(KeystoreKeys.backLinkSubsidiaries, routes.CommercialSaleController.show().toString())
            Future.successful(Redirect(routes.SubsidiariesController.show()))
          }
        }
        case None => Future.successful(Redirect(routes.DateOfIncorporationController.show()))
      }
    }

    commercialSaleForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(CommercialSale(formWithErrors)))
      },
      validFormData => {
        keyStoreConnector.saveFormData(KeystoreKeys.commercialSale, validFormData)
        for {
          model <- keyStoreConnector.fetchAndGetFormData[KiProcessingModel](KeystoreKeys.kiProcessingModel)
          route <- routeRequest(model)
        } yield route
      }
    )
  }
}
