import org.scalatest.FunSuite
import java.io.File
import scala.io.Source
import com.typesafe.config._
import twitter4j._
import twitter4j.auth.AccessToken
import collection.JavaConverters._


class TwitterGithubAppTests extends FunSuite {

    val config = ConfigFactory.parseFile(new File("src/main/resources/settings.conf"))

    test("load configuration settings") {
        assert(!config.isEmpty)
    }

    test("check Twitter Consumer Key") {
        assert(!config.getString("twitterConsumerKey").isEmpty);
        assert(config.getString("twitterConsumerKey").isInstanceOf[String]);
    }

    test("check Twitter Consumer Secret") {
        assert(!config.getString("twitterConsumerSecret").isEmpty);
        assert(config.getString("twitterConsumerSecret").isInstanceOf[String]);

    }

    test("check Twitter Access Token") {
        assert(!config.getString("twitterAccessToken").isEmpty);
        assert(config.getString("twitterAccessToken").isInstanceOf[String]);
    }

    test("check Twitter Access Token Secret") {
        assert(!config.getString("twitterAccessTokenSecret").isEmpty);
        assert(config.getString("twitterAccessTokenSecret").isInstanceOf[String]);
    }

    test("fetch data from GitHub") {
        def fetchData(url: String): String = {
            try {
                Source.fromURL(url).mkString
            } catch {
                case e: Exception => "None"
            }
        }
        val githubData = fetchData("https://api.github.com/search/repositories?q=nodejs")
        assert(githubData != "None");
    }

    test("make a query to Twitter") {
        def searchTwitter(query: Query): List[twitter4j.Status] = {
            val tf = new TwitterFactory().getInstance()
            tf.setOAuthConsumer(config.getString("twitterConsumerKey"), config.getString("twitterConsumerSecret"))
            tf.setOAuthAccessToken(new AccessToken(config.getString("twitterAccessToken"), config.getString("twitterAccessTokenSecret")))
            try {
                tf.search(query).getTweets.asScala.toList
            } catch {
                case e: Exception => List[twitter4j.Status]()
            }
        }
        val twitterStatuses = searchTwitter(new Query("hello"))
        assert(twitterStatuses.size != 0);
    }

}
