# **Atmbiz**
## project structure

* project structure consists of `AtmbizExtension` class which is extension of 
`com.generalbytes.batm.server.extension.AbstractExtensions` located in `com.atmbiz.extensions` package

* Implementation of specific REST endpoints are located in `com.atmbiz.extensions.rest` package

***************

## build&deploy

* run `clean`
* run `tasks/build`
* copy `build/libs/atmbiz-{VERSION}.jar` on server into folder `/batm/app/master/extensions` 
* on server run command `sudo ./batm-manage stop all` 
* on server run command `sudo ./batm-manage start all`

# Atm.biz Integration Procedure

To successfully integrate with atm.biz, follow these steps:

1. **Operator Registration**: Register as an operator at the following URL: [https://atm.biz/operator-sign-up](https://atm.biz/operator-sign-up)

2. **Operator Login and Setup**: Once registered, login to your operator account. Setup your account settings at the following URL: [https://atm.biz/dashboard/operator/settings](https://atm.biz/dashboard/operator/settings)

3. **Configuration File**: You'll need to create an `atmbiz` configuration file. This should be placed in the configuration folder of your CAS server, specifically in the `/batm/config/` directory. The content of this file will be provided to you and should be as follows:

    ```
      MQ_HOST=https://atm.biz/
      MQ_PORT=5672
      MQ_USER=operator
      MQ_PASSWORD=password
      MQ_PREFIX=operatorPrefix
    ```
   **Security considerations** : 
   
   You should change the ownership of the configuration file to the user that runs the CAS server. This way, only this user and the root user will have the ability to change the file.
    ```
      sudo chown cas_user:cas_group /batm/config/atmbiz
    ```
   Replace cas_user with the username and cas_group with the group name that runs the CAS server.
   Set File Permissions:

   Set the file permissions so that only the owner can read and write to the file, and others cannot access it.
     ```
      sudo chmod 600 /batm/config/atmbiz
     ```
   This command sets read and write permissions for the owner and no permissions for the group and others.
   
4. **Server Commands**: Finally, execute the following commands on your server:

    - To stop all services:

    ```bash
    sudo ./batm-manage stop all
    ```

    - To start all services:

    ```bash
    sudo ./batm-manage start all
    ```
