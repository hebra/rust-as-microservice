# rust-as-microservice
POC to compare Rust as a microservice to other languages


# What does the micro-service do?

The micro-service provides the same REST endpoint for each of the different languages.
This endpoint is to create a simple user object in a local SQLite database with optional password hashing using Argon2.

## API endpoint

There is only one API endpoint for this POC: `POST /api/users`. This endpoint will accept an `application/json` body payload of the format:
```json
{
  "email": "eddy@example.com",
  "password": "Some random password",
  "terms_accepted": true
}
```

## OpenAPI documentation

If a ;anguage/library has a built-in support for generating OpenAPI documentation from the source code it will be enabled on a dedicated endpoint.

## Data schema

The data model for the user object is a very simple struct with some specific properties:
- the `userid` can not be set via a REST request and is created during initial creation of a user in the database
- the `userid` is a random Bae64-encoded UUID
- the password_hash will be ignored in any HTTP response (it will not be serialized)
- for test purposes the password hashing itself should be able to toggle via a e.g. `.env` dotenv property

```rust
pub struct User {
    pub userid: String, // Read-only
    pub email: String,
    pub password_hash: String, // Ignored in HTTP response
    pub terms_accepted: bool,
}
```

## Request handling

When a new `POST /api/users` request is incoming the following steps will be performed in that order, if any the steps fails return an error:

- are the 3 requred properties of the JSON payload present
- check if the email address has a valid format (`name@domain.tld`)
- check the password complexity using [zxcvbn](https://github.com/dropbox/zxcvbn) (if available for the language), the entropy score should be greater than 2
- use the email address to check if the user already exists in the database
- (Optional if enabled) hash the password using Argon2
- store the user in the database with the generated Base64 UUID and password hash (if hashing is disabled just store the plaintext password)
- return the newly created object as JSON (userid, email, terms_accepted)

## SQLite database

To keep it simple the POC just uses a local SQLite database. The schema is:

```sql
CREATE TABLE users
(
    userid         VARCHAR(100) PRIMARY KEY,
    email          VARCHAR(255) NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    terms_accepted BOOLEAN      NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX email_addr_idx ON users(email);
```

# What is the performance test do?

The performance test `perf-test-cli` will run 1 million sequential user create REST requests while the password hashing is disabled.
It will run another 10.000 create requests with the Argon2 password hashing enabled.
For each run the SQLite database of the tested API will be reset.
