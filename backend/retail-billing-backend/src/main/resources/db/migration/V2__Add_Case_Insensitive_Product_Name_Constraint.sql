-- ============================================================================
-- Migration V2: Add case-insensitive unique constraint on product_name
-- Created: 2025-10-29
-- ============================================================================

-- Create a case-insensitive unique index on product_name
-- This prevents duplicates like 'T-Shirt', 't-shirt', 'T-SHIRT'
CREATE UNIQUE INDEX idx_products_name_lower ON products (LOWER(product_name));

-- Add a check to prevent leading/trailing spaces
ALTER TABLE products
ADD CONSTRAINT chk_product_name_trimmed
CHECK (product_name = TRIM(product_name));

-- ============================================================================
-- End of V2 Migration
-- ============================================================================
