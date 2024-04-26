pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "My Application"
include(":app")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io" )
        maven(url = "https://storage.googleapis.com/download.flutter.io")
        maven(url = "C:\\Users\\ThongNguyen\\Downloads\\trashcode\\android test\\add flutter to android native\\flutter_module\\build\\host\\outputs\\repo")
    }
}



