#!/bin/bash
wget https://download.java.net/openjdk/jdk11/ri/openjdk-11+28_linux-x64_bin.tar.gz
tar zxf openjdk-11+28_linux-x64_bin.tar.gz

echo "export JAVA_HOME=$(pwd)/jdk-11" >> ~/.bashrc
echo "export PATH=$(pwd)/jdk-11/bin:$PATH" >> ~/.bashrc
source ~/.bashrc