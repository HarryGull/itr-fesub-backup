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

package controllers.helpers

import java.io.File
import java.nio.file.{Paths, Files}
import java.util.UUID

import auth._
import builders.SessionBuilder
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.api.test.{FakeHeaders, FakeRequest}
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.SessionKeys

import scala.concurrent.Future

trait FakeRequestHelper{
  lazy val sessionId = UUID.randomUUID.toString
  lazy val fakeRequest = FakeRequest()
  lazy val fakeRequestWithSession = fakeRequest.withSession(SessionKeys.sessionId -> s"session-$sessionId")
  lazy val authorisedFakeRequest = authenticatedFakeRequest()
  lazy val timedOutFakeRequest = timeoutFakeRequest()

  lazy val multipartFileData = {
    val tempFile = TemporaryFile(new java.io.File("/tmp/test.file"))
    val part = FilePart[TemporaryFile](key = "supporting-docs", filename = "test.file", contentType = Some(".pdf"), ref = tempFile)
    MultipartFormData(
      dataParts = Map(),
      files = Seq(part),
      badParts = Seq(),
      missingFileParts = Seq())
  }

  def fakeRequestToPOST (input: (String, String)*): FakeRequest[AnyContentAsFormUrlEncoded] = {
    fakeRequest.withFormUrlEncodedBody(input: _*)
  }

  def fakeRequestWithSessionToPOST (input: (String, String)*): FakeRequest[AnyContentAsFormUrlEncoded] = {
    fakeRequestWithSession.withFormUrlEncodedBody(input: _*)
  }

  def authorisedFakeRequestToPOST (input: (String, String)*): FakeRequest[AnyContentAsFormUrlEncoded] = {
    authorisedFakeRequest.withFormUrlEncodedBody(input: _*)
  }

  def timeoutFakeRequestToPOST (input: (String, String)*): FakeRequest[AnyContentAsFormUrlEncoded] = {
    timedOutFakeRequest.withFormUrlEncodedBody(input: _*)
  }


  def showWithSessionAndAuth(action: Action[AnyContent])(test: Future[Result] => Any) {
    val result = action.apply(authorisedFakeRequest)
    test(result)
  }

  def showWithSessionWithoutAuth(action: Action[AnyContent])(test: Future[Result] => Any) {
    val result = action.apply(fakeRequestWithSession)
    test(result)
  }

  def showWithoutSession(action: Action[AnyContent])(test: Future[Result] => Any){
    val result = action.apply(fakeRequest)
    test(result)
  }

  def showWithTimeout(action: Action[AnyContent])(test: Future[Result] => Any){
    val result = action.apply(timedOutFakeRequest)
    test(result)
  }

  def submitWithSessionAndAuth(action: Action[AnyContent],input: (String, String)*)(test: Future[Result] => Any) {
    val result = action.apply(authorisedFakeRequestToPOST(input: _*))
    test(result)
  }

  def submitWithSessionWithoutAuth(action: Action[AnyContent],input: (String, String)*)(test: Future[Result] => Any) {
    val result = action.apply(fakeRequestWithSessionToPOST(input: _*))
    test(result)
  }

  def submitWithoutSession(action: Action[AnyContent],input: (String, String)*)(test: Future[Result] => Any) {
    val result = action.apply(fakeRequestToPOST(input: _*))
    test(result)
  }

  def submitWithTimeout(action: Action[AnyContent],input: (String, String)*)(test: Future[Result] => Any) {
    val result = action.apply(timeoutFakeRequestToPOST(input: _*))
    test(result)
  }

  def submitWithSessionAndAuthAndMultiPartFileData(action: Action[AnyContent],input: (String, String)*)(test: Future[Result] => Any) {
    val result = action.apply(authorisedFakeRequestToPOST(input: _*).withMultipartFormDataBody(multipartFileData))
    test(result)
  }

}

