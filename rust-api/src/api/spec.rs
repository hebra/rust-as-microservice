use poem_openapi::{ApiResponse, OpenApi};
use poem_openapi::payload::Json;

use crate::api::api_error::ApiError;
use crate::api::users_api::{create_user, SignupUser, User};

#[derive(ApiResponse)]
enum CreateUserResponse {
    #[oai(status = 201)]
    Ok(Json<User>),
    #[oai(status = 400)]
    BadRequest(Json<ApiError>),
}

pub struct Api {
    pub disable_password_hashing: bool
}

#[OpenApi]
impl Api {
    #[oai(path = "/users", method = "post")]
    async fn create_user(&self, payload: Json<SignupUser>) -> CreateUserResponse {
        match create_user(payload.0, &self.disable_password_hashing).await {
            Ok(user) => CreateUserResponse::Ok(Json(user)),
            Err(err) => CreateUserResponse::BadRequest(Json(err))
        }
    }
}
