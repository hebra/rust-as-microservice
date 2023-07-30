use tracing::error;

use crate::api::api_error::ApiError;
use crate::api::api_error::ApiErrorCode::{DatabaseError, FailedToInsertIntoDatabase};
use crate::api::users_api::User;
use crate::DB_POOL;

pub async fn get_user_by_email(email: &str) -> Result<Option<User>, ApiError> {
    let user = sqlx::query_as::<_, User>("SELECT * FROM users WHERE email = ?")
        .bind(email)
        .fetch_optional(DB_POOL.get().await).await;


    return match user {
        Err(err) => {
            error!("{:?}", err);
            Err(ApiError::new(500, DatabaseError, "A database error occurred."))
        }
        Ok(user) => Ok(user)
    };
}

pub async fn insert_user(user: User) -> Result<User, ApiError> {
    return match sqlx::query(
        "INSERT INTO users(userid, email, password_hash, terms_accepted)
        VALUES ($1, $2, $3, $4)")
        .bind(&user.userid)
        .bind(&user.email)
        .bind(&user.password_hash)
        .bind(&user.terms_accepted)
        .execute(DB_POOL.get().await).await {
        Ok(_) => Ok(user),
        Err(err) => {
            error!("Error inserting User into database: {:?}", err);
            Err(ApiError::new(500, FailedToInsertIntoDatabase, "Error inserting into database."))
        }
    };
}
