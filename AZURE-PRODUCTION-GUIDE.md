# Azure Production Deployment Guide

## Production Database Options

### Option 1: Azure Database for PostgreSQL (Recommended)

**Advantages:**

- Fully managed service (no maintenance overhead)
- Automatic backups and point-in-time recovery
- High availability with 99.99% SLA
- Automatic security updates
- Built-in monitoring and alerting
- Auto-scaling capabilities

**Setup Steps:**

1. **Create Azure Database for PostgreSQL:**

```bash
# Create resource group
az group create --name pulseiq-prod --location eastus

# Create PostgreSQL server
az postgres server create \
  --resource-group pulseiq-prod \
  --name pulseiq-db-server \
  --location eastus \
  --admin-user pulseiq_admin \
  --admin-password YourSecurePassword123! \
  --sku-name GP_Gen5_2 \
  --version 15

# Create database
az postgres db create \
  --resource-group pulseiq-prod \
  --server-name pulseiq-db-server \
  --name pulseiq_db

# Configure firewall for Azure VM
az postgres server firewall-rule create \
  --resource-group pulseiq-prod \
  --server pulseiq-db-server \
  --name AllowAzureVM \
  --start-ip-address YOUR_VM_IP \
  --end-ip-address YOUR_VM_IP
```

2. **Environment Configuration:**

```bash
# .env.azure for managed database
DOCKER_USERNAME=your_dockerhub_username
JWT_SECRET=your_jwt_secret
FIREBASE_ENABLED=true
FRONTEND_URL=http://your_vm_ip:8080
BACKEND_URL=http://your_vm_ip:8085

# Azure Database Connection
AZURE_DB_SERVER=pulseiq-db-server
AZURE_DB_NAME=pulseiq_db
AZURE_DB_USERNAME=pulseiq_admin
AZURE_DB_PASSWORD=YourSecurePassword123!
```

3. **Deploy with Managed Database:**

```bash
docker-compose -f docker-compose.azure-managed-db.yml --env-file .env.azure up -d
```

### Option 2: Self-Hosted PostgreSQL on Azure VM

**Use when:**

- Need full control over database configuration
- Custom PostgreSQL extensions required
- Cost optimization for smaller workloads

**Azure VM Requirements:**

- **Minimum:** Standard B2s (2 vCPUs, 4 GB RAM)
- **Recommended:** Standard D2s_v3 (2 vCPUs, 8 GB RAM)
- **Storage:** Premium SSD for database performance
- **Network:** Virtual Network with proper security groups

## Production Deployment Steps

### 1. Azure Infrastructure Setup

```bash
# Create production-ready VM
az vm create \
  --resource-group pulseiq-prod \
  --name pulseiq-vm \
  --image Ubuntu2004 \
  --size Standard_D2s_v3 \
  --admin-username azureuser \
  --generate-ssh-keys \
  --storage-sku Premium_LRS \
  --os-disk-size-gb 128 \
  --data-disk-sizes-gb 64

# Open required ports
az vm open-port --resource-group pulseiq-prod --name pulseiq-vm --port 8080
az vm open-port --resource-group pulseiq-prod --name pulseiq-vm --port 8085

# Get VM IP
az vm show -d -g pulseiq-prod -n pulseiq-vm --query publicIps -o tsv
```

### 2. VM Setup for Production

```bash
# Connect to VM
ssh azureuser@YOUR_VM_IP

# Install Docker (production version)
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Optimize system for production
echo 'vm.max_map_count=262144' | sudo tee -a /etc/sysctl.conf
echo 'fs.file-max=2097152' | sudo tee -a /etc/sysctl.conf
sudo sysctl -p

# Create application directory
mkdir -p ~/pulseiq-app
cd ~/pulseiq-app
```

### 3. Production Environment Configuration

```bash
# Create production environment file
cat > .env.azure << EOF
# Docker Configuration
DOCKER_USERNAME=your_dockerhub_username

# Database Configuration (for self-hosted)
DB_PASSWORD=your_very_secure_password_here

# Application Security
JWT_SECRET=your_256_bit_jwt_secret_key_here

# URLs (replace with your actual VM IP)
FRONTEND_URL=http://YOUR_VM_IP:8080
BACKEND_URL=http://YOUR_VM_IP:8085

# Firebase
FIREBASE_ENABLED=true
EOF
```

### 4. SSL/TLS Setup (Production Ready)

```bash
# Install Nginx for reverse proxy and SSL
sudo apt update
sudo apt install nginx certbot python3-certbot-nginx -y

# Configure Nginx reverse proxy
sudo tee /etc/nginx/sites-available/pulseiq << EOF
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /api {
        proxy_pass http://localhost:8085;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

# Enable site
sudo ln -s /etc/nginx/sites-available/pulseiq /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx

# Get SSL certificate (if you have a domain)
sudo certbot --nginx -d your-domain.com
```

### 5. Deploy Application

```bash
# For Azure managed database
docker-compose -f docker-compose.azure-managed-db.yml --env-file .env.azure up -d

# OR for self-hosted database
docker-compose -f docker-compose.azure.yml --env-file .env.azure up -d
```

## Production Monitoring

### Health Checks

```bash
# Check all services
docker-compose ps

# Check health endpoints
curl http://localhost:8085/actuator/health
curl http://localhost:8080/

# Monitor resource usage
docker stats
```

### Log Management

```bash
# Centralized logging
docker-compose logs -f --tail=100

# Service-specific logs
docker logs pulseiq_backend --tail=100
docker logs pulseiq_frontend --tail=100
docker logs pulseiq_postgres --tail=100  # if self-hosted
```

## Production Security Checklist

- ✅ **Database:** Not exposed to internet (no port 5432 binding)
- ✅ **SSL/TLS:** HTTPS enabled with valid certificates
- ✅ **Firewall:** Only required ports open (80, 443, 22)
- ✅ **Secrets:** All passwords and keys in environment variables
- ✅ **Updates:** Regular security updates scheduled
- ✅ **Monitoring:** Health checks and alerting configured
- ✅ **Backups:** Automated backup strategy (managed DB handles this)

## Scaling Considerations

### Horizontal Scaling

```bash
# Scale backend instances
docker-compose up -d --scale backend=3

# Add load balancer (nginx, HAProxy, or Azure Load Balancer)
```

### Database Scaling

- **Azure Database:** Use read replicas and higher tiers
- **Self-hosted:** Consider PostgreSQL streaming replication

## Cost Optimization

### Azure Managed Database

- Use **Basic tier** for development
- Use **General Purpose** for production
- Schedule scaling for peak hours

### Self-hosted Database

- Use **Spot VMs** for non-critical environments
- Implement auto-shutdown for development environments

## Troubleshooting

### Common Production Issues

1. **Database Connection:**

```bash
# Test database connectivity
docker exec pulseiq_backend nc -zv db 5432  # self-hosted
# OR test Azure DB
docker exec pulseiq_backend nc -zv your-server.postgres.database.azure.com 5432
```

2. **Memory Issues:**

```bash
# Check memory usage
free -h
docker stats --no-stream
```

3. **Disk Space:**

```bash
# Check disk usage
df -h
docker system df
```

This setup gives you enterprise-grade PostgreSQL deployment options with proper production standards!
