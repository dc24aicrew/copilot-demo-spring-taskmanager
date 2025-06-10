-- Create tasks table
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
    category VARCHAR(20),
    assigned_to_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    due_date TIMESTAMP,
    completed_at TIMESTAMP,
    estimated_hours INTEGER CHECK (estimated_hours >= 0),
    actual_hours INTEGER CHECK (actual_hours >= 0),
    is_archived BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Create indexes for better query performance
CREATE INDEX idx_task_assigned_to ON tasks (assigned_to_id);
CREATE INDEX idx_task_created_by ON tasks (created_by_id);
CREATE INDEX idx_task_status ON tasks (status);
CREATE INDEX idx_task_priority ON tasks (priority);
CREATE INDEX idx_task_category ON tasks (category);
CREATE INDEX idx_task_due_date ON tasks (due_date);
CREATE INDEX idx_task_archived ON tasks (is_archived);
CREATE INDEX idx_task_created_at ON tasks (created_at);
CREATE INDEX idx_task_completed_at ON tasks (completed_at);

-- Composite indexes for common queries
CREATE INDEX idx_task_assigned_status ON tasks (assigned_to_id, status) WHERE is_archived = false;
CREATE INDEX idx_task_created_status ON tasks (created_by_id, status) WHERE is_archived = false;
CREATE INDEX idx_task_due_status ON tasks (due_date, status) WHERE is_archived = false;

-- Add check constraints
ALTER TABLE tasks ADD CONSTRAINT chk_task_status 
    CHECK (status IN ('TODO', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED', 'CANCELLED'));

ALTER TABLE tasks ADD CONSTRAINT chk_task_priority 
    CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT'));

ALTER TABLE tasks ADD CONSTRAINT chk_task_category 
    CHECK (category IN ('PERSONAL', 'WORK', 'PROJECT', 'MEETING', 'RESEARCH', 
                       'DEVELOPMENT', 'TESTING', 'DOCUMENTATION', 'MAINTENANCE', 'OTHER'));

-- Trigger to automatically update updated_at on row updates
CREATE TRIGGER update_tasks_updated_at 
    BEFORE UPDATE ON tasks 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Trigger to automatically set completed_at when status changes to COMPLETED
CREATE OR REPLACE FUNCTION update_task_completed_at()
RETURNS TRIGGER AS $$
BEGIN
    -- Set completed_at when status changes to COMPLETED
    IF NEW.status = 'COMPLETED' AND OLD.status != 'COMPLETED' THEN
        NEW.completed_at = CURRENT_TIMESTAMP;
    END IF;
    
    -- Clear completed_at when status changes from COMPLETED to something else
    IF NEW.status != 'COMPLETED' AND OLD.status = 'COMPLETED' THEN
        NEW.completed_at = NULL;
    END IF;
    
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_task_completed_at_trigger 
    BEFORE UPDATE ON tasks 
    FOR EACH ROW 
    EXECUTE FUNCTION update_task_completed_at();

-- Insert demo tasks
DO $$
DECLARE
    admin_id UUID;
    manager_id UUID;
    user_id UUID;
BEGIN
    -- Get user IDs
    SELECT id INTO admin_id FROM users WHERE email = 'admin@taskmanager.demo';
    SELECT id INTO manager_id FROM users WHERE email = 'manager@taskmanager.demo';
    SELECT id INTO user_id FROM users WHERE email = 'user@taskmanager.demo';
    
    -- Insert demo tasks
    INSERT INTO tasks (id, title, description, status, priority, category, assigned_to_id, created_by_id, due_date, estimated_hours) VALUES
    (gen_random_uuid(), 'Setup Development Environment', 'Configure local development environment with required tools and dependencies', 'COMPLETED', 'HIGH', 'DEVELOPMENT', user_id, admin_id, CURRENT_TIMESTAMP - INTERVAL '2 days', 4),
    
    (gen_random_uuid(), 'Implement User Authentication', 'Develop JWT-based authentication system with login and registration', 'IN_PROGRESS', 'HIGH', 'DEVELOPMENT', user_id, manager_id, CURRENT_TIMESTAMP + INTERVAL '3 days', 8),
    
    (gen_random_uuid(), 'Design Database Schema', 'Create comprehensive database schema for the application', 'COMPLETED', 'MEDIUM', 'PROJECT', manager_id, admin_id, CURRENT_TIMESTAMP - INTERVAL '1 day', 6),
    
    (gen_random_uuid(), 'Write API Documentation', 'Document all REST API endpoints with OpenAPI/Swagger', 'TODO', 'MEDIUM', 'DOCUMENTATION', user_id, manager_id, CURRENT_TIMESTAMP + INTERVAL '5 days', 4),
    
    (gen_random_uuid(), 'Performance Testing', 'Conduct performance testing and optimization', 'TODO', 'LOW', 'TESTING', manager_id, admin_id, CURRENT_TIMESTAMP + INTERVAL '10 days', 12),
    
    (gen_random_uuid(), 'Security Audit', 'Review application security and fix vulnerabilities', 'TODO', 'URGENT', 'PROJECT', admin_id, admin_id, CURRENT_TIMESTAMP + INTERVAL '7 days', 16),
    
    (gen_random_uuid(), 'Code Review Meeting', 'Weekly code review and architecture discussion', 'IN_REVIEW', 'MEDIUM', 'MEETING', manager_id, admin_id, CURRENT_TIMESTAMP + INTERVAL '1 day', 2),
    
    (gen_random_uuid(), 'Update Dependencies', 'Update all project dependencies to latest stable versions', 'TODO', 'LOW', 'MAINTENANCE', user_id, admin_id, CURRENT_TIMESTAMP + INTERVAL '14 days', 3);
END $$;