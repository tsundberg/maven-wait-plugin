# Release instructions

* Make sure you are using Java 8
* Build with `./mvnw clean install`
* Set the new version number `./mvnw versions:set -DnewVersion=1.2.0`
* Commit with the new version number 
* Create a tag  `git tag 1.2.0`
* Deploy `./mvnw clean deploy -P release`
    * You will be prompted for a password, look in `.m2/settings.xml` for a hint
* Set the next development version number `./mvnw versions:set -DnewVersion=1.2.1-SNAPSHOT`
* Commit with the new version number 
* Push the updates, including the tag `git push --tags`
* Review and update these instructions


