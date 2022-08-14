package utils

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class NotSoFancySchoolDatabase {
  private var noOfGrades = 0
  private var sumOfGrades = 0

  private val errors = Seq("Bad Connection", "Tmp Overloaded", "Bad Luck")

  def storeGrade(grade: Int)(implicit ec: ExecutionContext): Future[Unit] = Future {
    val previousNoOfGrades = noOfGrades
    val previousSumOfGrades = sumOfGrades
    Thread.sleep(Random.nextInt(5) * 50)
    val true1percent = (Random.nextInt(100) == 0)
    if (true1percent) {
      val errorIdx = Random.nextInt(errors.size)
      throw new RuntimeException(errors(errorIdx))
    }

    noOfGrades = previousNoOfGrades + 1
    sumOfGrades = previousSumOfGrades + grade
  }

  def retrieveNoOfGrades: Int = noOfGrades

  def retrieveSumOfGrades: Int = sumOfGrades
}
