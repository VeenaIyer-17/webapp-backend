# CSYE 7374 - Spring 2020

## Team Information

| Name | NEU ID | Email Address |
| --- | --- | --- |
| Ravi Kiran | 001491808 | lnu.ra@husky.neu.edu |
| Veena Vasudevan Iyer | 001447061 | iyer.v@husky.neu.edu |

## Technology Stack

The Recipe Management Web application is developed using Java Spring Boot framework that uses the REST architecture 
to create, update and retrieve user
Spring Security using Base64 authentication to secure retrieve user information and update user information
Spring Security using Base64 authentication to create recipe and update recipe
A user can create a recipe, delete & update only authored recipes. Anyone can fetch a recipe

## Build Instructions
Pre-req : Need tool to run REST endpoints like POSTMAN, MariaDB , IDE, Ansible, KOPS
    * Bring up the infrastructure using code and instructions at https://github.com/advancecloud7374/Infrastructure
    
    * Setting up Jenkins *
    * Open your domain where Jenkins is hosted
    * Login to Jenkins console using the steps mentioned on the console
    * Download the plugins. Make sure github and docker plugins are installed
    * Click new to create a new job.
    * Select Pipeline and provide a name for your job.
    * Select "GitHub hook trigger for GITScm polling" in Build Triggers
    * Select Pipeline script from scm in Pipeline Defination
    * Select Git in SCM
    * Add the repository details. Add the credentials
    * Provide the path of Jenkinsfile "webapp/recipie_management_system/Jenkinsfile"
    * Apply and Save
    * Now add the environment variables:
    * - Add Docker credentials {DOCKER_USER} and {DOCKER_PASS} for pushing the image tto docker hub
    * - Add {BACKEND_IMAGE_NAME} for the docker image name
    

    * Setting up github *
    * Open the github repository and add the webhook for the Jenkins server under settings>webhooks option
    * Provide the payload url(url where jenkins is hosted) and append /github-webhook/ in the end. Example: jenkins.kiranravi.me
    * Content type: application/json
    * Save the Webhook
    

    * Triggering the job *
    * Push the code to the repository.
    * This should trigger the job in Jenkins.
    * Once completed, a new docker image should be available at docker hub.
    
