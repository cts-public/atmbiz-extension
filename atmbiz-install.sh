#!/bin/bash

# Function to print with color for better user output
print_message() {
    COLOR=$1
    MESSAGE=$2
    echo -e "\e[${COLOR}m${MESSAGE}\e[0m"
}

# Delete all versions of atmbiz-*.jar if they exist
if rm -f /batm/app/master/extensions/atmbiz-*.jar; then
    print_message 32 "Successfully deleted existing atmbiz-*.jar files."
else
    print_message 33 "No existing atmbiz-*.jar files found or an error occurred."
fi

# Download and copy atmbiz-0.0.2.jar directly to the desired directory
if wget -q -O /batm/app/master/extensions/atmbiz-0.0.2.jar https://github.com/cts-public/atmbiz-extension/releases/download/v0.0.2/atmbiz-0.0.2.jar; then
    print_message 32 "Successfully downloaded atmbiz-0.0.2.jar."
else
    print_message 31 "Failed to download atmbiz-0.0.2.jar."
    exit 1
fi

# Request user inputs for MQ_USER and MQ_PASSWORD
read -p "Please enter MQ_USER: " MQ_USER
read -sp "Please enter MQ_PASSWORD: " MQ_PASSWORD
echo "" # For better formatting

# Create Configuration File
cat << EOF > /batm/config/atmbiz
MQ_HOST=https://atm.biz/
MQ_PORT=5671
MQ_USER=${MQ_USER}
MQ_PASSWORD=${MQ_PASSWORD}
MQ_PREFIX=operatorPrefix
EOF
print_message 32 "Configuration file created."

# Set Ownership and Permissions for Configuration File
if sudo chown cas_user:cas_group /batm/config/atmbiz && sudo chmod 600 /batm/config/atmbiz; then
    print_message 32 "Set ownership and permissions for configuration file."
else
    print_message 31 "Failed to set ownership and permissions for configuration file."
    exit 1
fi

# Download and Copy the RabbitMQ Java Client Library directly to the desired directory
if wget -q -O /batm/app/master/lib/amqp-client-5.18.0.jar https://repo1.maven.org/maven2/com/rabbitmq/amqp-client/5.18.0/amqp-client-5.18.0.jar; then
    print_message 32 "Successfully downloaded amqp-client-5.18.0.jar."
else
    print_message 31 "Failed to download amqp-client-5.18.0.jar."
    exit 1
fi

# Ask the user if they want to stop and start services
read -p "Do you want to restart CAS services now? (y/n) " response

if [[ $response =~ ^[Yy]$ ]]; then
    if sudo /batm/batm-manage stop all; then
        print_message 32 "Successfully stopped CAS services."
        sleep 5
        # Start services and wait for "master service started" message
        sudo /batm/batm-manage start all | while read line; do
            echo $line
            if [[ $line == *"master service started"* ]]; then
                print_message 32 "Master service started."
                break
            fi
        done
    else
        print_message 31 "Failed to restart CAS services."
        exit 1
    fi
fi

print_message 34 "Script completed!"
