node{
	def DOCKERHUB_CREDENTIALS = 'dockerhub'
    def GITHUB_CREDENTIALS = 'github-credentials'
	def commit_id



	stage('Git Clone') {
		checkout scm
			dir('helm-charts'){
			   git branch: "${env.git_branch}" , credentialsId: 'github-credentials' , url: "${env.HELM_URL}"
			 }
	}

	stage('Build package') {

            sh ''' mvn clean
		   mvn package 
		'''
    }

    stage('Build docker image') {
		commit_id = sh(returnStdout: true, script: 'git rev-parse HEAD')
		commit_id = sh(returnStdout: true, script: """echo $commit_id . """).trim()
      
		sh """
			env && docker build -t ${BACKEND_IMAGE_NAME}:${commit_id} .
			"""
      
    }
    
    stage('Push image') {
            sh """
            docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}
            docker push ${BACKEND_IMAGE_NAME}:${commit_id}
            """
    }
	stage('Update helm-chart') {
			sh 'pwd'
			def scope = "${env.increment_type}"
			version = nextVersionFromGit(scope)
			
			sh "yq write -i helm-charts/helm-backend/Chart.yaml version ${version}"
			sh "yq write -i helm-charts/helm-backend/values.yaml backend_image ${DOCKER_USER}/webapp-backend:$commit_id"
			pushToGit("${env.git_branch}")
			
	}

		
}

def nextVersionFromGit(scope) {
	def latestVersion = sh returnStdout: true, script: 'yq read helm-charts/helm-backend/Chart.yaml version'
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
		dir("helm-charts"){
		sh "git config --global user.name ${DOCKER_USER}"
		sh	"sudo git commit -am 'version upgrade to ${version} by jenkins'"
		sh	"git push origin ${git_branch}"
		}
		
}
