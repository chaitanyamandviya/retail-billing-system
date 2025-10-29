-- ============================================================================
-- Vandana Retail Billing System - Initial Database Schema
-- Migration Version: V1 (CORRECTED - Using BIGSERIAL for all IDs)
-- Created: 2025-10-29
-- ============================================================================

-- Table: users
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) NOT NULL CHECK (role IN ('OWNER', 'CASHIER', 'MANAGER')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED')),
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: shop_settings
CREATE TABLE shop_settings (
    setting_id BIGSERIAL PRIMARY KEY,
    shop_name VARCHAR(255) NOT NULL,
    shop_logo_path VARCHAR(500),
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(255),
    tax_rate DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: products
CREATE TABLE products (
    product_id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    image_path VARCHAR(500),
    stock_quantity INTEGER DEFAULT 0 CHECK (stock_quantity >= 0),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: bills
CREATE TABLE bills (
    bill_id BIGSERIAL PRIMARY KEY,
    bill_number VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    customer_name VARCHAR(255),
    customer_phone VARCHAR(20),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    discount_percent DECIMAL(5,2) DEFAULT 10.00 CHECK (discount_percent >= 0 AND discount_percent <= 100),
    discount_amount DECIMAL(10,2) DEFAULT 0.00 CHECK (discount_amount >= 0),
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    payment_method VARCHAR(50) NOT NULL CHECK (payment_method IN ('CASH', 'ONLINE', 'CARD', 'UPI')),
    bill_status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED' CHECK (bill_status IN ('DRAFT', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    synced_at TIMESTAMP
);

-- Table: bill_items
CREATE TABLE bill_items (
    item_id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL REFERENCES bills(bill_id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES products(product_id) ON DELETE SET NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: payment_transactions
CREATE TABLE payment_transactions (
    transaction_id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL REFERENCES bills(bill_id) ON DELETE CASCADE,
    payment_method VARCHAR(50) NOT NULL CHECK (payment_method IN ('CASH', 'ONLINE', 'CARD', 'UPI')),
    amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS' CHECK (payment_status IN ('SUCCESS', 'PENDING', 'FAILED')),
    reference_number VARCHAR(100),
    notes TEXT
);

-- Table: sync_logs
CREATE TABLE sync_logs (
    sync_id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL CHECK (entity_type IN ('BILL', 'PRODUCT', 'USER', 'SHOP_SETTINGS')),
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL CHECK (action IN ('CREATE', 'UPDATE', 'DELETE')),
    device_id VARCHAR(100) NOT NULL,
    synced_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sync_status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS' CHECK (sync_status IN ('SUCCESS', 'PENDING', 'FAILED', 'CONFLICT')),
    conflict_data JSONB,
    resolved_at TIMESTAMP
);

-- ============================================================================
-- Indexes for Performance
-- ============================================================================

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

CREATE INDEX idx_products_name ON products(product_name);
CREATE INDEX idx_products_active ON products(is_active);

CREATE INDEX idx_bills_number ON bills(bill_number);
CREATE INDEX idx_bills_user_id ON bills(user_id);
CREATE INDEX idx_bills_created_at ON bills(created_at DESC);
CREATE INDEX idx_bills_status ON bills(bill_status);
CREATE INDEX idx_bills_synced ON bills(synced_at);

CREATE INDEX idx_bill_items_bill_id ON bill_items(bill_id);
CREATE INDEX idx_bill_items_product_id ON bill_items(product_id);

CREATE INDEX idx_payment_trans_bill_id ON payment_transactions(bill_id);
CREATE INDEX idx_payment_trans_date ON payment_transactions(transaction_date DESC);
CREATE INDEX idx_payment_trans_status ON payment_transactions(payment_status);

CREATE INDEX idx_sync_logs_entity ON sync_logs(entity_type, entity_id);
CREATE INDEX idx_sync_logs_device ON sync_logs(device_id);
CREATE INDEX idx_sync_logs_date ON sync_logs(synced_at DESC);
CREATE INDEX idx_sync_logs_status ON sync_logs(sync_status);

-- ============================================================================
-- Insert Default Data
-- ============================================================================

-- Insert default shop settings
INSERT INTO shop_settings (shop_name, address, phone, email, tax_rate)
VALUES ('Vandana Retail Store', 'Your Shop Address', '1234567890', 'contact@vandana.com', 0.00);

-- Insert default owner user (password: admin123 - BCrypt hashed)
INSERT INTO users (username, password_hash, email, full_name, role, status)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'admin@vandana.com', 'Shop Owner', 'OWNER', 'ACTIVE');

-- ============================================================================
-- End of V1 Migration
-- ============================================================================
