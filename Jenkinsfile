pipeline {
  agent {
    label 'android-build-slave'
  }  
   stages {
     /* prepare stage */ 
    stage ('Prepare') {
      steps {
        sh 'echo "complete Prepare stage"'
      }
    }
    /*build stage */
    stage ('Build') {
      steps {
          sh 'echo "start Build"'
         script {
               if (params.buildDebug) {
                     sh 'echo "build debug version"'
                     sh "cp -r -f /var/lib/jenkins/.android/local.properties ./"
                     sh "./gradlew clean" 
                     sh "./gradlew assembleDebug --debug"
               } else {
                     sh 'echo "build release version"'
                     sh "cp -r -f /var/lib/jenkins/.android/local.properties ./"
                     sh "./gradlew clean" 
                     sh "./gradlew assembleRelease"
          }  
        }
         sh 'echo "Complete build"'
      }
    }
    /* test stage */
    stage ('Test') {
      steps {
        sh 'echo "complete Test stagge"'
      }
    }
    /* deploy stage */
   stage ('Deploy') {
     when {
      expression { return params.doDeploy }
     }
      steps {
       sh 'echo "start Deploy"'
       sh 'rsync -auv app/build/outputs/apk/*.apk rsync.leautolink.com::ftp/ecolink-android' 
       sh 'echo "complete Deploy stagge"'
     }
    }
  }
}
