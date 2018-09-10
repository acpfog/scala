
This application searches for Node.sj projects in GitHub and for the first 10 found projects
it prints a summary in JSON format with recent tweets where the projects were mentioned.

The application is written in Scala. Below you can find a list of steps for
preparing, configuring, testing and how to run the application.

1. Prepare an environment

If you don't yet have scala and sbt, please, install them fist.
On MacOS you can do it using brew:
   $ brew update
   $ brew install scala
   $ brew install sbt

2. Configure the application

Visit https://apps.twitter.com and create credentials for the apllication.
Put the credentials into the application configuration file "src/main/resources/settings.conf".

3. Test the application

   $ sbt test

4. How to run the application

   $ sbt run

