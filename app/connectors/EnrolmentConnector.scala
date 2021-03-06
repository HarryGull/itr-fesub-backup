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

package connectors

import config.WSHttp
import play.api.http.Status._
import uk.gov.hmrc.play.config.ServicesConfig
import auth.Enrolment
import common.Constants
import play.api.Logger
import services.TokenService

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}


trait EnrolmentConnector extends ServicesConfig {

  def serviceUrl: String

  def authorityUri: String

  def http: HttpGet with HttpPost

  def getTAVCEnrolment(uri: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Enrolment]] = {
    val getUrl = s"$serviceUrl$uri/enrolments"
    http.GET[HttpResponse](getUrl).map {
      response =>
        response.status match {
          case OK => response.json.as[Seq[Enrolment]].find(_.key == Constants.enrolmentOrgKey)
          case status => None
        }
    }
  }

  def getTavcReferenceNumber(uri: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[String] = {
    getTAVCEnrolment(uri).map {
      case Some(enrolment) => enrolment.identifiers.find(_.key == Constants.enrolmentTavcRefKey).fold("")(_.value)
      case _ => ""
    }
  }

  def validateToken(tokenId: Option[String])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    Logger.info(s"[EnrolmentConnector][validateToken] - START tokenId=${tokenId.getOrElse("")}")
    TokenService.validateTemporaryToken(tokenId)(hc, ec)
  }
}


object EnrolmentConnector extends EnrolmentConnector {
  lazy val serviceUrl = baseUrl("auth")
  val authorityUri = "auth/authority"
  val http: HttpGet with HttpPost = WSHttp
}
