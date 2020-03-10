pipeline{
  agent any
  environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
        GITHUB_CREDENTIALS = credentials('90395f85-d16c-47e1-8ec4-5a9d99c8c4c1')
        version = null
        // DOCKERHUB_CREDENTIALS_USR and DOCKERHUB_CREDENTIALS_PSW automatically available
  }
  parameters {
    string(name: 'increment_type', defaultValue: 'patch', description: '')
  }

  stages{
    stage('Git Clone') {
      steps {
        dir('webapp-backend'){
          checkout scm
        }
        dir('helm-charts'){
          sh "echo ${GITHUB_CREDENTIALS}"
          git credentialsId: '90395f85-d16c-47e1-8ec4-5a9d99c8c4c1' , url: 'https://github.com/akashkatakam/helm-charts.git'
        }
      }
    }

    stage('Build package') {
      steps {
            sh ' cd webapp-backend && mvn clean install'
      }
    }

    stage('Build docker image') {
      steps {
	        sh '''
              env && docker build -t ${BACKEND_IMAGE_NAME}:${GIT_COMMIT} .
             '''
      }
    }
    
    stage('Push image') {
      steps {
            sh '''
            docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}
            docker push ${BACKEND_IMAGE_NAME}:${GIT_COMMIT}
            '''
      }
    }

    stage('Update helm-chart') {
      steps {
        sh 'pwd'
        script {
          scope = "${params.increment_type}"
          version = nextVersionFromGit(scope)
        }
        sh "yq write -i ./helm-charts/helm-backend/Chart.yaml version ${version}"
        sh "yq write -i ./helm-charts/helm-backend/values.yaml backend_image akashkatakam/webapp-backend:${GIT_COMMIT}"
        script{
            pushToGit("assignment8_test")
          }
        }
      }
  }
  post{
    success {  
         echo "done"
      }
  }
}

def nextVersionFromGit(scope) {
        def latestVersion = sh returnStdout: true, script: 'yq read ./helm-charts/helm-backend/Chart.yaml version'
        def (major, minor, patch) = latestVersion.tokenize('.').collect { it.toInteger() }
        def nextVersion
        switch (scope) {
            case 'major':
                nextVersion = "${major + 1}.0.0"
                break
            case 'minor':
                nextVersion = "${major}.${minor + 1}.0"
                break
            case 'patch':
                nextVersion = "${major}.${minor}.${patch + 1}"
                break
          }
         nextVersion
        }
def pushToGit(branch) {
    def git_branch =  branch
     script{
            sshagent (['GithubKey']) {
            sh("git push origin ${git_branch}")
          }
    }
}
        