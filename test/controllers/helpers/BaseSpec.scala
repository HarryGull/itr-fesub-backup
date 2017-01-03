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

package controllers.helpers

import common.Constants
import connectors.{EnrolmentConnector, S4LConnector, SubmissionConnector}
import fixtures.SubmissionFixture
import models.fileUpload.{EnvelopeFile, Metadata}
import models.registration.RegistrationDetailsModel
import models.{UsedInvestmentReasonBeforeModel, YourCompanyNeedModel, _}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import services.{FileUploadService, RegistrationDetailsService, SubscriptionService}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

trait BaseSpec extends UnitSpec with WithFakeApplication with MockitoSugar with FakeRequestHelper with SubmissionFixture with BeforeAndAfterEach {

  val mockS4lConnector = mock[S4LConnector]
  val mockEnrolmentConnector = mock[EnrolmentConnector]
  val mockSubmissionConnector = mock[SubmissionConnector]
  val mockSubscriptionService= mock[SubscriptionService]
  val mockRegistrationDetailsService = mock[RegistrationDetailsService]
  val mockFileUploadService = mock[FileUploadService]


  override def beforeEach() {
    reset(mockS4lConnector)
    reset(mockEnrolmentConnector)
    reset(mockSubmissionConnector)
  }

  val applicationHubModelMax = ApplicationHubModel("Company ltd", AddressModel("1 ABCDE Street","FGHIJ Town", Some("FGHIJKL Town"),Some("MNO County"),
    Some("tf4 2ls"),"GB"), ContactDetailsModel("Firstname","Lastname",Some("0123324234234"),Some("4567324234324"),"test@test.com"))
  val applicationHubModelMin = ApplicationHubModel("Company ltd", AddressModel("1 ABCDE Street","FGHIJ Town", None,None,None,"GB"),
    ContactDetailsModel("Firstname","Lastname",None,None,"test@test.com"))

  val addressModel = AddressModel("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), Some("AB1 1AB"), "GB")
  val subscriptionDetailsModel = SubscriptionDetailsModel("",contactDetailsModel,contactAddressModel)

  val contactDetailsModel = ContactDetailsModel("Test", "Name", Some("01111 111111"), Some("0872552488"), "test@test.com")
  val contactDetailsOneNumberModel = ContactDetailsModel("Test", "Name", None, Some("0872552488"), "test@test.com")
  val confirmContactDetailsModel = ConfirmContactDetailsModel(Constants.StandardRadioButtonYesValue, contactDetailsModel)

  val contactAddressModel = new AddressModel("ABC XYZ", "1 ABCDE Street", countryCode = "JP")
  
  val investmentGrowModel = InvestmentGrowModel("At vero eos et accusamusi et iusto odio dignissimos ducimus qui blanditiis praesentium " +
    "voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique " +
    "sunt in culpa qui officia deserunt mollitia animi, tid est laborum etttt dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. " +
    "Nam libero tempore, cum soluta nobis est eligendi optio cumque nihili impedit quo minus id quod maxime placeat facere possimus")

  val natureOfBusinessModel = NatureOfBusinessModel("Creating new products")

  val operatingCostsModel = OperatingCostsModel("4100200", "3600050", "4252500", "410020", "360005", "425250", "2006", "2005", "2004")

  val confirmCorrespondAddressModel = ConfirmCorrespondAddressModel(Constants.StandardRadioButtonYesValue, addressModel)

  val newGeographicalMarketModelYes = NewGeographicalMarketModel(Constants.StandardRadioButtonYesValue)
  val newGeographicalMarketModelNo = NewGeographicalMarketModel(Constants.StandardRadioButtonNoValue)

  val newProductMarketModelYes = NewProductModel(Constants.StandardRadioButtonYesValue)
  val newProductMarketModelNo = NewProductModel(Constants.StandardRadioButtonNoValue)

  val isKnowledgeIntensiveModelYes = IsKnowledgeIntensiveModel(Constants.StandardRadioButtonYesValue)
  val isKnowledgeIntensiveModelNo = IsKnowledgeIntensiveModel(Constants.StandardRadioButtonNoValue)

  val kiProcessingModelMet = KiProcessingModel(None, Some(true), Some(false), Some(false), Some(false))
  val kiProcessingModelNotMet = KiProcessingModel(Some(false),Some(false), Some(false), Some(false), Some(false))

  val kiProcessingModelIsKi = KiProcessingModel(Some(true), Some(true), Some(true), Some(true), Some(true), Some(true))

  val trueKIModel = KiProcessingModel(Some(true), Some(true), Some(true), Some(true), None, Some(true))
  val falseKIModel = KiProcessingModel(Some(false), Some(false), Some(false), Some(false), None, Some(false))
  val isKiKIModel = KiProcessingModel(Some(false), Some(true), Some(true), Some(true), Some(true), Some(true))
  val missingDataKIModel = KiProcessingModel(Some(true),None, Some(true), Some(true), Some(true), Some(true))

  val hadPreviousRFIModelYes = HadPreviousRFIModel(Constants.StandardRadioButtonYesValue)
  val hadPreviousRFIModelNo = HadPreviousRFIModel(Constants.StandardRadioButtonNoValue)

  val commercialSaleYear = 2004
  val commercialSaleMonth = 2
  val commercialSaleDay = 29
  val commercialSaleModelYes = CommercialSaleModel(Constants.StandardRadioButtonYesValue,
    Some(commercialSaleDay), Some(commercialSaleMonth), Some(commercialSaleYear))
  val commercialSaleModelNo = CommercialSaleModel(Constants.StandardRadioButtonNoValue, None, None, None)

  val subsidiariesModelYes = SubsidiariesModel(Constants.StandardRadioButtonYesValue)
  val subsidiariesModelNo = SubsidiariesModel(Constants.StandardRadioButtonNoValue)

  val subsidiariesNinetyOwnedModelYes = SubsidiariesNinetyOwnedModel(Constants.StandardRadioButtonYesValue)
  val subsidiariesNinetyOwnedModelNo = SubsidiariesNinetyOwnedModel(Constants.StandardRadioButtonNoValue)

  val previousBeforeDOFCSModelYes = PreviousBeforeDOFCSModel(Constants.StandardRadioButtonYesValue)
  val previousBeforeDOFCSModelNo = PreviousBeforeDOFCSModel(Constants.StandardRadioButtonNoValue)

  val percentageStaffWithMastersModelYes = PercentageStaffWithMastersModel(Constants.StandardRadioButtonYesValue)
  val percentageStaffWithMastersModelNo = PercentageStaffWithMastersModel(Constants.StandardRadioButtonNoValue)

  val subsidiariesSpendingInvestmentModelYes = SubsidiariesSpendingInvestmentModel(Constants.StandardRadioButtonYesValue)
  val subsidiariesSpendingInvestmentModelNo = SubsidiariesSpendingInvestmentModel(Constants.StandardRadioButtonNoValue)

  val proposedInvestmentAmount = 5000000
  val proposedInvestmentModel = ProposedInvestmentModel(proposedInvestmentAmount)

  val previousSchemeModel1 = PreviousSchemeModel(
    Constants.PageInvestmentSchemeEisValue, 2356, None, None, Some(4), Some(12), Some(2009), Some(1))
  val previousSchemeModel2 = PreviousSchemeModel(
    Constants.PageInvestmentSchemeSeisValue, 2356, Some(666), None, Some(4), Some(12), Some(2010), Some(3))
  val previousSchemeModel3 = PreviousSchemeModel(
    Constants.PageInvestmentSchemeAnotherValue, 2356, None, Some("My scheme"), Some(9), Some(8), Some(2010), Some(5))
  val previousSchemeVectorList = Vector(previousSchemeModel1, previousSchemeModel2, previousSchemeModel3)

  val registeredAddressModel = RegisteredAddressModel("AB1 1AB")

  val taxpayerReferenceModel = TaxpayerReferenceModel("1234567891012")

  val tenYearPlanModelYes = TenYearPlanModel(Constants.StandardRadioButtonYesValue, Some("At vero eos et accusamus et iusto odio dignissimos ducimus qui " +
    "blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique " +
    "sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. " +
    "Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus"))
  val tenYearPlanModelNo = TenYearPlanModel(Constants.StandardRadioButtonNoValue, None)

  val annualTurnoverCostsModel = AnnualTurnoverCostsModel("750000", "800000", "934000", "231000", "340000", "2004", "2005", "2006", "2007", "2008")

  val usedInvestmentReasonBeforeModelYes = UsedInvestmentReasonBeforeModel(Constants.StandardRadioButtonYesValue)
  val usedInvestmentReasonBeforeModelNo = UsedInvestmentReasonBeforeModel(Constants.StandardRadioButtonNoValue)

  val yourCompanyNeedModel = YourCompanyNeedModel("AA")

  val metaData = Metadata(None)
  val fileOne = EnvelopeFile("1","status","testOne.pdf","pdf","dateCreated",metaData,"href")
  val fileTwo = EnvelopeFile("2","status","testTwo.pdf","pdf","dateCreated",metaData,"href")
  val fileThree = EnvelopeFile("3","status","testThree.pdf","pdf","dateCreated",metaData,"href")
  val fileFour = EnvelopeFile("4","status","testFour.pdf","pdf","dateCreated",metaData,"href")
  val fileFive = EnvelopeFile("5","status","testFive.pdf","pdf","dateCreated",metaData,"href")
  val files = Seq(fileOne,fileTwo)
  val maxFiles = Seq(fileOne,fileTwo,fileThree,fileFour,fileFive)

}
