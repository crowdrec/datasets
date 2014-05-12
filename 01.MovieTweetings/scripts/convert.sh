#!/bin/sh
#$0: input dataset path (directory)
#$1: converted dataset output path (directory)
#
# example: sh convert.sh ~/datasets/MovieTweetings/snapshots_10K /tmp
# it requires groovy

if  [ $# -lt 2 ] ; then
	echo 'missing input parameters'
	exit
fi

DATASET_IN=$1
DATASET_OUT=$2

groovy ConvertMovieTweetingsToCrowdRec $DATASET_IN $DATASET_OUT
