#!/bin/bash

if [ $encrypted_43b7d2f1321f_key ];then
  openssl aes-256-cbc -K $encrypted_43b7d2f1321f_key -iv $encrypted_43b7d2f1321f_iv -in mdc.enc -out mdc -d
  cat mdc > ~/.ssh/id_rsa
  chmod 600 ~/.ssh/id_rsa
  echo "Add docker server success"
  sonar-scanner
fi

cp -f config/checkstyle/checkStyle.xml config/checkstyle/checkStyleAll.xml
