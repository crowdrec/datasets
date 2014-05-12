#!/bin/sh
#$0: dataset_name
#$1: converted dataset output dir
#
# example: sh convert.sh snapshots_10K /tmp
# it requires groovy

if  [ $# -lt 2 ] ; then
	echo 'missing input parameters'
	exit
fi

DATASET_IN=../datasets/$1
DATASET_OUT=$2

groovy ConvertMovieTweetingsToCrowdRec $DATASET_IN $DATASET_OUT
