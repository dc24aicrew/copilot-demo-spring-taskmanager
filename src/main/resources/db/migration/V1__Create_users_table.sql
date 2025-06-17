-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT true,
    last_login_at TIMESTAMP,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Create indexes for better query performance
CREATE INDEX idx_user_email ON users (email);
CREATE INDEX idx_user_username ON users (username);
CREATE INDEX idx_user_role ON users (role);
CREATE INDEX idx_user_active ON users (is_active);
CREATE INDEX idx_user_created_at ON users (created_at);

-- Add check constraint for role values
ALTER TABLE users ADD CONSTRAINT chk_user_role 
    CHECK (role IN ('USER', 'MANAGER', 'ADMIN'));

-- Add check constraint for email format
ALTER TABLE users ADD CONSTRAINT chk_user_email 
    CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to automatically update updated_at on row updates
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Insert default admin user (password: Admin@123)
INSERT INTO users (
    id, username, email, password_hash, first_name, last_name, role, is_active
) VALUES (
    gen_random_uuid(),
    'admin',
    'admin@taskmanager.demo',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqcsdQbeiFoqZBqfaG5a1K2',
    'System',
    'Administrator',
    'ADMIN',
    true
);

-- Insert demo manager user (password: Manager@123)
INSERT INTO users (
    id, username, email, password_hash, first_name, last_name, role, is_active
) VALUES (
    gen_random_uuid(),
    'manager',
    'manager@taskmanager.demo',
    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    'Demo',
    'Manager',
    'MANAGER',
    true
);

-- Insert demo regular user (password: User@123)
INSERT INTO users (
    id, username, email, password_hash, first_name, last_name, role, is_active
) VALUES (
    gen_random_uuid(),
    'user',
    'user@taskmanager.demo',
    '$2a$12$6UzEjctTiDhZ.Wj8Gj0h6eOb8zQJ2W8q8GjJyWz8rHqO8pHoQkX1y',
    'Demo',
    'User',
    'USER',
    true
);