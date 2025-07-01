#!/usr/bin/env bash
# wait-for-it.sh

host="$1"
shift
port="$1"
shift
cmd="$@"

echo "Waiting for $host:$port to be ready..."

while ! nc -z "$host" "$port"; do
  sleep 1
done

echo "$host:$port is up — executing command"
exec $cmd




##!/usr/bin/env bash
#
#host="$1"
#shift
#port="$1"
#shift
#cmd="$@"
#
#if [ -z "$host" ] || [ -z "$port" ]; then
#  echo "Error: Missing host or port."
#  exit 1
#fi
#
#echo "Waiting for $host:$port to be ready..."
#
#while ! nc -z "$host" "$port"; do
#  sleep 1
#done
#
#echo "$host:$port is up — executing command"
#exec $cmd



##!/usr/bin/env bash
## wait-for-it.sh
#
#host="$1"
#shift
#port="$1"
#shift
#cmd="$@"
#
#echo "Waiting for $host:$port to be ready..."
#
#while ! nc -z "$host" "$port"; do
#  sleep 1
#done
#
#echo "$host:$port is up — executing command"
#exec $cmd
