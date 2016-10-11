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

package Forms
import common.Constants
import models._
import models.submission._
import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class TestModelConversionSpec extends UnitSpec{

  val fullCorrespondenceAddress : AddressModel = AddressModel(addressLine1 = "line 1",
    addressLine2 = "Line 2", addressLine3 = Some("Line 3"), addressLine4 = Some("Line 4"),
    postCode = Some("TF1 4NY"), countryCode = "GB")

  val fullContactDetailsModel: ContactDetailsModel = ContactDetailsModel(forename = "Fred",
    surname = "Flinsstone", telephoneNumber = "01952 255899", email = "rubble@jurassic.com")

  val schemeTypes :SchemeTypesModel = SchemeTypesModel(eis = true, seis = false, vct = false, sitr = false)
  val testAgentRef = "AARN1234567"

  val marketInfo = SubmitMarketInfoModel( newGeographicalMarketModel = NewGeographicalMarketModel(Constants.StandardRadioButtonNoValue),
    newProductModel = NewProductModel(Constants.StandardRadioButtonYesValue))

  val opcostFull = OperatingCostsModel(operatingCosts1stYear = "101", operatingCosts2ndYear = "102",
    operatingCosts3rdYear = "103", rAndDCosts1stYear = "201", rAndDCosts2ndYear = "202", rAndDCosts3rdYear = "203")

  val costsFull = utils.Converters.operatingCostsToList(opcostFull, 2005)

  val turnover = List(TurnoverCostModel("2003", turnover = CostModel("66")),
    TurnoverCostModel("2004", turnover = CostModel("67")),
    TurnoverCostModel("2004", turnover = CostModel("68")),
    TurnoverCostModel("2004", turnover = CostModel("69")),
    TurnoverCostModel("2005", turnover = CostModel("70")))

  val dateOfIncorporationModel = DateOfIncorporationModel(day = Some(5), month = Some(6), year = Some(2007))

val subsidiaryPerformingTradeMinimumReq = SubsidiaryPerformingTradeModel(ninetyOwnedModel = SubsidiariesNinetyOwnedModel("true"),
  organisationName = "Made up test subsidiary org name")
val subsidiaryPerformingTradeWithAddress = SubsidiaryPerformingTradeModel(ninetyOwnedModel =
  SubsidiariesNinetyOwnedModel(Constants.StandardRadioButtonYesValue), organisationName = "Made up test subsidiary org name",
  companyAddress = Some(fullCorrespondenceAddress))

  val subsidiaryPerformingTradeWithFull = SubsidiaryPerformingTradeModel(ninetyOwnedModel =
    SubsidiariesNinetyOwnedModel(Constants.StandardRadioButtonYesValue), organisationName = "Made up test subsidiary org name",
    companyAddress = Some(fullCorrespondenceAddress), ctUtr = Some("1234567891"), crn = Some("555589852"))

  val previousSchemesFull = List(PreviousSchemeModel(schemeTypeDesc = Constants.schemeTypeEis, investmentAmount = 2000,
    day=Some(1),
    month = Some(2),
    year = Some(2003),
    processingId = None,
    investmentSpent = Some(19),
    otherSchemeName = Some("Other 1")),
    PreviousSchemeModel(schemeTypeDesc = Constants.schemeTypeEis, investmentAmount = 5000,
      day=Some(2),
      month = Some(3),
      year = Some(2004),
      processingId = None,
      investmentSpent = Some(20),
      otherSchemeName = Some("Other 2")),
    PreviousSchemeModel(schemeTypeDesc = Constants.schemeTypeEis, investmentAmount = 6000,
      day=Some(4),
      month = Some(5),
      year = Some(2006),
      processingId = None,
      investmentSpent = Some(21),
      otherSchemeName = Some("Other 3"))
  )

 val organisationFull = OrganisationDetailsModel(utr = Some("1234567891"),  organisationName = "my org name",
   chrn = Some("2222222222"), startDate = dateOfIncorporationModel, firstDateOfCommercialSale = Some("2009-04-01"),
   ctUtr = Some("5555555555"), crn = Some("crnvalue"), companyAddress=Some(fullCorrespondenceAddress),
   previousRFIs = Some(previousSchemesFull))

  val model = AdvancedAssuranceSubmissionType(
    agentReferenceNumber = Some(testAgentRef),
    acknowledgementReference = Some("AARN1234567"),
    whatWillUseForModel = Some(WhatWillUseForModel(Constants.businessActivityRAndD)),
    natureOfBusinessModel =  NatureOfBusinessModel("Some nature of business description"),
    contactDetailsModel = fullContactDetailsModel,
    correspondenceAddress = fullCorrespondenceAddress,
    schemeTypes = schemeTypes,
    marketInfo = Some(marketInfo),
    annualCosts = Some(costsFull),
    annualTurnover = Some(turnover),
    proposedInvestmentModel = ProposedInvestmentModel(250000),
    investmentGrowModel = InvestmentGrowModel("It will help me invest in new equipment and R&D"),
    knowledgeIntensive = Some(KiModel(skilledEmployeesConditionMet = true, innovationConditionMet = Some("reason met"), kiConditionMet = true)),
    subsidiaryPerformingTrade = Some(subsidiaryPerformingTradeWithFull),
    organisationDetails = organisationFull
    )

  val submission = Submission(model)

  "The submissions models" should {

    "call apply and unapply correctly using the correct Json implicit read/writes and mappings to convert between the two models" in {
      val json = Json.toJson(submission)

      // read source model into target model using mappings as apply
      val targetSubmissionModel = Json.parse(json.toString()).as[DesSubmitAdvancedAssuranceModel]

      // check some properties on the new structure that does not exist on the source to see if it mapped
      targetSubmissionModel.acknowledgementReference shouldBe Some("AARN1234567")
      targetSubmissionModel.submissionType.correspondenceDetails.contactName.name1 shouldBe "Fred"
      targetSubmissionModel.submissionType.correspondenceDetails.contactName.name2 shouldBe "Flinsstone"

      // write as unapply
      val targetJson = Json.toJson(targetSubmissionModel)
    }
  }

}
