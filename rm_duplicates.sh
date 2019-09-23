#!/usr/bin/env bash

# Usage: ./rm_duplicates.sh dir.csv

sed -e ':a' -e 's/,/\n/g' -i $1
sort -u $1 -o $1
sed -e ':a' -e 'N' -e '$!ba' -e 's/\n/,/g' -i $1