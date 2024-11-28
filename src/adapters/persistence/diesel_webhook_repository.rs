use crate::domain::models::webhook_repository::WebhookRepository;

pub struct DieselWebhookRepository {
    counter: Vec<u8>
}
impl DieselWebhookRepository {
    pub fn new() -> DieselWebhookRepository {
        DieselWebhookRepository {
            counter: Vec::new()
        }
    }
}

impl WebhookRepository for DieselWebhookRepository {
    fn create(&self) {
        println!("Hello, world!");
    }
}
