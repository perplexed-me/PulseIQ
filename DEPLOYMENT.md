# PulseIQ Deployment Guide

This guide covers deploying PulseIQ to Docker Hub and Azure using GitHub Actions CI/CD.

## Overview

The deployment process includes:

- **CI Pipeline**: Automated testing and building
- **Docker Hub**: Container image registry
- **Azure VM**: Production deployment environment
- **PostgreSQL**: Database with proper schema management

## Prerequisites

### 1. Docker Hub Account

- Create account at [hub.docker.com](https://hub.docker.com)
- Create repositories: `pulseiq-backend` and `pulseiq-frontend`

### 2. Azure Virtual Machine

- Ubuntu 20.04+ VM with Docker and Docker Compose installed
- SSH access configured
- Ports 8080 (frontend), 8085 (backend), 5432 (database) open
- **Your Azure VM IP: 132.196.64.104**

### 3. GitHub Secrets Configuration

Add these secrets to your GitHub repository (`Settings` → `Secrets and variables` → `Actions`):

#### Docker Hub Secrets

```
DOCKER_USERNAME=your_dockerhub_username
DOCKER_PASSWORD=your_dockerhub_password
```

#### Azure VM Secrets

```
AZURE_SSH_HOST=your_azure_vm_ip
AZURE_SSH_USER=your_azure_vm_username
AZURE_SSH_PRIVATE_KEY=your_private_ssh_key
```

#### Database Secrets

```
DB_PASSWORD=your_secure_database_password
```

#### Application Secrets

```
JWT_SECRET=your_jwt_secret_key
FIREBASE_SERVICE_ACCOUNT_JSON=your_firebase_service_account_json
```

#### Database Secrets (for CI testing)

```
SPRING_DATASOURCE_URL=your_test_database_url
SPRING_DATASOURCE_USERNAME=your_test_db_username
SPRING_DATASOURCE_PASSWORD=your_test_db_password
```

## Deployment Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   GitHub Repo   │    │   Docker Hub    │    │   Azure VM      │
│                 │    │                 │    │                 │
│ ┌─────────────┐ │    │ ┌─────────────┐ │    │ ┌─────────────┐ │
│ │   CI/CD     │─┼────┼─│   Backend   │─┼────┼─│  Frontend   │ │
│ │ Workflows   │ │    │ │   Image     │ │    │ │   :8080     │ │
│ └─────────────┘ │    │ └─────────────┘ │    │ └─────────────┘ │
│                 │    │                 │    │                 │
│                 │    │ ┌─────────────┐ │    │ ┌─────────────┐ │
│                 │    │ │  Frontend   │ │    │ │   Backend   │ │
│                 │    │ │   Image     │ │    │ │   :8085     │ │
│                 │    │ └─────────────┘ │    │ └─────────────┘ │
└─────────────────┘    └─────────────────┘    │                 │
                                              │ ┌─────────────┐ │
                                              │ │ PostgreSQL  │ │
                                              │ │   :5432     │ │
                                              │ └─────────────┘ │
                                              └─────────────────┘
```

## CI/CD Pipeline

### 1. Continuous Integration (CI)

Triggered on push/PR to `main` or `develop` branches:

- **Backend Testing**: Maven build and test with PostgreSQL
- **Frontend Testing**: ESLint and build validation
- **Artifacts**: Code coverage reports and build artifacts

### 2. Continuous Deployment (CD)

Triggered after successful CI on `main` branch:

- **Docker Build**: Multi-stage builds for backend and frontend
- **Docker Push**: Images pushed to Docker Hub with proper tags
- **Azure Deploy**: Automated deployment to Azure VM with health checks

## Database Management

### Schema Initialization

The database schema is automatically initialized with:

- `pulseiq` schema creation
- Proper user permissions
- Default privileges for new tables

### Backup Strategy

Automated database backups are created with the deployment script:

```bash
./backup_database.sh
```

### Migration Strategy

- **Development**: Hibernate DDL auto-update
- **Production**: Manual schema migrations recommended

## Manual Deployment (Alternative)

### 1. Prepare Azure VM

```bash
# Install Docker and Docker Compose
sudo apt update
sudo apt install docker.io docker-compose -y
sudo usermod -aG docker $USER

# Clone deployment files
git clone <your-repo-url>
cd PulseIQ-user
```

### 2. Configure Environment

```bash
# Copy and edit environment file
cp .env.azure.template .env.azure
nano .env.azure

# Update values:
# DOCKER_USERNAME=your_dockerhub_username
# DB_PASSWORD=your_secure_password
# JWT_SECRET=your_jwt_secret
# FRONTEND_URL=http://your_vm_ip:8080
# BACKEND_URL=http://your_vm_ip:8085
```

### 3. Deploy Application

```bash
# Make deployment script executable
chmod +x deploy-azure.sh

# Run deployment
./deploy-azure.sh
```

## Monitoring and Maintenance

### Health Checks

All services include health checks:

- **Database**: `pg_isready` check
- **Backend**: Spring Actuator health endpoint
- **Frontend**: HTTP availability check

### Service Management

```bash
# View logs
docker-compose -f docker-compose.azure.yml logs -f

# Restart services
docker-compose -f docker-compose.azure.yml restart

# Stop services
docker-compose -f docker-compose.azure.yml down

# Update to latest images
docker-compose -f docker-compose.azure.yml pull
docker-compose -f docker-compose.azure.yml up -d
```

### Backup Management

```bash
# Create backup
./backup_database.sh

# Restore from backup
docker exec -i pulseiq_postgres psql -U pulseiq_user -d pulseiq_db < backups/pulseiq_backup_20231201_120000.sql
```

## Security Considerations

### Docker Security

- Non-root users in containers
- Resource limits configured
- Health checks implemented
- Proper secret management

### Network Security

- Azure Network Security Groups configured
- Database not exposed publicly
- HTTPS recommended for production

### Database Security

- Strong passwords
- Limited user permissions
- Regular backups
- Connection pooling

## Troubleshooting

### Common Issues

1. **Docker Hub Authentication**

   ```bash
   docker login
   # Enter your Docker Hub credentials
   ```

2. **SSH Connection Issues**

   ```bash
   # Test SSH connection
   ssh -i ~/.ssh/your_key user@your_azure_vm_ip
   ```

3. **Database Connection Issues**

   ```bash
   # Check database logs
   docker logs pulseiq_postgres

   # Check database connectivity
   docker exec pulseiq_postgres pg_isready -U pulseiq_user
   ```

4. **Service Health Issues**

   ```bash
   # Check service status
   docker-compose -f docker-compose.azure.yml ps

   # View service logs
   docker-compose -f docker-compose.azure.yml logs backend
   ```

### Log Analysis

```bash
# Backend logs
docker logs pulseiq_backend --tail=100

# Frontend logs
docker logs pulseiq_frontend --tail=100

# Database logs
docker logs pulseiq_postgres --tail=100
```

## Performance Optimization

### Resource Allocation

- **Database**: 1GB memory limit, 512MB reservation
- **Backend**: 1GB memory limit, 512MB reservation
- **Frontend**: 256MB memory limit, 128MB reservation

### Scaling Considerations

- Horizontal scaling with load balancer
- Database read replicas
- CDN for static assets
- Container orchestration (Kubernetes)

## Support

For deployment issues:

1. Check GitHub Actions logs
2. Review Docker Hub build logs
3. Examine Azure VM logs
4. Check service health endpoints
5. Review application logs

## Testing Your Deployment

### Quick Test Commands

```bash
# Test from your local machine
curl http://132.196.64.104:8085/actuator/health  # Backend health
curl http://132.196.64.104:8080/                # Frontend

# Test port accessibility
nc -zv 132.196.64.104 8080  # Frontend port
nc -zv 132.196.64.104 8085  # Backend port
```

### Browser Testing

Open these URLs in your browser:
- **Frontend Application:** http://132.196.64.104:8080
- **Backend Health Check:** http://132.196.64.104:8085/actuator/health
- **Backend Info:** http://132.196.64.104:8085/actuator/info

### Manual Deployment Test

```bash
# SSH to your Azure VM
ssh azureuser@132.196.64.104

# Clone and deploy
git clone https://github.com/your-username/PulseIQ-user.git
cd PulseIQ-user

# Create environment file
cp .env.azure.template .env.azure
# Edit with your actual values

# Deploy
docker-compose -f docker-compose.azure.yml --env-file .env.azure up -d

# Check status
docker-compose -f docker-compose.azure.yml ps
```

### Health Verification

```bash
# On Azure VM, check all services
docker ps
docker logs pulseiq_backend --tail=20
docker logs pulseiq_frontend --tail=20
docker logs pulseiq_postgres --tail=20

# Test database connectivity
docker exec pulseiq_postgres pg_isready -U pulseiq_user -d pulseiq_db
```

For detailed testing procedures, see [TESTING-GUIDE.md](./TESTING-GUIDE.md).

## Updates

To update the deployment:

1. Push changes to `main` branch
2. CI/CD pipeline automatically deploys
3. Monitor deployment in GitHub Actions
4. Verify services are healthy post-deployment
