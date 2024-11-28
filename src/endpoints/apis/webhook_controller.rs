use actix_web::{get, web, Error, HttpResponse};
use crate::domain::models::webhook_repository::WebhookRepository;


#[get("/")]
pub async fn get_webhook(repository: web::Data<dyn WebhookRepository>) -> Result<HttpResponse, Error> {
    repository.create();
    Ok(HttpResponse::Ok().body("test"))
}
