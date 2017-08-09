# Maven wait plugin

A maven plugin that waits for an http resource to be available.

It will fail the build if the wanted resource isn't available within a time limit.

## Usage

Specify the url and call the plugin during your build. 

There is one goal. Maven requires you to call it explicit.

## Configuration

### Url

The url where an http resource should be available.

    <url>http://localhost:4040//geo/rest/v1/gata</url>

### Timeout

The time in milliseconds to wait for a resource.

    <timeout>1000</timeout>

### Headers

The headers you want to set. Multiple headers are supported. Specify each header in a `headers` section.

This example specifies one header.

    <headers>
        <applicationId>Acceptance test</applicationId>
    </headers>

### Example

Here is a complete example where we are waiting for a resource to appear at `http://localhost:4040//geo/rest/v1/gata`, 
allow 1000 milliseconds before a timeout and set the header `applicationId` to `Acceptance test`

    <plugin>
        <groupId>se.thinkcode.wait</groupId>
        <artifactId>http</artifactId>
        <version>${project.version}</version>
        <executions>
            <execution>
                <goals>
                    <goal>wait</goal>
                </goals>
                <configuration>
                    <url>http://localhost:4040/geo/rest/v1/gata</url>
                    <timeout>1000</timeout>
                    <headers>
                        <applicationId>Acceptance test</applicationId>
                    </headers>
                </configuration>
            </execution>
        </executions>
    </plugin>
