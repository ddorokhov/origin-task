# URL Shortener Service

A simple Spring Boot application that allows you to shorten long URLs, retrieve them, and delete mappings. Built with Java 17, Spring Boot 3, JPA, and H2 in-memory database.

## Features

- Shorten long URLs to a unique short code
- Retrieve original URL from a shortened one
- Delete a shortened URL mapping
- Validation of input URLs
- In-memory H2 database
- Unit and integration tests
- Simple redirect filter for resolving short links

## Build and Run

### Prerequisites
- Java 17+
- Maven 3+

### Build 

```
mvn clean package
```

### Run 
```
mvn spring-boot:run
```
or
```
java -jar target/origin-0.0.1-SNAPSHOT.jar
```
***Note:*** By default, the application runs on HTTP port 80.

### Testing
```
mvn test
```

## API Usage
All requests use HTTP query parameters.
The default API server port is 80.

> **Note:** HTTPS (https://) is intentionally not supported to keep the app simple and avoid the need for certificate configuration.

### Samples API requests
Below are examples of all supported API operations using `curl`.

#### Shorten a long URL

```
curl -X POST --include "http://localhost/shorten?originalUrl=http://example.com"
```

Response:
```
HTTP/1.1 201 
Location: http://short.ly/b3C416
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 24 Apr 2025 00:48:04 GMT

{"shortenedUrl":"http://short.ly/b3C416","originalUrl":"http://example.com"}
```

#### Retrieve shortened URL by original URL
```
curl -X GET --include "http://localhost/shorten?originalUrl=http://example.com"
```
Response:
```
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 24 Apr 2025 00:48:30 GMT

{"shortenedUrl":"http://short.ly/b3C416","originalUrl":"http://example.com"}
```

#### Resolve shortened URL to original

```
curl -X GET --include "http://localhost/original?shortenedUrl=http://short.ly/a1B2c3"
```
Response:
```
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 24 Apr 2025 00:49:14 GMT

{"shortenedUrl":"http://short.ly/b3C416","originalUrl":"http://example.com"}
```

#### Delete an existing URL mapping

```
curl -X DELETE --include "http://localhost/shorten?originalUrl=http://example.com"
```
Response:
```
HTTP/1.1 200 
Content-Length: 0
Date: Thu, 24 Apr 2025 00:49:45 GMT
```


### Local Redirect Testing via Domain
To simulate production behavior and test end-to-end in your browser:

Edit /etc/hosts on macOS or Linux, or C:\Windows\System32\drivers\etc\hosts on Windows.

Add/update the following line:
```
127.0.0.1 localhost www.originenergy.com.au short.ly
```

This maps fake domains to your local Spring Boot app on port 80.

It will allow to test the provided solution end-to-end

After updating the host file, open this page in your browser.

http://www.originenergy.com.au/electricity-gas/plans.html

Use curl or Postman to generate a shortened version:
```
curl -X POST http://localhost/shorten?originalUrl=http://www.originenergy.com.au/electricity-gas/plans.html
```
Use the returned shortenedUrl and it should redirect you to the original page