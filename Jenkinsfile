pipeline {
	agent none

	triggers {
		pollSCM 'H/10 * * * *'
	}

	options {
		disableConcurrentBuilds()
		buildDiscarder(logRotator(numToKeepStr: '14'))
	}

	stages {
		stage("test: baseline (jdk17)") {
			agent {
				docker {
					image 'springci/spring-data-openjdk17-with-mongodb-4.0.23:latest'
					args '-v $HOME/.m2:/tmp/jenkins-home/.m2'
				}
			}
			options { timeout(time: 30, unit: 'MINUTES') }
			steps {
				sh 'mkdir -p /tmp/mongodb/db /tmp/mongodb/log'
				sh 'mongod --setParameter transactionLifetimeLimitSeconds=90 --setParameter maxTransactionLockRequestTimeoutMillis=10000 --dbpath /tmp/mongodb/db --replSet rs0 --fork --logpath /tmp/mongodb/log/mongod.log &'
				sh 'sleep 10'
				sh 'mongo --eval "rs.initiate({_id: \'rs0\', members:[{_id: 0, host: \'127.0.0.1:27017\'}]});"'
				sh 'sleep 15'
				sh 'test/run.sh'
			}
		}

	}

	post {
		changed {
			script {
				slackSend(
						color: (currentBuild.currentResult == 'SUCCESS') ? 'good' : 'danger',
						channel: '#sagan-content',
						message: "${currentBuild.fullDisplayName} - `${currentBuild.currentResult}`\n${env.BUILD_URL}")
				emailext(
						subject: "[${currentBuild.fullDisplayName}] ${currentBuild.currentResult}",
						mimeType: 'text/html',
						recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']],
						body: "<a href=\"${env.BUILD_URL}\">${currentBuild.fullDisplayName} is reported as ${currentBuild.currentResult}</a>")
			}
		}
	}
}
