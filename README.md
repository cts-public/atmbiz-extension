# **Atmbiz**
## Project Structure

- Project structure consists of `AtmbizExtension` class which is an extension of `com.generalbytes.batm.server.extension.AbstractExtensions` located in the `com.atmbiz.extensions` package.

## Build & Deploy

1. Run `clean`.
2. Run `tasks/build`.
3. Copy `build/libs/atmbiz-{VERSION}.jar` on server into folder `/batm/app/master/extensions`.
4. On the server run command:
   ```bash
   sudo ./batm-manage stop all
   ```
5. On the server run command:
   ```bash
   sudo ./batm-manage start all
   ```

# Atm.biz Integration Procedure

To successfully integrate with atm.biz, follow these steps:

1. **Operator Registration**: Register as an operator at [https://atm.biz/operator-sign-up](https://atm.biz/operator-sign-up).
2. **Operator Login and Setup**: Once registered, login to your operator account. Set up your account settings at [https://atm.biz/dashboard/operator/settings](https://atm.biz/dashboard/operator/settings).
3. **Configuration File**: Create an `atmbiz` configuration file in the `/batm/config/` directory. The content should be:
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

4. **Download and Copy the RabbitMQ Java Client Library**:
   - Download the RabbitMQ Java client library from [RabbitMQ Java Client Library](https://repo1.maven.org/maven2/com/rabbitmq/amqp-client/5.18.0/amqp-client-5.18.0.jar).
   - Copy the `.jar` library file to:
     ```bash
     /batm/app/master/lib
     ```

5. **Server Commands**:
   - To stop all services:
     ```bash
     sudo ./batm-manage stop all
     ```
   - To start all services:
     ```bash
     sudo ./batm-manage start all
     ```
