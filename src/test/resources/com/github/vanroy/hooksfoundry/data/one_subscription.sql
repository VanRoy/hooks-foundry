INSERT INTO subscription(id, tenant_id, url, status, event_types, signatures) VALUES
(gen_random_uuid(), 'tenant-01', 'http://localhost:8888/test', 'enabled','{"order.created", "order.shipped"}', '[{ "secret": "2a806ede-aa41-4e19-93c6-3af8f3325c81", "expiration": null }, { "secret": "ef59c5e7-8a14-4b63-99c1-faa491952976", "expiration": null }]')
;
