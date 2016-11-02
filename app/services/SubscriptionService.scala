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

package services

import connectors.SubscriptionConnector
import models.etmp.SubscriptionTypeModel
import play.api.Logger
import uk.gov.hmrc.play.http.HeaderCarrier
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

trait SubscriptionService {

  val subscriptionConnector: SubscriptionConnector

  def getEtmpSubscriptionDetails(tavcRef: String)(implicit hc: HeaderCarrier) : Future[Option[SubscriptionTypeModel]] = {
    subscriptionConnector.getSubscriptionDetails(tavcRef) map {
      case Some(subscriptionDetails) =>
        Try (subscriptionDetails.json.as[SubscriptionTypeModel]) match {
          case Success(subscriptionData) => Some(subscriptionData)
          case Failure(ex) =>
            Logger.error(s"[SubscriptionService][getEtmpContactDetails] - Failed to parse JSON response into SubscriptionTypeModel. Message=${ex.getMessage}")
            None
        }
      case _ =>
        Logger.error(s"[SubscriptionService][getEtmpContactDetails] - No Subscription Details Retrieved")
        None
    }
  }
}

object SubscriptionService extends SubscriptionService {
  val subscriptionConnector = SubscriptionConnector
}
