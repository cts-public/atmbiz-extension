# **Atmbiz**
## project structure

* roject structure consists of `AtmbizExtension` class which is extension of 
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

3. **Configuration File Creation**: You'll need to create an `atmbiz` configuration file. This should be placed in the configuration folder of your CAS server, specifically in the `/batm/config/` directory. The content of this file should be as follows:

    ```
    API_KEY=YourAPIKey
    API_SECRET=YourAPISecret
    ```

4. **Server Commands**: Finally, execute the following commands on your server:

    - To stop all services:

    ```bash
    sudo ./batm-manage stop all
    ```

    - To start all services:

    ```bash
    sudo ./batm-manage start all
    ```

Please replace `YourAPIKey` and `YourAPISecret` with your actual API Key and Secret obtained from atm.biz.

