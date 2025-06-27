-- PulseIQ Database Initialization Script
-- This script initializes the database schema and user permissions

-- Create the pulseiq schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS pulseiq;

-- Set the default search path for the database
ALTER DATABASE pulseiq_db SET search_path TO pulseiq;

-- Grant necessary permissions to the pulseiq_user
GRANT USAGE ON SCHEMA pulseiq TO pulseiq_user;
GRANT CREATE ON SCHEMA pulseiq TO pulseiq_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA pulseiq TO pulseiq_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA pulseiq TO pulseiq_user;

-- Set default privileges for future tables and sequences
ALTER DEFAULT PRIVILEGES IN SCHEMA pulseiq GRANT ALL ON TABLES TO pulseiq_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA pulseiq GRANT ALL ON SEQUENCES TO pulseiq_user;