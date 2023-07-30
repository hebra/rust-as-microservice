use poem::web::Path;
use poem_openapi::{ApiResponse, OpenApi};
use poem_openapi::param::Query;
use poem_openapi::payload::{Json, PlainText};
use crate::api::api_error::ApiError;

use crate::api::users_api::{create_user, SignupUser, User};

#[derive(ApiResponse)]
enum CreateUserResponse {
    #[oai(status = 201)]
    Ok(Json<User>),
    #[oai(status = 400)]
    BadRequest(Json<ApiError>),
}

pub struct Api;

#[OpenApi]
impl Api {
    #[oai(path = "/hello", method = "get")]
    async fn index(&self, name: Query<Option<String>>) -> PlainText<String> {
        match name.0 {
            Some(name) => PlainText(format!("hello, {}!", name)),
            None => PlainText("hello!".to_string()),
        }
    }

    #[oai(path = "/users", method = "post")]
    async fn create_user(&self, payload: Json<SignupUser>) -> CreateUserResponse {
        match create_user(payload.0).await {
            Ok(user) => CreateUserResponse::Ok(Json(user)),
            Err(err) => CreateUserResponse::BadRequest(Json(err))
        }
    }

    #[oai(path = "/users/:user_id", method = "get")]
    async fn get_user(&self, user_id: Path<String>) -> PlainText<String> {
        PlainText(format!("User {}", user_id.as_str()))
    }
}
