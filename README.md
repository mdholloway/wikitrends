# wikitrends

![build status](https://github.com/mdholloway/wikitrends/actions/workflows/maven.yml/badge.svg)

## Motivation

As a large corpus of user-generated text content, Wikipedia is a source of data for large web services like Google and for training machine learning models. It is therefore important that Wikipedia remains a source of high-quality content. This project is envisioned as a tool for Wikipedia admins that consumes and processes public streams of Wikipedia change data to detect article revisions that are reverted shortly after being made. A large number of quickly reverted revisions on an article could indicate [edit-warring](https://en.wikipedia.org/wiki/Wikipedia:Edit_warring) or the need for [article protection](https://en.wikipedia.org/wiki/Wikipedia:Protection_policy).

## Quickstart
```
./mvnw install
docker-compose up --build
```
Upon launch, the Postgres and Kafka containers will start up, followed by the application containers. After a minute or two, you should begin to see messages indicating that reverted revisions are being stored, e.g.:
```
reverted-revisions-service-1  | 2023-12-12 19:35:26,534 INFO  [org.mdh.wik.RevertedRevisionsApp] (vert.x-eventloop-thread-1) Stored reverted revision: eswiki_Francisco_Pizarro_155980649
```
The ten most recently stored reverted revisions can be retrieved via API at http://localhost:8081/reverted-revisions.

Health check endpoints are exposed for each service:
* `reverted-revisions-service`: http://localhost:8081/q/health
* `stream-analysis-service`: http://localhost:8082/q/health
* `stream-consumer-service`: http://localhost:8083/q/health

## Run the tests
```
./mvnw test
```
Test source code can be found under `src/test/` and `src/integrationTest/` in the application directories.

## Architecture

The system consists of three applications, `stream-consumer-service`, `stream-analysis-service`, and `reverted-revisions-service`, which communicate with one another using Kafka. `stream-consumer-service` consumes public revision and tag-change data streams provided by the Wikimedia [EventStreams](https://wikitech.wikimedia.org/wiki/Event_Platform/EventStreams) service, filters those streams for events of interest, and passes those events along to `stream-analysis-service` via Kafka topics. `stream-analysis-service` is a Kafka Streams application that performs a windowed join on the streams to detect revisions that have a `mw-reverted` tag added, indicating that the article was reverted, within an hour of being created. Revisions reverted within the window are forwarded via Kafka to `reverted-revisions-service`, which stores them in Postgres for further analysis and exposes a simple API to the web.

All three applications run on the JVM and are built with the [Quarkus](https://quarkus.io/) framework.


## Production deployment

A deployment of the wikitrends services is currently running on Heroku.

The stored revisions API can be accessed at https://wikitrends-reverted-revisions-5bb7372b5825.herokuapp.com/reverted-revisions.

Health check endpoints:
* `reverted-revisions-service`: https://wikitrends-reverted-revisions-5bb7372b5825.herokuapp.com/q/health
* `stream-analysis-service`: https://wikitrends-stream-analysis-0cbfeb7dae93.herokuapp.com/q/health
* `stream-consumer-service`: https://wikitrends-stream-consumer-2de121ebdc3a.herokuapp.com/q/health

NOTE: The health check for `stream-analysis-service` is currently reporting `DOWN` because the Kafka Streams app is initialized in a nonstandard way in Heroku in order to provide the required SSL configuration for connecting to the Kafka broker.
