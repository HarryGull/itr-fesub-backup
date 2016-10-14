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

package utils

import models.{AnnualTurnoverCostsModel, OperatingCostsModel}
import models.submission.{AnnualCostModel, CostModel, TurnoverCostModel}

object Converters extends Converters{

}

trait Converters {

  def operatingCostsToList(operatingCostsModel: OperatingCostsModel, mostRecentYear: Int): List[AnnualCostModel] = {

    require(mostRecentYear >= 1000 && mostRecentYear <= 9999, "most recent year must be a 4 digit integer")

    val period1 = mostRecentYear.toString
    val period2 = (mostRecentYear - 1) toString
    val period3 = (mostRecentYear - 2).toString

    List(
      AnnualCostModel(period1, CostModel(amount = operatingCostsModel.operatingCosts1stYear),
        CostModel(amount = operatingCostsModel.rAndDCosts1stYear)),
      AnnualCostModel(period2, CostModel(amount = operatingCostsModel.operatingCosts2ndYear),
        CostModel(amount = operatingCostsModel.rAndDCosts2ndYear)),
      AnnualCostModel(period3, CostModel(amount = operatingCostsModel.operatingCosts3rdYear),
        CostModel(amount = operatingCostsModel.rAndDCosts3rdYear)))
  }

  def turnoverCostsToList(turnoverCostModel: AnnualTurnoverCostsModel, mostRecentYear: Int): List[TurnoverCostModel] = {

    require(mostRecentYear >= 1000 && mostRecentYear <= 9999, "most recent year must be a 4 digit integer")

    val period1 = mostRecentYear.toString
    val period2 = (mostRecentYear - 1) toString
    val period3 = (mostRecentYear - 2).toString
    val period4 = (mostRecentYear - 3).toString
    val period5 = (mostRecentYear - 4).toString

    List(
      TurnoverCostModel(period1, CostModel(amount = turnoverCostModel.amount1)),
      TurnoverCostModel(period2, CostModel(amount = turnoverCostModel.amount2)),
      TurnoverCostModel(period3, CostModel(amount = turnoverCostModel.amount3)),
      TurnoverCostModel(period4, CostModel(amount = turnoverCostModel.amount4)),
      TurnoverCostModel(period5, CostModel(amount = turnoverCostModel.amount5))
     )
  }
}
