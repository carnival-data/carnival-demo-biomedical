# Carnival Micronaut

Demonstration Micronaut server with a Carnival graph resource.

---

# Set up instructions

Prerequisite: JDK 11.

## 0. Set up github

Create environment variable GITHUB_USER
```
export GITHUB_USER=th5
```
You may also need to create a github access token with read:packages permissions and set it to GITHUB_TOKEN environment variable, https://github.com/settings/tokens
```
export GITHUB_TOKEN=[long token string]
```

## 1. Get development files for Carnival libraries

git clone https://github.com/pmbb-ibi/carnival.git

Note that here we are using the master branch, not the V1 branch.

## 2. Publish development version of carnival libraries to a local maven repository 
(as opposed to releases that are published as github packages)

```
cd carnival/app
./gradlew publishToMavenLocal
```

This should populate a local maven repository in $HOME/.m2

(Once we make a compatible release, steps 1 and 2 can be avoided. Instead of checking out the carnival repo and building local packages, you can pull in the released packages from github

The current published packages: https://github.com/orgs/pmbb-ibi/packages)

## 3. Create Carnival-Micronaut-Home

```
git clone https://github.com/pmbb-ibi/carnival-micronaut-home.git
```

Set an environment variable
```
export CARNIVAL_MICRONAUT_HOME=/full/path/to/carnival-micronaut-home
```
[TODO] Download neo4j plugin and set directory in application.yaml

## 4. Get Carnival-Micronaut code
```
git clone https://github.com/pmbb-ibi/carnival-micronaut.git
```

## 5. Build and run the Hello World app
```
cd carnival-micronaut
./gradlew run
```

Hopefully you now have a server running at http://localhost:5858


## 6. Create and run Docker container
```
./docker-build.zsh
docker run --publish 5858:5858 carnival-micronaut:0.1
```
