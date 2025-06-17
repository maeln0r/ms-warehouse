INSERT INTO orders (external_id, is_deleted, name, not_process, status, price, warehouse_id, created_date, created_by, last_modified_date, last_modified_by)
VALUES
    ('O-1001', false, 'iPhone 15 Pro Max (512GB, Черный)', false, 'PENDING', 1250.50, 1,
     '2025-01-01 00:00:00', '{"fullName": "User One", "username": "user1"}'::jsonb,
     '2025-01-01 00:00:00', '{"fullName": "User One", "username": "user1"}'::jsonb),

    ('O-1002', false, 'Ноутбук MacBook Pro 14" (M3, 16GB RAM)', false, 'PENDING', 2500.00, 2,
     '2025-01-01 00:00:00', '{"fullName": "User Two", "username": "user2"}'::jsonb,
     '2025-01-01 00:00:00', '{"fullName": "User Two", "username": "user2"}'::jsonb),

    ('O-1003', false, 'Игровая приставка PlayStation 5', true, 'PENDING', 599.99, 3,
     '2025-01-01 00:00:00', '{"fullName": "User One", "username": "user1"}'::jsonb,
     '2025-01-01 00:00:00', '{"fullName": "User One", "username": "user1"}'::jsonb),

    ('O-1004', false, 'Умные часы Samsung Galaxy Watch 6', false, 'DELIVERED', 540.75, 1,
     '2025-01-01 00:00:00', '{"fullName": "User Three", "username": "user3"}'::jsonb,
     '2025-01-01 00:00:00', '{"fullName": "User Three", "username": "user3"}'::jsonb),

    ('O-1005', false, 'Беспроводные наушники AirPods Pro 2', false, 'CANCELED', 175.30, 2,
     '2025-01-01 00:00:00', '{"fullName": "User Two", "username": "user2"}'::jsonb,
     '2025-01-01 00:00:00', '{"fullName": "User Two", "username": "user2"}'::jsonb);
