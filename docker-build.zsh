#!/bin/zsh

if [[ -z "${CARNIVAM_MICRONAUT_HOME}" ]]; then
    echo "required environment variable is missing: CARNIVAM_MICRONAUT_HOME"
    exit(1)
else
    HOME_DIR="${CARNIVAM_MICRONAUT_HOME}"
fi

BUILD_HOME=build/home
mkdir -p $BUILD_HOME/config
mkdir -p $BUILD_HOME/data/cache
mkdir -p $BUILD_HOME/data/graph
mkdir -p $BUILD_HOME/data/source
mkdir -p $BUILD_HOME/log

cp -fR $HOME_DIR/config/* $BUILD_HOME/config/
cp -fR $HOME_DIR/data/source/* $BUILD_HOME/data/source/
#cp -fR $HOME_DIR/data/graph/* $BUILD_HOME/data/graph/
#cp -fR $HOME_DIR/data/cache/* $BUILD_HOME/data/cache/

docker build . -t nccc-biobank-server:0.1
echo
echo
echo "To run the docker container execute:"
echo "    $ docker run --publish 5858:5858 nccc-biobank-server:0.1"
echo
echo "If running on a VPN:"
echo "    $ docker run --net=host --hostname=docker-cbs nccc-biobank-server:0.1"