#!/bin/sh
#-------------
# File:    leancop.sh
# Version: 2.0
# Date:    1 July 2007
#-------------
# Purpose: Invokes the leanCoP core theorem prover
# Usage:   ./leancop.sh <problem file> [<time limit>]
# Author:  Jens Otten
# Web:     www.leancop.de
#-------------

#-----------
# Parameters

# set ECLiPSe Prolog path
ECLIPSE=/usr/local/bin/eclipse

# set leanCoP prover path
PROVERPATH=/home/jeotten/Provers/leancop20

#----------
# Functions

leancop()
{
 # Input: $SETTINGS, $TLIMIT ; Output: $RETURN
 "$ECLIPSE" -b "$PROVERPATH/leancop_tlimit.pl" \
  -e "leancop_tlimit('$PROVERPATH/leancop20',$SETTINGS,'$FILE',$TLIMIT)"
 RETURN=$?
 if [ $RETURN -eq 0 ]; then echo " $FILE is a Theorem"; exit 0; fi
}

#-------------
# Main Program

if [ $# -eq 0 -o $# -gt 2 ]; then
 echo "Usage: $0 <problem file> [<time limit>]" >&2
 exit 2
fi

if [ ! -r "$1" ]; then
 echo "Error: File $1 not found"
 exit 2
fi

case "$2" in (*[!0-9]*)
 echo "Error: Time $2 is not a number"
 exit 2 ;;
esac

FILE=$1

if [ $# -eq 1 ]
 then TIMELIMIT=300
 else TIMELIMIT=$2
fi

# invoke the core prover with different settings

SETTINGS="[cut,comp(7)]";      TLIMIT=$(( 1 * $TIMELIMIT / 50)); leancop
if [ $RETURN -eq 1 ]; then echo "$FILE is a Non-Theorem"; exit 1; fi
SETTINGS="[conj,cut]";         TLIMIT=$((30 * $TIMELIMIT / 50)); leancop
SETTINGS="[def,scut,cut]";     TLIMIT=$(( 8 * $TIMELIMIT / 50)); leancop
SETTINGS="[nodef,scut,cut]";   TLIMIT=$(( 2 * $TIMELIMIT / 50)); leancop
SETTINGS="[conj,cut,reo(23)]"; TLIMIT=$(( 1 * $TIMELIMIT / 50)); leancop
SETTINGS="[def,scut,cut,reo(29)]";   TLIMIT=$(( 1 * $TIMELIMIT / 50)); leancop
SETTINGS="[conj,cut,reo(37)]";       TLIMIT=$(( 1 * $TIMELIMIT / 50)); leancop
SETTINGS="[nodef,scut,cut,reo(41)]"; TLIMIT=$(( 1 * $TIMELIMIT / 50)); leancop
SETTINGS="[conj,cut,reo(47)]"; TLIMIT=$(( 1 * $TIMELIMIT / 50)); leancop
SETTINGS="[]";                 TLIMIT=$TIMELIMIT               ; leancop
if [ $RETURN -eq 1 ]; then echo "$FILE is a Non-Theorem"; exit 1; fi
exit 2
