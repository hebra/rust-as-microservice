use core::default::Default;

use argon2::{Argon2, PasswordHasher};
use argon2::password_hash::rand_core::OsRng;
use argon2::password_hash::SaltString;
use base64::Engine;
use base64::prelude::BASE64_STANDARD_NO_PAD;
use email_address::EmailAddress;
use poem_openapi::Object;
use poem_openapi::types::{Email, Password};
use sqlx::{FromRow, Type};
use tracing::{error, warn};
use uuid::Uuid;
use zxcvbn::ZxcvbnError;

use ApiErrorCode::BlankPassword;

use crate::api::api_error::{ApiError, ApiErrorCode};
use crate::api::api_error::ApiErrorCode::{InvalidEmailAddress, PasswordCheckDurationOutOfBounds, PasswordHashingFailed, PasswordToWeak, UserWithEmailAddressAlreadyExists};
use crate::db::users_db::{get_user_by_email, insert_user};

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

pub async fn create_user(signup_user: SignupUser) -> Result<User, ApiError> {
    match check_email_address_validity(signup_user.email.as_str()) {
        Err(err) => return Err(err),
        Ok(_) => ()
    }

    match check_password_complexity(signup_user.password.as_str()) {
        Err(err) => return Err(err),
        Ok(_) => ()
    }

    match get_user_by_email(signup_user.email.as_str()).await {
        Err(err) => return Err(err),
        Ok(user_opt) => match user_opt {
            Some(user) => {
                warn!("User already exists: {:?}", user.email);
                return Err(ApiError::new(400, UserWithEmailAddressAlreadyExists, format!("User with email {:?} already exists.", signup_user.email).as_str()));
            }
            None => {
                let userid = BASE64_STANDARD_NO_PAD.encode(Uuid::new_v4().to_string());

                let user = match generate_password_hash(signup_user.password.as_str()) {
                    Err(err) => return Err(err),
                    Ok(password_hash) => User { userid, email: signup_user.email.to_string(), password_hash, terms_accepted: signup_user.terms_accepted },
                };

                return match insert_user(user).await {
                    Ok(user) => Ok(user),
                    Err(err) => Err(err)
                };
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

fn generate_password_hash(password: &str) -> Result<String, ApiError> {
    // let salt = SaltString::generate(&mut OsRng);
    //
    // let hash = match Argon2::default().hash_password(password.as_bytes(), &salt) {
    //     Err(err) => {
    //         error!("Error hashing password: {:?}", err);
    //         return Err(ApiError::new(500, PasswordHashingFailed, "Hashing the password failed."));
    //     }
    //     Ok(hash) => hash,
    // };

    let hash = "NOT_HASHED";
    Ok(hash.to_string())
}


#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_check_password_complexity() {
        let result = check_password_complexity("");
        assert_eq!(result.is_err(), true);
        assert_eq!(result.unwrap_err().code, BlankPassword);

        let result = check_password_complexity("            ");
        assert_eq!(result.is_err(), true);
        assert_eq!(result.unwrap_err().code, BlankPassword);

        let result = check_password_complexity("password");
        assert_eq!(result.is_err(), true);
        assert_eq!(result.unwrap_err().code, PasswordToWeak);

        let result = check_password_complexity("password1234");
        assert_eq!(result.is_err(), true);
        assert_eq!(result.unwrap_err().code, PasswordToWeak);

        let result = check_password_complexity("Hey42@Bla");
        assert_eq!(result.is_ok(), true);
    }

    #[test]
    fn test_check_email_address_validity() {
        let result = check_email_address_validity("");
        assert_eq!(result.is_err(), true);
        assert_eq!(result.unwrap_err().code, InvalidEmailAddress);

        let result = check_email_address_validity("eddyexample.com");
        assert_eq!(result.is_err(), true);
        assert_eq!(result.unwrap_err().code, InvalidEmailAddress);

        let result = check_email_address_validity("eddy@example");
        assert_eq!(result.is_err(), true);
        assert_eq!(result.unwrap_err().code, InvalidEmailAddress);

        let result = check_email_address_validity("eddy@example.com");
        assert_eq!(result.is_ok(), true);
    }
}

