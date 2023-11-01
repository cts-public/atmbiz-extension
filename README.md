# **Atmbiz Integration Guide**

Welcome to the Atm.biz integration guide. This document will walk you through the process of successfully integrating with atm.biz in clear and detailed steps.

## Table of Contents

1. [Operator Registration](#operator-registration)
2. [Plugin Configuration](#plugin-configuration)
    - [Script Installation](#script-installation)
    - [Manual Installation](#manual-installation)

## **Operator Registration**

### Step 1: Sign Up
Register as an operator at [https://atm.biz/operator-sign-up](https://atm.biz/operator-sign-up).

### Step 2: Login & Setup
After successful registration:
- Login to your operator account.
- Set up your account settings at [https://atm.biz/dashboard/operator/settings](https://atm.biz/dashboard/operator/settings).

## **Plugin Configuration**

You can either use the script installation method for automated setup or follow the manual installation steps.

### **Script Installation**

1. **Download Installation Script**: Fetch the script from the following link:
   ```bash
   wget https://raw.githubusercontent.com/cts-public/atmbiz-extension/main/atmbiz-install.sh
   ```
   
2. **Run the Script**:

    ```bash
    ./atmbiz-install.sh
    ```
   
Provide Credentials: During script execution, you'll be prompted to enter the MQ password and MQ user.

Restart: If prompted, restart the system. Alternatively, you can choose to restart later.

### Manual installation 

1. **Configuration File**: Create an `atmbiz` configuration file in the `/batm/config/` directory. The content should be:
   ```properties
   MQ_HOST=https://atm.biz/
   MQ_PORT=5672
   MQ_USER=operator
   MQ_PASSWORD=password
   MQ_PREFIX=operatorPrefix
   ```
   **Security considerations**:
   - Change the ownership of the configuration file:
     ```bash
     sudo chown cas_user:cas_group /batm/config/atmbiz
     ```
   - Set file permissions:
     ```bash
     sudo chmod 600 /batm/config/atmbiz
     ```

2. **Download and Copy the RabbitMQ Java Client Library**:
   - Download the RabbitMQ Java client library from [RabbitMQ Java Client Library](https://repo1.maven.org/maven2/com/rabbitmq/amqp-client/5.18.0/amqp-client-5.18.0.jar).
   - Copy the `.jar` library file to:
     ```bash
     /batm/app/master/lib
     ```

3. **Server Commands**:
   - To stop all services:
     ```bash
     sudo ./batm-manage stop all
     ```
   - To start all services:
     ```bash
     sudo ./batm-manage start all
     ```
