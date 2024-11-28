use std::io;
use std::sync::Arc;
use actix_web::{web, App, HttpServer};
use actix_web::middleware::Logger;
use env_logger::Env;
use crate::adapters::persistence::diesel_webhook_repository::DieselWebhookRepository;
use crate::domain::models::webhook_repository::WebhookRepository;
use crate::endpoints::apis::webhook_controller;

mod adapters;
mod domain;
mod endpoints;

#[actix_web::main]
async fn main() -> io::Result<()> {



    env_logger::init_from_env(Env::default().default_filter_or("debug"));

    HttpServer::new(|| {
        let webhook_repository: Arc<dyn WebhookRepository> = Arc::new(DieselWebhookRepository::new());

        App::new()
            .wrap(Logger::default())
            .app_data(web::Data::from(webhook_repository))
            // enable logger
            // .wrap(middleware::Logger::default())
            .service(webhook_controller::get_webhook)
    })
        .bind(("127.0.0.1", 8080))?
        .run()
        .await
}
