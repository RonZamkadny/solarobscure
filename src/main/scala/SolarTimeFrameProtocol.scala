import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}


object SolarTimeFrameProtocol {

  case class SolarTimeFrame(id: String, latitude: String, longitude: String, declination: String, azimuth: String, dateTime: DateTime)

  case object SolarTimeFrameRequested

  object SolarTimeFrame extends DefaultJsonProtocol {
    implicit object DateJsonFormat extends RootJsonFormat[DateTime] {

      private val parserISO : DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

      override def write(obj: DateTime) = JsString(parserISO.print(obj))

      override def read(json: JsValue) : DateTime = json match {
        case JsString(s) => parserISO.parseDateTime(s)
        case _ => throw new DeserializationException("Error info you want here ...")
      }
    }

    implicit val format = jsonFormat6(SolarTimeFrame.apply)
  }

  object Question extends DefaultJsonProtocol {
    implicit val format = jsonFormat5(Question.apply)
  }

  object Answer extends DefaultJsonProtocol {
    implicit object DateJsonFormat extends RootJsonFormat[DateTime] {

      private val parserISO : DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

      override def write(obj: DateTime) = JsString(parserISO.print(obj))

      override def read(json: JsValue) : DateTime = json match {
        case JsString(s) => parserISO.parseDateTime(s)
        case _ => throw new DeserializationException("Error info you want here ...")
      }
    }
    implicit val format = jsonFormat1(Answer.apply)
  }

  case class Question(id: String, latitude: String, longitude: String, declination: String, azimuth: String)

  case class Answer(dateTime: DateTime)

  implicit def toQuestion(sft: SolarTimeFrame): Question = Question(id = sft.id, latitude = sft.latitude, longitude = sft.longitude, declination = sft.declination, azimuth = sft.azimuth)

  implicit def toAnswer(sft: SolarTimeFrame): Answer = Answer(dateTime = sft.dateTime)

}
