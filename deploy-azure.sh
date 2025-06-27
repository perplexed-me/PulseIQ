#!/bin/bash

# PulseIQ Azure Deployment Script
# This script deploys the PulseIQ application to Azure using Docker Compose

set -e

echo "🚀 Starting PulseIQ deployment to Azure..."

# Configuration
COMPOSE_FILE="docker-compose.azure.yml"
ENV_FILE=".env.azure"

# Check if environment file exists
if [ ! -f "$ENV_FILE" ]; then
    echo "❌ Environment file $ENV_FILE not found!"
    echo "Please create $ENV_FILE based on .env.azure.template"
    exit 1
fi

# Load environment variables
source "$ENV_FILE"

# Validate required environment variables
required_vars=("DOCKER_USERNAME" "DB_PASSWORD" "JWT_SECRET" "FRONTEND_URL" "BACKEND_URL")
for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "❌ Required environment variable $var is not set!"
        exit 1
    fi
done

echo "✅ Environment variables validated"

# Create necessary directories
mkdir -p ~/pulseiq-app/logs
mkdir -p ~/pulseiq-app/backups

# Copy deployment files
echo "📁 Setting up deployment files..."
cp "$COMPOSE_FILE" ~/pulseiq-app/
cp "$ENV_FILE" ~/pulseiq-app/
cp "backend/init-schema.sql" ~/pulseiq-app/

# Navigate to deployment directory
cd ~/pulseiq-app

# Pull latest images
echo "📥 Pulling latest Docker images..."
docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" pull

# Stop existing containers
echo "🛑 Stopping existing containers..."
docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" down

# Remove unused Docker resources
echo "🧹 Cleaning up unused Docker resources..."
docker system prune -f

# Start the application
echo "🚀 Starting PulseIQ application..."
docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be healthy..."
timeout 300 bash -c "
    while true; do
        if docker-compose -f $COMPOSE_FILE ps | grep -q 'healthy'; then
            break
        fi
        echo 'Waiting for services to start...'
        sleep 10
    done
"

# Show service status
echo "📊 Service Status:"
docker-compose -f "$COMPOSE_FILE" ps

# Show logs
echo "📋 Recent logs:"
docker-compose -f "$COMPOSE_FILE" logs --tail=20

echo "✅ PulseIQ deployment completed successfully!"
echo "🌐 Frontend: $FRONTEND_URL"
echo "🔧 Backend: $BACKEND_URL"
echo "💾 Database: PostgreSQL running on port 5432"

# Create backup script
cat > backup_database.sh << 'EOF'
#!/bin/bash
# Database backup script
BACKUP_DIR="./backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/pulseiq_backup_$TIMESTAMP.sql"

mkdir -p "$BACKUP_DIR"

docker exec pulseiq_postgres pg_dump -U pulseiq_user -d pulseiq_db > "$BACKUP_FILE"
echo "Database backup created: $BACKUP_FILE"

# Keep only last 7 backups
ls -t "$BACKUP_DIR"/pulseiq_backup_*.sql | tail -n +8 | xargs -r rm
EOF

chmod +x backup_database.sh

echo "📋 Additional commands:"
echo "  - View logs: docker-compose -f $COMPOSE_FILE logs -f"
echo "  - Stop services: docker-compose -f $COMPOSE_FILE down"
echo "  - Restart services: docker-compose -f $COMPOSE_FILE restart"
echo "  - Backup database: ./backup_database.sh"
