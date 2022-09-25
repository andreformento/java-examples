# java


- create project
  ```shell
  mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes \
                        -DarchetypeArtifactId=maven-archetype-quickstart \
                        -DarchetypeVersion=1.4
  ```

- wrapper
  ```shell
  mvn wrapper:wrapper -Dmaven=3.8.6
  ```
