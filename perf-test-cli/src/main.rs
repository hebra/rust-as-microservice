use std::{env, u32};
use std::ops::Div;
use std::time::Duration;

use dotenv::dotenv;
use names::{Generator, Name};
use reqwest::{Client, Error};
use serde::{Deserialize, Serialize};
use tokio::time::Instant;
use tracing::{error, info};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct User {
    pub userid: String,
    pub email: String,
    pub password_hash: String,
    pub terms_accepted: bool,
}


#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    dotenv().ok();
    let url = env::var("USERS_API_URL").expect(".env variable USERS_API_URL not set.");


    info!("Generating list of names.");
    let mut generator = Generator::with_naming(Name::Numbered);
    let mut names: Vec<String> = Vec::new();
    for _ in 0..100 {
        names.push(generator.next().unwrap().to_owned());
    }
    info!("DONE generating list of names.");

    let client = Client::new();

    let mut request_times: Vec<Duration> = Vec::new();
    let start = Instant::now();
    for name in &names {
        match post_user(&client, url.as_str(), name.as_str()).await {
            Ok(req_time) => request_times.push(req_time),
            Err(err) => error!("Request error: {:?}", err)
        };
    }

    let total = start.elapsed();
    let tnor = u32::try_from(names.len()).unwrap();
    println!("Total execution time for {:?} requests: {:?}", tnor, total);

    println!("Min request time: {:?}", request_times.iter().min().unwrap());
    println!("Max request time: {:?}", request_times.iter().max().unwrap());

    println!("Avg request time: {:?}", total.div(tnor));

    request_times.sort();

    println!("Median request time: {:?}", request_times[request_times.len() / 2]);


    // let total = request_times.iter().find_map(|dur| Some(dur.as_millis())).sum();


    Ok(())
}

async fn post_user(client: &Client, url: &str, name: &str) -> Result<Duration, Error> {
    let json_data = format!(r#"{{"email": "{name}@example.com","password": "X1dxx_35FdfsgJ]","terms_accepted": true}}"#);

    let start = Instant::now();

    client
        .post(url)
        .header("Content-Type", "application/json")
        .body(json_data.to_owned())
        .send()
        .await?;

    Ok(start.elapsed())
}
