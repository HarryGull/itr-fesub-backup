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

package views

import auth.MockAuthConnector
import config.FrontendAppConfig
import controllers.{CheckAnswersController, routes}
import models._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.Mockito._
import play.api.i18n.Messages
import views.helpers.CheckAnswersSpec
import play.api.test.Helpers._

class CheckAnswersCompanyDetailsSpec extends CheckAnswersSpec {

  object TestController extends CheckAnswersController {
    override lazy val applicationConfig = FrontendAppConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val s4lConnector = mockS4lConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
  }

  "The Check Answers page" should {

    "Verify that the Check Answers page contains the correct elements for Section 1: Company details" +
      " when it is fully populated with company detail models" in new Setup {
      val document: Document = {
        previousRFISetup()
        contactDetailsSetup()
        contactAddressSetup()
        investmentSetup()
        companyDetailsSetup(Some(yourCompanyNeedModel),Some(taxpayerReferenceModel),Some(registeredAddressModel),Some(dateOfIncorporationModel),
          Some(natureOfBusinessModel), Some(commercialSaleModelYes), Some(isKnowledgeIntensiveModelYes), Some(operatingCostsModel),
          Some(percentageStaffWithMastersModelYes), Some(tenYearPlanModelYes), Some(subsidiariesModelYes))
        val result = TestController.show.apply(authorisedFakeRequest.withFormUrlEncodedBody())
        Jsoup.parse(contentAsString(result))
      }
      
      lazy val companyDetailsTableTBody = document.getElementById("company-details-table").select("tbody")
      lazy val notAvailableMessage = Messages("common.notAvailable")

      document.title() shouldBe Messages("page.checkAndSubmit.checkAnswers.heading")
      document.getElementById("main-heading").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.heading")
      document.getElementById("description").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.description")

      //Section 1 table heading
      document.getElementById("companyDetailsSection-table-heading").text() shouldBe Messages("summaryQuestion.companyDetailsSection")
      //Nature of business
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-question").text() shouldBe
        Messages("summaryQuestion.natureOfBusiness")
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-answer").text() shouldBe
        natureOfBusinessModel.natureofbusiness
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-link")
        .attr("href") shouldEqual routes.NatureOfBusinessController.show().url
      //Date of incorporation
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-question").text() shouldBe
        Messages("summaryQuestion.dateOfIncorporation")
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-answer").text() shouldBe
        DateOfIncorporationModel.toDateString(dateOfIncorporationModel.day.get,dateOfIncorporationModel.month.get,dateOfIncorporationModel.year.get)
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-link")
        .attr("href") shouldEqual routes.DateOfIncorporationController.show().url
      //Date of first commercial sale
      companyDetailsTableTBody.select("tr").get(2).getElementById("commercialSaleDate-question").text() shouldBe
        Messages("summaryQuestion.commercialSaleDate")
      companyDetailsTableTBody.select("tr").get(2).getElementById("commercialSaleDate-answer").text() shouldBe
        CommercialSaleModel.toDateString(commercialSaleModelYes.commercialSaleDay.get,commercialSaleModelYes.commercialSaleMonth.get,commercialSaleModelYes.commercialSaleYear.get)
      companyDetailsTableTBody.select("tr").get(2).getElementById("commercialSaleDate-link")
        .attr("href") shouldEqual routes.CommercialSaleController.show().url
      //Is Knowledge Intensive
      companyDetailsTableTBody.select("tr").get(3).getElementById("knowledgeIntensive-question").text() shouldBe
        Messages("summaryQuestion.knowledgeIntensive")
      companyDetailsTableTBody.select("tr").get(3).getElementById("knowledgeIntensive-answer").text() shouldBe
        isKnowledgeIntensiveModelYes.isKnowledgeIntensive
      companyDetailsTableTBody.select("tr").get(3).getElementById("knowledgeIntensive-link")
        .attr("href") shouldEqual routes.IsKnowledgeIntensiveController.show().url


      //Operating costs
      companyDetailsTableTBody.select("tr").get(4).getElementById("operatingCosts-question").text() shouldBe
        Messages("summaryQuestion.operatingCosts")

      companyDetailsTableTBody.select("tr").get(4).getElementById("operatingCosts-Line0").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.operatingCosts1stYear, Messages("page.companyDetails.OperatingCosts.row.heading.one"))

      companyDetailsTableTBody.select("tr").get(4).getElementById("operatingCosts-Line1").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.operatingCosts2ndYear, Messages("page.companyDetails.OperatingCosts.row.heading.two"))

      companyDetailsTableTBody.select("tr").get(4).getElementById("operatingCosts-Line2").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.operatingCosts3rdYear, Messages("page.companyDetails.OperatingCosts.row.heading.three"))

      companyDetailsTableTBody.select("tr").get(4).getElementById("operatingCosts-link")
        .attr("href") shouldEqual routes.OperatingCostsController.show().url


      //R&D costs
      companyDetailsTableTBody.select("tr").get(5).getElementById("rdCosts-question").text() shouldBe
        Messages("summaryQuestion.rdCosts")

      companyDetailsTableTBody.select("tr").get(5).getElementById("rdCosts-Line0").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.rAndDCosts1stYear, Messages("page.companyDetails.OperatingCosts.row.heading.one"))

      companyDetailsTableTBody.select("tr").get(5).getElementById("rdCosts-Line1").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.rAndDCosts2ndYear, Messages("page.companyDetails.OperatingCosts.row.heading.two"))

      companyDetailsTableTBody.select("tr").get(5).getElementById("rdCosts-Line2").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.rAndDCosts3rdYear, Messages("page.companyDetails.OperatingCosts.row.heading.three"))

      companyDetailsTableTBody.select("tr").get(5).getElementById("rdCosts-link")
        .attr("href") shouldEqual routes.OperatingCostsController.show().url
      //Percentage of staff with masters
      companyDetailsTableTBody.select("tr").get(6).getElementById("percentageStaffWithMasters-question").text() shouldBe
        Messages("summaryQuestion.percentageStaffWithMasters")
      companyDetailsTableTBody.select("tr").get(6).getElementById("percentageStaffWithMasters-answer").text() shouldBe
        PercentageStaffWithMastersModel.staffWithMastersToString(percentageStaffWithMastersModelYes.staffWithMasters)
      companyDetailsTableTBody.select("tr").get(6).getElementById("percentageStaffWithMasters-link")
        .attr("href") shouldEqual routes.PercentageStaffWithMastersController.show().url

      document.getElementById("submit").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.button.confirm")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.SupportingDocumentsController.show().url
    }
  }


  "The Check Answers page" should {

    "Verify that the Check Answers page contains an empty table for Section 1: Company details" +
      " when an empty set of company detail models are passed" in new Setup {
      val document: Document = {
        previousRFISetup()
        contactDetailsSetup()
        contactAddressSetup()
        investmentSetup()
        companyDetailsSetup()
        val result = TestController.show.apply(authorisedFakeRequest.withFormUrlEncodedBody())
        Jsoup.parse(contentAsString(result))
      }

      lazy val companyDetailsTableTBody = document.getElementById("company-details-table").select("tbody")
      lazy val notAvailableMessage = Messages("common.notAvailable")

      document.title() shouldBe Messages("page.checkAndSubmit.checkAnswers.heading")
      document.getElementById("main-heading").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.heading")
      document.getElementById("description").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.description")

      //Section 1 table heading
      document.getElementById("companyDetailsSection-table-heading").text() shouldBe Messages("summaryQuestion.companyDetailsSection")
      companyDetailsTableTBody.select("tr").size() shouldBe 0

      document.getElementById("submit").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.button.confirm")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.SupportingDocumentsController.show().url
    }
  }


  "The Check Answers page" should {

    "Verify that the Check Answers page contains the correct elements for Section 1: Company details" +
      " when it is fully populated with company detail models but a commercial sale has not been made" in new Setup {
      val document: Document = {
        previousRFISetup()
        contactDetailsSetup()
        contactAddressSetup()
        investmentSetup()
        companyDetailsSetup(Some(yourCompanyNeedModel), Some(taxpayerReferenceModel), Some(registeredAddressModel), Some(dateOfIncorporationModel),
          Some(natureOfBusinessModel), Some(commercialSaleModelNo), Some(isKnowledgeIntensiveModelYes), Some(operatingCostsModel),
          Some(percentageStaffWithMastersModelNo), Some(tenYearPlanModelYes), Some(subsidiariesModelYes))
        val result = TestController.show.apply(authorisedFakeRequest.withFormUrlEncodedBody())
        Jsoup.parse(contentAsString(result))
      }

      lazy val companyDetailsTableTBody = document.getElementById("company-details-table").select("tbody")
      lazy val notAvailableMessage = Messages("common.notAvailable")

      document.title() shouldBe Messages("page.checkAndSubmit.checkAnswers.heading")
      document.getElementById("main-heading").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.heading")
      document.getElementById("description").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.description")

      //Section 1 table heading
      document.getElementById("companyDetailsSection-table-heading").text() shouldBe Messages("summaryQuestion.companyDetailsSection")
      //Nature of business
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-question").text() shouldBe
        Messages("summaryQuestion.natureOfBusiness")
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-answer").text() shouldBe
        natureOfBusinessModel.natureofbusiness
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-link")
        .attr("href") shouldEqual routes.NatureOfBusinessController.show().url
      //Date of incorporation
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-question").text() shouldBe
        Messages("summaryQuestion.dateOfIncorporation")
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-answer").text() shouldBe
        DateOfIncorporationModel.toDateString(dateOfIncorporationModel.day.get,dateOfIncorporationModel.month.get,dateOfIncorporationModel.year.get)
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-link")
        .attr("href") shouldEqual routes.DateOfIncorporationController.show().url
      //Is Knowledge Intensive
      companyDetailsTableTBody.select("tr").get(2).getElementById("knowledgeIntensive-question").text() shouldBe
        Messages("summaryQuestion.knowledgeIntensive")
      companyDetailsTableTBody.select("tr").get(2).getElementById("knowledgeIntensive-answer").text() shouldBe
        isKnowledgeIntensiveModelYes.isKnowledgeIntensive
      companyDetailsTableTBody.select("tr").get(2).getElementById("knowledgeIntensive-link")
        .attr("href") shouldEqual routes.IsKnowledgeIntensiveController.show().url
      //Operating costs
      companyDetailsTableTBody.select("tr").get(3).getElementById("operatingCosts-question").text() shouldBe
        Messages("summaryQuestion.operatingCosts")

      // check multi line field
      companyDetailsTableTBody.select("tr").get(3).getElementById("operatingCosts-Line0").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.operatingCosts1stYear, Messages("page.companyDetails.OperatingCosts.row.heading.one"))

      companyDetailsTableTBody.select("tr").get(3).getElementById("operatingCosts-Line1").text() shouldBe
      OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.operatingCosts2ndYear, Messages("page.companyDetails.OperatingCosts.row.heading.two"))

      companyDetailsTableTBody.select("tr").get(3).getElementById("operatingCosts-Line2").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.operatingCosts3rdYear, Messages("page.companyDetails.OperatingCosts.row.heading.three"))

      companyDetailsTableTBody.select("tr").get(3).getElementById("operatingCosts-link")
        .attr("href") shouldEqual routes.OperatingCostsController.show().url
      //R&D costs
      companyDetailsTableTBody.select("tr").get(4).getElementById("rdCosts-question").text() shouldBe
        Messages("summaryQuestion.rdCosts")

      // check multi line field
      companyDetailsTableTBody.select("tr").get(4).getElementById("rdCosts-Line0").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.rAndDCosts1stYear, Messages("page.companyDetails.OperatingCosts.row.heading.one"))

      companyDetailsTableTBody.select("tr").get(4).getElementById("rdCosts-Line1").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.rAndDCosts2ndYear, Messages("page.companyDetails.OperatingCosts.row.heading.two"))

      companyDetailsTableTBody.select("tr").get(4).getElementById("rdCosts-Line2").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.rAndDCosts3rdYear, Messages("page.companyDetails.OperatingCosts.row.heading.three"))

      companyDetailsTableTBody.select("tr").get(4).getElementById("rdCosts-link")
        .attr("href") shouldEqual routes.OperatingCostsController.show().url
      //Percentage of staff with masters
      companyDetailsTableTBody.select("tr").get(5).getElementById("percentageStaffWithMasters-question").text() shouldBe
        Messages("summaryQuestion.percentageStaffWithMasters")
      companyDetailsTableTBody.select("tr").get(5).getElementById("percentageStaffWithMasters-answer").text() shouldBe
        PercentageStaffWithMastersModel.staffWithMastersToString(percentageStaffWithMastersModelNo.staffWithMasters)
      companyDetailsTableTBody.select("tr").get(5).getElementById("percentageStaffWithMasters-link")
        .attr("href") shouldEqual routes.PercentageStaffWithMastersController.show().url
      //Has ten year plan
      companyDetailsTableTBody.select("tr").get(6).getElementById("tenYearPlan-question").text() shouldBe
        Messages("summaryQuestion.developmentPlan")++" "++Messages("summaryQuestion.developmentPlanDesc")

      companyDetailsTableTBody.select("tr").get(6).getElementById("tenYearPlan-answer").text() shouldBe
        tenYearPlanModelYes.hasTenYearPlan++" "++tenYearPlanModelYes.tenYearPlanDesc.get
      companyDetailsTableTBody.select("tr").get(6).getElementById("tenYearPlan-link")
        .attr("href") shouldEqual routes.TenYearPlanController.show().url

      document.getElementById("submit").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.button.confirm")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.SupportingDocumentsController.show().url
    }
  }

  "The Check Answers page" should {

    "Verify that the Check Answers page contains the correct elements for Section 1: Company details" +
      " when it is fully populated with company detail models but it is not knowledge intensive and therefore should not show KI pages" in new Setup {
      val document: Document = {
        previousRFISetup()
        contactDetailsSetup()
        contactAddressSetup()
        investmentSetup()
        companyDetailsSetup(Some(yourCompanyNeedModel), Some(taxpayerReferenceModel), Some(registeredAddressModel), Some(dateOfIncorporationModel),
          Some(natureOfBusinessModel), Some(commercialSaleModelNo), Some(isKnowledgeIntensiveModelNo), Some(operatingCostsModel),
          Some(percentageStaffWithMastersModelYes), Some(tenYearPlanModelYes), Some(subsidiariesModelYes))
        val result = TestController.show.apply(authorisedFakeRequest.withFormUrlEncodedBody())
        Jsoup.parse(contentAsString(result))
      }

      lazy val companyDetailsTableTBody = document.getElementById("company-details-table").select("tbody")
      lazy val notAvailableMessage = Messages("common.notAvailable")

      document.title() shouldBe Messages("page.checkAndSubmit.checkAnswers.heading")
      document.getElementById("main-heading").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.heading")
      document.getElementById("description").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.description")

      //Section 1 table heading
      document.getElementById("companyDetailsSection-table-heading").text() shouldBe Messages("summaryQuestion.companyDetailsSection")
      //Nature of business
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-question").text() shouldBe
        Messages("summaryQuestion.natureOfBusiness")
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-answer").text() shouldBe
        natureOfBusinessModel.natureofbusiness
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-link")
        .attr("href") shouldEqual routes.NatureOfBusinessController.show().url
      //Date of incorporation
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-question").text() shouldBe
        Messages("summaryQuestion.dateOfIncorporation")
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-answer").text() shouldBe
        DateOfIncorporationModel.toDateString(dateOfIncorporationModel.day.get,dateOfIncorporationModel.month.get,dateOfIncorporationModel.year.get)
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-link")
        .attr("href") shouldEqual routes.DateOfIncorporationController.show().url
      //Is Knowledge Intensive
      companyDetailsTableTBody.select("tr").get(2).getElementById("knowledgeIntensive-question").text() shouldBe
        Messages("summaryQuestion.knowledgeIntensive")
      companyDetailsTableTBody.select("tr").get(2).getElementById("knowledgeIntensive-answer").text() shouldBe
        isKnowledgeIntensiveModelNo.isKnowledgeIntensive
      companyDetailsTableTBody.select("tr").get(2).getElementById("knowledgeIntensive-link")
        .attr("href") shouldEqual routes.IsKnowledgeIntensiveController.show().url


      document.getElementById("submit").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.button.confirm")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.SupportingDocumentsController.show().url
    }
  }

  "The Check Answers page" should {

    "Verify that the Check Answers page contains the correct elements for Section 1: Company details" +
      " when it is fully populated with company detail models but it does not have a ten year plan and" +
      " so should not have a ten year description row" in new Setup {
      val document: Document = {
        previousRFISetup()
        contactDetailsSetup()
        contactAddressSetup()
        investmentSetup()
        companyDetailsSetup(Some(yourCompanyNeedModel), Some(taxpayerReferenceModel), Some(registeredAddressModel), Some(dateOfIncorporationModel),
          Some(natureOfBusinessModel), Some(commercialSaleModelYes), Some(isKnowledgeIntensiveModelYes), Some(operatingCostsModel),
          Some(percentageStaffWithMastersModelNo), Some(tenYearPlanModelNo), Some(subsidiariesModelYes))
        val result = TestController.show.apply(authorisedFakeRequest.withFormUrlEncodedBody())
        Jsoup.parse(contentAsString(result))
      }

      lazy val companyDetailsTableTBody = document.getElementById("company-details-table").select("tbody")
      lazy val notAvailableMessage = Messages("common.notAvailable")

      document.title() shouldBe Messages("page.checkAndSubmit.checkAnswers.heading")
      document.getElementById("main-heading").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.heading")
      document.getElementById("description").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.description")

      //Section 1 table heading
      document.getElementById("companyDetailsSection-table-heading").text() shouldBe Messages("summaryQuestion.companyDetailsSection")
      //Nature of business
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-question").text() shouldBe
        Messages("summaryQuestion.natureOfBusiness")
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-answer").text() shouldBe
        natureOfBusinessModel.natureofbusiness
      companyDetailsTableTBody.select("tr").get(0).getElementById("natureOfBusiness-link")
        .attr("href") shouldEqual routes.NatureOfBusinessController.show().url
      //Date of incorporation
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-question").text() shouldBe
        Messages("summaryQuestion.dateOfIncorporation")
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-answer").text() shouldBe
        DateOfIncorporationModel.toDateString(dateOfIncorporationModel.day.get,dateOfIncorporationModel.month.get,dateOfIncorporationModel.year.get)
      companyDetailsTableTBody.select("tr").get(1).getElementById("dateOfIncorporation-link")
        .attr("href") shouldEqual routes.DateOfIncorporationController.show().url
      //Date of first commercial sale
      companyDetailsTableTBody.select("tr").get(2).getElementById("commercialSaleDate-question").text() shouldBe
        Messages("summaryQuestion.commercialSaleDate")
      companyDetailsTableTBody.select("tr").get(2).getElementById("commercialSaleDate-answer").text() shouldBe
        CommercialSaleModel.toDateString(commercialSaleModelYes.commercialSaleDay.get,commercialSaleModelYes.commercialSaleMonth.get,commercialSaleModelYes.commercialSaleYear.get)
      companyDetailsTableTBody.select("tr").get(2).getElementById("commercialSaleDate-link")
        .attr("href") shouldEqual routes.CommercialSaleController.show().url
      //Is Knowledge Intensive
      companyDetailsTableTBody.select("tr").get(3).getElementById("knowledgeIntensive-question").text() shouldBe
        Messages("summaryQuestion.knowledgeIntensive")
      companyDetailsTableTBody.select("tr").get(3).getElementById("knowledgeIntensive-answer").text() shouldBe
        isKnowledgeIntensiveModelYes.isKnowledgeIntensive
      companyDetailsTableTBody.select("tr").get(3).getElementById("knowledgeIntensive-link")
        .attr("href") shouldEqual routes.IsKnowledgeIntensiveController.show().url
      //Operating costs
      companyDetailsTableTBody.select("tr").get(4).getElementById("operatingCosts-question").text() shouldBe
        Messages("summaryQuestion.operatingCosts")

      companyDetailsTableTBody.select("tr").get(4).getElementById("operatingCosts-Line0").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.operatingCosts1stYear, Messages("page.companyDetails.OperatingCosts.row.heading.one"))
      companyDetailsTableTBody.select("tr").get(4).getElementById("operatingCosts-Line1").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.operatingCosts2ndYear, Messages("page.companyDetails.OperatingCosts.row.heading.two"))
      companyDetailsTableTBody.select("tr").get(4).getElementById("operatingCosts-Line2").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.operatingCosts3rdYear, Messages("page.companyDetails.OperatingCosts.row.heading.three"))

      companyDetailsTableTBody.select("tr").get(4).getElementById("operatingCosts-link")
        .attr("href") shouldEqual routes.OperatingCostsController.show().url
      //R&D costs
      companyDetailsTableTBody.select("tr").get(5).getElementById("rdCosts-question").text() shouldBe
        Messages("summaryQuestion.rdCosts")

      companyDetailsTableTBody.select("tr").get(5).getElementById("rdCosts-Line0").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.rAndDCosts1stYear, Messages("page.companyDetails.OperatingCosts.row.heading.one"))

      companyDetailsTableTBody.select("tr").get(5).getElementById("rdCosts-Line1").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.rAndDCosts2ndYear, Messages("page.companyDetails.OperatingCosts.row.heading.two"))

      companyDetailsTableTBody.select("tr").get(5).getElementById("rdCosts-Line2").text() shouldBe
        OperatingCostsModel.getOperatingAndRDCostsAsFormattedString(operatingCostsModel.rAndDCosts3rdYear, Messages("page.companyDetails.OperatingCosts.row.heading.three"))

      companyDetailsTableTBody.select("tr").get(5).getElementById("rdCosts-link")
        .attr("href") shouldEqual routes.OperatingCostsController.show().url
      //Percentage of staff with masters
      companyDetailsTableTBody.select("tr").get(6).getElementById("percentageStaffWithMasters-question").text() shouldBe
        Messages("summaryQuestion.percentageStaffWithMasters")
      companyDetailsTableTBody.select("tr").get(6).getElementById("percentageStaffWithMasters-answer").text() shouldBe
        PercentageStaffWithMastersModel.staffWithMastersToString(percentageStaffWithMastersModelNo.staffWithMasters)
      companyDetailsTableTBody.select("tr").get(6).getElementById("percentageStaffWithMasters-link")
        .attr("href") shouldEqual routes.PercentageStaffWithMastersController.show().url

      //Has ten year plan
      companyDetailsTableTBody.select("tr").get(7).getElementById("tenYearPlan-question").text() shouldBe Messages("summaryQuestion.developmentPlan")
      companyDetailsTableTBody.select("tr").get(7).getElementById("tenYearPlan-answer").text() shouldBe
        tenYearPlanModelNo.hasTenYearPlan
      companyDetailsTableTBody.select("tr").get(7).getElementById("tenYearPlan-link")
        .attr("href") shouldEqual routes.TenYearPlanController.show().url


      document.getElementById("submit").text() shouldBe Messages("page.checkAndSubmit.checkAnswers.button.confirm")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.SupportingDocumentsController.show().url
    }
  }
}
