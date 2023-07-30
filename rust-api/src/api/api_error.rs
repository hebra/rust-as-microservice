use poem_openapi::{Enum, Object};
use serde::Serialize;

use ApiErrorCode::UnknownError;

#[derive(Debug, Clone, Eq, PartialEq, Serialize, Enum)]
#[serde(rename_all = "lowercase")]
pub enum ApiErrorCode {
    UnknownError,
    BlankPassword,
    FailedToInsertIntoDatabase,
    PasswordCheckDurationOutOfBounds,
    PasswordHashingFailed,
    PasswordToWeak,
    InvalidEmailAddress,
    DatabaseError,
    UserWithEmailAddressAlreadyExists,
}

#[derive(Debug, Clone, Eq, PartialEq, Serialize, Object)]
pub struct ApiError {
    pub status_code: i16,
    pub code: ApiErrorCode,
    pub message: String,
}

impl ApiError {
    pub fn new(status_code: i16, code: ApiErrorCode, message: &str) -> Self {
        Self { status_code, code, message: message.to_string() }
    }
}

impl Into<Result<(), Self>> for ApiError {
    fn into(self) -> Result<(), Self> {
        Err(self)
    }
}

impl Default for ApiError {
    fn default() -> Self {
        ApiError::new(500, UnknownError, "An unknown error has occurred.")
    }
}
