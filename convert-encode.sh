#! /bin/bash
#
# convert-encode.sh
# Copyright (C) 2017 zdw <zdw@zdw-mint>
#
# Distributed under terms of the MIT license.
#

set -x

pushd ~/tmp/ConverterTool

find . -name *.java > src.list

while read -r file
do
  encode=$(enca -i $file)
  if [[ $encode == '???' ]]
  then
    :
    #continue
  fi
  iconv -f $encode -t utf-8 "$file" -o "$file"
done < src.list


popd
