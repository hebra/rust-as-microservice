use std::env;

use dotenv::dotenv;
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


#[tokio::main]
async fn main() -> Result<(), std::io::Error> {
    tracing_subscriber::fmt::init();

    dotenv().ok();

    let api = Api {
        db_pool: init_database().await
    };

    let api_service =
        OpenApiService::new(api, "Gymergy REST API", "1.0").server("http://localhost:3000/api");

    let ui = api_service.rapidoc();

    let app = Route::new()
        .nest("/api", api_service)
        .nest("/", ui);

    poem::Server::new(TcpListener::bind("127.0.0.1:3000"))
        .run(app)
        .await
}

async fn init_database() -> SqlitePool {
    let db_url = env::var("DATABASE_URL").expect("DATABASE_URL not set");

    Sqlite::database_exists(&db_url).await.expect("Database not found. Use sqlx CLI to create and migrate database.");

    let pool = SqlitePoolOptions::new()
        .max_connections(5)
        .connect(db_url.as_str())
        .await
        .expect("Unable to connect to database file.");

    let count: i64 = sqlx::query_scalar("SELECT COUNT(1) FROM users")
        .fetch_one(&pool)
        .await.unwrap();

    info!("Current rows in users table: {:?}", count);

    pool
}
