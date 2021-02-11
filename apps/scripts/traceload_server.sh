#!/bin/bash

if [[ $# -lt 2 || $# -eq 3 || $# -gt 5 ]]; then
    echo "Usage: $0 <send-interface> <trace-dir> [<listen-ip> <listen-port> [-k]]"
    exit 1
fi

INTERFACE=$1
TRACEDIR=$2

if [[ $# -gt 2 ]]; then
    LISTEN_IP=$3
    echo "LISTEN_IP: $LISTEN_IP"	
    LISTEN_PORT=$4
    echo "LISTEN_PORT: $LISTEN_PORT"
else
    LISTEN_IP=`host $HOSTNAME | cut -f4 -d' '`
    LISTEN_PORT=8080
fi

if [[ $# -gt 4 ]]; then
    KEEP=$5
    echo "KEEP: $KEEP"
fi

echo "Send on interface $INTERFACE"
echo "Starting traceload server"
echo "Listening for commands on $LISTEN_IP:$LISTEN_PORT"
starting_time=$(date +%s%N)
nc $KEEP -l $LISTEN_IP $LISTEN_PORT | while read COMMAND; do
    echo $COMMAND
    TOKENS=($COMMAND)
    echo "TOKENS is: $TOKENS"
    echo "ACTION is: $ACTION"
    echo "TRACE is: $TRACE"
    echo "NUMPKTS is: $NUMPKTS"
    ACTION=${TOKENS[0]}
    TRACE=${TOKENS[1]}
    RATE=${TOKENS[2]}
    NUMPKTS=${TOKENS[3]}
    if [[ $ACTION == "start" ]]; then
    	if [[ $NUMPKTS == -1 && $RATE == -1 ]]; then
		echo "This is 1 situation"
        	tcpreplay -i $INTERFACE $TRACEDIR/$TRACE &
        elif [[ $NUMPKTS == -1 ]]; then
			echo "This is 2 situation"
			echo "NUMPKTS : $NUMPKTS"
			echo "RATE : $RATE"
			echo "INTERFACE TRACEDIR/TRACE : $INTERFACE $TRACEDIR/$TRACE"
			tcpreplay -p $RATE -i $INTERFACE $TRACEDIR/$TRACE &
        elif [[ $NUMPKTS == -1 ]]; then
			echo "This is 3 situation"
			tcpreplay -L $NUMPKTS -i $INTERFACE $TRACEDIR/$TRACE &
        else
			echo "This is 4 situation $NUMPKTS"
			tcpreplay -L $NUMPKTS -p $RATE -i $INTERFACE $TRACEDIR/$TRACE &
		fi
    elif [[ $ACTION == "stop" ]]; then
	echo "ACTION =: $ACTION"
	echo "Time to stop"
        PSLINE=`ps uwax | grep tcpreplay | grep "$TRACE"`
        PSCOLS=($PSLINE)
        KILLPID=${PSCOLS[1]}
        kill -s SIGINT $KILLPID
     fi
done
relay_time=$((($(date +%s%N) - $starting_time)/1000000))
echo "Time to replay"
echo $replay_time
