use std::{env};
use async_once::AsyncOnce;

use dotenv::dotenv;
use lazy_static::lazy_static;
use poem::listener::TcpListener;
use poem_openapi::__private::poem;
use poem_openapi::__private::poem::Route;
use poem_openapi::OpenApiService;
use sqlx::{Sqlite, SqlitePool};
use sqlx::migrate::MigrateDatabase;
use sqlx::sqlite::SqlitePoolOptions;
use tracing::{info};

use crate::api::spec::Api;

mod api;
mod db;

#[tokio::main]
async fn main() -> Result<(), std::io::Error> {
    tracing_subscriber::fmt::init();

    dotenv().ok();

    let api_service =
        OpenApiService::new(Api, "Gymergy REST API", "1.0").server("http://localhost:3000/api");

    let ui = api_service.rapidoc();

    let app = Route::new()
        .nest("/api", api_service)
        .nest("/", ui);

    poem::Server::new(TcpListener::bind("127.0.0.1:3000"))
        .run(app)
        .await
}


lazy_static! {
      static ref DB_POOL: AsyncOnce<SqlitePool> = AsyncOnce::new(async {
        init_database().await
    });
}


async fn init_database() -> SqlitePool {
    let db_url = env::var("DATABASE_URL").expect("DATABASE_URL not set");

    if !Sqlite::database_exists(&db_url).await.expect("") {
        info!("New database file created {:?}", db_url);
        Sqlite::create_database(&db_url).await.expect("Unable to create database file");
    }

    SqlitePoolOptions::new()
        .max_connections(5)
        .connect(db_url.as_str())
        .await
        .expect("Unable to connect to database file.")
}
