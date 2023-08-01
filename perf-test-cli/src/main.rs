use std::{env, u32};
use std::ops::Div;
use std::time::Duration;

use dotenv::dotenv;
use names::{Generator, Name};
use reqwest::{Client, Error};
use reqwest::header::{CONTENT_TYPE, HeaderMap};
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
    let total_requests = env::var("TOTAL_REQUESTS").expect(".env variable TOTAL_REQUESTS not set.").parse().unwrap();

    info!("Using API URL {:?}", &url);

    let mut headers = HeaderMap::new();
    headers.insert(CONTENT_TYPE, "application/json".parse().unwrap());

    let client = Client::builder()
        .tcp_keepalive(Duration::from_nanos(0))
        .default_headers(headers)
        .build()?;

    let mut request_times: Vec<Duration> = Vec::new();

    for name in generate_names(total_requests) {
        match post_user(&client, &url, name).await {
            Ok(dur) => request_times.push(dur),
            Err(err) => error!("{:?}", err)
        }
    }

    result_summary(&mut request_times);

    Ok(())
}

async fn post_user(client: &Client, url: &String, name: String) -> Result<Duration, Error> {
    let json_data = format!(r#"{{"email": "{name}@example.com","password": "X1dxx_35FdfsgJ]","terms_accepted": true}}"#);

    let start = Instant::now();

    client
        .post(url)
        .body(json_data.to_owned())
        .send()
        .await?;

    Ok(start.elapsed())
}

fn generate_names(total_requests: i32) -> Vec<String> {
    info!("Generating list of names.");
    let mut generator = Generator::with_naming(Name::Numbered);
    let mut names: Vec<String> = Vec::new();
    for _ in 0..total_requests {
        names.push(generator.next().unwrap().to_owned());
    }
    info!("DONE generating list of names.");

    names
}


fn result_summary(request_times: &mut Vec<Duration>) {
    request_times.sort();

    let tnor = u32::try_from(request_times.len()).unwrap();
    let total: Duration = request_times.iter().sum();

    println!("Total execution time for {:?} requests: {:?}", tnor, total);

    println!("Min request time: {:?}", request_times.iter().min().unwrap());
    println!("Max request time: {:?}", request_times.iter().max().unwrap());
    println!("Avg request time: {:?}", total.div(tnor));
    println!("Median request time: {:?}", request_times[request_times.len() / 2]);
}
