# confhub
[![Circle CI](https://circleci.com/gh/omartell/confhub.svg?style=svg&circle-token=e5148f66f7b6a89c42504a513f315769d047e04a)](https://circleci.com/gh/omartell/confhub)

Simple REST API for page configuration files

## Usage

The application is currently running on [Heroku](http://heroku.com) http://confhub.herokuapp.com. See [example](http://confhub.herokuapp.com/pages/breaking-news).

Creating a configuration file:

```sh
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X POST -d '{"page": {"id":"home", "foo": "bar"}}' http://confhub.herokuapp.com/pages
```

Retrieving an existing configuration file:
```sh
curl -i -H "Accept: application/json" -X GET http://confhub.herokuapp.com/pages/home
```

Updating an existing configuration file:
```sh
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X PUT -d '{"page": {"id":"home", "bar": "bar"}}' http://confhub.herokuapp.com/pages/home
```

Deleting an existing configuration file:
```sh
curl -i -H "Accept: application/json" -X DELETE http://confhub.herokuapp.com/pages/home
```

## Structure
- The project uses [duct](http://github.com/weavejester/duct) as a mini framework for the project to facilitate setup and deployment to heroku.
- The API for `/pages` is defined in [confhub.endpoint.pages](https://github.com/omartell/confhub/blob/master/src/confhub/endpoint/pages.clj) using the Clojure libraries ring and compojure. Currently the HTTP methods supported for page configuration files are `GET`, `POST`, `PUT` and `DELETE`.
- Integration tests for `/pages` are defined in [confhub.endpoint.pages-test](https://github.com/omartell/confhub/blob/master/test/confhub/endpoint/pages_test.clj). These tests are implemented using [ring-mock](https://github.com/ring-clojure/ring-mock), which allows to not require an HTTP server running when executing the tests.
- The application is bootstrapped using [component](http://github.com/stuartsierra/component) by instantiating a handler, endpoint and server component in [system.clj](https://github.com/omartell/confhub/blob/master/src/confhub/system.clj). The entry point is in [main.clj](https://github.com/omartell/confhub/blob/master/src/confhub/main.clj).

## Developing


### Setup

When you first clone this repository, run:

```sh
lein setup
```

This will create files for local configuration, and prep your system for the project.

The project uses PostgreSQL as the local database. Run the following command to create the development db:
```sh
createdb confhub_development
```
And the test database:
```sh
createdb confhub_test
```

Migrate the development database:

```db
lein migrate
```

And the test database:

```sh
lein with-profile test migrate
```

Run the application locally using leiningen:

```sh
lein run

```
This will start a local web server on port 3000.
### Environment

To begin developing, start with a REPL.

```sh
lein repl
```

Run `go` to initiate and start the system.

```clojure
user=> (go)
:started
```

By default this creates a web server at <http://localhost:3000>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```clojure
user=> (reset)
:reloading (...)
:resumed
```

### Configuration

The following environment variables are used:
- `DATABASE_URL`: The database url for the Postgres connection.
- `PORT`: The port used by the web server.
- `HOST`: The hostname used in the links included in responses.

### Testing

Testing is fastest through the REPL, as you avoid environment startup
time.

```clojure
user=> (test)
...
```

But you can also run tests through Leiningen.

```sh
lein test
```

## Legal

Copyright © 2016 Oliver Martell
