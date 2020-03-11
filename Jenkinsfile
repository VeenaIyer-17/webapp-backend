pipeline{
  agent any
  environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
        GITHUB_CREDENTIALS = credentials('90395f85-d16c-47e1-8ec4-5a9d99c8c4c1')
        version = null
        git_branch = "${params.git_branch}"
        // DOCKERHUB_CREDENTIALS_USR and DOCKERHUB_CREDENTIALS_PSW automatically available
  }
  parameters {
    string(name: 'increment_type', defaultValue: 'patch', description: '')
    string(name: 'git_branch', defaultValue: 'patch', description: '')
  }

  stages{
    stage('Git Clone') {
      steps {
        dir('webapp-backend'){
          checkout scm
        }
        dir('helm-charts'){
          sh "echo ${GITHUB_CREDENTIALS}"
          // script{git_info = checkout([
          //               $class: 'GitSCM', branches: [[name: "*/${git_branch}"]], 
          //               doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [],
          //               userRemoteConfigs: [[
          //                   credentialsId: 'GitHubKey', 
          //                   url: 'git@github.com:akashkatakam/helm-charts.git'
          //               ]]
          //           ])}
           git branch: 'assignment8_test' , credentialsId: '90395f85-d16c-47e1-8ec4-5a9d99c8c4c1' , url: 'https://github.com/akashkatakam/helm-charts.git'
          }
        }
      }
    

    // stage('Build package') {
    //   steps {
    //         sh ' cd webapp-backend && mvn clean install'
    //   }
    // }

    // stage('Build docker image') {
    //   steps {
	  //       sh '''
    //           env && docker build -t ${BACKEND_IMAGE_NAME}:${GIT_COMMIT} .
    //          '''
    //   }
    // }
    
    // stage('Push image') {
    //   steps {
    //         sh '''
    //         docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}
    //         docker push ${BACKEND_IMAGE_NAME}:${GIT_COMMIT}
    //         '''
    //   }
    // }

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
          //   sshagent (credentials:['GithubKey']) {
          //   // sh "git branch ${git_branch} 2>/dev/null"
          //   sh (script: """
          //         if [ `git branch --list ${git_branch}`]
          //         then
          //           echo "branch exists!"
          //         fi """,returnStdout: true)
          //   // sh """git rev-parse --verify ${git_branch} && git branch ${git_branch} """
          //   sh  """git checkout ${git_branch}"""
          //   sh """git add . && git commit -m \"Jenkins updated the chart version to ${version}\""""
          //   sh """git push origin ${git_branch} """
          // }
          withCredentials([usernamePassword(credentialsId: '90395f85-d16c-47e1-8ec4-5a9d99c8c4c1', usernameVariable: "${GITHUB_CREDENTIALS_USR}", passwordVariable: "${GITHUB_CREDENTIALS_PSW}")]){    
    sh("""
        cd helm-charts
        echo ${git_branch}
        git checkout ${git_branch}
        git config --local credential.helper "!f() { echo username=\\$GITHUB_CREDENTIALS_USR; echo password=\\$GITHUB_CREDENTIALS_PSW; }; f"
        sudo git commit -am 'version upgrade to ${version} by jenkins'
        git push origin ${git_branch}
    """)
}
    }
}
        