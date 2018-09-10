package TwitterGithubApp

import com.typesafe.config._
import spray.json._
import spray.json.DefaultJsonProtocol._
import twitter4j._
import twitter4j.auth.AccessToken
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.io.Source
import collection.JavaConverters._

object TwitterGithubApp {

    def main(args: Array[String]) {

        // a request url for getting data about Node.js projects from GitHub
        val githubUrl = "https://api.github.com/search/repositories?q=nodejs"

        // load the configuration settings
        val config = ConfigFactory.load("settings")

        // set the secrets for accessing Twitter
        val twitterConsumerKey = config.getString("twitterConsumerKey")
        val twitterConsumerSecret = config.getString("twitterConsumerSecret")
        val twitterAccessToken = config.getString("twitterAccessToken")
        val twitterAccessTokenSecret = config.getString("twitterAccessTokenSecret")

        // fetching data using a site url
        def fetchData(url: String): String = {
            try {
                Source.fromURL(url).mkString
            } catch {
                case e: Exception => "None"
            }
        }

        // extract projects' names from a GitHub response in JSON format
        def extractNames(jsonData: String) = {
            jsonData.parseJson.asJsObject.getFields("items")
            .collect { case JsArray(item) => item }
            .flatten.flatMap(_.asJsObject.getFields("name"))
            .collect { case JsString(name) => name }
        }

        // create a class for an element wich will be added to a summary
        case class Output(projectName: String, twitterUser: String, twitterStatus: String)

        // methods for converting a summary to JSON format
        implicit val outputJsonFormat = new RootJsonFormat[Output] {
          def write(output: Output): JsValue = {
            JsObject(
              "project_name" -> output.projectName.toJson,
              "twitter_user" -> output.twitterUser.toJson,
              "twitter_status" -> output.twitterStatus.toJson
            )
          }
          def read(value: JsValue): Output = ???
        }

        // connect to Twitter and search in Twitter
        def searchTwitter(query: Query): List[twitter4j.Status] = {
            val tf = new TwitterFactory().getInstance()
            tf.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecret)
            tf.setOAuthAccessToken(new AccessToken(twitterAccessToken, twitterAccessTokenSecret))
            try {
                tf.search(query).getTweets.asScala.toList
            } catch {
                case e: Exception => List[twitter4j.Status]()
            }
        }

        // Make a request to GitHub and check a response from it
        val githubResponse = fetchData(githubUrl)
        if (githubResponse != "None") {

            // get names of 10 projects from GitHub
            val projectsNames = extractNames(githubResponse).toSeq.take(10)
    
            // create an array for the summary
            val summary = new ListBuffer[Output]()
            projectsNames.foreach { project =>
                val twitterStatuses = searchTwitter(new Query(project))
                twitterStatuses.foreach { status =>
                    // fill the summary array with projects' names and statuses from Twitter
                    summary += Output(projectName = project, twitterUser = status.getUser().getName(), twitterStatus = status.getText())
                }
            }
    
            // print the summary in a human redable JSON format
            println(summary.toList.toJson.prettyPrint)

        } else {

            // print an error message if we don't get JSON data from GitHub
            println("Error! Data from GitHub not fetched!")

        }

    }

}
