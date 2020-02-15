node {
    def docker_image
    def commit_id
        stage('Clone Repository') {
            checkout scm
        }
        stage('Build') {
            sh 'mvn -B -DskipTests clean package'
        }
        stage('Test') {
            sh 'mvn test'
        }
        stage('Building image') {
            script {
                commit_id = sh(returnStdout: true, script: 'git rev-parse HEAD')
                docker_image = docker.build("${env.registry}")
            }
        }
        stage('Pushing image') {
            script {
                    docker.withRegistry('', "${env.docker_creds}" ) {
                    docker_image.push("${commit_id}")
                    docker_image.push("latest")
                }
            }
        }
    }