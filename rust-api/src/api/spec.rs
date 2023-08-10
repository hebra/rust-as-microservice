use base64::Engine;
use base64::prelude::BASE64_STANDARD_NO_PAD;
use email_address::EmailAddress;
use poem_openapi::{ApiResponse, OpenApi};
use poem_openapi::Object;
use poem_openapi::payload::Json;
use poem_openapi::types::{Email, Password};
use sqlx::{FromRow, SqlitePool, Type};
use tracing::{error, warn};
use uuid::Uuid;
use zxcvbn::ZxcvbnError;

use ApiErrorCode::BlankPassword;

use crate::api::api_error::{ApiError, ApiErrorCode};
use crate::api::api_error::ApiErrorCode::{DatabaseError, FailedToInsertIntoDatabase, InvalidEmailAddress, PasswordCheckDurationOutOfBounds, PasswordToWeak, UserWithEmailAddressAlreadyExists};

#[derive(ApiResponse)]
enum CreateUserResponse {
    #[oai(status = 201)]
    Ok(Json<User>),
    #[oai(status = 400)]
    BadRequest(Json<ApiError>),
}

pub struct Api {
    pub db_pool: SqlitePool
}

#[OpenApi]
impl Api {
    #[oai(path = "/users", method = "post")]
    async fn create_user(&self, payload: Json<SignupUser>) -> CreateUserResponse {
        match create_user(payload.0, &self.db_pool).await {
            Ok(user) => CreateUserResponse::Ok(Json(user)),
            Err(err) => CreateUserResponse::BadRequest(Json(err))
        }
    }
}


#[derive(Debug, Object)]
pub struct SignupUser {
    #[oai(validator(max_length = 200))]
    email: Email,
    #[oai(validator(max_length = 100))]
    password: Password,
    terms_accepted: bool,
}

#[derive(Debug, Clone, Object, FromRow, Type)]
pub struct User {
    #[oai(read_only)]
    pub userid: String,
    pub email: String,
    pub password_hash: String,
    pub terms_accepted: bool,
}

pub async fn create_user(signup_user: SignupUser, db_pool: &SqlitePool) -> Result<User, ApiError> {
    match check_email_address_validity(signup_user.email.as_str()) {
        Err(err) => return Err(err),
        Ok(_) => ()
    }

    match check_password_complexity(signup_user.password.as_str()) {
        Err(err) => return Err(err),
        Ok(_) => ()
    }

    return match get_user_by_email(signup_user.email.as_str(), db_pool).await {
        Err(err) => Err(err),
        Ok(user_opt) => match user_opt {
            Some(user) => {
                warn!("User already exists: {:?}", user.email);
                Err(ApiError::new(400, UserWithEmailAddressAlreadyExists, format!("User with email {:?} already exists.", signup_user.email).as_str()))
            }
            None => {
                let userid = BASE64_STANDARD_NO_PAD.encode(Uuid::new_v4().to_string());

                let user  = User {
                    userid,
                    email: signup_user.email.to_string(),
                    password_hash: signup_user.password.as_str().to_string(),
                    terms_accepted: signup_user.terms_accepted };

                match insert_user(user, db_pool).await {
                    Ok(user) => Ok(user),
                    Err(err) => Err(err)
                }
            }
        }
    }
}

fn check_email_address_validity(email: &str) -> Result<(), ApiError> {
    if EmailAddress::is_valid(email) && EmailAddress::new_unchecked(email).domain().contains(".") {
        return Ok(());
    }
    Err(ApiError::new(400, InvalidEmailAddress, format!("Value '{}' is not a valid email address", email).as_str()))
}

fn check_password_complexity(password: &str) -> Result<(), ApiError> {
    if password.trim().is_empty() {
        return Err(ApiError::new(400, BlankPassword, "Password is empty or contains only white-spaces."));
    }

    return match zxcvbn::zxcvbn(password, &[]) {
        Err(err) => match err {
            ZxcvbnError::BlankPassword => Err(ApiError::new(400, BlankPassword, "Password is empty or contains only white-spaces.")),
            ZxcvbnError::DurationOutOfRange => Err(ApiError::new(500, PasswordCheckDurationOutOfBounds, "DurationOutOfBounds error while validating password."))
        },

        Ok(entropy) => if entropy.score() > 2 { Ok(()) } else { Err(ApiError::new(400, PasswordToWeak, "The password entered has too weak a security.")) },
    };
}

pub async fn get_user_by_email(email: &str, db_pool: &SqlitePool) -> Result<Option<User>, ApiError> {
    let user = sqlx::query_as::<_, User>("SELECT * FROM users WHERE email = ?")
        .bind(email)
        .fetch_optional(db_pool).await;


    return match user {
        Err(err) => {
            error!("{:?}", err);
            Err(ApiError::new(500, DatabaseError, "A database error occurred."))
        }
        Ok(user) => Ok(user)
    };
}

pub async fn insert_user(user: User, db_pool: &SqlitePool) -> Result<User, ApiError> {
    return match sqlx::query(
        "INSERT INTO users(userid, email, password_hash, terms_accepted)
        VALUES ($1, $2, $3, $4)")
        .bind(&user.userid)
        .bind(&user.email)
        .bind(&user.password_hash)
        .bind(&user.terms_accepted)
        .execute(db_pool).await {
        Ok(_) => Ok(user),
        Err(err) => {
            error!("Error inserting User into database: {:?}", err);
            Err(ApiError::new(500, FailedToInsertIntoDatabase, "Error inserting into database."))
        }
    };
}
